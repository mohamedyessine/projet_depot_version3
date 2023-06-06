package com.example.bureau.repo;

import com.example.bureau.models.Depot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepotRepo extends JpaRepository<Depot,Long> {

     Depot findByNumero(Long numero);

    List<Depot> findByNameContaining(String name);

}
