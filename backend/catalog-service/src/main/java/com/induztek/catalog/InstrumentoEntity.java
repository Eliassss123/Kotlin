package com.induztek.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "instrumentos")
public class InstrumentoEntity {
    @Id
    private String id;
    @Column(nullable = false)
    private String tipo;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private String vencimientoCalibracion;
    private boolean activo;

    protected InstrumentoEntity() {
    }

    public InstrumentoEntity(String id, String tipo, String marca, String modelo, String numeroSerie,
            String vencimientoCalibracion, boolean activo) {
        this.id = id;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.numeroSerie = numeroSerie;
        this.vencimientoCalibracion = vencimientoCalibracion;
        this.activo = activo;
    }

    public String getId() { return id; }
    public String getTipo() { return tipo; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getNumeroSerie() { return numeroSerie; }
    public String getVencimientoCalibracion() { return vencimientoCalibracion; }
    public boolean isActivo() { return activo; }
    public void setVencimientoCalibracion(String value) { vencimientoCalibracion = value; }
    public void setActivo(boolean value) { activo = value; }
}
