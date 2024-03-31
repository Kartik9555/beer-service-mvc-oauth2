package com.example.practice.beerservicemvc.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerDTO {
    private UUID id;
    private String name;
    private String email;
    private Integer version;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
