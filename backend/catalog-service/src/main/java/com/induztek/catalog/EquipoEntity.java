package com.induztek.catalog;

import com.induztek.catalog.CatalogController.EquipoRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipos")
public class EquipoEntity {
    @Id
    private String id;
    @Column(nullable = false, unique = true, length = 80)
    private String codigo;
    private String tagCliente;
    @Column(nullable = false)
    private String tipo;
    private String cliente;
    @Column(nullable = false)
    private String ubicacion;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private String potenciaTension;
    private String anio;
    private String tensiones;
    private String tap;
    private String conexion;
    private String ultimoEstado;

    protected EquipoEntity() {
    }

    public EquipoEntity(String id, String codigo, String tagCliente, String tipo, String cliente, String ubicacion,
            String marca, String modelo, String numeroSerie, String potenciaTension, String anio, String tensiones,
            String tap, String conexion, String ultimoEstado) {
        this.id = id;
        this.codigo = codigo;
        this.tagCliente = tagCliente;
        this.tipo = tipo;
        this.cliente = cliente;
        this.ubicacion = ubicacion;
        this.marca = marca;
        this.modelo = modelo;
        this.numeroSerie = numeroSerie;
        this.potenciaTension = potenciaTension;
        this.anio = anio;
        this.tensiones = tensiones;
        this.tap = tap;
        this.conexion = conexion;
        this.ultimoEstado = ultimoEstado;
    }

    public String getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getTagCliente() { return tagCliente; }
    public String getTipo() { return tipo; }
    public String getCliente() { return cliente; }
    public String getUbicacion() { return ubicacion; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getNumeroSerie() { return numeroSerie; }
    public String getPotenciaTension() { return potenciaTension; }
    public String getAnio() { return anio; }
    public String getTensiones() { return tensiones; }
    public String getTap() { return tap; }
    public String getConexion() { return conexion; }
    public String getUltimoEstado() { return ultimoEstado; }

    public void update(EquipoRequest request) {
        codigo = request.codigo();
        tagCliente = request.tagCliente();
        tipo = request.tipo();
        cliente = request.cliente();
        ubicacion = request.ubicacion();
        marca = request.marca();
        modelo = request.modelo();
        numeroSerie = request.numeroSerie();
        potenciaTension = request.potenciaTension();
        anio = request.anio();
        tensiones = request.tensiones();
        tap = request.tap();
        conexion = request.conexion();
        ultimoEstado = request.ultimoEstado();
    }
}
