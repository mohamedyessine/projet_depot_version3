package com.example.bureau.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Defectieux {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Article article;

    @ManyToOne
    private Bureau sourceBureau;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDefect;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateReparation;

    private int quantity;

}
