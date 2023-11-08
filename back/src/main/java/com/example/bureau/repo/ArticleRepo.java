package com.example.bureau.repo;

import com.example.bureau.models.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepo extends JpaRepository<Article,Long> {
    Article findByCode(String code);
    List<Article> findByType(String type);
    @Query("SELECT a FROM Article a " +
            "WHERE LOWER(a.name) LIKE :searchTerm " +
            "OR LOWER(a.lebelle) LIKE :searchTerm " +
            "OR LOWER(a.code) LIKE :searchTerm " +
            "OR LOWER(a.type) LIKE :searchTerm")
    List<Article> searchByAllFields(@Param("searchTerm") String searchTerm);
}
