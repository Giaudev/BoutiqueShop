package edu.poly.shop.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.shop.domain.Account;
import edu.poly.shop.domain.Customer;
import edu.poly.shop.repository.AccountRepository;
import edu.poly.shop.repository.CustomerRepository;
import edu.poly.shop.utils.SessionService;

@Controller("siteAccountController")
@RequestMapping("site/account")
public class AccountController {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    SessionService sessionService;
    @Autowired
    CustomerRepository customerRepository;

    @GetMapping("change-password")
    public String changePassword() {
        return "site/accounts/changepassword";
    }

    @PostMapping("change-password")
    public String changePassword(Model model, @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword) {

        Account account = (Account) sessionService.get("currentUser");
        if (account == null) {
            model.addAttribute("message", "You need to log in to change your password");
            return "redirect:/site/account/login";
        }
        if (!account.getPassword().equals(oldPassword)) {
            model.addAttribute("message", "The old password is incorrect");
            return "site/accounts/changepassword";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "New password and confirm password not match");
            return "site/accounts/changepassword";
        }
        account.setPassword(newPassword);
        accountRepository.save(account);
        model.addAttribute("message", "changePassword succsess");

        return "site/accounts/changepassword";
    }

    @GetMapping("profile")
    public String profile(Model model) {
        Account account = (Account) sessionService.get("currentUser");
        if (account == null) {
            return "redirect:/site/account/login";
        }
        Customer customer = customerRepository.findByAccountId(account.getAccountId());
        model.addAttribute("customer", customer);
        return "site/accounts/profile";
    }

    @PostMapping("profile")
    public String updateProfile(Model model, @RequestParam("fullname") String fullname,
            @RequestParam("address") String address, @RequestParam("phone") String phone) {
        Account account = (Account) sessionService.get("currentUser");
        if (account == null) {
            return "redirect:/site/account/login";
        }
        Customer customer = customerRepository.findByAccountId(account.getAccountId());
        if (customer != null) {
            customer.setFullname(fullname);
            customer.setPhone(phone);
            customer.setAddress(address);
            customerRepository.save(customer);
        }
        model.addAttribute("customer", customer);
        return "site/accounts/profile"; // trả về file html
    }
}
