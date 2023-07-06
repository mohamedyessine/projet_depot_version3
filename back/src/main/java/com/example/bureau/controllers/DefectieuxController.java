package com.example.bureau.controllers;


import com.example.bureau.models.Article;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Defectieux;
import com.example.bureau.payload.response.ApiResponse;
import com.example.bureau.payload.response.ErrorResponse;
import com.example.bureau.repo.ArticleRepo;
import com.example.bureau.repo.BureauRepo;
import com.example.bureau.services.ArticleService;
import com.example.bureau.services.BureauService;
import com.example.bureau.services.DefectieuxService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/defectueux")
@CrossOrigin(origins = "*")
public class DefectieuxController {

    private final DefectieuxService defectieuxService;

    private final ArticleRepo articleRepo;
    private final BureauRepo bureauRepo;
    private final ArticleService articleService;
    private final BureauService bureauService;

    public DefectieuxController(DefectieuxService defectieuxService, ArticleRepo articleRepo, BureauRepo bureauRepo, ArticleService articleService, BureauService bureauService) {
        this.defectieuxService = defectieuxService;
        this.articleRepo = articleRepo;
        this.bureauRepo = bureauRepo;
        this.articleService = articleService;
        this.bureauService = bureauService;
    }

   /* @PostMapping("/add")
    public ResponseEntity<Defectieux> createDefectueux(
            @RequestBody CreateDefectueuxRequest request) {
        Article article = request.getArticle();
        Bureau sourceBureau = request.getSourceBureau();
        int quantity = request.getQuantity();

        try {
            Defectieux defectueux = defectieuxService.createDefectueux(article, sourceBureau, quantity);
            return ResponseEntity.ok(defectueux);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }*/
 /*  @PostMapping("/add")
   public ResponseEntity<Defectieux> createDefectueux(@RequestParam Long articleId, @RequestParam Long sourceBureau, @RequestParam int quantity) {
       Defectieux defectueux = defectieuxService.createDefectueux(articleId, sourceBureau, quantity);
       return ResponseEntity.ok(defectueux);
   }*/

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createDefectueux(@RequestParam Long articleId,
                                                       @RequestParam Long sourceBureauId,
                                                       @RequestParam int quantity) {
        if (quantity <= 0) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Quantité invalide");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            Defectieux defectueux = defectieuxService.createDefectueux(articleId, sourceBureauId, quantity);
            return ResponseEntity.ok(defectueux);
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Entity not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

   /* @PostMapping("/reparation")
    public ResponseEntity<String> updateReparation(@RequestBody ReparationRequest request) {
        try {
            Article article = request.getArticle(); // Retrieve the article from the request or database
                    Bureau sourceBureau = request.getSourceBureau(); // Retrieve the source bureau from the request or database
            int quantity = request.getQuantity();

            defectieuxService.updateReparation(article, sourceBureau, quantity);

            return ResponseEntity.ok("Reparation updated successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }*/

   /* @PostMapping("/update")
    public ResponseEntity<String> updateReparation(
            @RequestParam("articleId") Long articleId,
            @RequestParam("sourceBureauId") Long sourceBureauId,
            @RequestParam("quantity") int quantity) {

        try {
            Article article = articleService.findById(articleId);
            Bureau sourceBureau = bureauService.findById(sourceBureauId);

            defectieuxService.updateReparation(article, sourceBureau, quantity);

            return ResponseEntity.ok("Reparation updated successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("Article or Bureau not found");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }*/
   @PostMapping("/update")
   @PreAuthorize("hasRole('ROLE_USER')")
   @ResponseBody
   public ResponseEntity<?> updateReparation(@RequestParam("articleId") Long articleId,
                                             @RequestParam("sourceBureauId") Long sourceBureauId,
                                             @RequestParam("quantity") int quantity) {
       if (quantity <= 0) {
           ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Quantité invalide");
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
       }

       try {
           Article article = articleService.findById(articleId);
           Bureau sourceBureau = bureauService.findById(sourceBureauId);

           defectieuxService.updateReparation(article, sourceBureau, quantity);

           // Create a map to hold the success response data
           Map<String, Object> response = new HashMap<>();
           response.put("status", "success");
           response.put("message", "Reparation updated successfully");
           return ResponseEntity.ok(response);
       } catch (EntityNotFoundException e) {
           ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Article ou Bureau introuvable");
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
       } catch (IllegalArgumentException e) {
           ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
       }
   }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Defectieux> getAllDefectieux() {
        return defectieuxService.getAllDefectieux();
    }

}
