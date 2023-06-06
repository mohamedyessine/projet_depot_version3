package com.example.bureau.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Article article;

    @ManyToOne
    private Bureau sourceBureau;

    @ManyToOne
    private Depot sourceDepot;

    @ManyToOne
    private Bureau targetBureau;

    @ManyToOne
    private Depot targetDepot;

    private int quantity;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTransfer;
}
