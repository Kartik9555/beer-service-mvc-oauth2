package com.example.practice.beerservicemvc.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BeerDTO {
    private UUID id;
    private Integer version;

    @NotEmpty
    @NotNull
    private String name;

    @NotNull
    private BeerStyle beerStyle;

    @NotEmpty
    @NotNull
    private String upc;

    @NotNull
    private BigDecimal price;
    private Integer quantityOnHand;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
