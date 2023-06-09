package com.example.bureau.repo;

import com.example.bureau.models.Bureau;
import com.example.bureau.models.Depot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BureauRepo extends JpaRepository<Bureau,Long> {
    Bureau findByNumero(String numero);
    List<Bureau> findByDepot(Depot depot);
}
