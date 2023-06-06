package com.example.bureau.controllers;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Depot;
import com.example.bureau.services.BureauService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bureau")
@CrossOrigin(origins = "*")
public class BureauController {
    private final BureauService bureauService;

    public BureauController(BureauService bureauService) {
        this.bureauService = bureauService;
    }

    @PostMapping("/create")
    public ResponseEntity<Bureau> createBureauInDepot(@RequestBody Bureau bureau, @RequestParam Long depotId) {
        Bureau createdBureau = bureauService.createBureauInDepot(bureau, depotId);
        return ResponseEntity.ok(createdBureau);
    }
    @GetMapping
    public List<Bureau> findAll(){
        return bureauService.getAllBureau();
    }

    @GetMapping("/{depotId}/bureau")
    public List<Bureau> getAllBureauByDepot(@PathVariable("depotId") Long depotId) {
        Depot depot = new Depot();
        depot.setId(depotId);
        return bureauService.getAllBureauByDepot(depot);
    }
}
