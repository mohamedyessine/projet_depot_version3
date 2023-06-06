package com.example.bureau.payload.request;

import com.example.bureau.models.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleWithQuantity {
    private Article article;
    private int quantity;
}
