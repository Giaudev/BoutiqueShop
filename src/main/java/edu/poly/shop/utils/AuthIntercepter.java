package edu.poly.shop.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import edu.poly.shop.domain.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthIntercepter implements HandlerInterceptor {
    @Autowired
    SessionService sessionService;

    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        Account user = sessionService.get("username");
        String error = "";
        if (user == null) { // chua dang nhap
            error = "Please login!";
        } else if (!user.getRole() && uri.startsWith("/admin/")) { // khong dung vai tro
            error = "Access denied";
        }
        if (error.length() > 0) { // co loi
            sessionService.set("security_uri", uri);
            response.sendRedirect("/admin/account/login?error=" + error);
            return false;
        }
        return true;
    }
}
