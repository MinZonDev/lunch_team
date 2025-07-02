package com.lunch_team.service;

import com.lunch_team.dto.*;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw BusinessException.conflict("Tên đăng nhập đã tồn tại");
        }

         if (userRepository.existsByEmail(registerDto.getEmail())) {
             throw BusinessException.conflict("Email đã được sử dụng");
         }

        try {
            User user = new User();
            user.setName(registerDto.getName());
            user.setUsername(registerDto.getUsername());
            user.setEmail(registerDto.getEmail());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

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

    public UserProfileDto getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("Người dùng không tồn tại"));

        return convertToUserProfileDto(user);
    }

    public UserProfileDto updateMyProfile(String username, UpdateProfileDto updateProfileDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("Người dùng không tồn tại"));

        if (!user.getEmail().equals(updateProfileDto.getEmail()) &&
                userRepository.existsByEmail(updateProfileDto.getEmail())) {
            throw BusinessException.conflict("Email đã được sử dụng");
        }

        try {
            user.setName(updateProfileDto.getName());
            user.setEmail(updateProfileDto.getEmail());

            User updatedUser = userRepository.save(user);
            return convertToUserProfileDto(updatedUser);

        } catch (Exception e) {
            throw new BusinessException("Lỗi khi cập nhật thông tin: " + e.getMessage());
        }
    }

    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("Người dùng không tồn tại"));

        UserProfileDto profile = convertToUserProfileDto(user);
        profile.setDeposit(null);
        return profile;
    }

    public List<UserProfileDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserProfileDto)
                .collect(Collectors.toList());
    }

    public List<UserProfileDto> searchUsers(String keyword) {
        List<User> users = userRepository.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
                keyword, keyword);

        return users.stream()
                .map(user -> {
                    UserProfileDto profile = convertToUserProfileDto(user);
                    profile.setDeposit(null); // Ẩn thông tin deposit trong tìm kiếm
                    return profile;
                })
                .collect(Collectors.toList());
    }

    private UserProfileDto convertToUserProfileDto(User user) {
        Set<String> authorityNames = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new UserProfileDto(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getDeposit(),
                authorityNames
        );
    }
}