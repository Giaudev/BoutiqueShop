package edu.poly.shop.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Accounts")
public class Account implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "accountId")
	private Long accountId;

	@Column(nullable = false, length = 30)
	private String username;

	@Column(nullable = false, length = 30)
	private String password;

	@Column(columnDefinition = "bit default 0", nullable = false)
	private Boolean role;

	@Column(nullable = false, length = 50)
	private String email;

	@OneToMany(mappedBy = "account")
	private List<Customer> customer;
}
