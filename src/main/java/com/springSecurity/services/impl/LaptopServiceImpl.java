package com.springSecurity.services.impl;

import com.springSecurity.dtos.CreateLaptopDto;
import com.springSecurity.entities.Laptop;
import com.springSecurity.entities.UserData;
import com.springSecurity.errors.exception.ApiRequestException;
import com.springSecurity.repository.LaptopRepo;
import com.springSecurity.repository.UserRepository;
import com.springSecurity.services.LaptopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LaptopServiceImpl implements LaptopService {

    private final LaptopRepo laptopRepository;
    private final UserRepository userRepository;

    @Override
    public CreateLaptopDto createLaptop(CreateLaptopDto createLaptopDto) {
        UserData userData = getUserData();
        Laptop  laptop = new Laptop();
        laptop.setBrand(createLaptopDto.getBrand());
        laptop.setModel(createLaptopDto.getModel());
        laptop.setSerialNumber(createLaptopDto.getSerialNumber());
        laptop.setUserData(userData);
         laptopRepository.save(laptop);
         return  createLaptopDto;
    }

    @Override
    public Laptop updateLaptop(Long laptopId, CreateLaptopDto updatedLaptop) {
        UserData userData = getUserData();
        Laptop laptop = laptopRepository.findById(laptopId)
                .orElseThrow(() -> new ApiRequestException("Laptop not found", HttpStatus.NOT_FOUND));
        if (!laptop.getUserData().getId().equals(userData.getId())) {
            throw new ApiRequestException("Laptop does not belong to the user", HttpStatus.FORBIDDEN);
        }
        laptop.setBrand(updatedLaptop.getBrand());
        laptop.setModel(updatedLaptop.getModel());
        laptop.setSerialNumber(updatedLaptop.getSerialNumber());
        return laptopRepository.save(laptop);
    }

    @Override
    public void deleteLaptop(Long laptopId) {
        Laptop laptop = laptopRepository.findById(laptopId)
                .orElseThrow(() -> new ApiRequestException("Laptop not found", HttpStatus.NOT_FOUND));
        UserData userData = laptop.getUserData();
        userData.getLaptops().remove(laptop);
        laptopRepository.deleteById(laptop.getId());
    }

    @Override
    public List<Laptop> getAllLaptops() {
        UserData userData = getUserData();
        return laptopRepository.findByUserDataId(userData.getId());
    }

    private UserData getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return (UserData) userDetails;
            } else {
                throw new ApiRequestException("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new ApiRequestException("User not authenticated", HttpStatus.UNAUTHORIZED);
        }
    }
}
