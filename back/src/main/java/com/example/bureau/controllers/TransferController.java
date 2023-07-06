package com.example.bureau.controllers;


import com.example.bureau.models.Article;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Depot;
import com.example.bureau.models.Transfer;
import com.example.bureau.payload.request.TransferRequest;
import com.example.bureau.repo.ArticleRepo;
import com.example.bureau.repo.BureauRepo;
import com.example.bureau.repo.DepotRepo;
import com.example.bureau.services.ArticleService;
import com.example.bureau.services.BureauService;
import com.example.bureau.services.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/transfers")
public class TransferController {
    private final TransferService transferService;
    private final ArticleService articleService;
    private final BureauService bureauService;
    private final DepotRepo depotRepo;
    private final BureauRepo bureauRepo;
    private final ArticleRepo articleRepo;

    public TransferController(TransferService transferService, ArticleService articleService, BureauService bureauService, DepotRepo depotRepo, BureauRepo bureauRepo, ArticleRepo articleRepo) {
        this.transferService = transferService;
        this.articleService = articleService;
        this.bureauService = bureauService;
        this.depotRepo = depotRepo;
        this.bureauRepo = bureauRepo;
        this.articleRepo = articleRepo;
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Transfer> getAllTransfers() {
        return transferService.getAllTransfers();
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody TransferRequest request) {
        Article article = articleService.findById(request.getArticleId());
        Bureau targetDepot = bureauService.findById(request.getTargetBureauId());
        Bureau sourceDepot = bureauService.findById(request.getSourceBureauId());

        Map<String, String> response = new HashMap<>();
        try {
            transferService.transfer(article, targetDepot, request.getQuantity(), sourceDepot);
            response.put("message", "Article ajouté au Bureau avec succès");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            String errorMessage = "Quantité insuffisante dans le bureau source.";
            response.put("message", errorMessage);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            String errorMessage = "Bureau ou article invalide ";
            response.put("message", errorMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, String>> transferArticles(@RequestBody TransferRequest transferRequest) {
        Article article = articleRepo.findById(transferRequest.getArticleId()).orElse(null);
        Bureau sourceBureau = bureauRepo.findById(transferRequest.getSourceBureauId()).orElse(null);
        Depot sourceDepot = depotRepo.findById(transferRequest.getSourceDepotId()).orElse(null);
        Bureau targetBureau = bureauRepo.findById(transferRequest.getTargetBureauId()).orElse(null);
        Depot targetDepot = depotRepo.findById(transferRequest.getTargetDepotId()).orElse(null);

        Map<String, String> response = new HashMap<>();
        try {
            if (article != null && sourceBureau != null && sourceDepot != null && targetBureau != null && targetDepot != null) {
                transferService.transferSourceBureauAboutSourceDepotToTargetBureauAboutTargetDepot(
                        article, sourceBureau, sourceDepot, targetBureau, targetDepot, transferRequest.getQuantity());

                response.put("message", "Transfert terminé avec succès.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Paramètres d'entrée non valides.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "Une erreur s'est produite lors du transfert.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




}
