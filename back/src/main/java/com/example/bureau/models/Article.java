package com.example.bureau.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data

@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))

public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lebelle;
    @Column(unique = true)
    private Long code;


    @OneToMany(mappedBy = "article")
    private Set<ArticleBureau> articleBureaux;

}
