package edu.poly.shop.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import edu.poly.shop.domain.Customer;

public interface CustomerService {

	void deleteAll();

	<S extends Customer> List<S> findAll(Example<S> example, Sort sort);

	<S extends Customer> List<S> findAll(Example<S> example);

	void deleteAll(Iterable<? extends Customer> entities);

	Customer getReferenceById(Long id);

	void deleteAllById(Iterable<? extends Long> ids);

	void delete(Customer entity);

	Customer getById(Long id);

	void deleteById(Long id);

	long count();

	<S extends Customer, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction);

	Customer getOne(Long id);

	void deleteAllInBatch();

	<S extends Customer> boolean exists(Example<S> example);

	void deleteAllByIdInBatch(Iterable<Long> ids);

	<S extends Customer> long count(Example<S> example);

	boolean existsById(Long id);

	void deleteAllInBatch(Iterable<Customer> entities);

	Optional<Customer> findById(Long id);

	<S extends Customer> Page<S> findAll(Example<S> example, Pageable pageable);

	void deleteInBatch(Iterable<Customer> entities);

	List<Customer> findAllById(Iterable<Long> ids);

	List<Customer> findAll();

	<S extends Customer> List<S> saveAllAndFlush(Iterable<S> entities);

	<S extends Customer> S saveAndFlush(S entity);

	void flush();

	List<Customer> findAll(Sort sort);

	<S extends Customer> Optional<S> findOne(Example<S> example);

	<S extends Customer> List<S> saveAll(Iterable<S> entities);

	<S extends Customer> S save(S entity);

	Page<Customer> findAll(Pageable pageable);

	Page<Customer> findByFullnameContaining(String fullname, Pageable pageable);

	List<Customer> findByFullnameContaining(String fullname);

	Customer findByAccountId(Long accountId);

	Customer findByCustomerByCustomerId(Long customerId);
}
