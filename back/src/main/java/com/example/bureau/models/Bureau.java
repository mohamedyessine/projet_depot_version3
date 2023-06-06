package com.example.bureau.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"numero"}))
public class Bureau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String adresse;
    private Long numero;

    @JsonIgnore
    @ManyToOne
    private Depot depot;

    @OneToMany(mappedBy = "bureau")
    private Set<ArticleBureau> articleBureaux;
}
