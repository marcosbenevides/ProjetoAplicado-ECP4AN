package br.una.zisc.entidades;

/**
 * Created by Marcos Benevides on 03/11/2017.
 */

public class DptoPolicia {

    private long id;
    private String nome, endereco, numero, bairro, cidade, ddd, telefone, email, site, baseFixa, latitude, longitude;

    @Override
    public String toString() {
        return "DptoPolicia{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", numero='" + numero + '\'' +
                ", bairro='" + bairro + '\'' +
                ", cidade='" + cidade + '\'' +
                ", ddd='" + ddd + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", site='" + site + '\'' +
                ", baseFixa='" + baseFixa + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }

    public DptoPolicia(String nome, String endereco, String numero, String bairro, String cidade, String ddd, String telefone, String email, String site, String baseFixa, String latitude, String longitude) {
        this.nome = nome;
        this.endereco = endereco;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.ddd = ddd;
        this.telefone = telefone;
        this.email = email;
        this.site = site;
        this.baseFixa = baseFixa;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getBaseFixa() {
        return baseFixa;
    }

    public void setBaseFixa(String baseFixa) {
        this.baseFixa = baseFixa;
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
}
