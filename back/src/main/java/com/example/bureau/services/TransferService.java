package com.example.bureau.services;

import com.example.bureau.models.*;
import com.example.bureau.repo.ArticleBureauRepo;
import com.example.bureau.repo.TransferRepo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransferService {
    private final TransferRepo transferRepo;
    private final ArticleBureauRepo articleBureauRepo;

    public TransferService(TransferRepo transferRepo, ArticleBureauRepo articleBureauRepo) {
        this.transferRepo = transferRepo;
        this.articleBureauRepo = articleBureauRepo;
    }

    public List<Transfer> getAllTransfers() {
        return transferRepo.findAll();
    }

    public void transfer(Article article, Bureau targetBureau, int quantity, Bureau sourceBureau) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        ArticleBureau targetArticleBureau = articleBureauRepo.findByArticleAndBureau(article, targetBureau);
        ArticleBureau sourceArticleBureau = articleBureauRepo.findByArticleAndBureau(article, sourceBureau);
        int targetDepotQuantity = targetArticleBureau != null ? targetArticleBureau.getQuantity() : 0;
        int sourceDepotQuantity = sourceArticleBureau != null ? sourceArticleBureau.getQuantity() : 0;
        if (sourceDepotQuantity >= quantity) {
            if (targetArticleBureau == null) {
                targetArticleBureau = new ArticleBureau();
                targetArticleBureau.setArticle(article);
                targetArticleBureau.setBureau(targetBureau);
            }
            targetArticleBureau.setQuantity(targetDepotQuantity + quantity);
            targetArticleBureau.setArticleBureauId(article.getId(), targetBureau.getId());
            articleBureauRepo.save(targetArticleBureau);

            sourceArticleBureau.setQuantity(sourceDepotQuantity - quantity);
            articleBureauRepo.save(sourceArticleBureau);

            Transfer transfer = new Transfer();
            transfer.setArticle(article);
            transfer.setSourceBureau(sourceBureau);
            transfer.setTargetBureau(targetBureau);
            transfer.setQuantity(quantity);
            transfer.setDateTransfer(new Date());
            transferRepo.save(transfer);
        } else {
            throw new IllegalArgumentException("Insufficient quantity in source depot.");
        }
    }

    public void transferSourceBureauAboutSourceDepotToTargetBureauAboutTargetDepot(
            Article article, Bureau sourceBureau, Depot sourceDepot,
            Bureau targetBureau, Depot targetDepot, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        ArticleBureau sourceArticleBureau = articleBureauRepo.findByArticleAndBureau(article, sourceBureau);
        ArticleBureau targetArticleBureau = articleBureauRepo.findByArticleAndBureau(article, targetBureau);

        int sourceDepotQuantity = sourceArticleBureau != null ? sourceArticleBureau.getQuantity() : 0;
        int targetDepotQuantity = targetArticleBureau != null ? targetArticleBureau.getQuantity() : 0;

        if (sourceDepotQuantity >= quantity) {
            if (targetArticleBureau == null) {
                targetArticleBureau = new ArticleBureau();
                targetArticleBureau.setArticle(article);
                targetArticleBureau.setBureau(targetBureau);
            }

            targetArticleBureau.setQuantity(targetDepotQuantity + quantity);
            targetArticleBureau.setArticleBureauId(article.getId(), targetBureau.getId());
            articleBureauRepo.save(targetArticleBureau);

            sourceArticleBureau.setQuantity(sourceDepotQuantity - quantity);
            articleBureauRepo.save(sourceArticleBureau);

            Transfer transfer = new Transfer();
            transfer.setArticle(article);
            transfer.setSourceBureau(sourceBureau);
            transfer.setTargetBureau(targetBureau);
            transfer.setSourceDepot(sourceDepot);
            transfer.setTargetDepot(targetDepot);
            transfer.setQuantity(quantity);
            transfer.setDateTransfer(new Date());
            transferRepo.save(transfer);
        } else {
            throw new IllegalArgumentException("Insufficient quantity in the source depot.");
        }
    }


}
