package com.example.jpaboard.controller;

import java.util.Map;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class SessionController {

   

    @GetMapping("/login")
    public String loginForm() {
        return "login"; // 로그인 폼
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, HttpSession session) {
        session.setAttribute("sessionData", username); // 입력한 이름 저장
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}



