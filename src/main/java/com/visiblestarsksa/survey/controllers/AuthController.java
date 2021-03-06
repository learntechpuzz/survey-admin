package com.visiblestarsksa.survey.controllers;

import com.visiblestarsksa.survey.models.ERole;
import com.visiblestarsksa.survey.models.Role;
import com.visiblestarsksa.survey.models.User;
import com.visiblestarsksa.survey.payload.request.LoginRequest;
import com.visiblestarsksa.survey.payload.request.SignupRequest;
import com.visiblestarsksa.survey.payload.response.JwtResponse;
import com.visiblestarsksa.survey.payload.response.MessageResponse;
import com.visiblestarsksa.survey.repository.RoleRepository;
import com.visiblestarsksa.survey.repository.UserRepository;
import com.visiblestarsksa.survey.security.jwt.JwtUtils;
import com.visiblestarsksa.survey.security.service.UserDetailsImpl;
import com.visiblestarsksa.survey.util.EnumUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired AuthenticationManager authenticationManager;

    @Autowired UserRepository userRepository;

    @Autowired RoleRepository roleRepository;

    @Autowired PasswordEncoder encoder;

    @Autowired JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles =
                userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toList());

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user =
                User.builder()
                        .username(signUpRequest.getUsername())
                        .password(encoder.encode(signUpRequest.getPassword()))
                        .email(signUpRequest.getEmail())
                        .build();

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(Role.builder().name(ERole.User).build());
        } else {
            strRoles.forEach(
                    role -> {
                        try {
                            roles.add(
                                    Role.builder()
                                            .name(EnumUtil.value(ERole.class, role, ERole.User))
                                            .build());
                        } catch (Exception e) {
                            throw new RuntimeException("Error: Role is not found.");
                        }
                    });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
