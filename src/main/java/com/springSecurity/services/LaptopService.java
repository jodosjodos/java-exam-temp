package com.springSecurity.services;

import com.springSecurity.dtos.CreateLaptopDto;
import com.springSecurity.entities.Laptop;

import java.util.List;

public interface LaptopService {
    CreateLaptopDto createLaptop(CreateLaptopDto laptop);
    Laptop updateLaptop(Long laptopId, CreateLaptopDto updatedLaptop);
    void deleteLaptop(Long laptopId);
    List<Laptop> getAllLaptops();
}
