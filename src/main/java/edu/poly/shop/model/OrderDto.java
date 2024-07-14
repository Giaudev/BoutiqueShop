package edu.poly.shop.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
	private Long orderId;
	private Date orderDate = new Date();
	private Long customerId;
	private Double amount;
	private Short status;
	private Boolean isEdit = false;
}
