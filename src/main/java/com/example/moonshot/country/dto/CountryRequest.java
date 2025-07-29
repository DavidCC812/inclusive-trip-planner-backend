package com.example.moonshot.country.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountryRequest {
    private String name;
    private boolean available;
}
