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
import edu.poly.shop.domain.Customer;
import edu.poly.shop.model.CustomerDto;
import edu.poly.shop.service.AccountService;
import edu.poly.shop.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.ui.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("admin/customers")
public class CustomerController {
	@Autowired
	AccountService accountService;
	@Autowired
	CustomerService customerService;

	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("customer", new CustomerDto());
		model.addAttribute("accounts", accountService.findAll());
		return "admin/customers/addOrEdit";
	}

	@GetMapping("edit/{customerId}")
	public ModelAndView edit(ModelMap model, @PathVariable("customerId") Long customerId) {
		Optional<Customer> optional = customerService.findById(customerId);
		CustomerDto customerDto = new CustomerDto();
		if (optional.isPresent()) { // kiểm tra tồn tại
			Customer customer = optional.get();
			BeanUtils.copyProperties(customer, customerDto);
			customerDto.setIsEdit(true);
			model.addAttribute("customer", customerDto);
			model.addAttribute("accounts", accountService.findAll());
			return new ModelAndView("admin/customers/addOrEdit", model);
		}
		model.addAttribute("message", "Customer is not existed");
		return new ModelAndView("forward:/admin/customers", model);
	}

	@GetMapping("delete/{customerId}")
	public ModelAndView delete(ModelMap model, @PathVariable("customerId") Long customerId) {
		customerService.deleteById(customerId);
		model.addAttribute("message", "Customer is deleted");
		return new ModelAndView("forward:/admin/customers/searchpaginated", model);
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("customer") CustomerDto dto,
			BindingResult result) {
		if (result.hasErrors()) {
			model.addAttribute("accounts", accountService.findAll());
			return new ModelAndView("admin/customers/addOrEdit");
		}
		Customer customer = new Customer();
		BeanUtils.copyProperties(dto, customer);
		customer.setAccount(accountService.findById(dto.getAccountId()).get());
		customerService.save(customer);
		model.addAttribute("message", "Customer is saved");
		return new ModelAndView("forward:/admin/customers", model);
	}

	@RequestMapping("")
	public String list(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		Sort sort = Sort.by("fullname");
		Pageable pageable = PageRequest.of(currentPage, pageSize, sort);
		Page<Customer> list = customerService.findAll(pageable);
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
		model.addAttribute("customerPage", list);
		return "admin/customers/list";
	}

	@GetMapping("search")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
		List<Customer> list = null;
		if (org.springframework.util.StringUtils.hasText(name)) { // kiểm tra name có dữ liệu không nếu có thì true
			list = customerService.findByFullnameContaining(name);
		} else {
			list = customerService.findAll();
		}
		model.addAttribute("customers", list);
		return "admin/customers/search";
	}

	@GetMapping("searchpaginated")
	public String searchAndPage(ModelMap model, @RequestParam(name = "name", required = false) String name,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0); // trang hiện tại
		int pageSize = size.orElse(5); // mặc định hiển thị 5 thể loại
		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("fullname"));
		Page<Customer> resultPage = null;
		if (org.springframework.util.StringUtils.hasText(name)) { // kiểm tra name có dữ liệu không nếu có thì true
			resultPage = customerService.findByFullnameContaining(name, pageable);
			model.addAttribute("name", name);
		} else {
			resultPage = customerService.findAll(pageable);
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
		model.addAttribute("customerPage", resultPage);
		return "admin/customers/searchpaginated";
	}

}
