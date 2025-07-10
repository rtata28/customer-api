package com.example.customerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    private UUID id;
    private String name;
    private String email;
    private BigDecimal annualSpend;
    private LocalDate lastPurchaseDate;
    private String tier; // Silver, Gold, Platinum
}
