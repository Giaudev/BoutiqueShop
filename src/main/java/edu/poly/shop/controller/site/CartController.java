package edu.poly.shop.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.shop.domain.Account;
import edu.poly.shop.domain.CartItem;
import edu.poly.shop.domain.Customer;
import edu.poly.shop.domain.Order;
import edu.poly.shop.service.CartService;
import edu.poly.shop.service.CustomerService;
import edu.poly.shop.service.OrderDetailService;
import edu.poly.shop.utils.SessionService;
import java.util.*;;

@Controller
@RequestMapping("site/carts")
public class CartController {
    @Autowired
    CartService cartService;
    @Autowired
    SessionService sessionService;
    @Autowired
    OrderDetailService orderDetailService;
    @Autowired
    CustomerService customerService;

    @GetMapping("cartdetail")
    public String shopDetail(Model model) {
        Account account = sessionService.get("currentUser");
        if (account == null) {
            return "redirect:/site/account/login";
        }
        // Lấy customer từ account
        Customer customer = customerService.findByAccountId(account.getAccountId());
        if (customer == null) {
            return "redirect:/site/account/login";
        }
        // lấy orderId hiện tại
        Long orderId = cartService.getCurrentOrderId(customer.getCustomerId());
        // Lấy thông tin chi tiết đơn hàng
        List<CartItem> cartItems = orderDetailService.findOrderProductDetailsByOrderId(orderId);
        // tính tổng tiền hiện ở cart summary
        Double totalAmount = cartItems.stream().mapToDouble(cart -> cart.getPrice() * cart.getQuantity()).sum();
        // hiển thị totalAmount
        model.addAttribute("totalAmount", totalAmount);
        // thêm thông tin chi tiết vào model để hiển thị
        model.addAttribute("cartItems", cartItems);
        // số lượng giỏ hàng hiển thị ở header
        int totalCartItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        model.addAttribute("totalCartItems", totalCartItems);
        return "site/carts/cart";
    }

    @PostMapping("cartdetail/{productId}")
    public String shopDetail(Model model, @PathVariable("productId") String productId) {
        // Chuyển đổi productId từ String sang Long
        Long parsedProductId = Long.parseLong(productId);

        Account account = sessionService.get("currentUser");
        if (account == null) {
            return "redirect:/site/account/login";
        }
        // Lấy customer từ account
        Customer customer = customerService.findByAccountId(account.getAccountId());
        if (customer == null) {
            return "redirect:/site/account/login";
        }
        // thêm sản phẩm vào giỏ hàng
        cartService.addToCart(customer.getCustomerId(), parsedProductId);
        // lấy orderId hiện tại
        Long orderId = cartService.getCurrentOrderId(customer.getCustomerId());
        // Lấy thông tin chi tiết đơn hàng
        List<CartItem> cartItems = orderDetailService.findOrderProductDetailsByOrderId(orderId);
        // thêm thông tin chi tiết vào model để hiển thị
        model.addAttribute("cartItems", cartItems);
        // tính tổng tiền hiện ở cart summary
        Double totalAmount = cartItems.stream().mapToDouble(cart -> cart.getPrice() * cart.getQuantity()).sum();
        // hiển thị totalAmount
        model.addAttribute("totalAmount", totalAmount);
        // số lượng giỏ hàng hiển thị ở header
        int totalCartItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        model.addAttribute("totalCartItems", totalCartItems);
        return "site/carts/cart";
    }

    @GetMapping("deletecart/{productId}")
    public String deleteCart(Model model, @PathVariable("productId") Long productId) {
        Account account = sessionService.get("currentUser");
        if (account == null) {
            return "redirect:/site/account/login";
        }
        Customer customer = customerService.findByAccountId(account.getAccountId());
        if (customer == null) {
            return "redirect:/site/account/login";
        }
        cartService.removeCart(customer.getCustomerId(), productId);
        Long orderId = cartService.getCurrentOrderId(customer.getCustomerId());
        List<CartItem> cartItems = orderDetailService.findOrderProductDetailsByOrderId(orderId);
        model.addAttribute("cartItems", cartItems);
        // tính tổng tiền hiện ở cart summary
        Double totalAmount = cartItems.stream().mapToDouble(cart -> cart.getPrice() * cart.getQuantity()).sum();
        // hiển thị totalAmount
        model.addAttribute("totalAmount", totalAmount);
        // số lượng giỏ hàng hiển thị ở header
        int totalCartItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        model.addAttribute("totalCartItems", totalCartItems);
        return "site/carts/cart";
    }

    @GetMapping("increase/{productId}")
    public String increaseQuantity(Model model, @PathVariable("productId") Long productId) {
        Account account = sessionService.get("currentUser");
        if (account == null) {
            return "redirect:/site/account/login";
        }
        Customer customer = customerService.findByAccountId(account.getAccountId());
        if (customer == null) {
            return "redirect:/site/account/login";
        }
        cartService.increaseQuantity(customer.getCustomerId(), productId);
        return "redirect:/site/carts/cartdetail";
    }

    @GetMapping("decrease/{productId}")
    public String decreaseQuantity(Model model, @PathVariable("productId") Long productId) {
        Account account = sessionService.get("currentUser");
        if (account == null) {
            return "redirect:/site/account/login";
        }
        Customer customer = customerService.findByAccountId(account.getAccountId());
        if (customer == null) {
            return "redirect:/site/account/login";
        }
        cartService.decreaseQuantity(customer.getCustomerId(), productId);
        return "redirect:/site/carts/cartdetail";
    }
    // private void addCartItemCountToModel(Model model, Account account) {
    // if (account != null) {
    // Customer customer = customerService.findByAccountId(account.getAccountId());
    // int totalCartItems = cartService.getTotalCartItems(customer.getCustomerId());
    // model.addAttribute("totalCartItems", totalCartItems);
    // }
}
