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

import edu.poly.shop.domain.Account;
import edu.poly.shop.model.AccountDto;
import edu.poly.shop.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.ui.*;
import org.springframework.validation.BindingResult;

@Controller("adminAccountController")
@RequestMapping("admin/accounts")
public class AccountController {
	@Autowired
	AccountService accountService;

	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("account", new AccountDto());
		return "admin/accounts/addOrEdit";
	}

	@GetMapping("edit/{accountId}")
	public ModelAndView edit(ModelMap model, @PathVariable("accountId") Long accountId) {
		Optional<Account> optional = accountService.findById(accountId);
		AccountDto accountDto = new AccountDto();
		if (optional.isPresent()) { // kiểm tra tồn tại
			Account account = optional.get();
			BeanUtils.copyProperties(account, accountDto);
			accountDto.setIsEdit(true);
			model.addAttribute("account", accountDto);
			return new ModelAndView("admin/accounts/addOrEdit", model);
		}
		model.addAttribute("message", "Account is not existed");
		return new ModelAndView("forward:/admin/accounts", model);
	}

	@GetMapping("delete/{accountId}")
	public ModelAndView delete(ModelMap model, @PathVariable("accountId") Long accountId) {
		accountService.deleteById(accountId);
		model.addAttribute("message", "Account is deleted");
		return new ModelAndView("forward:/admin/accounts/searchpaginated", model);
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("account") AccountDto dto,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/accounts/addOrEdit");
		}
		Account account = new Account();
		BeanUtils.copyProperties(dto, account);
		accountService.save(account);
		model.addAttribute("message", "Account is saved");
		return new ModelAndView("forward:/admin/accounts", model);
	}

	@RequestMapping("")
	public String list(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		Sort sort = Sort.by("username");
		Pageable pageable = PageRequest.of(currentPage, pageSize, sort);
		Page<Account> list = accountService.findAll(pageable);
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
		model.addAttribute("accountPage", list);
		return "admin/accounts/list";
	}

	@GetMapping("search")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
		List<Account> list = null;
		if (org.springframework.util.StringUtils.hasText(name)) { // kiểm tra name có dữ liệu không nếu có thì true
			list = accountService.findByUsernameContaining(name);
		} else {
			list = accountService.findAll();
		}
		model.addAttribute("accounts", list);
		return "admin/accounts/search";
	}

	@GetMapping("searchpaginated")
	public String searchAndPage(ModelMap model, @RequestParam(name = "name", required = false) String name,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0); // trang hiện tại
		int pageSize = size.orElse(5); // mặc định hiển thị 5 thể loại
		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("username"));
		Page<Account> resultPage = null;
		if (org.springframework.util.StringUtils.hasText(name)) { // kiểm tra name có dữ liệu không nếu có thì true
			resultPage = accountService.findByUsernameContaining(name, pageable);
			model.addAttribute("name", name);
		} else {
			resultPage = accountService.findAll(pageable);
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
		model.addAttribute("accountPage", resultPage);
		return "admin/accounts/searchpaginated";
	}

}
