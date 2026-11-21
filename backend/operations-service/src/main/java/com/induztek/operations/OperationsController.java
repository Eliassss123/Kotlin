package com.induztek.operations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class OperationsController {
    private static final DateTimeFormatter LOCAL_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final TypeReference<List<Map<String, Object>>> JSON_OBJECT_LIST = new TypeReference<>() {};
    private final VisitRepository visits;
    private final ProtocolRepository protocols;
    private final TestRepository tests;
    private final ActaRepository actas;
    private final CatalogClient catalog;
    private final ObjectMapper mapper;

    public OperationsController(
            VisitRepository visits, ProtocolRepository protocols, TestRepository tests,
            ActaRepository actas, CatalogClient catalog, ObjectMapper mapper) {
        this.visits = visits;
        this.protocols = protocols;
        this.tests = tests;
        this.actas = actas;
        this.catalog = catalog;
        this.mapper = mapper;
    }

    @GetMapping("/api/visitas")
    public List<VisitResponse> listVisits(@AuthenticationPrincipal Jwt jwt) {
        List<VisitEntity> result = isClient(jwt)
                ? visits.findByClienteIgnoreCase(jwt.getClaimAsString("company"))
                : visits.findAll();
        return result.stream().map(VisitResponse::from).toList();
    }

    @GetMapping("/api/visitas/{id}")
    public VisitResponse getVisit(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        VisitEntity visit = visits.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found"));
        ensureVisitAccess(visit, jwt);
        return VisitResponse.from(visit);
    }

    @PostMapping("/api/visitas")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FIELD_TECHNICIAN')")
    @ResponseStatus(HttpStatus.CREATED)
    public VisitResponse createVisit(@Valid @RequestBody VisitRequest request) {
        return VisitResponse.from(visits.save(new VisitEntity(request)));
    }

    @PatchMapping("/api/visitas/{id}/cerrar")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FIELD_TECHNICIAN','ROLE_TEST_ENGINEER')")
    public VisitResponse closeVisit(@PathVariable String id) {
        VisitEntity visit = visits.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found"));
        visit.close();
        return VisitResponse.from(visits.save(visit));
    }

    @GetMapping("/api/protocolos")
    public List<ProtocolResponse> listProtocols(
            @RequestParam(required = false) String visitaId,
            @RequestParam(required = false) String equipoCodigo,
            @AuthenticationPrincipal Jwt jwt) {
        List<ProtocolEntity> result;
        if (isClient(jwt)) {
            List<String> visitIds = visits.findByClienteIgnoreCase(jwt.getClaimAsString("company")).stream()
                    .map(VisitEntity::getId).toList();
            result = visitIds.isEmpty() ? List.of() : protocols.findByVisitaIdIn(visitIds);
            if (visitaId != null) {
                result = result.stream().filter(protocol -> visitaId.equals(protocol.getVisitaId())).toList();
            }
            if (equipoCodigo != null) {
                result = result.stream().filter(protocol -> equipoCodigo.equals(protocol.getEquipoCodigo())).toList();
            }
        } else {
            result = visitaId != null
                    ? protocols.findByVisitaId(visitaId)
                    : equipoCodigo != null ? protocols.findByEquipoCodigo(equipoCodigo) : protocols.findAll();
        }
        return result.stream().map(this::protocolResponse).toList();
    }

    @GetMapping("/api/protocolos/{id}")
    public ProtocolResponse getProtocol(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        ProtocolEntity protocol = protocols.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Protocol not found"));
        ensureProtocolAccess(protocol, jwt);
        return protocolResponse(protocol);
    }

    @PostMapping("/api/protocolos")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FIELD_TECHNICIAN','ROLE_TEST_ENGINEER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProtocolResponse createProtocol(@Valid @RequestBody ProtocolRequest request) {
        catalog.requireEquipment(request.equipoCodigo());
        if (protocols.existsByVisitaIdAndEquipoCodigo(request.visitaId(), request.equipoCodigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A protocol already exists for this equipment and visit");
        }
        return protocolResponse(protocols.save(new ProtocolEntity(request, mapper)));
    }

    @PostMapping("/api/protocolos/{id}/formalizar")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_TEST_ENGINEER')")
    public ProtocolResponse formalizeProtocol(@PathVariable String id, @Valid @RequestBody FormalizeRequest request) {
        ProtocolEntity protocol = protocols.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Protocol not found"));
        protocol.formalize(request.usuario(), LocalDateTime.now().format(LOCAL_DATE_TIME));
        return protocolResponse(protocols.save(protocol));
    }

    @GetMapping({"/api/equipos/{codigo}/pruebas", "/api/pruebas"})
    public List<TestResponse> listTests(
            @PathVariable(required = false) String codigo,
            @RequestParam(required = false) String protocoloId,
            @RequestParam(required = false) String equipoCodigo,
            @AuthenticationPrincipal Jwt jwt) {
        String code = codigo != null ? codigo : equipoCodigo;
        List<TestEntity> result = protocoloId != null
                ? tests.findByProtocoloId(protocoloId)
                : code != null ? tests.findByEquipoCodigo(code) : tests.findAll();
        if (isClient(jwt)) {
            result = result.stream().filter(test -> canAccessProtocol(test.getProtocoloId(), jwt)).toList();
        }
        return result.stream().map(this::testResponse).toList();
    }

    @GetMapping("/api/pruebas/{id}")
    public TestResponse getTest(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        TestEntity test = tests.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test not found"));
        if (isClient(jwt) && !canAccessProtocol(test.getProtocoloId(), jwt)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Test not found");
        }
        return testResponse(test);
    }

    @PostMapping("/api/pruebas")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FIELD_TECHNICIAN','ROLE_TEST_ENGINEER')")
    @ResponseStatus(HttpStatus.CREATED)
    public TestResponse createTest(@Valid @RequestBody TestRequest request) {
        catalog.requireEquipment(request.equipoCodigo());
        return testResponse(tests.save(new TestEntity(request, mapper)));
    }

    @GetMapping("/api/actas")
    public List<ActaResponse> listActs(
            @RequestParam(required = false) String visitaId,
            @AuthenticationPrincipal Jwt jwt) {
        if (visitaId != null) {
            List<ActaEntity> result = actas.findByVisitaId(visitaId).map(List::of).orElseGet(List::of);
            if (isClient(jwt)) {
                result = result.stream().filter(act -> sameCompany(act.getCliente(), jwt)).toList();
            }
            return result
                    .stream().map(ActaResponse::from).toList();
        }
        return actas.findAll().stream()
                .filter(act -> !isClient(jwt) || sameCompany(act.getCliente(), jwt))
                .map(ActaResponse::from).toList();
    }

    @GetMapping("/api/actas/{id}")
    public ActaResponse getAct(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        ActaEntity act = actas.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Act not found"));
        if (isClient(jwt) && !sameCompany(act.getCliente(), jwt)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Act not found");
        }
        return ActaResponse.from(act);
    }

    @PostMapping("/api/actas")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FIELD_TECHNICIAN','ROLE_TEST_ENGINEER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ActaResponse createAct(@Valid @RequestBody ActaRequest request) {
        return ActaResponse.from(actas.save(new ActaEntity(request)));
    }

    private ProtocolResponse protocolResponse(ProtocolEntity entity) {
        return new ProtocolResponse(
                entity.getId(), entity.getCodigoProtocolo(), entity.getVisitaId(), entity.getOrdenTrabajo(),
                entity.getEquipoCodigo(), entity.getTipoEquipo(), entity.getFechaHora(), entity.getEstado(),
                entity.getResultadoGlobal(), entity.getTecnico(), entity.getTecnicoCreador(),
                entity.getDescripcionMantenimiento(), entity.getInstrumento(), entity.isCalibracionVigente(),
                entity.getTemperaturaAmbiente(), entity.getHumedad(), entity.isActaEntregadaCliente(),
                entity.getFormalizadoPor(), entity.getFechaFormalizacion(), entity.getSyncStatus(),
                entity.getObservaciones(), parseJsonList(entity.getPruebasJson()),
                parseJsonList(entity.getMedicionesJson()), parseStringList(entity.getFotosJson()));
    }

    private boolean isClient(Jwt jwt) {
        return "CLIENT".equals(jwt.getClaimAsString("role"));
    }

    private boolean sameCompany(String client, Jwt jwt) {
        String company = jwt.getClaimAsString("company");
        return company != null && client != null && company.equalsIgnoreCase(client);
    }

    private void ensureVisitAccess(VisitEntity visit, Jwt jwt) {
        if (isClient(jwt) && !sameCompany(visit.getCliente(), jwt)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found");
        }
    }

    private void ensureProtocolAccess(ProtocolEntity protocol, Jwt jwt) {
        if (isClient(jwt)) {
            VisitEntity visit = visits.findById(protocol.getVisitaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Protocol not found"));
            ensureVisitAccess(visit, jwt);
        }
    }

    private boolean canAccessProtocol(String protocolId, Jwt jwt) {
        if (protocolId == null || protocolId.isBlank()) {
            return false;
        }
        return protocols.findById(protocolId).map(protocol -> {
            try {
                ensureProtocolAccess(protocol, jwt);
                return true;
            } catch (ResponseStatusException exception) {
                return false;
            }
        }).orElse(false);
    }

    private TestResponse testResponse(TestEntity entity) {
        return new TestResponse(
                entity.getId(), entity.getProtocoloId(), entity.getOrdenTrabajo(), entity.getEquipoCodigo(),
                entity.getTipoPrueba(), entity.getUbicacion(), entity.getFechaHora(), entity.getEstadoPrueba(),
                entity.getMotivoNoRealizada(), entity.getResultado(), entity.getInstrumentoId(),
                entity.getInstrumento(), entity.getInstrumentoSerie(), entity.getInstrumentoCalibracion(),
                entity.isCalibracionVigente(), entity.getTemperaturaAmbiente(), entity.getHumedad(),
                parseJsonList(entity.getMedicionesJson()), entity.getObservaciones(),
                entity.getVariacionHistoricaPorcentaje(), entity.isSuperaVariacion25());
    }

    private List<Map<String, Object>> parseJsonList(String value) {
        try {
            return mapper.readValue(value, JSON_OBJECT_LIST);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Stored operation data is invalid", exception);
        }
    }

    private List<String> parseStringList(String value) {
        try {
            return mapper.readValue(value, new TypeReference<>() {});
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Stored image URLs are invalid", exception);
        }
    }

    public record VisitRequest(
            String id, @NotBlank String ordenTrabajo, String fecha, String horaInicio,
            String cliente, String instalacion, String modalidad, String ingenieroACargo,
            String tecnicos, String temperaturaAmbiente, String humedad, String syncStatus) {
    }

    public record VisitResponse(
            String id, String ordenTrabajo, String fecha, String horaInicio, String cliente,
            String instalacion, String modalidad, String ingenieroACargo, String tecnicos,
            String temperaturaAmbiente, String humedad, boolean cerrada, String syncStatus) {
        static VisitResponse from(VisitEntity entity) {
            return new VisitResponse(entity.getId(), entity.getOrdenTrabajo(), entity.getFecha(), entity.getHoraInicio(),
                    entity.getCliente(), entity.getInstalacion(), entity.getModalidad(), entity.getIngenieroACargo(),
                    entity.getTecnicos(), entity.getTemperaturaAmbiente(), entity.getHumedad(),
                    entity.isCerrada(), entity.getSyncStatus());
        }
    }

    public record Measurement(String campo, String valorNumerico, String unidad) {
    }

    public record ProtocolRequest(
            String id, @NotBlank String codigoProtocolo, @NotBlank String visitaId,
            String ordenTrabajo, @NotBlank String equipoCodigo, String tipoEquipo, String fechaHora,
            String estado, String resultadoGlobal, String tecnico, String tecnicoCreador,
            String descripcionMantenimiento, String instrumento, boolean calibracionVigente,
            String temperaturaAmbiente, String humedad, boolean actaEntregadaCliente,
            String formalizadoPor, String fechaFormalizacion, String syncStatus, String observaciones,
            List<Map<String, Object>> pruebas, List<Measurement> mediciones, List<String> fotos) {
    }

    public record ProtocolResponse(
            String id, String codigoProtocolo, String visitaId, String ordenTrabajo,
            String equipoCodigo, String tipoEquipo, String fechaHora, String estado, String resultadoGlobal,
            String tecnico, String tecnicoCreador, String descripcionMantenimiento, String instrumento,
            boolean calibracionVigente, String temperaturaAmbiente, String humedad,
            boolean actaEntregadaCliente, String formalizadoPor, String fechaFormalizacion,
            String syncStatus, String observaciones, List<Map<String, Object>> pruebas,
            List<Map<String, Object>> mediciones, List<String> fotos) {
    }

    public record TestRequest(
            String id, String protocoloId, String ordenTrabajo, @NotBlank String equipoCodigo,
            String tipoPrueba, String ubicacion, String fechaHora, String estadoPrueba,
            String motivoNoRealizada, String resultado, String instrumentoId, String instrumento,
            String instrumentoSerie, String instrumentoCalibracion, boolean calibracionVigente,
            String temperaturaAmbiente, String humedad, List<Measurement> mediciones,
            String observaciones, Double variacionHistoricaPorcentaje, boolean superaVariacion25) {
    }

    public record TestResponse(
            String id, String protocoloId, String ordenTrabajo, String equipoCodigo, String tipoPrueba,
            String ubicacion, String fechaHora, String estadoPrueba, String motivoNoRealizada,
            String resultado, String instrumentoId, String instrumento, String instrumentoSerie,
            String instrumentoCalibracion, boolean calibracionVigente, String temperaturaAmbiente,
            String humedad, List<Map<String, Object>> mediciones, String observaciones,
            Double variacionHistoricaPorcentaje, boolean superaVariacion25) {
    }

    public record FormalizeRequest(@NotBlank String usuario) {
    }

    public record ActaRequest(
            String id, @NotBlank String visitaId, String ordenTrabajo, String cliente, String instalacion,
            String fechaEmision, String tecnicoEntrega, String recibidoPor, boolean marcaPendienteFormalizacion,
            String resumenProtocolos, String syncStatus) {
    }

    public record ActaResponse(
            String id, String visitaId, String ordenTrabajo, String cliente, String instalacion, String fechaEmision,
            String tecnicoEntrega, String recibidoPor, boolean marcaPendienteFormalizacion,
            String resumenProtocolos, String syncStatus) {
        static ActaResponse from(ActaEntity entity) {
            return new ActaResponse(entity.getId(), entity.getVisitaId(), entity.getOrdenTrabajo(), entity.getCliente(),
                    entity.getInstalacion(), entity.getFechaEmision(), entity.getTecnicoEntrega(),
                    entity.getRecibidoPor(), entity.isMarcaPendienteFormalizacion(),
                    entity.getResumenProtocolos(), entity.getSyncStatus());
        }
    }
}
