package com.trustedsolutions.crypto.controller;

import com.trustedsolutions.crypto.exception.ResourceNotFoundException;
import com.trustedsolutions.crypto.model.User;
import com.trustedsolutions.crypto.repository.UserRepository;
import com.trustedsolutions.crypto.security.CurrentUser;
import com.trustedsolutions.crypto.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
    
    
}
