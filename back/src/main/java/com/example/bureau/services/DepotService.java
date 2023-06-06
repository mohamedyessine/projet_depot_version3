package com.example.bureau.services;


import com.example.bureau.models.*;
import com.example.bureau.payload.request.ArticleWithQuantity;
import com.example.bureau.payload.request.ArticleWithQuantityAndBureau;
import com.example.bureau.repo.ArticleBureauRepo;
import com.example.bureau.repo.ArticleRepo;
import com.example.bureau.repo.BureauRepo;
import com.example.bureau.repo.DepotRepo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepotService {

    private final DepotRepo depotRepo;
    private final BureauService bureauService;
    private final BureauRepo bureauRepo;
    private final ArticleBureauRepo articleBureauRepo;
    private final ArticleRepo articleRepo;

    public DepotService(DepotRepo depotRepo, BureauService bureauService, BureauRepo bureauRepo, ArticleBureauRepo articleBureauRepo, ArticleRepo articleRepo) {
        this.depotRepo = depotRepo;
        this.bureauService = bureauService;
        this.bureauRepo = bureauRepo;
        this.articleBureauRepo = articleBureauRepo;
        this.articleRepo = articleRepo;
    }

    public Depot addDepot(Depot depot) {
        return depotRepo.save(depot);
    }

    public Depot findById(Long id) {
        return depotRepo.findById(id).orElse(null);
    }
    public Depot findByNumero(Long numero) {
        return depotRepo.findByNumero(numero);
    }

    public List<Depot> getAllDepots() {
        return depotRepo.findAll();
    }
    public List<Depot> findDepotsByNameContaining(String name) {
        return depotRepo.findByNameContaining(name);
    }

    public List<Long> getListBureauIdsByDepotId(Long depotId) {
        Depot depot = depotRepo.findById(depotId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid depot ID: " + depotId));

        return depot.getBureaux()
                .stream()
                .map(Bureau::getId)
                .collect(Collectors.toList());
    }
    public List<Article> findAllArticlesByDepotId(Long depotId) {
        List<Long> bureauIds = getListBureauIdsByDepotId(depotId);

        Set<Long> articleIds = articleBureauRepo.findAllByBureau_IdIn(bureauIds)
                .stream()
                .map(ArticleBureau::getArticle)
                .map(Article::getId)
                .collect(Collectors.toSet());

        return articleRepo.findAllById(articleIds);
    }
    public List<ArticleBureau> findAllArticleByDepotId(Long depotId) {
        List<Long> bureauIds = getListBureauIdsByDepotId(depotId);

        return articleBureauRepo.findAllByBureau_IdIn(bureauIds);
    }




    public int getAllQte(Long depotId) {
        List<ArticleBureau> articleBureaus = findAllArticleByDepotId(depotId);

        int totalQuantity = articleBureaus.stream()
                .mapToInt(ArticleBureau::getQuantity)
                .sum();

        return totalQuantity;
    }


    public List<ArticleWithQuantity>findAllArticlesWithQuantityByDepotId(Long depotId) {
        List<ArticleBureau> articleBureaus = findAllArticleByDepotId(depotId);

        Map<Long, Integer> articleQuantityMap = new HashMap<>();

        for (ArticleBureau articleBureau : articleBureaus) {
            Long articleId = articleBureau.getArticle().getId();
            int quantity = articleBureau.getQuantity();

            if (articleQuantityMap.containsKey(articleId)) {
                quantity += articleQuantityMap.get(articleId);
            }

            articleQuantityMap.put(articleId, quantity);
        }

        List<ArticleWithQuantity> articlesWithQuantity = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : articleQuantityMap.entrySet()) {
            Long articleId = entry.getKey();
            int quantity = entry.getValue();

            Article article = articleRepo.findById(articleId).orElse(null);

            if (article != null) {
                ArticleWithQuantity articleWithQuantity = new ArticleWithQuantity(article, quantity);
                articlesWithQuantity.add(articleWithQuantity);
            }
        }

        return articlesWithQuantity;
    }
    public List<ArticleWithQuantityAndBureau> findAllArticlesWithQuantityAndBureauByDepotId(Long depotId) {
        List<ArticleBureau> articleBureaus = findAllArticleByDepotId(depotId);

        Map<ArticleBureauId, Integer> articleQuantityMap = new HashMap<>();

        for (ArticleBureau articleBureau : articleBureaus) {
            ArticleBureauId articleBureauId = articleBureau.getId();
            int quantity = articleBureau.getQuantity();

            if (articleQuantityMap.containsKey(articleBureauId)) {
                quantity += articleQuantityMap.get(articleBureauId);
            }

            articleQuantityMap.put(articleBureauId, quantity);
        }

        List<ArticleWithQuantityAndBureau> articlesWithQuantityAndBureau = new ArrayList<>();

        for (Map.Entry<ArticleBureauId, Integer> entry : articleQuantityMap.entrySet()) {
            ArticleBureauId articleBureauId = entry.getKey();
            int quantity = entry.getValue();

            Article article = articleRepo.findById(articleBureauId.getArticleId()).orElse(null);
            Bureau bureau = bureauRepo.findById(articleBureauId.getBureauId()).orElse(null);

            if (article != null && bureau != null) {
                ArticleWithQuantity articleWithQuantity = new ArticleWithQuantity(article, quantity);
                ArticleWithQuantityAndBureau articleWithQuantityAndBureau = new ArticleWithQuantityAndBureau(articleWithQuantity, bureau);
                articlesWithQuantityAndBureau.add(articleWithQuantityAndBureau);
            }
        }

        return articlesWithQuantityAndBureau;
    }


    private List<Bureau> getBureauxByDepotId(Long depotId) {
        Depot depot = depotRepo.findById(depotId).orElse(null);
        if (depot != null) {
            return bureauService.getAllBureauByDepot(depot);
        }
        return Collections.emptyList(); // Return an empty list if depot not found
    }





}
