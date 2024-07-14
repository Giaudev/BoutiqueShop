package edu.poly.shop.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;

import edu.poly.shop.domain.Account;
import edu.poly.shop.domain.Customer;
import edu.poly.shop.domain.Order;
import edu.poly.shop.repository.OrderRepository;
import edu.poly.shop.service.CustomerService;
import edu.poly.shop.service.OrderService;
import edu.poly.shop.utils.SessionService;

@Controller
@RequestMapping("site/carts")
public class CompleteOrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    SessionService sessionService;
    @Autowired
    CustomerService customerService;
    @Autowired
    OrderRepository orderRepository;

    @GetMapping("listcompleteorder")
    public String listCompleteOrder(Model model) {
        Account account = (Account) sessionService.get("currentUser");
        if (account == null) {
            return "redirect:/site/account/login";
        }
        Customer customerCheck = customerService.findByAccountId(account.getAccountId());
        if (customerCheck == null) {
            return "redirect:/site/account/login";
        }
        List<Order> list = orderService.findAllByCustomerId(customerCheck.getCustomerId());
        model.addAttribute("orders", list);
        return "site/carts/completeorder";
    }

    @GetMapping("completeorder")
    public String updateCompleteOrder(Model model) {
        Account account = (Account) sessionService.get("currentUser");
        if (account == null) {
            return "redirect:/site/account/login";
        }
        Customer customerCheck = customerService.findByAccountId(account.getAccountId());
        if (customerCheck == null) {
            return "redirect:/site/account/login";
        }
        Optional<Order> orderOptional = orderRepository.findByOrderByCustomer(customerCheck.getCustomerId());
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus((short) 1);
            orderService.save(order);
        }
        List<Order> list = orderService.findAllByCustomerId(customerCheck.getCustomerId());
        model.addAttribute("orders", list);
        return "site/carts/completeorder";
    }
}