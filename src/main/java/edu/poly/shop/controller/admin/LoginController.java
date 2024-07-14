package edu.poly.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.shop.domain.Account;
import edu.poly.shop.repository.AccountRepository;
import edu.poly.shop.utils.SessionService;

@Controller("adminLoginController")
public class LoginController {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    SessionService sessionService;

    @GetMapping("/admin/account/login")
    public String login() {
        return "admin/accounts/login";
    }

    @PostMapping("/admin/account/login")
    public String login(Model model, @RequestParam("username") String username,
            @RequestParam("password") String password) {
        try {
            Account user = accountRepository.findByUsernameEquals(username);
            if (!user.getPassword().equals(password)) {
                model.addAttribute("message", "Password Invalid");
            } else {
                String uri = sessionService.get("security_uri");
                if (uri != null) {
                    return "redirect:" + uri;
                } else {
                    sessionService.set("currentUser", user);
                    model.addAttribute("message", "Login succsess");
                    return "redirect:" + "/admin/home/view";
                }
            }
        } catch (Exception e) {
            model.addAttribute("message", "Username Invalid");
        }
        return "admin/accounts/login";
    }

    @GetMapping("/admin/account/logout")
    public ModelAndView logout(ModelMap model) {
        sessionService.remove("currentUser");
        return new ModelAndView("redirect:/admin/account/login", model);
    }
}
