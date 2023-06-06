package com.example.bureau.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    private Long articleId;
    private Long targetBureauId;
    private Long sourceBureauId;
    private Long sourceDepotId;

    private Long targetDepotId;

    private int quantity;

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getTargetBureauId() {
        return targetBureauId;
    }

    public void setTargetBureauId(Long targetBureauId) {
        this.targetBureauId = targetBureauId;
    }

    public Long getSourceBureauId() {
        return sourceBureauId;
    }

    public void setSourceBureauId(Long sourceBureauId) {
        this.sourceBureauId = sourceBureauId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
