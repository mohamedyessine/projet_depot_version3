package com.example.bureau.controllers;
import com.example.bureau.exceptions.DuplicateException;
import com.example.bureau.models.Article;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Depot;
import com.example.bureau.services.BureauService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Bureau> createBureauInDepot(@RequestBody Bureau bureau, @RequestParam Long depotId) {
        // Check if any field contains only spaces
        if (containsOnlySpaces(bureau)) {
            throw new DuplicateException("Fields cannot contain only spaces");
        }
        Bureau createdBureau = bureauService.createBureauInDepot(bureau, depotId);
        return ResponseEntity.ok(createdBureau);
    }
    private boolean containsOnlySpaces(Bureau bureau) {
        String numero = bureau.getNumero().trim();
        String name = bureau.getName().trim();

        return !numero.matches("^[a-zA-Z0-9]+( [a-zA-Z0-9]+)*$") || !name.matches("^[a-zA-Z0-9]+( [a-zA-Z0-9]+)*$");
    }
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Bureau> findAll(){
        return bureauService.getAllBureau();
    }

    @GetMapping("/{depotId}/bureau")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Bureau> getAllBureauByDepot(@PathVariable("depotId") Long depotId) {
        Depot depot = new Depot();
        depot.setId(depotId);
        return bureauService.getAllBureauByDepot(depot);
    }
}
