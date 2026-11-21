package com.induztek.catalog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class CatalogController {
    private final EquipoRepository equipos;
    private final InstrumentoRepository instrumentos;

    public CatalogController(EquipoRepository equipos, InstrumentoRepository instrumentos) {
        this.equipos = equipos;
        this.instrumentos = instrumentos;
    }

    @GetMapping("/equipos")
    public List<EquipoResponse> listEquipment(
            @RequestParam(required = false) String cliente,
            @AuthenticationPrincipal Jwt jwt) {
        String scope = "CLIENT".equals(jwt.getClaimAsString("role")) ? jwt.getClaimAsString("company") : cliente;
        return equipos.findAll().stream()
                .filter(equipo -> scope == null || scope.equalsIgnoreCase(equipo.getCliente()))
                .map(EquipoResponse::from)
                .toList();
    }

    @GetMapping("/equipos/{codigo}")
    public EquipoResponse getEquipment(@PathVariable String codigo, @AuthenticationPrincipal Jwt jwt) {
        EquipoEntity entity = equipos.findByCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found"));
        if ("CLIENT".equals(jwt.getClaimAsString("role"))
                && !java.util.Objects.equals(entity.getCliente(), jwt.getClaimAsString("company"))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found");
        }
        return EquipoResponse.from(entity);
    }

    @GetMapping("/equipos/internal/{codigo}")
    public EquipmentExistsResponse findEquipmentInternal(@PathVariable String codigo) {
        return new EquipmentExistsResponse(equipos.findByCodigo(codigo).isPresent());
    }

    @GetMapping("/equipos/duplicados")
    public DuplicateResponse duplicateCheck(
            @RequestParam String ubicacion,
            @RequestParam(required = false, defaultValue = "") String tag,
            @RequestParam(required = false, defaultValue = "") String numeroSerie,
            @RequestParam(required = false, defaultValue = "") String codigo) {
        return equipos.findAll().stream()
                .filter(equipo -> ubicacion.equalsIgnoreCase(equipo.getUbicacion()))
                .filter(equipo -> (!tag.isBlank() && tag.equalsIgnoreCase(equipo.getTagCliente()))
                        || (!numeroSerie.isBlank() && numeroSerie.equalsIgnoreCase(equipo.getNumeroSerie()))
                        || (!codigo.isBlank() && codigo.equalsIgnoreCase(equipo.getCodigo())))
                .findFirst()
                .map(EquipmentDuplicate::from)
                .map(duplicate -> new DuplicateResponse(true, duplicate))
                .orElseGet(() -> new DuplicateResponse(false, null));
    }

    @PostMapping("/equipos")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FIELD_TECHNICIAN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EquipoResponse createEquipment(@Valid @RequestBody EquipoRequest request) {
        if (equipos.findByCodigo(request.codigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Equipment code already exists");
        }
        return EquipoResponse.from(equipos.save(request.toEntity(UUID.randomUUID().toString())));
    }

    @PutMapping("/equipos/{codigo}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FIELD_TECHNICIAN')")
    public EquipoResponse updateEquipment(@PathVariable String codigo, @Valid @RequestBody EquipoRequest request) {
        EquipoEntity entity = equipos.findByCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found"));
        if (!codigo.equals(request.codigo()) && equipos.findByCodigo(request.codigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Equipment code already exists");
        }
        entity.update(request);
        return EquipoResponse.from(equipos.save(entity));
    }

    @GetMapping("/instrumentos")
    public List<InstrumentoResponse> listInstruments(@RequestParam(required = false, defaultValue = "false") boolean activos) {
        return (activos ? instrumentos.findByActivoTrue() : instrumentos.findAll())
                .stream().map(InstrumentoResponse::from).toList();
    }

    @GetMapping("/instrumentos/{id}")
    public InstrumentoResponse getInstrument(@PathVariable String id) {
        return instrumentos.findById(id).map(InstrumentoResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrument not found"));
    }

    @PostMapping("/instrumentos")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_TEST_ENGINEER')")
    @ResponseStatus(HttpStatus.CREATED)
    public InstrumentoResponse createInstrument(@Valid @RequestBody InstrumentoRequest request) {
        String id = request.id() == null || request.id().isBlank() ? UUID.randomUUID().toString() : request.id();
        if (instrumentos.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Instrument ID already exists");
        }
        return InstrumentoResponse.from(instrumentos.save(new InstrumentoEntity(
                id, request.tipo(), request.marca(), request.modelo(), request.numeroSerie(),
                request.vencimientoCalibracion(), request.activo() == null || request.activo())));
    }

    @PutMapping("/instrumentos/{id}/calibracion")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_TEST_ENGINEER')")
    public InstrumentoResponse updateCalibration(@PathVariable String id, @Valid @RequestBody CalibrationRequest request) {
        InstrumentoEntity entity = instrumentos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrument not found"));
        entity.setVencimientoCalibracion(request.vencimientoCalibracion());
        return InstrumentoResponse.from(instrumentos.save(entity));
    }

    @PutMapping("/instrumentos/{id}/estado")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public InstrumentoResponse setInstrumentActive(@PathVariable String id, @Valid @RequestBody ActiveRequest request) {
        InstrumentoEntity entity = instrumentos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrument not found"));
        entity.setActivo(request.activo());
        return InstrumentoResponse.from(instrumentos.save(entity));
    }

    public record EquipoRequest(
            @NotBlank String codigo,
            @Size(max = 80) String tagCliente,
            @NotBlank String tipo,
            String cliente,
            @NotBlank String ubicacion,
            String marca,
            String modelo,
            String numeroSerie,
            String potenciaTension,
            String anio,
            String tensiones,
            String tap,
            String conexion,
            @NotBlank String ultimoEstado) {
        EquipoEntity toEntity(String id) {
            return new EquipoEntity(id, codigo, tagCliente, tipo, cliente, ubicacion, marca, modelo,
                    numeroSerie, potenciaTension, anio, tensiones, tap, conexion, ultimoEstado);
        }
    }

    public record EquipoResponse(
            String id, String codigo, String tagCliente, String tipo, String cliente, String ubicacion,
            String marca, String modelo, String numeroSerie, String potenciaTension, String anio,
            String tensiones, String tap, String conexion, String ultimoEstado) {
        static EquipoResponse from(EquipoEntity entity) {
            return new EquipoResponse(entity.getId(), entity.getCodigo(), entity.getTagCliente(), entity.getTipo(),
                    entity.getCliente(), entity.getUbicacion(), entity.getMarca(), entity.getModelo(),
                    entity.getNumeroSerie(), entity.getPotenciaTension(), entity.getAnio(), entity.getTensiones(),
                    entity.getTap(), entity.getConexion(), entity.getUltimoEstado());
        }
    }

    public record EquipmentExistsResponse(boolean exists) {
    }

    public record EquipmentDuplicate(String id, String codigo, String tagCliente, String numeroSerie) {
        static EquipmentDuplicate from(EquipoEntity entity) {
            return new EquipmentDuplicate(entity.getId(), entity.getCodigo(), entity.getTagCliente(), entity.getNumeroSerie());
        }
    }

    public record DuplicateResponse(boolean duplicate, EquipmentDuplicate equipment) {
    }

    public record InstrumentoRequest(
            String id,
            @NotBlank String tipo,
            String marca,
            String modelo,
            String numeroSerie,
            String vencimientoCalibracion,
            Boolean activo) {
    }

    public record InstrumentoResponse(
            String id, String tipo, String marca, String modelo, String numeroSerie,
            String vencimientoCalibracion, boolean activo) {
        static InstrumentoResponse from(InstrumentoEntity entity) {
            return new InstrumentoResponse(entity.getId(), entity.getTipo(), entity.getMarca(), entity.getModelo(),
                    entity.getNumeroSerie(), entity.getVencimientoCalibracion(), entity.isActivo());
        }
    }

    public record CalibrationRequest(@NotBlank String vencimientoCalibracion) {
    }

    public record ActiveRequest(@NotNull Boolean activo) {
    }
}
