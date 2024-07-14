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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.poly.shop.domain.Order;
import edu.poly.shop.domain.OrderDetail;
import edu.poly.shop.domain.Product;
import edu.poly.shop.model.OrderDetailDto;
import edu.poly.shop.service.CustomerService;
import edu.poly.shop.service.OrderDetailService;
import edu.poly.shop.service.OrderService;
import edu.poly.shop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.ui.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("admin/orderdetails")
public class OrderDetailController {
	@Autowired
	CustomerService customerService;
	@Autowired
	ProductService productService;
	@Autowired
	OrderService orderService;
	@Autowired
	OrderDetailService orderDetailService;

	@GetMapping("add")
	public String add(Model model) {
		OrderDetailDto orderDetailDto = new OrderDetailDto();
		model.addAttribute("orderDetail", orderDetailDto);
		return "admin/orderdetails/addOrEdit";
	}

	@GetMapping("edit/{orderDetailId}")
	public ModelAndView edit(ModelMap model, @PathVariable("orderDetailId") Long orderDetailId) {
		Optional<OrderDetail> optional = orderDetailService.findById(orderDetailId);
		OrderDetailDto orderDetailDto = new OrderDetailDto();
		if (optional.isPresent()) { // kiểm tra tồn tại
			OrderDetail orderDetail = optional.get();
			BeanUtils.copyProperties(orderDetail, orderDetailDto);
			orderDetailDto.setIsEdit(true);
			if (orderDetail != null) {
				orderDetailDto.setOrderId(orderDetail.getOrder().getOrderId());
				orderDetailDto.setProductId(orderDetail.getProduct().getProductId());
			}
			model.addAttribute("orderDetail", orderDetailDto);
			return new ModelAndView("admin/orderdetails/addOrEdit", model);
		}
		model.addAttribute("message", "OrderDetail is not existed");
		return new ModelAndView("forward:/admin/orderdetails", model);
	}

	@GetMapping("delete/{orderDetailId}")
	public ModelAndView delete(ModelMap model, @PathVariable("orderDetailId") Long orderDetailId) {
		orderService.deleteById(orderDetailId);
		model.addAttribute("message", "OrderDetail is deleted");
		return new ModelAndView("forward:/admin/orderdetails/searchpaginated", model);
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("orderDetail") OrderDetailDto dto,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/orderdetails/addOrEdit");
		}
		OrderDetail orderDetail = new OrderDetail();
		BeanUtils.copyProperties(dto, orderDetail);
		Order order = orderService.findById(dto.getOrderId()).get();
		if (order != null) {
			orderDetail.setOrder(order);
		}
		Product product = productService.findById(dto.getProductId()).get();
		if (product != null) {
			orderDetail.setProduct(product);
		}
		orderDetailService.save(orderDetail);
		model.addAttribute("message", "OrderDetail is saved");
		return new ModelAndView("redirect:/admin/orderdetails", model);
	}

	@RequestMapping("")
	public String list(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		Sort sort = Sort.by("orderDetailId");
		Pageable pageable = PageRequest.of(currentPage, pageSize, sort);
		Page<OrderDetail> list = orderDetailService.findAll(pageable);
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
		model.addAttribute("orderDetailPage", list);
		return "admin/orderdetails/list";
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
	public String searchAndPage(ModelMap model,
			@RequestParam(name = "orderId", required = false) Long orderId,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("orderDetailId"));
		Page<OrderDetail> resultPage = null;

		if (orderId != null) {
			resultPage = orderDetailService.findByOrderOrderId(orderId, pageable);
			model.addAttribute("orderId", orderId);
		} else {
			resultPage = orderDetailService.findAll(pageable);
		}

		int totalPages = resultPage.getTotalPages();
		if (totalPages > 0) {
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
		model.addAttribute("orderDetailPage", resultPage);
		return "admin/orderdetails/searchpaginated";
	}

}
