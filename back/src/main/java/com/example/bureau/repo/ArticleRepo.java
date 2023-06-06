package com.example.bureau.repo;

import com.example.bureau.models.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepo extends JpaRepository<Article,Long> {

    public Article findByCode(Long code);


    List<Article> findByNameContaining(String name);
}
