package com.Nisal.Agrimatch.service;

import com.Nisal.Agrimatch.dto.UserLoginRequest;
import com.Nisal.Agrimatch.dto.UserLoginResponse;
import com.Nisal.Agrimatch.dto.UserRegisterRequest;
import com.Nisal.Agrimatch.entity.User;
import com.Nisal.Agrimatch.repository.UserRepository;
import com.Nisal.Agrimatch.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(UserRegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    @Autowired
    private JwtUtil jwtUtil;

    public UserLoginResponse loginUser(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new UserLoginResponse(token, user.getName(), user.getRole().name());
    }


}
