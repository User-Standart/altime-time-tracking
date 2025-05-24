package br.com.altave.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cargo")
@Getter
@Setter
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cargo")
    private Long id;

    @Column(name = "nome_cargo", nullable = false)
    private String nomeCargo;

    @Column(nullable = true)
    private String descricao;
}