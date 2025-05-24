package br.com.altave.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.altave.api.model.Cargo;

public interface CargoRepository extends JpaRepository<Cargo, Integer> {
    Optional<Cargo> findById(Long id);
    Optional<Cargo> deleteById(Long id);
}