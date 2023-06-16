package com.example.bureau.services;


import com.example.bureau.models.Article;
import com.example.bureau.models.ArticleBureau;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Defectieux;
import com.example.bureau.repo.ArticleBureauRepo;
import com.example.bureau.repo.ArticleRepo;
import com.example.bureau.repo.BureauRepo;
import com.example.bureau.repo.DefectieuxRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DefectieuxService {


    private final DefectieuxRepo defectieuxRepo;
    private final ArticleBureauRepo articleBureauRepo;
    private final ArticleRepo articleRepo;
    private final BureauRepo bureauRepo;

    public DefectieuxService(DefectieuxRepo defectieuxRepo, ArticleBureauRepo articleBureauRepo, ArticleRepo articleRepo,  BureauRepo bureauRepo1) {
        this.defectieuxRepo = defectieuxRepo;
        this.articleBureauRepo = articleBureauRepo;
        this.articleRepo = articleRepo;
        this.bureauRepo = bureauRepo1;

    }

   /* public Defectieux createDefectueux(Article article, Bureau sourceBureau, int quantity) {
        ArticleBureau articleBureau = articleBureauRepo.findByArticleAndBureau(article, sourceBureau);
        if (articleBureau == null) {
            throw new EntityNotFoundException("Article or Bureau not found");
        }
        Defectieux defectueux = new Defectieux();
        defectueux.setArticle(article);
        defectueux.setSourceBureau(sourceBureau);
        defectueux.setDateDefect(new Date());
        defectueux.setQuantity(quantity);

        // Calculate the quantity to be deducted from ArticleBureau
        int quantityToDeduct = Math.min(quantity, articleBureau.getQuantity());
        articleBureau.setQuantityDefect(articleBureau.getQuantityDefect() + quantityToDeduct);
        articleBureau.setQuantity(articleBureau.getQuantity() - quantityToDeduct);

        defectieuxRepo.save(defectueux);

        return defectueux;
    }*/
  /* public Defectieux createDefectueux(Long articleId, Long sourceBureau, int quantity) {
       Article article = articleRepo.findById(articleId).orElseThrow(() -> new EntityNotFoundException("Article not found"));
       Bureau bureau = bureauRepo.findById(sourceBureau).orElseThrow(() -> new EntityNotFoundException("Bureau not found"));

       ArticleBureau articleBureau = articleBureauRepo.findByArticleAndBureau(article, bureau);
       if (articleBureau == null) {
           throw new EntityNotFoundException("ArticleBureau not found");
       }

       Defectieux defectueux = new Defectieux();
       defectueux.setArticle(article);
       defectueux.setSourceBureau(bureau);
       defectueux.setDateDefect(new Date());
       defectueux.setQuantity(quantity);

       // Calculate the quantity to be deducted from ArticleBureau
       int quantityToDeduct = Math.min(quantity, articleBureau.getQuantity());
       articleBureau.setQuantityDefect(articleBureau.getQuantityDefect() + quantityToDeduct);
       articleBureau.setQuantity(articleBureau.getQuantity() - quantityToDeduct);

       defectieuxRepo.save(defectueux);

       return defectueux;
   }*/
   public Defectieux createDefectueux(Long articleId, Long sourceBureauId, int quantity) {
       Article article = articleRepo.findById(articleId).orElseThrow(() -> new EntityNotFoundException("Article not found"));
       Bureau bureau = bureauRepo.findById(sourceBureauId).orElseThrow(() -> new EntityNotFoundException("Bureau not found"));

       Defectieux defectueux = defectieuxRepo.findByArticleIdAndSourceBureauId(articleId, sourceBureauId);

       if (defectueux == null) {
           defectueux = new Defectieux();
           defectueux.setArticle(article);
           defectueux.setSourceBureau(bureau);
           defectueux.setQuantity(quantity);
           defectueux.setDateDefect(new Date());

       } else {
           defectueux.setDateDefect(new Date());
           defectueux.setQuantity(defectueux.getQuantity() + quantity);
       }

       // Calculate the quantity to be deducted from ArticleBureau
       ArticleBureau articleBureau = articleBureauRepo.findByArticleAndBureau(article, bureau);
       if (articleBureau == null) {
           throw new EntityNotFoundException("ArticleBureau not found");
       }

       int availableQuantity = articleBureau.getQuantity();
       if (quantity > availableQuantity) {
           throw new IllegalArgumentException("Quantity not available");
       }

       int quantityToDeduct = Math.min(quantity, availableQuantity);
       articleBureau.setQuantityDefect(articleBureau.getQuantityDefect() + quantityToDeduct);
       articleBureau.setQuantity(articleBureau.getQuantity() - quantityToDeduct);

       defectieuxRepo.save(defectueux);

       return defectueux;
   }





  /*  public void updateReparation(Article article, Bureau sourceBureau, int quantity) {
        // Retrieve the ArticleBureau for the given article and sourceBureau
        ArticleBureau articleBureau = articleBureauRepo.findByArticleAndBureau(article, sourceBureau);
        if (articleBureau == null) {
            throw new EntityNotFoundException("Article or Bureau not found");
        }

        // Check if the article is defectieux with a non-zero quantity
        if (articleBureau.getQuantityDefect() <= 0) {
            throw new IllegalArgumentException("The article is not defectieux or has a quantity of 0");
        }

        // Check if the quantity to be repaired is valid
        if (quantity <= 0 || quantity > articleBureau.getQuantityDefect()) {
            throw new IllegalArgumentException("Invalid quantity to be repaired");
        }

        // Update the quantities in the ArticleBureau entity
        int quantityToReparer = Math.min(quantity, articleBureau.getQuantityDefect());
        articleBureau.setQuantity(articleBureau.getQuantity() + quantityToReparer);
        articleBureau.setQuantityDefect(articleBureau.getQuantityDefect() - quantityToReparer);

        // Save the updated ArticleBureau entity
        articleBureauRepo.save(articleBureau);

        // Create the Defectieux entity
        Defectieux defectueux = new Defectieux();
        defectueux.setArticle(article);
        defectueux.setSourceBureau(sourceBureau);
        defectueux.setQuantity(quantityToReparer);
        defectueux.setDateReparation(new Date());
        defectueux.setDefectieux(false);

        // Save the Defectieux entity
        defectieuxRepo.save(defectueux);
    }
*/

    public void updateReparation(Article article, Bureau sourceBureau, int quantity) {
        // Retrieve the ArticleBureau for the given article and depot
        ArticleBureau articleBureau = articleBureauRepo.findByArticleAndBureau(article, sourceBureau);
        if (articleBureau == null) {
            throw new EntityNotFoundException("Article or Depot not found");
        }

        // Check if the article is defectieux with a non-zero quantity
        if (articleBureau.getQuantityDefect() <= 0 ) {
            throw new IllegalArgumentException("The article is not defectieux or has a quantity of 0");
        }

        // Check if the quantity to be repaired is valid
        if (quantity <= 0 || quantity > articleBureau.getQuantityDefect()) {
            throw new IllegalArgumentException("Invalid quantity to be repaired");
        }

        // Check if the article and depot exist in the defectieux
        Defectieux existingDefectieux = defectieuxRepo.findByArticleAndSourceBureau(article, sourceBureau);
        if (existingDefectieux != null) {
            // Modify the existing defectieux
            existingDefectieux.setQuantity(existingDefectieux.getQuantity() - quantity);
            existingDefectieux.setDateReparation(new Date());

            // Save the updated defectieux
            defectieuxRepo.save(existingDefectieux);
        }

        // Update the quantities in the ArticleBureau entity
        int quantityToReparer = Math.min(quantity, articleBureau.getQuantityDefect());
        articleBureau.setQuantity(articleBureau.getQuantity() + quantityToReparer);
        articleBureau.setQuantityDefect(articleBureau.getQuantityDefect() - quantityToReparer);

        // Save the updated ArticleBureau entity
        articleBureauRepo.save(articleBureau);
    }


    public List<Defectieux> getAllDefectieux() {
        return defectieuxRepo.findAll();
    }






}
