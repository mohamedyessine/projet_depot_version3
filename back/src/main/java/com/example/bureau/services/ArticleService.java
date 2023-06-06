package com.example.bureau.services;

import com.example.bureau.models.Article;
import com.example.bureau.models.ArticleBureau;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Depot;
import com.example.bureau.repo.ArticleBureauRepo;
import com.example.bureau.repo.ArticleRepo;
import com.example.bureau.repo.DepotRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {
    private final ArticleRepo articleRepo;
    private final DepotRepo depotRepo;
    private final ArticleBureauRepo articleBureauRepo;

    public ArticleService(ArticleRepo articleRepo, DepotRepo depotRepo, ArticleBureauRepo articleBureauRepo) {
        this.articleRepo = articleRepo;
        this.depotRepo = depotRepo;
        this.articleBureauRepo = articleBureauRepo;
    }

    public Article addArticle(Article article) {
        return articleRepo.save(article);
    }

    public Article findById(Long id) {
        return articleRepo.findById(id).orElse(null);
    }
    public Article findByCode(Long code) {
        return articleRepo.findByCode(code);
    }
    public List<Article> getAllArticles() {
        return articleRepo.findAll();
    }

    public Article updateArticle(Article article) {
        return articleRepo.save(article);
    }

    public void addArticleToBureauGeneral(Article article, Bureau bureau, int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        ArticleBureau targetArticleBureau = articleBureauRepo.findByArticleAndBureau(article, bureau);
        int targetDepotQuantity = targetArticleBureau != null ? targetArticleBureau.getQuantity() : 0;

        if (targetArticleBureau == null) {
            targetArticleBureau = new ArticleBureau();
            targetArticleBureau.setArticle(article);
            targetArticleBureau.setBureau(bureau);
        }
        targetArticleBureau.setQuantity(targetDepotQuantity + quantity);
        targetArticleBureau.setArticleBureauId(article.getId(), bureau.getId());
        articleBureauRepo.save(targetArticleBureau);
    }
    public void addArticleToBureauAboutDepot(Article article, Bureau bureau, Depot depot, int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        ArticleBureau targetArticleBureau = articleBureauRepo.findByArticleAndBureau(article, bureau);
        int targetArticleBureauQuantity = targetArticleBureau != null ? targetArticleBureau.getQuantity() : 0;

        if (targetArticleBureau == null) {
            targetArticleBureau = new ArticleBureau();
            targetArticleBureau.setArticle(article);
            targetArticleBureau.setBureau(bureau);
        }
        targetArticleBureau.addQuantity(quantity);
        targetArticleBureau.setArticleBureauId(article.getId(), bureau.getId());
        articleBureauRepo.save(targetArticleBureau);

        // Update the depot quantity
       /* int updatedDepotQuantity = depot.getQuantity() + targetArticleBureauQuantity;
        depot.setQuantity(updatedDepotQuantity);
        depotRepo.save(depot);*/
    }



}
