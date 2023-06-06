package com.example.bureau.services;

import com.example.bureau.models.ArticleBureau;
import com.example.bureau.repo.ArticleBureauRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {
    private final ArticleBureauRepo articleBureauRepo;

    public StockService(ArticleBureauRepo articleBureauRepo) {
        this.articleBureauRepo = articleBureauRepo;
    }

    public List<ArticleBureau> getAllStock() {
        return articleBureauRepo.findAll();
    }
    public List<ArticleBureau> getArticleByBureauId(Long bureauId) {
        return articleBureauRepo.findAllByBureau_Id(bureauId);
    }

}
