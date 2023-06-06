package com.example.bureau.controllers;


import com.example.bureau.exceptions.DuplicateException;
import com.example.bureau.exceptions.ErrorResponse;
import com.example.bureau.models.Article;
import com.example.bureau.models.Depot;
import com.example.bureau.payload.request.ArticleWithQuantity;
import com.example.bureau.payload.request.ArticleWithQuantityAndBureau;
import com.example.bureau.services.DepotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Depot addDepot(@RequestBody Depot depot) {
        Depot existingDepot = depotService.findByNumero(depot.getNumero());

        if (existingDepot != null) {
            throw new DuplicateException("Depot with numero '" + depot.getNumero() + "' already exists");
        }

        return depotService.addDepot(depot);

    }

    @GetMapping
    public List<Depot> getAllDepots() {
        return depotService.getAllDepots();
    }
    @GetMapping("/{depotId}")
    public Depot findById(@PathVariable Long depotId) {
        return depotService.findById(depotId);
    }

    @PostMapping("/numero")
    public ResponseEntity<?> getByNumero(@RequestBody Map<String, Object> requestBody) {
        Long depotNumero = Long.valueOf(requestBody.get("numero").toString());
        Depot depot = depotService.findByNumero(depotNumero);

        if (depot != null) {
            return ResponseEntity.ok(depot);
        } else {
            String errorMessage = "Depot not found with numero: " + depotNumero;
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    @GetMapping("/numero/{depotNumero}")
    public ResponseEntity<?> getByNumero(@PathVariable Long depotNumero) {
        Depot depot = depotService.findByNumero(depotNumero);

        if (depot != null) {
            return ResponseEntity.ok(depot);
        } else {
            String errorMessage = "Depot not found with numero: " + depotNumero;
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/name")
    public ResponseEntity<?> findDepotsByNameContaining(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        List<Depot> depots = depotService.findDepotsByNameContaining(name);

        if (depots.isEmpty()) {
            String errorMessage = "No depots found containing name: " + name;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

        return ResponseEntity.ok(depots);
    }
    @GetMapping("/depot/{depotId}")
    public ResponseEntity<List<Article>> findAllArticlesByDepotId(@PathVariable Long depotId) {
        List<Article> articles = depotService.findAllArticlesByDepotId(depotId);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{depotId}/allQte")
    public ResponseEntity<Integer> getAllQteForDepot(@PathVariable Long depotId) {
        int totalQuantity = depotService.getAllQte(depotId);
        return ResponseEntity.ok(totalQuantity);
    }
    @GetMapping("/{depotId}/articles")
    public List<ArticleWithQuantity> findAllArticlesWithQuantityByDepotId(@PathVariable Long depotId) {
        return depotService.findAllArticlesWithQuantityByDepotId(depotId);
    }

    @GetMapping("/ArticleWithQuantityAndBureau/{depotId}")
    public List<ArticleWithQuantityAndBureau> getArticlesWithQuantityAndBureauByDepotId(@PathVariable Long depotId) {
        return depotService.findAllArticlesWithQuantityAndBureauByDepotId(depotId);
    }

}
