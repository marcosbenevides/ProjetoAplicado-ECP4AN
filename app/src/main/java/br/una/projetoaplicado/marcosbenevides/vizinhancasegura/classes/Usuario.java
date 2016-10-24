package br.una.projetoaplicado.marcosbenevides.vizinhancasegura.classes;


/**
 * Created by marcos.benevides on 21/10/2016.
 */

public class Usuario {

    private String nome;
    private String email;


    public Usuario() {
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}