package br.com.altave.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "funcionario")
@Getter
@Setter
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funcionario")
    private Integer id;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false)
    private String nome;

    @Column(name = "carga_horaria", nullable = true)
    private Integer cargaHoraria;

    @Column(nullable = true) // Agora é opcional!
    private String funcao;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String imagem;

    // Relacionamento direto com empresa
    @ManyToOne(optional = false)
    @JoinColumn(name = "cnpj_empresa", referencedColumnName = "cnpj")
    private Empresa empresa;

    // Relação 1:1 com contrato
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "id_contrato")
    @JsonManagedReference
    private Contrato contrato;

    @ManyToOne
    @JoinColumn(name = "id_cargo")
    private Cargo cargo;
}