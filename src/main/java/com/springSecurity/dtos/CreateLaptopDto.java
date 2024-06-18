package com.springSecurity.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLaptopDto {
    private String brand;
    private String model;
    private String serialNumber;
}
