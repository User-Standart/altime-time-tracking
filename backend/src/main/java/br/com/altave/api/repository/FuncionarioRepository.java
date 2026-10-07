package br.com.altave.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.altave.api.model.Funcionario;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    @Query("SELECT f.empresa.cnpj, COUNT(f) FROM Funcionario f WHERE f.empresa IS NOT NULL GROUP BY f.empresa.cnpj")
    List<Object[]> countFuncionariosPorEmpresa();

    List<Funcionario> findByEmpresaCnpj(String cnpj);

    @Query("SELECT f FROM Funcionario f LEFT JOIN FETCH f.empresa LEFT JOIN FETCH f.cargo LEFT JOIN FETCH f.contrato")
    List<Funcionario> findAllComDetalhes();

    @Query("SELECT f FROM Funcionario f LEFT JOIN FETCH f.empresa LEFT JOIN FETCH f.cargo LEFT JOIN FETCH f.contrato WHERE f.id = :id")
    Optional<Funcionario> findByIdComDetalhes(Long id);
}
