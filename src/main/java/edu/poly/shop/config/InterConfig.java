package edu.poly.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.poly.shop.utils.AuthIntercepter;

@Configuration
public class InterConfig implements WebMvcConfigurer {
    @Autowired
    AuthIntercepter authIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authIntercepter)
                .addPathPatterns("/account/edit", "account/chgpwd", "/order/**", "/admin/**")
                .excludePathPatterns("/assets/**", "/admin/home/index");
    }
}
