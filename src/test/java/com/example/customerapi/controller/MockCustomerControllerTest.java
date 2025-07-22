package com.example.customerapi.controller;

import com.example.customerapi.dto.CustomerRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MockCustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController controller;

    private CustomerRequest request;
    private CustomerResponse response;
    private UUID id;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        request = new CustomerRequest("Test User", "test@example.com", BigDecimal.valueOf(5000), LocalDate.now().minusMonths(3));
        response = new CustomerResponse(id, "Test User", "test@example.com", BigDecimal.valueOf(5000), LocalDate.now().minusMonths(3), "Gold");
    }

    @Test
    void testCreateCustomerValidEmail() {
        when(customerService.createCustomer(request)).thenReturn(response);
        ResponseEntity<CustomerResponse> result = controller.createCustomer(request);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Test User", result.getBody().getName());
    }

    @Test
    void testCreateCustomerInvalidEmail() {
        request.setEmail("invalid-email");
        ResponseEntity<CustomerResponse> result = controller.createCustomer(request);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void testGetCustomerById() {
        when(customerService.getCustomerById(id)).thenReturn(response);
        ResponseEntity<CustomerResponse> result = controller.getCustomerById(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Test User", result.getBody().getName());
    }

    @Test
    void testGetCustomerByNameAndEmail() {
        when(customerService.getByNameAndEmail("Test User", "test@example.com")).thenReturn(response);
        ResponseEntity<CustomerResponse> result = controller.getCustomer("Test User", "test@example.com");
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testGetCustomerByNameOnly() {
        when(customerService.getByName("Test User")).thenReturn(response);
        ResponseEntity<CustomerResponse> result = controller.getCustomer("Test User", null);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testGetCustomerByEmailOnly() {
        when(customerService.getByEmail("test@example.com")).thenReturn(response);
        ResponseEntity<CustomerResponse> result = controller.getCustomer(null, "test@example.com");
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testGetCustomerWithNoParams() {
        ResponseEntity<CustomerResponse> result = controller.getCustomer(null, null);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void testUpdateCustomer() {
        when(customerService.updateCustomer(id, request)).thenReturn(response);
        ResponseEntity<CustomerResponse> result = controller.updateCustomer(id, request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Test User", result.getBody().getName());
    }

    @Test
    void testDeleteCustomer() {
        doNothing().when(customerService).deleteCustomer(id);
        ResponseEntity<Void> result = controller.deleteCustomer(id);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void testHandleIllegalArgumentException() {
        ResponseEntity<String> result = controller.handleIllegalArgumentException(new IllegalArgumentException("Invalid input"));
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid input", result.getBody());
    }
}
