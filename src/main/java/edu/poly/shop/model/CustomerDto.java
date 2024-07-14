package edu.poly.shop.model;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
	private Long customerId;
	@NotBlank
	private String fullname;
	@NotBlank
	private String phone;
	@NotBlank
	private String address;

	private Date registerDate = new Date();

	private Long accountId;

	private Boolean isEdit = false;
}
