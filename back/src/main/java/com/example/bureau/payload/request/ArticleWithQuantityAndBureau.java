package com.example.bureau.payload.request;

import com.example.bureau.models.Bureau;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleWithQuantityAndBureau {
    private ArticleWithQuantity articleWithQuantity;
    private Bureau bureau;
}
