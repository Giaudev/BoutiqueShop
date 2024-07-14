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

@Controller
@RequestMapping("site/account")
public class RegisterController {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    SessionService sessionService;

    @GetMapping("register")
    public String register() {
        return "site/accounts/register";
    }

    @PostMapping("register")
    public String register(@RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("cfmpassword") String confirmPassword,
            @RequestParam("email") String email,
            Model model) {
        // kiểm tra password có khớp với confirmPassword
        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Password and Comfirm Password do not match");
            return "site/accounts/register";
        }
        // kiểm tra sự tồn tại username
        if (accountRepository.findByUsername(username).isPresent()) {
            model.addAttribute("message", "Username already exists");
            return "site/accounts/register";
        }
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setEmail(email);
        account.setRole(false); // mặc định là false

        accountRepository.save(account);
        model.addAttribute("message", "Sign Up Succsess");
        return "redirect:/site/accounts/register";
    }

}
