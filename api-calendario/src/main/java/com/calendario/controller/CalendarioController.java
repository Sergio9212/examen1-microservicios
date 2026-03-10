package com.calendario.controller;

import com.calendario.dto.DiaCalendarioDto;
import com.calendario.dto.FestivoDto;
import com.calendario.service.CalendarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendario")
public class CalendarioController {

    private final CalendarioService calendarioService;

    public CalendarioController(CalendarioService calendarioService) {
        this.calendarioService = calendarioService;
    }

    @GetMapping("/festivos/{anio}")
    public ResponseEntity<List<FestivoDto>> listarFestivos(@PathVariable int anio) {
        List<FestivoDto> festivos = calendarioService.listarFestivos(anio);
        return ResponseEntity.ok(festivos);
    }

    @PostMapping("/poblar/{anio}")
    public ResponseEntity<Map<String, Boolean>> poblarCalendario(@PathVariable int anio) {
        boolean exito = calendarioService.poblarCalendario(anio);
        return ResponseEntity.ok(Map.of("completado", exito));
    }

    @GetMapping("/{anio}")
    public ResponseEntity<List<DiaCalendarioDto>> obtenerCalendario(@PathVariable int anio) {
        List<DiaCalendarioDto> calendario = calendarioService.obtenerCalendario(anio);
        return ResponseEntity.ok(calendario);
    }
}
