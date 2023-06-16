package com.example.bureau.repo;

import com.example.bureau.models.Article;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Defectieux;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefectieuxRepo extends JpaRepository<Defectieux, Long> {
    Defectieux findByArticleAndSourceBureau(Article article, Bureau sourceBureau);
    Defectieux findByArticleIdAndSourceBureauId(Long articleId, Long sourceBureauId);

}
