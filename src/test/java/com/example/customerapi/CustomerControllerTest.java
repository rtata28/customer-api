package com.example.customerapi;

import com.example.customerapi.controller.CustomerController;
import com.example.customerapi.dto.CustomerRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private CustomerRequest request;
    private CustomerResponse response;
    private UUID customerId;

    @BeforeEach
    public void setup() {
        customerId = UUID.randomUUID();
        request = new CustomerRequest("Alice", "alice@example.com", new BigDecimal("12000"), LocalDate.now().minusMonths(2));
        response = new CustomerResponse(customerId, "Alice", "alice@example.com", new BigDecimal("12000"), LocalDate.now().minusMonths(2), "Platinum");
    }

    @Test
    public void testCreateCustomer() throws Exception {
        Mockito.when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.tier").value("Platinum"));
    }

    @Test
    public void testGetCustomerById() throws Exception {
        Mockito.when(customerService.getCustomerById(eq(customerId))).thenReturn(response);

        mockMvc.perform(get("/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    public void testGetCustomerByEmail() throws Exception {
        Mockito.when(customerService.getByEmail(eq("alice@example.com"))).thenReturn(response);

        mockMvc.perform(get("/customers?email=alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    public void testGetCustomerByName() throws Exception {
        Mockito.when(customerService.getByName(eq("Alice"))).thenReturn(response);

        mockMvc.perform(get("/customers?name=Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        CustomerRequest updatedRequest = new CustomerRequest("Alicia", "alicia@example.com", new BigDecimal("2000"), LocalDate.now().minusMonths(1));
        CustomerResponse updatedResponse = new CustomerResponse(customerId, "Alicia", "alicia@example.com", new BigDecimal("2000"), LocalDate.now().minusMonths(1), "Gold");

        Mockito.when(customerService.updateCustomer(eq(customerId), any(CustomerRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alicia"))
                .andExpect(jsonPath("$.tier").value("Gold"));
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        Mockito.doNothing().when(customerService).deleteCustomer(customerId);

        mockMvc.perform(delete("/customers/{id}", customerId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCreateCustomer_InvalidEmail() throws Exception {
        CustomerRequest invalidRequest = new CustomerRequest("Bob", "invalid-email", new BigDecimal("500"), LocalDate.now());

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is2xxSuccessful());
    }
}
