package edu.poly.shop.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.poly.shop.domain.CartItem;
import edu.poly.shop.domain.Customer;
import edu.poly.shop.domain.Order;
import edu.poly.shop.domain.OrderDetail;
import edu.poly.shop.domain.Product;
import edu.poly.shop.repository.CustomerRepository;
import edu.poly.shop.repository.OrderDetailRepository;
import edu.poly.shop.repository.OrderRepository;
import edu.poly.shop.repository.ProductRepository;
import edu.poly.shop.service.CartService;

@Service
public class CartServiceImpl implements CartService {
	List<CartItem> cart = new ArrayList<>();
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderDetailRepository orderDetailRepository;
	@Autowired
	private CustomerRepository customerRepository;

	public CartServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public void addToCart(Long customerId, Long productId) {
		Optional<Customer> customerOptional = customerRepository.findById(customerId);
		if (customerOptional.isEmpty()) {
			throw new RuntimeException("Customer not found");
		}
		Customer customer = customerOptional.get();
		// Tìm đơn hàng hiện tại (status = 0)
		Optional<Order> orderOptional = orderRepository.findByOrderByCustomer(customerId);
		Order order;
		if (orderOptional.isPresent()) {
			order = orderOptional.get();
		} else {
			order = new Order();
			order.setCustomer(customer);
			order.setOrderDate(new Date());
			order.setAmount(0.0);
			order.setStatus((short) 0);
			orderRepository.save(order);
		}

		// Kiểm tra xem sản phẩm đã tồn tại trong đơn hàng chưa
		Optional<OrderDetail> existingOrderDetailOpt = orderDetailRepository
				.findOneByOrderidAndProductid(order.getOrderId(), productId);
		if (existingOrderDetailOpt.isPresent()) {
			// Nếu sản phẩm đã tồn tại trong đơn hàng, chỉ cần cập nhật số lượng
			OrderDetail existingOrderDetail = existingOrderDetailOpt.get();
			existingOrderDetail.setQuantity(existingOrderDetail.getQuantity() + 1);
			// existingOrderDetail.setPrice(existingOrderDetail.getPrice());
			orderDetailRepository.save(existingOrderDetail);

			// Cập nhật tổng tiền đơn hàng
			// Double totalPrice = existingOrderDetail.getPrice() *
			// existingOrderDetail.getQuantity();
			// order.setAmount(order.getAmount() + totalPrice);
			// orderRepository.save(order);
		} else {
			// Nếu sản phẩm chưa tồn tại trong đơn hàng, thêm mới
			Optional<Product> productOpt = productRepository.findById(productId);
			if (productOpt.isPresent()) {
				Product product = productOpt.get();
				OrderDetail orderDetail = new OrderDetail();
				orderDetail.setOrder(order);
				orderDetail.setProduct(product);
				orderDetail.setQuantity(1); // Giả định mỗi lần thêm 1 sản phẩm
				orderDetail.setPrice(product.getPrice());
				orderDetailRepository.save(orderDetail);

				// Cập nhật tổng tiền đơn hàng
				// order.setAmount(order.getAmount() + product.getPrice());
				// orderRepository.save(order);
			}
		}
		// Cập nhật tổng tiền đơn hàng
		updateOrderAmount(order);
	}

	private void updateOrderAmount(Order order) {
		Double totalAmount = orderDetailRepository.findByOrderOrderId(order.getOrderId()).stream()
				.mapToDouble(od -> od.getQuantity() * od.getPrice()).sum();
		order.setAmount(totalAmount);
		orderRepository.save(order);
	}

	// cách 1
	@Override
	public Long getCurrentOrderId(Long customerId) {
		Optional<Order> orderOptional = orderRepository.findByOrderByCustomer(customerId);
		if (orderOptional.isPresent()) {
			Order order = orderOptional.get();
			return order.getOrderId();
		} else {
			return null;
		}
	}
	// cách 2
	// @Override
	// public Long getCurrentOrderId(Long customerId) {
	// Optional<Order> orderOptional =
	// orderRepository.findByOrderByCustomer(customerId);
	// return orderOptional.map(Order::getOrderId).orElse(null);
	// }

	@Override
	public void removeCart(Long customerId, Long productId) {
		List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdAndProductId(customerId, productId);
		if (orderDetails != null && !orderDetails.isEmpty()) {
			for (OrderDetail orderDetail : orderDetails) {
				orderDetailRepository.delete(orderDetail);
			}
			Optional<Order> orderOptional = orderRepository.findByOrderByCustomer(customerId);
			if (orderOptional.isPresent()) {
				Order order = orderOptional.get();
				updateOrderAmount(order);
			}
		}
	}

	@Override
	public void increaseQuantity(Long customerId, Long productId) {
		List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdAndProductId(customerId, productId);
		if (orderDetails != null && !orderDetails.isEmpty()) {
			for (OrderDetail orderDetail : orderDetails) {
				orderDetail.setQuantity(orderDetail.getQuantity() + 1);
				orderDetailRepository.save(orderDetail);
			}
			Optional<Order> orderOptional = orderRepository.findByOrderByCustomer(customerId);
			if (orderOptional.isPresent()) {
				Order order = orderOptional.get();
				updateOrderAmount(order);
			}
		}
	}

	@Override
	public void decreaseQuantity(Long customerId, Long productId) {
		List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdAndProductId(customerId, productId);
		if (orderDetails != null && !orderDetails.isEmpty()) {
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getQuantity() > 1) {
					orderDetail.setQuantity(orderDetail.getQuantity() - 1);
					orderDetailRepository.save(orderDetail);
				}
			}
			Optional<Order> orderOptional = orderRepository.findByOrderByCustomer(customerId);
			if (orderOptional.isPresent()) {
				Order order = orderOptional.get();
				updateOrderAmount(order);
			}
		}
	}

	// xứ lý hiện name price ở phần checkout
	@Override
	public List<CartItem> findOrderProductDetailsByCustomerId(Long customerId) {
		return orderDetailRepository.findOrderProductDetailsByCustomerId(customerId);
	}

}
