package com.induztek.operations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "protocolos")
public class ProtocolEntity {
    @Id
    private String id;
    @Column(nullable = false, unique = true)
    private String codigoProtocolo;
    @Column(nullable = false)
    private String visitaId;
    private String ordenTrabajo;
    @Column(nullable = false)
    private String equipoCodigo;
    private String tipoEquipo;
    private String fechaHora;
    private String estado;
    private String resultadoGlobal;
    private String tecnico;
    private String tecnicoCreador;
    @Column(columnDefinition = "text")
    private String descripcionMantenimiento;
    private String instrumento;
    private boolean calibracionVigente;
    private String temperaturaAmbiente;
    private String humedad;
    private boolean actaEntregadaCliente;
    private String formalizadoPor;
    private String fechaFormalizacion;
    private String syncStatus;
    @Column(columnDefinition = "text")
    private String observaciones;
    @Column(columnDefinition = "longtext")
    private String pruebasJson;
    @Column(columnDefinition = "longtext")
    private String medicionesJson;
    @Column(columnDefinition = "longtext")
    private String fotosJson;

    protected ProtocolEntity() {
    }

    public ProtocolEntity(OperationsController.ProtocolRequest request, com.fasterxml.jackson.databind.ObjectMapper mapper) {
        id = request.id() == null || request.id().isBlank() ? java.util.UUID.randomUUID().toString() : request.id();
        codigoProtocolo = request.codigoProtocolo();
        visitaId = request.visitaId();
        ordenTrabajo = request.ordenTrabajo();
        equipoCodigo = request.equipoCodigo();
        tipoEquipo = request.tipoEquipo();
        fechaHora = request.fechaHora();
        estado = request.estado() == null ? "REGISTRADO_TERRENO" : request.estado();
        resultadoGlobal = request.resultadoGlobal();
        tecnico = request.tecnico();
        tecnicoCreador = request.tecnicoCreador();
        descripcionMantenimiento = request.descripcionMantenimiento();
        instrumento = request.instrumento();
        calibracionVigente = request.calibracionVigente();
        temperaturaAmbiente = request.temperaturaAmbiente();
        humedad = request.humedad();
        actaEntregadaCliente = request.actaEntregadaCliente();
        formalizadoPor = request.formalizadoPor();
        fechaFormalizacion = request.fechaFormalizacion();
        syncStatus = request.syncStatus() == null ? "PENDIENTE" : request.syncStatus();
        observaciones = request.observaciones();
        pruebasJson = serialize(mapper, request.pruebas());
        medicionesJson = serialize(mapper, request.mediciones());
        fotosJson = serialize(mapper, request.fotos());
    }

    static String serialize(com.fasterxml.jackson.databind.ObjectMapper mapper, Object value) {
        try {
            return mapper.writeValueAsString(value == null ? java.util.List.of() : value);
        } catch (com.fasterxml.jackson.core.JsonProcessingException exception) {
            throw new IllegalArgumentException("Could not serialize protocol data", exception);
        }
    }

    public String getId() { return id; }
    public String getCodigoProtocolo() { return codigoProtocolo; }
    public String getVisitaId() { return visitaId; }
    public String getOrdenTrabajo() { return ordenTrabajo; }
    public String getEquipoCodigo() { return equipoCodigo; }
    public String getTipoEquipo() { return tipoEquipo; }
    public String getFechaHora() { return fechaHora; }
    public String getEstado() { return estado; }
    public String getResultadoGlobal() { return resultadoGlobal; }
    public String getTecnico() { return tecnico; }
    public String getTecnicoCreador() { return tecnicoCreador; }
    public String getDescripcionMantenimiento() { return descripcionMantenimiento; }
    public String getInstrumento() { return instrumento; }
    public boolean isCalibracionVigente() { return calibracionVigente; }
    public String getTemperaturaAmbiente() { return temperaturaAmbiente; }
    public String getHumedad() { return humedad; }
    public boolean isActaEntregadaCliente() { return actaEntregadaCliente; }
    public String getFormalizadoPor() { return formalizadoPor; }
    public String getFechaFormalizacion() { return fechaFormalizacion; }
    public String getSyncStatus() { return syncStatus; }
    public String getObservaciones() { return observaciones; }
    public String getPruebasJson() { return pruebasJson; }
    public String getMedicionesJson() { return medicionesJson; }
    public String getFotosJson() { return fotosJson; }
    public void formalize(String user, String date) { formalizadoPor = user; fechaFormalizacion = date; estado = "FORMALIZADO"; }
}
