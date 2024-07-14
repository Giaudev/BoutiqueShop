package edu.poly.shop.model;

import java.io.Serializable;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto implements Serializable {
	private Long accountId;
	@NotBlank
	private String username;
	@NotBlank
	private String password;

	private Boolean role = false;

	@NotBlank
	private String email;

	private Boolean isEdit = false;
}
