package com.example.bureau.controllers;

import com.example.bureau.models.Depot;
import com.example.bureau.payload.response.ArticleBureauResponse;
import com.example.bureau.exceptions.DuplicateException;
import com.example.bureau.exceptions.ErrorResponse;
import com.example.bureau.exceptions.ResourceNotFoundException;
import com.example.bureau.models.Article;
import com.example.bureau.models.Bureau;
import com.example.bureau.payload.request.ArticleBureauRequest;
import com.example.bureau.services.ArticleService;
import com.example.bureau.services.BureauService;
import com.example.bureau.services.DepotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Article addArticle(@RequestBody Article article) {
        Article existingArticle = articleService.findByCode(article.getCode());

        if (existingArticle != null) {
            throw new DuplicateException("Article with code '" + article.getCode() + "' already exists");
        }

        return articleService.addArticle(article);

    }

    @GetMapping
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }
    @GetMapping("/{articleId}")
    public ResponseEntity<Article> findById(@PathVariable Long articleId) {
        Article article = articleService.findById(articleId);
        if (article == null) {
            throw new ResourceNotFoundException("Article not found with id: " + articleId);
        }
        return ResponseEntity.ok().body(article);
    }

    @PostMapping("/code")
    public ResponseEntity<?> getByCode(@RequestBody Map<String, Object> requestBody) {
        String articleCode = String.valueOf(requestBody.get("code").toString());
        Article article = articleService.findByCode(articleCode);

        if (article != null) {
            return ResponseEntity.ok(article);
        } else {
            String errorMessage = "Article not found with code: " + articleCode;
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    @GetMapping("/articles/{code}")
    public ResponseEntity<?> getByCode(@PathVariable("code") String code) {
        Article article = articleService.findByCode(code);

        if (article != null) {
            return ResponseEntity.ok(article);
        } else {
            String errorMessage = "Article not found with code: " + code;
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/add-to-depot--general")
    public ResponseEntity<ArticleBureauResponse> addArticleToDepot(@RequestBody ArticleBureauRequest request) {
        Article article = articleService.findById(request.getArticleId());
        Bureau bureau = bureauService.findById(request.getBureauId());

        if (article == null || bureau == null) {
            return ResponseEntity.badRequest().body(new ArticleBureauResponse("Invalid article or depot", 400, "Bad Request"));
        }

        try {
            articleService.addArticleToBureauGeneral(article, bureau, request.getQuantity());
            return ResponseEntity.ok(new ArticleBureauResponse("Article added to depot successfully", 200, "OK"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ArticleBureauResponse(e.getMessage(), 400, "Bad Request"));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ArticleBureauResponse> addArticleToBureauAboutDepot(@RequestBody ArticleBureauRequest request) {
        Article article = articleService.findById(request.getArticleId());
        Bureau bureau = bureauService.findById(request.getBureauId());
        Depot depot = depotService.findById(request.getDepotId());

        if (article == null || bureau == null || depot == null) {
            return ResponseEntity.badRequest().body(new ArticleBureauResponse("Invalid article, bureau, or depot", 400, "Bad Request"));
        }

        try {
            articleService.addArticleToBureauAboutDepot(article, bureau, depot, request.getQuantity());
            return ResponseEntity.ok(new ArticleBureauResponse("Article added to bureau and updated depot quantity successfully", 200, "OK"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ArticleBureauResponse(e.getMessage(), 400, "Bad Request"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArticleBureauResponse("An error occurred while adding the article to the bureau and updating depot quantity", 500, "Internal Server Error"));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        try {
            articleService.uploadAndInsertArticles(file);
            return ResponseEntity.ok("File uploaded and data inserted successfully!");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error uploading file: " + e.getMessage());
        }
    }


}
