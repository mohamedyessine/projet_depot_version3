package com.example.bureau.controllers;


import com.example.bureau.exceptions.DuplicateException;
import com.example.bureau.exceptions.ErrorResponse;
import com.example.bureau.models.Article;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Depot;
import com.example.bureau.payload.request.ArticleWithQuantities;
import com.example.bureau.payload.request.ArticleWithQuantity;
import com.example.bureau.payload.request.ArticleWithQuantityAndBureau;
import com.example.bureau.services.DepotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/depots")
@CrossOrigin(origins = "*")
public class DepotController {
    private final DepotService depotService;

    public DepotController(DepotService depotService) {
        this.depotService = depotService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public Depot addDepot(@RequestBody Depot depot) {
        // Check if any field contains only spaces
       /* if (containsOnlySpaces(depot)) {
            throw new DuplicateException("Fields cannot contain only spaces");
        }*/

        Depot existingDepot = depotService.findByNumero(depot.getNumero());

        if (existingDepot != null) {
            throw new DuplicateException("Dépôt avec numéro '" + depot.getNumero() + "' existe déjà");
        }

        return depotService.addDepot(depot);
    }

    /*private boolean containsOnlySpaces(Depot depot) {
        String numero = depot.getNumero().trim();
        String name = depot.getName().trim();

        return !numero.matches("^[a-zA-Z0-9]+( [a-zA-Z0-9]+)*$") || !name.matches("^[a-zA-Z0-9]+( [a-zA-Z0-9]+)*$");
    }*/
    /*public Depot addDepot(@RequestBody Depot depot) {
        Depot existingDepot = depotService.findByNumero(depot.getNumero());

        if (existingDepot != null) {
            throw new DuplicateException("Depot with numero '" + depot.getNumero() + "' already exists");
        }

        return depotService.addDepot(depot);

    }*/

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Depot> getAllDepots() {
        return depotService.getAllDepots();
    }
    @GetMapping("/{depotId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Depot findById(@PathVariable Long depotId) {
        return depotService.findById(depotId);
    }

    @PostMapping("/numero")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getByNumero(@RequestBody Map<String, Object> requestBody) {
        String depotNumero = String.valueOf(requestBody.get("numero").toString());
        Depot depot = depotService.findByNumero(depotNumero);

        if (depot != null) {
            return ResponseEntity.ok(depot);
        } else {
            String errorMessage = "Dépôt introuvable avec le numéro: " + depotNumero;
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    @GetMapping("/numero/{depotNumero}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getByNumero(@PathVariable String depotNumero) {
        Depot depot = depotService.findByNumero(depotNumero);

        if (depot != null) {
            return ResponseEntity.ok(depot);
        } else {
            String errorMessage = "Dépôt introuvable avec le numéro: " + depotNumero;
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/name")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> findDepotsByNameContaining(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        List<Depot> depots = depotService.findDepotsByNameContaining(name);

        if (depots.isEmpty()) {
            String errorMessage = "Aucun dépôt contenant le nom trouvé: " + name;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

        return ResponseEntity.ok(depots);
    }
    @GetMapping("/depot/{depotId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<Article>> findAllArticlesByDepotId(@PathVariable Long depotId) {
        List<Article> articles = depotService.findAllArticlesByDepotId(depotId);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{depotId}/allQte")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Integer> getAllQteForDepot(@PathVariable Long depotId) {
        int totalQuantity = depotService.getAllQte(depotId);
        return ResponseEntity.ok(totalQuantity);
    }
    @GetMapping("/{depotId}/articles")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<ArticleWithQuantity> findAllArticlesWithQuantityByDepotId(@PathVariable Long depotId) {
        return depotService.findAllArticlesWithQuantityByDepotId(depotId);
    }
    @GetMapping("/{depotId}/articlesWithDefect")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<ArticleWithQuantities> findAllArticlesWithQuantityAndQuantityDefectByDepotId(@PathVariable Long depotId) {
        return depotService.findAllArticlesWithQuantityAndQuantityDefectByDepotId(depotId);
    }

    @GetMapping("/ArticleWithQuantityAndBureau/{depotId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<ArticleWithQuantityAndBureau> getArticlesWithQuantityAndBureauByDepotId(@PathVariable Long depotId) {
        return depotService.findAllArticlesWithQuantityAndBureauByDepotId(depotId);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        try {
            depotService.uploadAndInsertDepots(file);
            return ResponseEntity.ok("File uploaded and data inserted successfully!");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public String exportAllDepotsToExcel() {
        String filePath = "D:\\excel\\depots.xlsx"; // Specify the desired file path
        try {
            depotService.exportAllDepotsToExcel(filePath);
            return "Dépôts exportés vers Excel avec succès.";
        } catch (Exception e) {
            return "Erreur lors de l'exportation des dépôts vers Excel: " + e.getMessage();
        }
    }


}
