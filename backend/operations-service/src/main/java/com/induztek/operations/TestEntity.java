package com.induztek.operations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pruebas")
public class TestEntity {
    @Id
    private String id;
    private String protocoloId;
    private String ordenTrabajo;
    private String equipoCodigo;
    private String tipoPrueba;
    private String ubicacion;
    private String fechaHora;
    private String estadoPrueba;
    @Column(columnDefinition = "text")
    private String motivoNoRealizada;
    private String resultado;
    private String instrumentoId;
    private String instrumento;
    private String instrumentoSerie;
    private String instrumentoCalibracion;
    private boolean calibracionVigente;
    private String temperaturaAmbiente;
    private String humedad;
    @Column(columnDefinition = "longtext")
    private String medicionesJson;
    @Column(columnDefinition = "text")
    private String observaciones;
    private Double variacionHistoricaPorcentaje;
    private boolean superaVariacion25;

    protected TestEntity() {
    }

    public TestEntity(OperationsController.TestRequest request, com.fasterxml.jackson.databind.ObjectMapper mapper) {
        id = request.id() == null || request.id().isBlank() ? java.util.UUID.randomUUID().toString() : request.id();
        protocoloId = request.protocoloId();
        ordenTrabajo = request.ordenTrabajo();
        equipoCodigo = request.equipoCodigo();
        tipoPrueba = request.tipoPrueba();
        ubicacion = request.ubicacion();
        fechaHora = request.fechaHora();
        estadoPrueba = request.estadoPrueba();
        motivoNoRealizada = request.motivoNoRealizada();
        resultado = request.resultado();
        instrumentoId = request.instrumentoId();
        instrumento = request.instrumento();
        instrumentoSerie = request.instrumentoSerie();
        instrumentoCalibracion = request.instrumentoCalibracion();
        calibracionVigente = request.calibracionVigente();
        temperaturaAmbiente = request.temperaturaAmbiente();
        humedad = request.humedad();
        medicionesJson = ProtocolEntity.serialize(mapper, request.mediciones());
        observaciones = request.observaciones();
        variacionHistoricaPorcentaje = request.variacionHistoricaPorcentaje();
        superaVariacion25 = request.superaVariacion25();
    }

    public String getId() { return id; }
    public String getProtocoloId() { return protocoloId; }
    public String getOrdenTrabajo() { return ordenTrabajo; }
    public String getEquipoCodigo() { return equipoCodigo; }
    public String getTipoPrueba() { return tipoPrueba; }
    public String getUbicacion() { return ubicacion; }
    public String getFechaHora() { return fechaHora; }
    public String getEstadoPrueba() { return estadoPrueba; }
    public String getMotivoNoRealizada() { return motivoNoRealizada; }
    public String getResultado() { return resultado; }
    public String getInstrumentoId() { return instrumentoId; }
    public String getInstrumento() { return instrumento; }
    public String getInstrumentoSerie() { return instrumentoSerie; }
    public String getInstrumentoCalibracion() { return instrumentoCalibracion; }
    public boolean isCalibracionVigente() { return calibracionVigente; }
    public String getTemperaturaAmbiente() { return temperaturaAmbiente; }
    public String getHumedad() { return humedad; }
    public String getMedicionesJson() { return medicionesJson; }
    public String getObservaciones() { return observaciones; }
    public Double getVariacionHistoricaPorcentaje() { return variacionHistoricaPorcentaje; }
    public boolean isSuperaVariacion25() { return superaVariacion25; }
}
