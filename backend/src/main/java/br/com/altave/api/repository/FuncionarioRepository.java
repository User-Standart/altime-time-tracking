package br.com.altave.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.altave.api.model.Funcionario;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    /**
     * Conta funcionários agrupados por empresa (usando o campo direto 'empresa')
     */
    @Query("SELECT f.empresa.cnpj, COUNT(f) FROM Funcionario f WHERE f.empresa IS NOT NULL GROUP BY f.empresa.cnpj")
    List<Object[]> countFuncionariosPorEmpresa();

    /**
     * Busca funcionários por CNPJ da empresa
     */
    List<Funcionario> findByEmpresaCnpj(String cnpj);
}