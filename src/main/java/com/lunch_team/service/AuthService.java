package com.lunch_team.service;

import com.lunch_team.dto.AuthResponse;
import com.lunch_team.dto.LoginDto;
import com.lunch_team.dto.RegisterDto;
import com.lunch_team.entity.Role;
import com.lunch_team.entity.User;
import com.lunch_team.exception.BusinessException;
import com.lunch_team.repository.RoleRepository;
import com.lunch_team.repository.UserRepository;
import com.lunch_team.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public User register(RegisterDto registerDto) {
        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw BusinessException.conflict("Tên đăng nhập đã tồn tại");
        }

        // Kiểm tra email đã tồn tại (nếu cần)
        // if (userRepository.existsByEmail(registerDto.getEmail())) {
        //     throw BusinessException.conflict("Email đã được sử dụng");
        // }

        try {
            User user = new User();
            user.setName(registerDto.getName());
            user.setUsername(registerDto.getUsername());
            user.setEmail(registerDto.getEmail());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            // Tìm role mặc định
            Role role = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> BusinessException.notFound("Vai trò mặc định không tồn tại"));

            user.setAuthorities(Collections.singleton(role.getName()));

            return userRepository.save(user);

        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("Lỗi khi tạo tài khoản: " + e.getMessage());
        }
    }

    public AuthResponse login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            // Chuyển đổi Collection thành Set
            Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());

            return new AuthResponse(token, loginDto.getUsername(), authorities);

        } catch (BadCredentialsException e) {
            throw BusinessException.unauthorized("Tên đăng nhập hoặc mật khẩu không đúng");
        } catch (AuthenticationException e) {
            throw BusinessException.unauthorized("Xác thực thất bại: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("Lỗi đăng nhập: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}