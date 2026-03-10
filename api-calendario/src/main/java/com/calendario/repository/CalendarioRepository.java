package com.calendario.repository;

import com.calendario.model.Calendario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CalendarioRepository extends JpaRepository<Calendario, Integer> {

    List<Calendario> findByFechaBetweenOrderByFecha(LocalDate inicio, LocalDate fin);

    @Modifying
    @Query("DELETE FROM Calendario c WHERE c.fecha BETWEEN :inicio AND :fin")
    void deleteByFechaBetween(LocalDate inicio, LocalDate fin);
}
