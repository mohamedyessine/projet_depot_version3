package com.example.bureau.controllers;


import com.example.bureau.models.Article;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Defectieux;
import com.example.bureau.payload.request.CreateDefectueuxRequest;
import com.example.bureau.repo.ArticleRepo;
import com.example.bureau.repo.BureauRepo;
import com.example.bureau.services.ArticleService;
import com.example.bureau.services.BureauService;
import com.example.bureau.services.DefectieuxService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Defectieux> createDefectueux(@RequestParam Long articleId,
                                                       @RequestParam Long sourceBureauId,
                                                       @RequestParam int quantity) {
        if (quantity <= 0) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            Defectieux defectueux = defectieuxService.createDefectueux(articleId, sourceBureauId, quantity);
            return ResponseEntity.ok(defectueux);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
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
   @ResponseBody
   public ResponseEntity<Map<String, Object>> updateReparation( 
           @RequestParam("articleId") Long articleId,
           @RequestParam("sourceBureauId") Long sourceBureauId,
           @RequestParam("quantity") int quantity) {
       if (quantity <= 0) {
           return ResponseEntity.badRequest().body(null);
       }

       try {
           Article article = articleService.findById(articleId);
           Bureau sourceBureau = bureauService.findById(sourceBureauId);

           defectieuxService.updateReparation(article, sourceBureau, quantity);

           // Create a map to hold the response data
           Map<String, Object> response = new HashMap<>();
           response.put("status", "success");
           response.put("message", "Reparation updated successfully");
           return ResponseEntity.ok(response);
       } catch (EntityNotFoundException e) {
           // Create a map to hold the error response data
           Map<String, Object> response = new HashMap<>();
           response.put("status", "error");
           response.put("message", "Article or Bureau not found");
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
       } catch (IllegalArgumentException e) {
           // Create a map to hold the error response data
           Map<String, Object> response = new HashMap<>();
           response.put("status", "error");
           response.put("message", e.getMessage());
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
       }
   }

    @GetMapping
    public List<Defectieux> getAllDefectieux() {
        return defectieuxService.getAllDefectieux();
    }

}
