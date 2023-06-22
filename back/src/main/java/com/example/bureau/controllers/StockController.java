package com.example.bureau.controllers;

import com.example.bureau.models.ArticleBureau;
import com.example.bureau.payload.response.ApiResponse;
import com.example.bureau.payload.response.ResponseExport;
import com.example.bureau.services.StockService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
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
            return ResponseEntity.ok().body(Collections.emptyList());        }
        return ResponseEntity.ok().body(articleBureaux);
    }

    @GetMapping("/{depotId}/export")
    public void exportArticlesWithQuantityToExcelByDepotId(@PathVariable Long depotId, HttpServletResponse response) throws IOException, SQLException {
        stockService.exportArticlesWithQuantityToExcelByDepotId(depotId, response);
    }
    @GetMapping("/{depotId}/exportWithDefect")
    public void exportArticlesWithQuantityAndDefectToExcelByDepotId(@PathVariable Long depotId, HttpServletResponse response) throws IOException, SQLException {
        stockService.exportArticlesWithQuantityAndDefectToExcelByDepotId(depotId, response);
    }
    @GetMapping("/{depotId}/exportToPDFWithDefect")
    public void exportArticlesWithQuantityAndDefectToPDFByDepotId(@PathVariable Long depotId, HttpServletResponse response) throws IOException, DocumentException, SQLException {
            // Call the exportArticlesToPDFByDepotId method from the service
            stockService.exportArticlesWithQuantityAndDefectToPDFByDepotId(depotId, response);
    }


    @GetMapping("/{bureauId}/exportBureau")
    public void exportArticlesWithQuantityToExcelByBureauId(@PathVariable Long bureauId, HttpServletResponse response) throws IOException, SQLException {
        stockService.exportArticlesWithQuantityToExcelByBureauId(bureauId, response);
    }
    @GetMapping("/{bureauId}/exportBureauToPDF")
    public void exportArticlesWithQuantityToPDFByBureauId(@PathVariable Long bureauId, HttpServletResponse response) throws IOException, DocumentException, SQLException {
            // Call the exportArticlesWithQuantityToPDFByBureauId method from the service
            stockService.exportArticlesWithQuantityToPDFByBureauId(bureauId, response);
    }

    @GetMapping("/export/articles_with_quantity")
    public void exportArticlesWithQuantityToExcelForAllDepots(HttpServletResponse response) throws IOException, SQLException {
        stockService.exportsAllArticlesWithQuantityToExcelByAllDepotId(response);
    }
    @GetMapping("/export/articles_with_quantityAndDefect")
    public void exportArticlesWithQuantityAndQuantityDefectToExcelForAllDepots(HttpServletResponse response) throws IOException, SQLException {
        stockService.exportsAllArticlesWithQuantityAndQuantityDefectToExcelByAllDepotId(response);
    }
    @GetMapping("/export/articles_with_quantityAndDefectWithPDF")
    public void exportArticlesWithQuantityAndQuantityDefectToPDFForAllDepots(HttpServletResponse response) throws IOException, DocumentException, SQLException {
        stockService.exportsAllArticlesWithQuantityAndQuantityDefectToPDFByAllDepotId(response);
    }




}
