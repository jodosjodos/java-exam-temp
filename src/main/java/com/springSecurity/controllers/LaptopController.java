package com.springSecurity.controllers;

import com.springSecurity.dtos.CreateLaptopDto;
import com.springSecurity.entities.Laptop;
import com.springSecurity.services.LaptopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laptops")
@RequiredArgsConstructor
public class LaptopController {

    private final LaptopService laptopService;

    @Operation(summary = "Create a new laptop", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<CreateLaptopDto> createLaptop(@RequestBody CreateLaptopDto laptop) {
        CreateLaptopDto createdLaptop = laptopService.createLaptop(laptop);
        return ResponseEntity.ok(createdLaptop);
    }

    @Operation(summary = "Update an existing laptop", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{laptopId}")
    public ResponseEntity<Laptop> updateLaptop(@PathVariable Long laptopId,
                                               @RequestBody CreateLaptopDto updatedLaptop) {
        Laptop updated = laptopService.updateLaptop(laptopId, updatedLaptop);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a laptop", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{laptopId}")
    public ResponseEntity<Void> deleteLaptop(@PathVariable Long laptopId) {
        laptopService.deleteLaptop(laptopId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all laptops for the authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<Laptop>> getAllLaptops() {
        List<Laptop> laptops = laptopService.getAllLaptops();
        return ResponseEntity.ok(laptops);
    }
}
