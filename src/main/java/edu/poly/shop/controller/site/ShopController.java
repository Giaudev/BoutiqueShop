package edu.poly.shop.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.poly.shop.service.ProductService;

@Controller
@RequestMapping("site/pages")
public class ShopController {
    @Autowired
    ProductService productService;

    @GetMapping("shop")
    public String shop(Model model) {
        return "site/pages/shop";
    }
}
