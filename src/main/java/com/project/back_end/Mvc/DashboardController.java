package com.project.back_end.Mvc;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    // 管理者ダッシュボード
    @GetMapping("/dashboard")
    public String redirectBasedOnRole() {
    	
    	System.out.println("aaa");
    	
        var auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            for (GrantedAuthority authority : auth.getAuthorities()) {
                String role = authority.getAuthority();
                return switch (role) {
                    case "ROLE_ADMIN" -> "admin/adminDashboard";
                    case "ROLE_DOCTOR" -> "doctor/doctorDashboard";
                    case "ROLE_PATIENT" -> "patient/patientDashboard";
                    default -> "redirect:/login";
                };
            }
        }
        return "redirect:/login";
    }
}
