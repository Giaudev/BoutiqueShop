package edu.poly.shop.service;

import java.util.*;

import edu.poly.shop.domain.CartItem;

public interface CartService {

	public void addToCart(Long customerId, Long productId);

	public Long getCurrentOrderId(Long customerId);

	// public List<CartItem> getCartItems();
	public void removeCart(Long customerId, Long productId);

	public void increaseQuantity(Long customerId, Long productId);

	public void decreaseQuantity(Long customerId, Long productId);

	public List<CartItem> findOrderProductDetailsByCustomerId(Long customerId);
}
