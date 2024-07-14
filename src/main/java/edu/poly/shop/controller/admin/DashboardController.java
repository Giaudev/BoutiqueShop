package edu.poly.shop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/home")
public class DashboardController {
    @GetMapping("view")
    public String view() {
        return "admin/dashboard";
    }
}
