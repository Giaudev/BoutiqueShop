package edu.poly.shop.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.shop.domain.Product;
import edu.poly.shop.service.ProductService;
import java.util.*;

@Controller
@RequestMapping("site/pages")
public class HomeController {
    @Autowired
    ProductService productService;

    @GetMapping("home")
    public String home(Model model) {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> list = productService.findAll(pageable);
        model.addAttribute("products", list);
        return "site/pages/home";
    }

    @PostMapping("search")
    public String search(Model model, @RequestParam("search") String search) {
        List<Product> list = productService.findByNameContaining(search);
        model.addAttribute("products", list);
        return "site/pages/home";
    }

    @GetMapping("byCategory/{categoryId}/{name}")
    public String searchByCategory(Model model, @PathVariable(name = "categoryId", required = false) Long categoryId,
            @PathVariable(name = "name", required = false) String name) {
        List<Product> list = productService.findByNameAndCategoryId(categoryId, name);
        model.addAttribute("products", list);
        return "site/pages/home";
    }

    @GetMapping("contact")
    public String contact() {
        return "site/contact";
    }
}
