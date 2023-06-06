package com.example.bureau.repo;

import com.example.bureau.models.Article;
import com.example.bureau.models.ArticleBureau;
import com.example.bureau.models.ArticleBureauId;
import com.example.bureau.models.Bureau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleBureauRepo extends JpaRepository<ArticleBureau,ArticleBureauId> {
    ArticleBureau findByArticleAndBureau(Article article, Bureau bureau);
    List<ArticleBureau> findAllByBureau_Id(Long bureauId);
    List<ArticleBureau> findByBureau(Bureau bureau);

    List<ArticleBureau> findAllByBureau_IdIn(List<Long> bureauIds);


}
