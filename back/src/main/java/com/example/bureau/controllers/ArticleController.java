package com.example.bureau.controllers;

import com.example.bureau.models.Depot;
import com.example.bureau.payload.response.ArticleBureauResponse;
import com.example.bureau.exceptions.DuplicateException;
import com.example.bureau.exceptions.ErrorResponse;
import com.example.bureau.exceptions.ResourceNotFoundException;
import com.example.bureau.models.Article;
import com.example.bureau.models.Bureau;
import com.example.bureau.payload.request.ArticleBureauRequest;
import com.example.bureau.payload.response.ResponseExport;
import com.example.bureau.services.ArticleService;
import com.example.bureau.services.BureauService;
import com.example.bureau.services.DepotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final BureauService bureauService;

    private final DepotService depotService;

    public ArticleController(ArticleService articleService, BureauService bureauService, DepotService depotService) {
        this.articleService = articleService;
        this.bureauService = bureauService;
        this.depotService = depotService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public Article addArticle(@RequestBody Article article) {

        // Check if any field contains only spaces
       /* if (containsOnlySpaces(article)) {
            throw new DuplicateException("Fields cannot contain only spaces");
        }*/
        Article existingArticle = articleService.findByCode(article.getCode());

        if (existingArticle != null) {
            throw new DuplicateException("L'article avec le code '" + article.getCode() + "' existe déjà");
        }

        return articleService.addArticle(article);

    }
    private boolean containsOnlySpaces(Article article) {
        String code = article.getCode().trim();
        String name = article.getName().trim();

        return !code.matches("^[a-zA-Z0-9]+( [a-zA-Z0-9]+)*$") || !name.matches("^[a-zA-Z0-9]+( [a-zA-Z0-9]+)*$");
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{articleId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Article> findById(@PathVariable Long articleId) {
        Article article = articleService.findById(articleId);
        if (article == null) {
            throw new ResourceNotFoundException("Article introuvable avec l'identifiant: " + articleId);
        }
        return ResponseEntity.ok().body(article);
    }

    @PostMapping("/code")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getByCode(@RequestBody Map<String, Object> requestBody) {
        String articleCode = String.valueOf(requestBody.get("code").toString());
        Article article = articleService.findByCode(articleCode);

        if (article != null) {
            return ResponseEntity.ok(article);
        } else {
            String errorMessage = "Article introuvable avec le code: " + articleCode;
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    @GetMapping("/articles/{code}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getByCode(@PathVariable("code") String code) {
        Article article = articleService.findByCode(code);

        if (article != null) {
            return ResponseEntity.ok(article);
        } else {
            String errorMessage = "Article introuvable avec le code: " + code;
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/add-to-depot--general")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ArticleBureauResponse> addArticleToDepot(@RequestBody ArticleBureauRequest request) {
        Article article = articleService.findById(request.getArticleId());
        Bureau bureau = bureauService.findById(request.getBureauId());

        if (article == null || bureau == null) {
            return ResponseEntity.badRequest().body(new ArticleBureauResponse("Article ou dépôt invalide", 400, "Bad Request"));
        }

        try {
            articleService.addArticleToBureauGeneral(article, bureau, request.getQuantity());
            return ResponseEntity.ok(new ArticleBureauResponse("Article ajouté au dépôt avec succès", 200, "OK"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ArticleBureauResponse(e.getMessage(), 400, "Bad Request"));
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ArticleBureauResponse> addArticleToBureauAboutDepot(@RequestBody ArticleBureauRequest request) {

        // Validate quantity
        if (request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body(new ArticleBureauResponse("La quantité doit être supérieure à zéro", 400, "Bad Request"));
        }
        Article article = articleService.findById(request.getArticleId());
        Bureau bureau = bureauService.findById(request.getBureauId());
        Depot depot = depotService.findById(request.getDepotId());

        if (article == null || bureau == null || depot == null) {
            return ResponseEntity.badRequest().body(new ArticleBureauResponse("Article, bureau ou dépôt invalide", 400, "Bad Request"));
        }

        try {
            articleService.addArticleToBureauAboutDepot(article, bureau, depot, request.getQuantity());
            return ResponseEntity.ok(new ArticleBureauResponse("Achat ajouté avec succès", 200, "OK"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ArticleBureauResponse(e.getMessage(), 400, "Bad Request"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArticleBureauResponse("Une erreur s'est produite lors de l'ajout", 500, "Internal Server Error"));
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        try {
            articleService.uploadAndInsertArticles(file);
            return ResponseEntity.ok("File uploaded and data inserted successfully!");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error uploading file: " + e.getMessage());
        }
    }
     @GetMapping("/exportToPDF")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> exportArticlesToPDF(HttpServletResponse response) {
        try {
            // Call the exportAllArticleToPDF method from the service, passing the HttpServletResponse
            articleService.exportAllArticleToPDF(response);

            // Build the success response
            String message = "Articles exported to PDF successfully";
            return new ResponseEntity<>(new ResponseExport(true, message), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();

            // Build the error response
            String message = "Failed to export articles to PDF";
            return new ResponseEntity<>(new ResponseExport(false, message), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportAllArticlesToExcel() {
        String filePath = "D:\\excel\\articles.xlsx"; // Specify the desired file path
        try {
            articleService.exportAllArticleToExcel(filePath);
            return ResponseEntity.ok("Articles exportés vers Excel avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to export articles to Excel: " + e.getMessage());
        }
    }

}
