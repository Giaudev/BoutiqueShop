package edu.poly.shop.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.shop.domain.Account;
import edu.poly.shop.repository.AccountRepository;
import edu.poly.shop.utils.SessionService;

@Controller("siteLoginController")
@RequestMapping("site/account")
public class LoginController {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    SessionService sessionService;

    @GetMapping("login")
    public String login() {
        return "site/accounts/login";
    }

    @PostMapping("login")
    public String login(Model model, @RequestParam("username") String username,
            @RequestParam("password") String password) {
        try {
            Account user = accountRepository.findByUsernameEquals(username);
            if (!user.getPassword().equals(password)) {
                model.addAttribute("message", "Password Invalid");
            } else {
                if (user.getRole() == false) {
                    String uri = sessionService.get("security_uri");
                    if (uri != null) {
                        return "redirect:" + uri;
                    } else {
                        sessionService.set("currentUser", user);
                        model.addAttribute("message", "Login succsess");
                        return "redirect:" + "/site/pages/home";
                    }
                }
            }
        } catch (Exception e) {
            model.addAttribute("message", "Username Invalid");
        }
        return "site/accounts/login";
    }

    @GetMapping("logout")
    public ModelAndView logout(ModelMap model) {
        sessionService.remove("currentUser");
        return new ModelAndView("redirect:/site/account/login", model);
    }

    // @GetMapping("forgotpassword")
    // public String forgotpassword() {
    // return "site/accounts/forgotpassword";
    // }
    // @PostMapping("forgotpassword")
    // public String forgotpassword(Model model) {

    // return "site/accounts/forgotpassword";
    // }
}
