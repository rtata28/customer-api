package com.example.customerapi.controller;

import com.example.customerapi.dto.CustomerRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.service.CustomerService;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.slf4j.Logger;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // POST /customers
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        logger.info("Creating new customer with name: {}", request.getName());

        // Manual email format validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!request.getEmail().matches(emailRegex)) {
            logger.warn("Invalid email format received: {}", request.getEmail());
            return ResponseEntity.badRequest().body(null); // or throw custom exception
        }

        CustomerResponse createdCustomer = customerService.createCustomer(request);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }


    // GET /customers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID id) {
        logger.info("Fetching customer by ID: {}", id);
        CustomerResponse customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    // GET /customers?name={name}&email={email}
    @GetMapping
    public ResponseEntity<CustomerResponse> getCustomer(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {

        logger.info("Fetching customer by query params: name={}, email={}", name, email);

        if (name != null && email != null) {
            return ResponseEntity.ok(customerService.getByNameAndEmail(name, email));
        } else if (name != null) {
            return ResponseEntity.ok(customerService.getByName(name));
        } else if (email != null) {
            return ResponseEntity.ok(customerService.getByEmail(email));
        } else {
            logger.warn("No query parameters provided for customer fetch");
            return ResponseEntity.badRequest().body(null); // Or throw a custom exception
        }
    }

    // PUT /customers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID id,
                                                           @RequestBody CustomerRequest request) {
        logger.info("Updating customer with ID: {}", id);
        CustomerResponse updatedCustomer = customerService.updateCustomer(id, request);
        logger.info("Customer updated successfully: {}", updatedCustomer);
        return ResponseEntity.ok(updatedCustomer);
    }

    // DELETE /customers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        logger.info("Deleting customer with ID: {}", id);
        customerService.deleteCustomer(id);
        logger.info("Customer deleted successfully");
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

