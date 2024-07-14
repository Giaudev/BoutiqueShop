package edu.poly.shop.controller.admin;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.poly.shop.domain.Customer;
import edu.poly.shop.domain.Order;
import edu.poly.shop.model.OrderDto;
import edu.poly.shop.service.CategoryService;
import edu.poly.shop.service.CustomerService;
import edu.poly.shop.service.OrderService;
import edu.poly.shop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.ui.*;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("admin/orders")
public class OrderController {
	@Autowired
	CustomerService customerService;
	@Autowired
	ProductService productService;
	@Autowired
	OrderService orderService;

	@GetMapping("add")
	public String add(Model model) {
		OrderDto orderDto = new OrderDto();
		model.addAttribute("order", orderDto);
		return "admin/orders/addOrEdit";
	}

	@GetMapping("edit/{orderId}")
	public ModelAndView edit(ModelMap model, @PathVariable("orderId") Long orderId) {
		Optional<Order> optional = orderService.findById(orderId);
		OrderDto orderDto = new OrderDto();
		if (optional.isPresent()) { // kiểm tra tồn tại
			Order order = optional.get();
			BeanUtils.copyProperties(order, orderDto);
			orderDto.setIsEdit(true);
			if (order != null) {
				orderDto.setCustomerId(order.getCustomer().getCustomerId());
			}
			model.addAttribute("order", orderDto);
			return new ModelAndView("admin/orders/addOrEdit", model);
		}
		model.addAttribute("message", "Order is not existed");
		return new ModelAndView("forward:/admin/orders", model);
	}

	@GetMapping("delete/{orderId}")
	public ModelAndView delete(ModelMap model, @PathVariable("orderId") Long orderId) {
		orderService.deleteById(orderId);
		model.addAttribute("message", "Order is deleted");
		return new ModelAndView("forward:/admin/orders/searchpaginated", model);
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("order") OrderDto dto,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/orders/addOrEdit");
		}
		Order order = new Order();
		BeanUtils.copyProperties(dto, order);
		Customer customer = customerService.findById(dto.getCustomerId()).get();
		if (customer != null) {
			order.setCustomer(customer);
		} else {
			return new ModelAndView("admin/orders/addOrEdit");
		}
		orderService.save(order);
		model.addAttribute("message", "Order is saved");
		return new ModelAndView("redirect:/admin/orders", model);
	}

	@RequestMapping("")
	public String list(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		Sort sort = Sort.by("orderId");
		Pageable pageable = PageRequest.of(currentPage, pageSize, sort);
		Page<Order> list = orderService.findAll(pageable);
		int totalPages = list.getTotalPages();
		if (totalPages > 0) {
			// 1 2 3 4 5
			// trang hiện tại trang 3 thì start sẽ là 1 và 2
			// và end sẽ là 4 và 5; 1: là lấy số 1 nhỏ nhất không lấy âm
			// totalPages có nghĩa là không được vượt quá tổng số trang
			int start = Math.max(1, currentPage + 1 - 2);
			int end = Math.min(currentPage + 1 + 2, totalPages);
			if (totalPages > 5) {
				if (end == totalPages) {
					start = end - 5;
				} else if (start == 1) {
					end = start + 5;
				}
			}
			List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
		model.addAttribute("orderPage", list);
		return "admin/orders/list";
	}

	// @GetMapping("search")
	// public String search(ModelMap model, @RequestParam(name = "name", required =
	// false) String name) {
	// List<Order> list = null;
	// if (org.springframework.util.StringUtils.hasText(name)) { // kiểm tra name có
	// dữ liệu không nếu có thì true
	// list = orderService.findByStatusContaining(name);
	// } else {
	// list = orderService.findAll();
	// }
	// model.addAttribute("products", list);
	// return "admin/products/search";
	// }

	@GetMapping("searchpaginated")
	public String searchAndPage(ModelMap model, @RequestParam(name = "date", required = false) String datestr,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size)
			throws ParseException {
		int currentPage = page.orElse(0); // trang hiện tại
		int pageSize = size.orElse(5); // mặc định hiển thị 5 thể loại
		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("orderId"));
		Page<Order> resultPage = null;
		if (StringUtils.hasText(datestr)) { // kiểm tra date
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(datestr);
			resultPage = orderService.findByOrderDateEquals(date, pageable);
			model.addAttribute("date", date);
		} else {
			resultPage = orderService.findAll(pageable);
		}
		int totalPages = resultPage.getTotalPages();
		if (totalPages > 0) {
			// 1 2 3 4 5
			// trang hiện tại trang 3 thì start sẽ là 1 và 2
			// và end sẽ là 4 và 5; 1: là lấy số 1 nhỏ nhất không lấy âm
			// totalPages có nghĩa là không được vượt quá tổng số trang
			int start = Math.max(1, currentPage + 1 - 2);
			int end = Math.min(currentPage + 1 + 2, totalPages);
			if (totalPages > 5) {
				if (end == totalPages) {
					start = end - 5;
				} else if (start == 1) {
					end = start + 5;
				}
			}
			List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
		model.addAttribute("orderPage", resultPage);
		return "admin/orders/searchpaginated";
	}

}
