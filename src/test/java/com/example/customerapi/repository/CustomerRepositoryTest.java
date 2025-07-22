package com.example.customerapi.repository;

import com.example.customerapi.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        testCustomer = new Customer();
        testCustomer.setName("Alice");
        testCustomer.setEmail("alice@example.com");
        testCustomer.setAnnualSpend(BigDecimal.valueOf(5000));
        testCustomer.setLastPurchaseDate(LocalDate.now().minusMonths(3));
        repository.save(testCustomer);
    }

    @Test
    void testFindByName() {
        Optional<Customer> found = repository.findByName("Alice");
        assertTrue(found.isPresent());
        assertEquals("alice@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail() {
        Optional<Customer> found = repository.findByEmail("alice@example.com");
        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getName());
    }

    @Test
    void testFindByNameAndEmail() {
        Optional<Customer> found = repository.findByNameAndEmail("Alice", "alice@example.com");
        assertTrue(found.isPresent());
        assertEquals(testCustomer.getId(), found.get().getId());
    }

    @Test
    void testFindByNameNotFound() {
        Optional<Customer> found = repository.findByName("Bob");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<Customer> found = repository.findByEmail("bob@example.com");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByNameAndEmailNotFound() {
        Optional<Customer> found = repository.findByNameAndEmail("Bob", "bob@example.com");
        assertFalse(found.isPresent());
    }
}
