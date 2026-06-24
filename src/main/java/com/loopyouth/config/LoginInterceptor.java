package com.loopyouth.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("user_id") != null) {
            return true;
        }

        Cookie cookie = new Cookie("url", request.getRequestURI());
        cookie.setPath("/");
        response.addCookie(cookie);

        response.sendRedirect("/user/login/");
        return false;
    }
}
