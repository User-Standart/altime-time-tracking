package br.com.altave.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "registro_de_ponto")
@Getter
@Setter
public class RegistroDePonto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro") // Nome correto da coluna
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_funcionario")
    private Funcionario funcionario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cnpj_empresa", referencedColumnName = "cnpj")
    private Empresa empresa;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "horario_entrada", nullable = false)
    private LocalTime horarioEntrada;

    @Column(name = "horario_saida", nullable = false)
    private LocalTime horarioSaida;

    @Column(nullable = true)
    private String observacao;
}