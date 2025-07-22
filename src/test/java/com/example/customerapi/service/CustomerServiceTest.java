package com.example.customerapi.service;

import com.example.customerapi.dto.CustomerRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.entity.Customer;
import com.example.customerapi.exception.NoSuchElementException;
import com.example.customerapi.exception.NotFoundException;
import com.example.customerapi.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class);

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    final private UUID uuid = UUID.randomUUID();
    private Customer customer;
    private CustomerRequest validRequest;


    @BeforeEach
    public void setUp() {
        logger.info("Setting up mocks and test data...");
        MockitoAnnotations.openMocks(this);
        validRequest = new CustomerRequest("John Doe", "john@example.com",
                new BigDecimal("5000"), LocalDate.now().minusMonths(5));
        customer = new Customer(uuid, "John Doe", "john@example.com",
                new BigDecimal("5000"), LocalDate.now().minusMonths(5));
    }

    /**
     * Test successful creation of a customer.
     */
    @Test
    public void testCreateCustomer() {
        logger.info("Running testCreateCustomer...");
        when(repository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponse response = service.createCustomer(validRequest);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        logger.info("Customer created successfully with tier: {}", response.getTier());
    }

    /**
     * Test validation failure: Invalid email should throw IllegalArgumentException.
     */
    @Test
    public void testCreateCustomer_InvalidEmail() {
        logger.info("Running testCreateCustomer_InvalidEmail...");
        CustomerRequest invalidEmailRequest = new CustomerRequest("Jane Doe", "invalid-email",
                new BigDecimal("2000"), LocalDate.now());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createCustomer(invalidEmailRequest));

        assertEquals("Invalid email format: invalid-email", exception.getMessage());
        logger.info("Caught expected exception for invalid email: {}", exception.getMessage());
    }

    @Test
    public void testCreateCustomerByRequestNull() {
        logger.info("Running testCreateCustomerByRequestNull...");
        assertThrows(IllegalArgumentException.class,
                () -> service.createCustomer(null));
    }

    /**
     * Test retrieving a customer by ID and verifying tier logic.
     */
    @Test
    public void testGetCustomerById() {
        logger.info("Running testGetCustomerById...");
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(customer));

        CustomerResponse response = service.getCustomerById(UUID.randomUUID());

        assertNotNull(response);
        assertEquals("Gold", response.getTier());
        logger.info("Customer retrieved with tier: {}", response.getTier());
    }

    /**
     * Test updating a customer.
     */
    @Test
    public void testUpdateCustomer() {
        logger.info("Running testUpdateCustomer...");
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(customer));
        when(repository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponse response = service.updateCustomer(customer.getId(), validRequest);

        assertEquals("John Doe", response.getName());
        logger.info("Customer updated successfully");
    }

    /**
     * Test deletion of a customer.
     */
    @Test
    public void testDeleteCustomer() {
        logger.info("Running testDeleteCustomer...");
        UUID id = customer.getId();
        doNothing().when(repository).deleteById(id);

        assertDoesNotThrow(() -> service.deleteCustomer(id));
        verify(repository, times(1)).deleteById(id);
        logger.info("Customer deleted successfully");
    }

    /**
     * Test tier calculation: Silver tier for spend < 1000.
     */
    @Test
    public void testTierCalculation_Silver() {
        logger.info("Running testTierCalculation_Silver...");
        Customer silverCustomer = new Customer(UUID.randomUUID(), "Silver User", "silver@example.com",
                new BigDecimal("500"), LocalDate.now());
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(silverCustomer));

        CustomerResponse response = service.getCustomerById(UUID.randomUUID());

        assertEquals("Silver", response.getTier());
        logger.info("Tier calculated: {}", response.getTier());
    }

    /**
     * Test tier calculation: Gold tier for spend >= 1000 and < 10000.
     */
    @Test
    public void testTierCalculation_Gold() {
        logger.info("Running testTierCalculation_Gold...");
        Customer goldCustomer = new Customer(UUID.randomUUID(), "Gold User", "gold@example.com",
                new BigDecimal("5000"), LocalDate.now().minusMonths(11));
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(goldCustomer));

        CustomerResponse response = service.getCustomerById(UUID.randomUUID());

        assertEquals("Gold", response.getTier());
        logger.info("Tier calculated: {}", response.getTier());
    }

    /**
     * Test tier calculation: Platinum tier for spend >= 10000.
     */
    @Test
    public void testTierCalculation_Platinum() {
        logger.info("Running testTierCalculation_Platinum...");
        Customer platinumCustomer = new Customer(UUID.randomUUID(), "Platinum User", "platinum@example.com",
                new BigDecimal("15000"), LocalDate.now().minusMonths(5));
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(platinumCustomer));

        CustomerResponse response = service.getCustomerById(UUID.randomUUID());

        assertEquals("Platinum", response.getTier());
        logger.info("Tier calculated: {}", response.getTier());
    }

    @Test
    void testGetCustomerByIdSuccess() {
        when(repository.findById(uuid)).thenReturn(Optional.of(customer));
        CustomerResponse response = service.getCustomerById(uuid);
        assertEquals("John Doe", response.getName());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        when(repository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getCustomerById(uuid));
    }

    @Test
    void testGetCustomerByIdNull() {
        assertThrows(IllegalArgumentException.class, () -> service.getCustomerById(null));
    }

    @Test
    void testGetCustomerByNameNull() {
        assertThrows(IllegalArgumentException.class, () -> service.getByName(null));
    }

    @Test
    void testDeleteCustomerByIdNull() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteCustomer(null));
    }

    @Test
    void testGetByNameAndEmailFound() {
        when(repository.findByNameAndEmail("John Doe", "john@example.com")).thenReturn(Optional.of(customer));
        CustomerResponse response = service.getByNameAndEmail("John Doe", "john@example.com");
        assertEquals("John Doe", response.getName());
    }

    @Test
    void testGetByNameAndEmailNotFound() {
        when(repository.findByNameAndEmail("Test User", "test@example.com")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.getByNameAndEmail("Test User", "test@example.com"));
    }

    @Test
    void testGetByNameFound() {
        when(repository.findByName("John Doe")).thenReturn(Optional.of(customer));
        CustomerResponse response = service.getByName("John Doe");
        assertEquals("John Doe", response.getName());
    }

    @Test
    void testGetByNameNotFound() {
        when(repository.findByName("Test User")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getByName("Test User"));
    }

    @Test
    void testGetByEmailFound() {
        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(customer));
        CustomerResponse response = service.getByEmail("john@example.com");
        assertEquals("John Doe", response.getName());
    }

    @Test
    void testGetByEmailInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> service.getByEmail("invalid-email"));
    }

    @Test
    void testUpdateCustomerSuccess() {
        when(repository.findById(uuid)).thenReturn(Optional.of(customer));
        when(repository.save(any())).thenReturn(customer);
        CustomerResponse response = service.updateCustomer(uuid, validRequest);
        assertEquals("John Doe", response.getName());
    }

    @Test
    void testUpdateCustomerNotFound() {
        when(repository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateCustomer(uuid, validRequest));
    }

    @Test
    void testUpdateCustomerNull() {
        assertThrows(IllegalArgumentException.class, () -> service.updateCustomer(null, validRequest));
    }

    @Test
    void testValidateRequestNullName() {
        CustomerRequest badRequest = new CustomerRequest(null, "test@example.com", BigDecimal.ONE, LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> service.createCustomer(badRequest));
    }

    @Test
    void testValidateRequestInvalidEmail() {
        CustomerRequest badRequest = new CustomerRequest("User", "invalid", BigDecimal.ONE, LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> service.createCustomer(badRequest));
    }

    @Test
    void testValidateRequestNegativeSpend() {
        CustomerRequest badRequest = new CustomerRequest("User", "user@example.com", BigDecimal.valueOf(-1), LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> service.createCustomer(badRequest));
    }

    @Test
    void testValidateRequestNullDate() {
        CustomerRequest badRequest = new CustomerRequest("User", "user@example.com", BigDecimal.ONE, null);
        assertThrows(IllegalArgumentException.class, () -> service.createCustomer(badRequest));
    }
}
