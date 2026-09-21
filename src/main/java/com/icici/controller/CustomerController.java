package com.icici.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icici.entity.Customer;
import com.icici.exception.InvalidCustomerIdException;
import com.icici.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	
	CustomerService customerService;
	
	@Autowired
	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}
	
	@PostMapping
	public ResponseEntity<Customer> addNewCustomer(@RequestBody Customer customer) {
		System.out.println("Customer to be sadved : " + customer);
		Customer saved = customerService.createCustomer(customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Customer> getCustomerById(@PathVariable Integer id) throws InvalidCustomerIdException {
		Customer foundCustomer = customerService.searchCustomerById(id);
		return ResponseEntity.ok(foundCustomer);
	}
	
	@GetMapping
	public ResponseEntity<List<Customer>> getAllCustomers() {
		List<Customer> customers = customerService.getAllCustomers();
		return ResponseEntity.status(HttpStatus.OK).body(customers);
	}
	
	
}
