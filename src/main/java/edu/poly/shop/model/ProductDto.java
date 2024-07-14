package edu.poly.shop.model;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
	private Long productId;
	@NotBlank
	private String name;

	private Integer quantity;

	private Double price;

	private String image;
	private MultipartFile imageFile;
	private String description;

	private Double discount;

	private Date enteredDate = new Date();

	private Long categoryId;

	private Boolean isEdit = false;
}
