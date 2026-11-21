package com.induztek.operations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "actas")
public class ActaEntity {
    @Id
    private String id;
    private String visitaId;
    private String ordenTrabajo;
    private String cliente;
    private String instalacion;
    private String fechaEmision;
    private String tecnicoEntrega;
    private String recibidoPor;
    private boolean marcaPendienteFormalizacion;
    @Column(columnDefinition = "text")
    private String resumenProtocolos;
    private String syncStatus;

    protected ActaEntity() {
    }

    public ActaEntity(OperationsController.ActaRequest request) {
        id = request.id() == null || request.id().isBlank() ? java.util.UUID.randomUUID().toString() : request.id();
        visitaId = request.visitaId();
        ordenTrabajo = request.ordenTrabajo();
        cliente = request.cliente();
        instalacion = request.instalacion();
        fechaEmision = request.fechaEmision();
        tecnicoEntrega = request.tecnicoEntrega();
        recibidoPor = request.recibidoPor();
        marcaPendienteFormalizacion = request.marcaPendienteFormalizacion();
        resumenProtocolos = request.resumenProtocolos();
        syncStatus = request.syncStatus() == null ? "PENDIENTE" : request.syncStatus();
    }

    public String getId() { return id; }
    public String getVisitaId() { return visitaId; }
    public String getOrdenTrabajo() { return ordenTrabajo; }
    public String getCliente() { return cliente; }
    public String getInstalacion() { return instalacion; }
    public String getFechaEmision() { return fechaEmision; }
    public String getTecnicoEntrega() { return tecnicoEntrega; }
    public String getRecibidoPor() { return recibidoPor; }
    public boolean isMarcaPendienteFormalizacion() { return marcaPendienteFormalizacion; }
    public String getResumenProtocolos() { return resumenProtocolos; }
    public String getSyncStatus() { return syncStatus; }
}
