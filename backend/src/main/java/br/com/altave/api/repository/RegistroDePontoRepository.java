package br.com.altave.api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.altave.api.model.RegistroDePonto;

public interface RegistroDePontoRepository extends JpaRepository<RegistroDePonto, Integer> {
    Optional<RegistroDePonto> findById(Integer idRegistro);

    List<RegistroDePonto> findByDataRegistroGreaterThanEqualOrderByDataRegistroDesc(LocalDateTime inicio);
}
