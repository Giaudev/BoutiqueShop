package edu.poly.shop.controller.site;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.poly.shop.domain.Account;
import edu.poly.shop.domain.CartItem;
import edu.poly.shop.domain.Customer;
import edu.poly.shop.service.CartService;
import edu.poly.shop.service.CustomerService;
import edu.poly.shop.utils.SessionService;;

@Controller
@RequestMapping("site/carts")
public class CheckoutController {
    @Autowired
    CustomerService customerService;
    @Autowired
    CartService cartService;
    @Autowired
    SessionService sessionService;

    @GetMapping("checkout")
    public String checkDetail(Model model) {
        Account account = (Account) sessionService.get("currentUser");
        if (account == null) {
            return "redirect:/site/account/login";
        }
        Customer customerCheck = customerService.findByAccountId(account.getAccountId());
        if (customerCheck == null) {
            return "redirect:/site/account/login";
        }
        // Customer customer = customerService.findByCustomerByCustomerId(customerId);
        List<CartItem> cartItems = cartService.findOrderProductDetailsByCustomerId(customerCheck.getCustomerId());
        Double totalAmount = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        model.addAttribute("customer", customerCheck);
        // model.addAttribute("customer", customer);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "site/carts/checkout";
    }
}
