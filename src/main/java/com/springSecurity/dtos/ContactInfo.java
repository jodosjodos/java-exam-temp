package com.springSecurity.dtos;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Embeddable
@Data
public class ContactInfo {
    @Past
    private LocalDate dateOfBirth;
    private String location;
}
