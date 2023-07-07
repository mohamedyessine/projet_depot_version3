package com.example.bureau.services;
import com.example.bureau.exceptions.DuplicateException;

import com.example.bureau.models.Bureau;
import com.example.bureau.models.Depot;
import com.example.bureau.repo.BureauRepo;
import com.example.bureau.repo.DepotRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BureauService {
    private final BureauRepo bureauRepo;
    private final DepotRepo depotRepo;


    public BureauService(BureauRepo bureauRepo, DepotRepo depotRepo) {
        this.bureauRepo = bureauRepo;
        this.depotRepo = depotRepo;
    }

    @Transactional
    public Bureau createBureauInDepot(Bureau bureau, Long depotId) {
         if (bureauRepo.existsByNumero(bureau.getNumero())) {
            throw new DuplicateException("Le code bureau est exist déja");
        }
        Depot depot = depotRepo.findById(depotId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid depot"));

        bureau.setDepot(depot);

        return bureauRepo.save(bureau);
    }

    public List<Bureau>getAllBureau(){
        return bureauRepo.findAll();
    }

    public Bureau findById(Long id) {
        return bureauRepo.findById(id).orElse(null);
    }

    public Bureau findByNumero(String numero) {
        return bureauRepo.findByNumero(numero);
    }
    public List<Bureau> getAllBureauByDepot(Depot depot) {
        return bureauRepo.findByDepot(depot);
    }






}
