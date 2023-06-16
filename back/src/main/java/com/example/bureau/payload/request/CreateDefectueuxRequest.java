package com.example.bureau.payload.request;

import com.example.bureau.models.Article;
import com.example.bureau.models.Bureau;
import lombok.Data;


@Data
public class CreateDefectueuxRequest {

    private Article article;
    private Bureau sourceBureau;
    private int quantity;
}
