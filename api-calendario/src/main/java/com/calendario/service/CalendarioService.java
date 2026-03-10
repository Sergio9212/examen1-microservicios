package com.calendario.service;

import com.calendario.dto.DiaCalendarioDto;
import com.calendario.dto.FestivoDto;
import com.calendario.model.Calendario;
import com.calendario.model.Tipo;
import com.calendario.repository.CalendarioRepository;
import com.calendario.repository.TipoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalendarioService {

    private final CalendarioRepository calendarioRepository;
    private final TipoRepository tipoRepository;
    private final FestivosClientService festivosClient;

    public static final String TIPO_LABORAL = "Día laboral";
    public static final String TIPO_FIN_SEMANA = "Fin de Semana";
    public static final String TIPO_FESTIVO = "Día festivo";

    public CalendarioService(CalendarioRepository calendarioRepository,
                             TipoRepository tipoRepository,
                             FestivosClientService festivosClient) {
        this.calendarioRepository = calendarioRepository;
        this.tipoRepository = tipoRepository;
        this.festivosClient = festivosClient;
    }

    @Transactional
    public void asegurarTiposExistentes() {
        crearTipoSiNoExiste(TIPO_LABORAL);
        crearTipoSiNoExiste(TIPO_FIN_SEMANA);
        crearTipoSiNoExiste(TIPO_FESTIVO);
    }

    private void crearTipoSiNoExiste(String nombreTipo) {
        tipoRepository.findByTipo(nombreTipo).orElseGet(() -> {
            Tipo tipo = new Tipo();
            tipo.setTipo(nombreTipo);
            return tipoRepository.save(tipo);
        });
    }

    public List<FestivoDto> listarFestivos(int anio) {
        return festivosClient.listarFestivos(anio);
    }

    @Transactional
    public boolean poblarCalendario(int anio) {
        try {
            asegurarTiposExistentes();

            LocalDate inicio = LocalDate.of(anio, 1, 1);
            LocalDate fin = LocalDate.of(anio, 12, 31);

            calendarioRepository.deleteByFechaBetween(inicio, fin);

            List<FestivoDto> festivos = festivosClient.listarFestivos(anio);
            Set<String> fechasFestivas = festivos.stream()
                    .map(FestivoDto::getFecha)
                    .collect(Collectors.toSet());

            Tipo tipoLaboral = tipoRepository.findByTipo(TIPO_LABORAL).orElseThrow();
            Tipo tipoFinSemana = tipoRepository.findByTipo(TIPO_FIN_SEMANA).orElseThrow();
            Tipo tipoFestivo = tipoRepository.findByTipo(TIPO_FESTIVO).orElseThrow();

            Map<String, String> descripcionFestivos = festivos.stream()
                    .collect(Collectors.toMap(FestivoDto::getFecha, FestivoDto::getNombre, (a, b) -> a));

            List<Calendario> registros = new ArrayList<>();

            for (LocalDate fecha = inicio; !fecha.isAfter(fin); fecha = fecha.plusDays(1)) {
                Calendario cal = new Calendario();
                cal.setFecha(fecha);
                String fechaStr = fecha.toString();

                if (fechasFestivas.contains(fechaStr)) {
                    cal.setTipo(tipoFestivo);
                    cal.setDescripcion(descripcionFestivos.get(fechaStr));
                } else if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    cal.setTipo(tipoFinSemana);
                    cal.setDescripcion(null);
                } else {
                    cal.setTipo(tipoLaboral);
                    cal.setDescripcion(null);
                }
                registros.add(cal);
            }

            calendarioRepository.saveAll(registros);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DiaCalendarioDto> obtenerCalendario(int anio) {
        LocalDate inicio = LocalDate.of(anio, 1, 1);
        LocalDate fin = LocalDate.of(anio, 12, 31);

        List<Calendario> registros = calendarioRepository.findByFechaBetweenOrderByFecha(inicio, fin);

        return registros.stream()
                .map(c -> new DiaCalendarioDto(
                        c.getFecha(),
                        c.getTipo().getTipo(),
                        c.getDescripcion()
                ))
                .collect(Collectors.toList());
    }
}
