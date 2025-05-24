package br.com.altave.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.altave.api.model.Funcionario;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    /**
     * Conta funcionários agrupados por empresa
     */
    @Query("SELECT f.empresa.cnpj, COUNT(f) FROM Funcionario f WHERE f.empresa IS NOT NULL GROUP BY f.empresa.cnpj")
    List<Object[]> countFuncionariosPorEmpresa();

    /**
     * Busca funcionários por CNPJ da empresa
     */
    List<Funcionario> findByEmpresaCnpj(String cnpj);

    /**
     * Busca todos os funcionários com empresa, cargo e contrato carregados
     */
    @Query("SELECT f FROM Funcionario f LEFT JOIN FETCH f.empresa LEFT JOIN FETCH f.cargo LEFT JOIN FETCH f.contrato")
    List<Funcionario> findAllComDetalhes();

    /**
     * Busca funcionário por ID com empresa, cargo e contrato
     */
    @Query("SELECT f FROM Funcionario f LEFT JOIN FETCH f.empresa LEFT JOIN FETCH f.cargo LEFT JOIN FETCH f.contrato WHERE f.id = :id")
    Optional<Funcionario> findByIdComDetalhes(Integer id);
}