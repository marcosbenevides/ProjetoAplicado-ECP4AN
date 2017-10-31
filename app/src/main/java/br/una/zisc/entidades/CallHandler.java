package br.una.zisc.entidades;

import java.util.Date;

/**
 * Created by Avanti Premium on 30/10/2017.
 */

public class CallHandler {

    private Integer id;
    private Usuario usuario;
    private String latitude, longitude, cidade, estado, bairro;
    private Boolean ativo;
    private Date log;

    public CallHandler(String latitude, String longitude, String cidade, String estado, String bairro, Boolean ativo, Date log) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.cidade = cidade;
        this.estado = estado;
        this.bairro = bairro;
        this.ativo = ativo;
        this.log = log;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    @Override
    public String toString() {
        return "CallHandler{" +
                "id=" + id +
                ", usuario=" + usuario +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                ", ativo=" + ativo +
                ", log=" + log +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Date getLog() {
        return log;
    }

    public void setLog(Date log) {
        this.log = log;
    }
}
