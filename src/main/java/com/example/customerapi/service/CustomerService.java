package com.example.customerapi.service;

import com.example.customerapi.dto.CustomerRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.entity.Customer;
import com.example.customerapi.exception.NoSuchElementException;
import com.example.customerapi.exception.NotFoundException;
import com.example.customerapi.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository repository;

    public CustomerService(CustomerRepository customerRepository) {
        this.repository = customerRepository;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        logger.debug("Validating email: {}", request.getEmail());

        validateRequest(request);

        Customer customer = new Customer(
                null,
                request.getName(),
                request.getEmail(),
                request.getAnnualSpend(),
                request.getLastPurchaseDate()
        );

        Customer saved = repository.save(customer);
        logger.info("Saved customer with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    public CustomerResponse getCustomerById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Customer ID must not be null");
        }
        logger.debug("Fetching customer by ID: {}", id);
        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    public CustomerResponse getByNameAndEmail(String name, String email) {
        logger.debug("Fetching customer by name and email: {}, {}", name, email);

        Customer customer = repository.findByNameAndEmail(name, email)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with name and email"));
        return mapToResponse(customer);
    }

    public CustomerResponse getByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        logger.debug("Fetching customer by name: {}", name);
        return repository.findByName(name)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    public CustomerResponse getByEmail(String email) {
        validateEmail(email);
        logger.debug("Fetching customer by email: {}", email);
        return repository.findByEmail(email)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    public CustomerResponse updateCustomer(UUID id, CustomerRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("Customer ID must not be null");
        }
        logger.debug("Updating customer ID: {}", id);
        validateRequest(request);

        Customer customer = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setAnnualSpend(request.getAnnualSpend());
        customer.setLastPurchaseDate(request.getLastPurchaseDate());
        CustomerResponse updatedCustomer = mapToResponse(repository.save(customer));
        logger.info("Customer updated with ID: {}", updatedCustomer.getId());

        return updatedCustomer;
    }

    public void deleteCustomer(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Customer ID must not be null");
        }
        logger.debug("Deleting customer ID: {}", id);
        repository.deleteById(id);
    }

    private CustomerResponse mapToResponse(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getName(), c.getEmail(), c.getAnnualSpend(), c.getLastPurchaseDate(), calculateTier(c)
        );
    }

    public String calculateTier(Customer c) {
        if (c.getAnnualSpend() == null) return "Silver";
        LocalDate today = LocalDate.now();
        if (c.getAnnualSpend().compareTo(BigDecimal.valueOf(10000)) >= 0 &&
                c.getLastPurchaseDate() != null &&
                c.getLastPurchaseDate().isAfter(today.minusMonths(6))) {
            return "Platinum";
        } else if (c.getAnnualSpend().compareTo(BigDecimal.valueOf(1000)) >= 0 &&
                c.getAnnualSpend().compareTo(BigDecimal.valueOf(10000)) < 0 &&
                c.getLastPurchaseDate() != null &&
                c.getLastPurchaseDate().isAfter(today.minusMonths(12))) {
            return "Gold";
        }
        return "Silver";
    }

    private void validateRequest(CustomerRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }

        validateEmail(request.getEmail());

        if (request.getAnnualSpend() == null || request.getAnnualSpend().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Annual spend must not be null or negative");
        }

        if (request.getLastPurchaseDate() == null) {
            throw new IllegalArgumentException("Last purchase date must not be null");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format: " + email);

        }
    }
}
