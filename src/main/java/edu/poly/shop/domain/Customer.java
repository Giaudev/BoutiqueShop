package edu.poly.shop.domain;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Customers")
public class Customer implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long customerId;

	@Column(nullable = false, length = 50, columnDefinition = "nvarchar(50)")
	private String fullname;

	@Column(nullable = false, length = 15)
	private String phone;

	@Column(name = "address", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
	private String address;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date registerDate;

	@ManyToOne
	@JoinColumn(name = "accountId", nullable = false)
	private Account account;
	// khi xóa customer thì các bảng liên quan sẽ bị xóa theo
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<Order> Orders = new ArrayList<>();
}
