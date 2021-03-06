package br.una.zisc.entidades;


import java.util.Date;

public class Alerta implements java.io.Serializable {

    private int id;
    private Usuario usuario;
    private Date logHora;
    private String longitude;
    private String latitude;
    private String bairro = "Sem bairro";
    private String cidade = "Sem cidade";
    private String estado = "Sem estado";
    private String observacao;
    private String tipo;
    private Boolean ePositivo;
    private boolean ativo;

    public Alerta(Alerta alerta) {
        try {
            alerta.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates and returns a copy of this {@code Object}. The default
     * implementation returns a so-called "shallow" copy: It creates a new
     * instance of the same class and then copies the field values (including
     * object references) from this instance to the new instance. A "deep" copy,
     * in contrast, would also recursively clone nested objects. A subclass that
     * needs to implement this kind of cloning should call {@code super.clone()}
     * to create the new instance and then create deep copies of the nested,
     * mutable objects.
     *
     * @return a copy of this object.
     * @throws CloneNotSupportedException if this object's class does not implement the {@code
     *                                    Cloneable} interface.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Alerta(Date logHora, String longitude, String latitude, String bairro, String cidade, String estado, String observacao, String tipo, Boolean ePositivo) {
        this.logHora = logHora;
        this.longitude = longitude;
        this.latitude = latitude;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.observacao = observacao;
        this.tipo = tipo;
        this.ePositivo = ePositivo;
    }

    public Alerta(int id, Date data, String latitude, String longitude, String bairro, String cidade, String estado, String observacao, String tipo, Boolean ePositivo) {
        this.id = id;
        this.logHora = data;
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
        this.ativo = true;
        this.ePositivo = ePositivo;
    }

    public Alerta(int id, Usuario usuario, Date logHora, String latitude, String longitude, String bairro, String cidade, String estado, String observacao, String tipo, boolean ativo) {
        this.id = id;
        this.usuario = usuario;
        this.logHora = logHora;
        this.longitude = longitude;
        this.latitude = latitude;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.observacao = observacao;
        this.tipo = tipo;
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Alerta{" + "id=" + id + ", logHora=" + logHora + ", longitude=" + longitude + ", latitude=" + latitude + ", bairro=" + bairro + ", cidade=" + cidade + ", estado=" + estado + ", observacao=" + observacao + ", tipo=" + tipo + ", ativo=" + ativo + '}';
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getLogHora() {
        return this.logHora;
    }

    public void setLogHora(){
        this.logHora = new Date(System.currentTimeMillis());
    }

    public void setLogHora(Date logHora) {
        this.logHora = logHora;
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

    public boolean isAtivo() {
        return this.ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getePositivo() {
        return ePositivo;
    }

    public void setePositivo(Boolean ePositivo) {
        this.ePositivo = ePositivo;
    }
}
