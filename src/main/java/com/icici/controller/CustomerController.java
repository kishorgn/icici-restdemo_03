package com.icici.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.icici.entity.Customer;
import com.icici.exception.InvalidCustomerIdException;
import com.icici.security.JsonViews;
import com.icici.service.CustomerService;

@RestController
@RequestMapping("/customers")
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
	
	@JsonView(JsonViews.SensitiveView.class)
	@GetMapping("/{id}")
	public ResponseEntity<?> getCustomerById(@PathVariable Integer id, Authentication authentication) throws InvalidCustomerIdException {
		boolean isCustomer = authentication.getAuthorities()
		        .contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
		    if (isCustomer) {
		        Customer customer = customerService.searchCustomerById(id);
		        // FIX: compare login username to customer.username (NOT id)
		        if (!authentication.getName().equals(customer.getUsername())) {
		            return ResponseEntity.status(HttpStatus.FORBIDDEN)
		                .body("Access denied: you can only view your own account.");
		        }
		        return ResponseEntity.ok(customer);
		    }
		    return ResponseEntity.ok(customerService.searchCustomerById(id));

	}
	
	@JsonView(JsonViews.PublicView.class)
	@GetMapping
	public ResponseEntity<List<Customer>> getAllCustomers() {
		List<Customer> customers = customerService.getAllCustomers();
		return ResponseEntity.status(HttpStatus.OK).body(customers);
	}
	
	@PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Integer id,
            @RequestBody Customer customer) throws InvalidCustomerIdException {
        if (!id.equals(customer.getId())) {
            throw new InvalidCustomerIdException("Path id and body id do not match");
        }
        customerService.editCustomerDetails(customer);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(customer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable Integer id)
            throws InvalidCustomerIdException {
        Customer deleted = customerService.deleteCustomer(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(deleted);
    }
	
	
}
