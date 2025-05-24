package br.com.altave.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "empresa")
@Getter
@Setter
public class Empresa {

    @Id
    @Column(length = 14)
    private String cnpj;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String endereco;
}