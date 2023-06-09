package com.example.bureau.repo;

import com.example.bureau.models.Depot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepotRepo extends JpaRepository<Depot,Long> {

     Depot findByNumero(String numero);

    List<Depot> findByNameContaining(String name);

}
