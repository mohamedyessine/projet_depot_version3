package com.example.bureau.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "article_bureau")
public class ArticleBureau {
    @EmbeddedId
    private ArticleBureauId id;
    private int quantity;
    // other fields and methods

    private int quantityDefect = 0;

    @ManyToOne
    @MapsId("articleId")
    private Article article;

    @ManyToOne
    @MapsId("bureauId")
    private Bureau bureau;
    public void setArticleBureauId(Long articleId, Long bureauId) {
        this.id = new ArticleBureauId(articleId, bureauId);
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }
}
