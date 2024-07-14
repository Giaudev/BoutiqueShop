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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.poly.shop.domain.Category;
import edu.poly.shop.domain.Product;
import edu.poly.shop.model.ProductDto;
import edu.poly.shop.service.CategoryService;
import edu.poly.shop.service.ProductService;
import edu.poly.shop.utils.UploadUtils;
import jakarta.validation.Valid;
import org.springframework.ui.*;
import org.springframework.validation.BindingResult;

@Controller("adminProductController")
@RequestMapping("admin/products")
public class ProductController {
	@Autowired
	CategoryService categoryService;
	@Autowired
	ProductService productService;

	@GetMapping("add")
	public String add(Model model) {
		ProductDto productDto = new ProductDto();
		model.addAttribute("product", productDto);
		List<Category> categories = categoryService.findAll();
		model.addAttribute("categories", categories);
		return "admin/products/addOrEdit";
	}

	@GetMapping("edit/{productId}")
	public ModelAndView edit(ModelMap model, @PathVariable("productId") Long productId) {
		Optional<Product> optional = productService.findById(productId);
		ProductDto productDto = new ProductDto();
		if (optional.isPresent()) { // kiểm tra tồn tại
			Product product = optional.get();
			BeanUtils.copyProperties(product, productDto);
			productDto.setIsEdit(true);
			if (product.getCategory() != null) {
				productDto.setCategoryId(product.getCategory().getCategoryId());
			}
			model.addAttribute("product", productDto);
			List<Category> categories = categoryService.findAll();
			model.addAttribute("categories", categories);
			return new ModelAndView("admin/products/addOrEdit", model);
		}
		model.addAttribute("message", "Product is not existed");
		return new ModelAndView("forward:/admin/products", model);
	}

	@GetMapping("delete/{productId}")
	public ModelAndView delete(ModelMap model, @PathVariable("productId") Long productId) {
		productService.deleteById(productId);
		model.addAttribute("message", "Product is deleted");
		return new ModelAndView("forward:/admin/products/searchpaginated", model);
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("product") ProductDto dto,
			BindingResult result) {
		if (result.hasErrors()) {
			List<Category> categories = categoryService.findAll();
			model.addAttribute("categories", categories);
			model.addAttribute("product", dto);
			return new ModelAndView("admin/products/addOrEdit");
		}
		Product product = new Product();
		// xứ lý tệp ảnh MultipartFile
		MultipartFile imageFile = dto.getImageFile();
		if (imageFile != null && !imageFile.isEmpty()) {
			try {
				String uploadDir = "src/main/resources/static/uploads";
				String originalFileName = imageFile.getOriginalFilename();
				String newFileName = UploadUtils.saveFile(uploadDir, originalFileName, imageFile);
				dto.setImage(newFileName);
			} catch (IOException e) {
				e.printStackTrace();
				model.addAttribute("message", "Lỗi khi tải tệp: " + e.getMessage());
				return new ModelAndView("admin/products/addOrEdit", model);
			}
		} else {
			// Giữ hình ảnh cũ
			Optional<Product> existProduct = productService.findById(dto.getProductId());
			if (existProduct.isPresent()) {
				product.setImage(existProduct.get().getImage());
			}
		}

		BeanUtils.copyProperties(dto, product);
		if (dto.getCategoryId() != null) {
			Category category = categoryService.findById(dto.getCategoryId()).orElse(null);
			product.setCategory(category);
		}
		if (dto.getProductId() != null) {
			Product product2 = productService.findById(dto.getProductId()).orElse(null);
			product.setEnteredDate(product2.getEnteredDate());
		}
		productService.save(product);
		model.addAttribute("message", "Product is saved");
		return new ModelAndView("redirect:/admin/products", model);
	}

	private String saveImage(MultipartFile imageFile) {
		try {
			// định nghĩa thư mục lưu trữ hình ảnh
			String uploadDir = "src/main/resources/static/images/";
			// lấy tên file gốc
			String originalFileName = imageFile.getOriginalFilename();
			// tạo đường dẫn tệp
			Path filePath = Paths.get(uploadDir + originalFileName);
			// Lưu tệp vào thư mục đã định nghĩa
			Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			// trả về đường dẫn tương đối
			return "/images/" + originalFileName;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Loi: " + e.getMessage());
			return null;
		}
	}

	@RequestMapping("")
	public String list(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		Sort sort = Sort.by("name");
		Pageable pageable = PageRequest.of(currentPage, pageSize, sort);
		Page<Product> list = productService.findAll(pageable);
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
		model.addAttribute("productPage", list);
		return "admin/products/list";
	}

	@GetMapping("search")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
		List<Product> list = null;
		if (org.springframework.util.StringUtils.hasText(name)) { // kiểm tra name có dữ liệu không nếu có thì true
			list = productService.findByNameContaining(name);
		} else {
			list = productService.findAll();
		}
		model.addAttribute("products", list);
		return "admin/products/search";
	}

	@GetMapping("searchpaginated")
	public String searchAndPage(ModelMap model, @RequestParam(name = "name", required = false) String name,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0); // trang hiện tại
		int pageSize = size.orElse(5); // mặc định hiển thị 5 thể loại
		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("name"));
		Page<Product> resultPage = null;
		if (org.springframework.util.StringUtils.hasText(name)) { // kiểm tra name có dữ liệu không nếu có thì true
			resultPage = productService.findByNameContaining(name, pageable);
			model.addAttribute("name", name);
		} else {
			resultPage = productService.findAll(pageable);
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
		model.addAttribute("productPage", resultPage);
		return "admin/products/searchpaginated";
	}

}
