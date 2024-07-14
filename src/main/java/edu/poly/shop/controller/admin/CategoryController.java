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
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import edu.poly.shop.domain.Category;
import edu.poly.shop.model.CategoryDto;
import edu.poly.shop.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.ui.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("admin/categories")
public class CategoryController {
	@Autowired
	CategoryService categoryService;

	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("category", new CategoryDto());
		return "admin/categories/addOrEdit";
	}

	@GetMapping("edit/{categoryId}")
	public ModelAndView edit(ModelMap model, @PathVariable("categoryId") Long categoryId) {
		Optional<Category> optional = categoryService.findById(categoryId);
		CategoryDto categoryDto = new CategoryDto();
		if (optional.isPresent()) { // kiểm tra tồn tại
			Category category = optional.get();
			BeanUtils.copyProperties(category, categoryDto);
			categoryDto.setIsEdit(true);
			model.addAttribute("category", categoryDto);
			return new ModelAndView("admin/categories/addOrEdit", model);
		}
		model.addAttribute("message", "Category is not existed");
		return new ModelAndView("forward:/admin/categories", model);
	}

	@GetMapping("delete/{categoryId}")
	public ModelAndView delete(ModelMap model, @PathVariable("categoryId") Long categoryId) {
		categoryService.deleteById(categoryId);
		model.addAttribute("message", "Category is deleted");
		return new ModelAndView("forward:/admin/categories/searchpaginated", model);
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("category") CategoryDto dto,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/categories/addOrEdit");
		}
		Category category = new Category();
		BeanUtils.copyProperties(dto, category);
		categoryService.save(category);
		model.addAttribute("message", "Category is saved");
		return new ModelAndView("forward:/admin/categories", model);
	}

	@RequestMapping("")
	public String list(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		Sort sort = Sort.by("name");
		Pageable pageable = PageRequest.of(currentPage, pageSize, sort);
		Page<Category> list = categoryService.findAll(pageable);
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
		model.addAttribute("categoryPage", list);
		return "admin/categories/list";
	}

	@GetMapping("search")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
		List<Category> list = null;
		if (org.springframework.util.StringUtils.hasText(name)) { // kiểm tra name có dữ liệu không nếu có thì true
			list = categoryService.findByNameContaining(name);
		} else {
			list = categoryService.findAll();
		}
		model.addAttribute("categories", list);
		return "admin/categories/search";
	}

	@GetMapping("searchpaginated")
	public String searchAndPage(ModelMap model, @RequestParam(name = "name", required = false) String name,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0); // trang hiện tại
		int pageSize = size.orElse(5); // mặc định hiển thị 5 thể loại
		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("name"));
		Page<Category> resultPage = null;
		if (org.springframework.util.StringUtils.hasText(name)) { // kiểm tra name có dữ liệu không nếu có thì true
			resultPage = categoryService.findByNameContaining(name, pageable);
			model.addAttribute("name", name);
		} else {
			resultPage = categoryService.findAll(pageable);
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
		model.addAttribute("categoryPage", resultPage);
		return "admin/categories/searchpaginated";
	}

}
