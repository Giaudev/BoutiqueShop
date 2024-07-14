package edu.poly.shop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
	private Long orderDetailId;
	private Long orderId;
	private Long productId;
	private Integer quantity;
	private Double price;
	private Boolean isEdit = false;
}
