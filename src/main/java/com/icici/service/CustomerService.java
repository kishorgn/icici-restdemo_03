package com.icici.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.icici.entity.Customer;
import com.icici.exception.InvalidCustomerIdException;
import com.icici.repo.CustomerRepo;

@Service
public class CustomerService {
	CustomerRepo customerRepo;
	
	@Autowired
	public CustomerService(CustomerRepo customerRepo) {
		this.customerRepo = customerRepo;
	}
	
	public Customer createCustomer(Customer customer) {
		return customerRepo.save(customer);
	}
	public List<Customer> getAllCustomers(){
		return customerRepo.findAll();
	}
	public Customer searchCustomerById(Integer id) throws InvalidCustomerIdException {
		Optional<Customer> optCustomer = customerRepo.findById(id);
		if( optCustomer.isEmpty() ) {
			// Create an exception and throw.
			throw new InvalidCustomerIdException("Customer ID : "+id+" is not valid");
		}
		return optCustomer.get();
	}
//	searchCustomersByName
//	searchCustomerByEmail
//	editCustomerDetails
//	closeCustomer
}
