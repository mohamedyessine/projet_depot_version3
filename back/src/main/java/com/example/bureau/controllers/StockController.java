package com.example.bureau.controllers;

import com.example.bureau.models.ArticleBureau;
import com.example.bureau.services.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/stock")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<ArticleBureau> getStock() {
        return stockService.getAllStock();
    }

    @GetMapping("/bureau/{bureauId}")
    public ResponseEntity<List<ArticleBureau>> getArticlesByDepotId(@PathVariable Long bureauId) {
        List<ArticleBureau> articleBureaux = stockService.getArticleByBureauId(bureauId);
        if (articleBureaux.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this bureau is empty: ");
        }
        return ResponseEntity.ok().body(articleBureaux);
    }

}
