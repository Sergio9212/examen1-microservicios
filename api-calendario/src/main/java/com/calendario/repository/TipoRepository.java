package com.calendario.repository;

import com.calendario.model.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoRepository extends JpaRepository<Tipo, Integer> {

    Optional<Tipo> findByTipo(String tipo);
}
