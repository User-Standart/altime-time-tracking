package br.com.altave.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.altave.api.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, String> {
    Optional<Empresa> findById(String cnpj); // CNPJ como ID
}