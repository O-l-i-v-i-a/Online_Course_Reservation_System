package com.example.onlinecourse.controller;

import com.example.onlinecourse.domain.AppUser;
import com.example.onlinecourse.domain.UserRole;
import com.example.onlinecourse.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        Optional<AppUser> user = appUserRepository.findByEmail(email);
        
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
        
        // Redirect to appropriate dashboard - handled by AuthenticationSuccessHandler
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        // This will redirect based on role via the success handler
        return "dashboard";
    }
}
