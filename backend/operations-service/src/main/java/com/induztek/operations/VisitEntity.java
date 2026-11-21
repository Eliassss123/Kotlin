package com.induztek.operations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visitas")
public class VisitEntity {
    @Id
    private String id;
    @Column(nullable = false)
    private String ordenTrabajo;
    private String fecha;
    private String horaInicio;
    private String cliente;
    private String instalacion;
    private String modalidad;
    private String ingenieroACargo;
    @Column(columnDefinition = "text")
    private String tecnicos;
    private String temperaturaAmbiente;
    private String humedad;
    private boolean cerrada;
    private String syncStatus;

    protected VisitEntity() {
    }

    public VisitEntity(OperationsController.VisitRequest request) {
        id = request.id() == null || request.id().isBlank() ? java.util.UUID.randomUUID().toString() : request.id();
        ordenTrabajo = request.ordenTrabajo();
        fecha = request.fecha();
        horaInicio = request.horaInicio();
        cliente = request.cliente();
        instalacion = request.instalacion();
        modalidad = request.modalidad();
        ingenieroACargo = request.ingenieroACargo();
        tecnicos = request.tecnicos();
        temperaturaAmbiente = request.temperaturaAmbiente();
        humedad = request.humedad();
        cerrada = false;
        syncStatus = request.syncStatus() == null ? "PENDIENTE" : request.syncStatus();
    }

    public String getId() { return id; }
    public String getOrdenTrabajo() { return ordenTrabajo; }
    public String getFecha() { return fecha; }
    public String getHoraInicio() { return horaInicio; }
    public String getCliente() { return cliente; }
    public String getInstalacion() { return instalacion; }
    public String getModalidad() { return modalidad; }
    public String getIngenieroACargo() { return ingenieroACargo; }
    public String getTecnicos() { return tecnicos; }
    public String getTemperaturaAmbiente() { return temperaturaAmbiente; }
    public String getHumedad() { return humedad; }
    public boolean isCerrada() { return cerrada; }
    public String getSyncStatus() { return syncStatus; }
    public void close() { cerrada = true; }
}
