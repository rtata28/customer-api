package com.example.customerapi.service;

import com.example.customerapi.dto.CustomerRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.entity.Customer;
import com.example.customerapi.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    private CustomerRequest validRequest;
    private Customer customer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        validRequest = new CustomerRequest("John Doe", "john@example.com",
                new BigDecimal("5000"), LocalDate.now().minusMonths(5));
        customer = new Customer(UUID.randomUUID(), "John Doe", "john@example.com",
                new BigDecimal("5000"), LocalDate.now().minusMonths(5));
    }

    @Test
    public void testCreateCustomer() {
        when(repository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponse response = service.createCustomer(validRequest);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    public void testGetCustomerById() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(customer));

        CustomerResponse response = service.getCustomerById(UUID.randomUUID());

        assertNotNull(response);
        assertEquals("Gold", response.getTier());
    }

    @Test
    public void testUpdateCustomer() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(customer));
        when(repository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponse response = service.updateCustomer(customer.getId(), validRequest);

        assertEquals("John Doe", response.getName());
    }

    @Test
    public void testDeleteCustomer() {
        UUID id = customer.getId();
        doNothing().when(repository).deleteById(id);

        assertDoesNotThrow(() -> service.deleteCustomer(id));
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    public void testTierCalculation_Silver() {
        Customer silverCustomer = new Customer(UUID.randomUUID(), "Silver User", "silver@example.com",
                new BigDecimal("500"), LocalDate.now());
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(silverCustomer));

        CustomerResponse response = service.getCustomerById(UUID.randomUUID());

        assertEquals("Silver", response.getTier());
    }

    @Test
    public void testTierCalculation_Gold() {
        Customer goldCustomer = new Customer(UUID.randomUUID(), "Gold User", "gold@example.com",
                new BigDecimal("5000"), LocalDate.now().minusMonths(11));
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(goldCustomer));

        CustomerResponse response = service.getCustomerById(UUID.randomUUID());

        assertEquals("Gold", response.getTier());
    }

    @Test
    public void testTierCalculation_Platinum() {
        Customer platinumCustomer = new Customer(UUID.randomUUID(), "Platinum User", "platinum@example.com",
                new BigDecimal("15000"), LocalDate.now().minusMonths(5));
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(platinumCustomer));

        CustomerResponse response = service.getCustomerById(UUID.randomUUID());

        assertEquals("Platinum", response.getTier());
    }

    @Test
    public void testCreateCustomer_InvalidEmail() {
        CustomerRequest invalidEmailRequest = new CustomerRequest("Jane Doe", "invalid-email",
                new BigDecimal("2000"), LocalDate.now());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createCustomer(invalidEmailRequest));

        assertEquals("Invalid email format: invalid-email", exception.getMessage());
    }
}
