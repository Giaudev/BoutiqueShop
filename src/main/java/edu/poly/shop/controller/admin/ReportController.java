package edu.poly.shop.controller.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.poly.shop.service.OrderService;

@Controller
@RequestMapping("admin/reports")
public class ReportController {
    @Autowired
    private OrderService orderService;

    @GetMapping("lastweek")
    public String getRevenueForLastWeek(Model model) {
        Double amount = orderService.getRevenueForLastWeek();
        model.addAttribute("amountForLastWeek", amount);
        return "admin/report";
    }

    @GetMapping("lastmonth")
    public String getRevenueForLastMonth(Model model) {
        Double amount = orderService.getRevenueForLastMonth();
        model.addAttribute("amountForLastMonth", amount);
        return "admin/report";
    }

    @GetMapping("lastyear")
    public String getRevenueForLastYear(Model model) {
        Double amount = orderService.getRevenueForLastYear();
        model.addAttribute("amountForLastYear", amount);
        return "admin/report";
    }

    @GetMapping("bycategory")
    public String getRevenueByCategoryForLastMonth(Model model) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date startDate = calendar.getTime();
        Date endDate = new Date();
        List<Object[]> revenueByCategory = orderService.getRevenueByCategoryForPeriod(startDate, endDate);
        model.addAttribute("revenueByCategory", revenueByCategory);
        return "admin/report";
    }
}
