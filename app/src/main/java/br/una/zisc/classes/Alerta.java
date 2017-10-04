package br.una.zisc.classes;

import java.sql.Date;
import java.sql.Timestamp;

public class Alerta implements java.io.Serializable {

    private int idalerta;
    private Usuario usuario;
    private Timestamp loghora;
    private String longitude;
    private String latitude;
    private String bairro = "Sem bairro";
    private String cidade = "Sem cidade";
    private String estado = "Sem estado";
    private String observacao;
    private String tipo;
    private Boolean epositivo;
    private boolean statusAtivo;

    public Alerta() {
    }

    public Alerta(int idalerta, Timestamp data,String latitude, String longitude, String bairro, String cidade, String estado, String observacao, String tipo, Boolean epositivo) {
        this.idalerta = idalerta;
        this.loghora = data;
        this.longitude = longitude;
        this.latitude = latitude;
        if(bairro != null) {
            this.bairro = bairro;
        }
        if(cidade != null){
            this.cidade = cidade;
        }
        if(estado != null) {
            this.estado = estado;
        }
        this.observacao = observacao;
        this.tipo = tipo;
        this.statusAtivo = true;
        this.epositivo = epositivo;
    }

    public Alerta(int idalerta, Usuario usuario, Timestamp loghora, String latitude, String longitude, String bairro, String cidade, String estado, String observacao, String tipo, boolean statusAtivo) {
        this.idalerta = idalerta;
        this.usuario = usuario;
        this.loghora = loghora;
        this.longitude = longitude;
        this.latitude = latitude;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.observacao = observacao;
        this.tipo = tipo;
        this.statusAtivo = statusAtivo;
    }

    @Override
    public String toString() {
        return "Alerta{" + "idalerta=" + idalerta +"}";// + ", loghora=" + loghora + ", longitude=" + longitude + ", latitude=" + latitude + ", bairro=" + bairro + ", cidade=" + cidade + ", estado=" + estado + ", observacao=" + observacao + ", tipo=" + tipo + ", statusAtivo=" + statusAtivo + '}';
    }


    public int getIdalerta() {
        return this.idalerta;
    }

    public void setIdalerta(int idalerta) {
        this.idalerta = idalerta;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Timestamp getLoghora() {
        return this.loghora;
    }

    public void setLoghora(Timestamp loghora) {
        this.loghora = loghora;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getBairro() {
        return this.bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return this.cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacao() {
        return this.observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isStatusAtivo() {
        return this.statusAtivo;
    }

    public void setStatusAtivo(boolean statusAtivo) {
        this.statusAtivo = statusAtivo;
    }

    public Boolean getEpositivo() {
        return epositivo;
    }

    public void setEpositivo(Boolean epositivo) {
        this.epositivo = epositivo;
    }
}
