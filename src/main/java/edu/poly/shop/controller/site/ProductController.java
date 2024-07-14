package edu.poly.shop.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;
import edu.poly.shop.domain.Product;
import edu.poly.shop.service.ProductService;

@Controller("siteProductController")
@RequestMapping("site/products")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("productdetail/{productId}")
    public String shopDetail(Model model, @PathVariable("productId") Long productId) {
        Product product = productService.findById(productId).orElse(null);
        model.addAttribute("product", product);

        List<Product> list = productService.findByCategoryID(product.getCategory().getCategoryId());
        model.addAttribute("products", list);
        return "site/products/detail";
    }
}
