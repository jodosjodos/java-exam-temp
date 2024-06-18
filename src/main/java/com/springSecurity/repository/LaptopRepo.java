package com.springSecurity.repository;

import com.springSecurity.entities.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LaptopRepo extends JpaRepository<Laptop,Long> {
    List<Laptop> findByUserDataId(Integer userId);
}
