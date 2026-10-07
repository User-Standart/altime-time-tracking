package br.com.altave.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.altave.api.model.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Integer> {
    Optional<Contrato> findById(Integer idContrato);
}