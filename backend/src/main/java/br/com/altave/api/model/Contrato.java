package br.com.altave.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "contrato")
@Getter
@Setter
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Integer id;

    // Relacionamento 1:1 com funcionário
    @OneToOne
    @JoinColumn(name = "id_funcionario", referencedColumnName = "id_funcionario", nullable = false)
    @JsonBackReference
    private Funcionario funcionario;

    // Empresa do contrato (vem do funcionário)
    @ManyToOne(optional = false)
    @JoinColumn(name = "cnpj_empresa", referencedColumnName = "cnpj")
    private Empresa empresa;
}