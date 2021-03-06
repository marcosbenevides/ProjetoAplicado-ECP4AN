package br.una.zisc.mapaUtil;

import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import br.una.zisc.entidades.Alerta;

/**
 * Created by Marcos Benevides on 05/11/2016.
 */

public class Marcador {

    private Alerta alerta;
    private Integer idMarcador;
    private List<Alerta> marcadorList = new ArrayList<>();
    Marker marcador;

    public Marcador() {
    }

    public List<Marcador> setReferencia(List<Alerta> lista) {
        List<Marcador> listaM = new ArrayList<>();
        listaM.add(new Marcador(lista.get(0)));
        for (int i = 1; i < lista.size(); i++) {
            boolean controle = true;
            Alerta a = lista.get(i);
            for(int j = 0; j < listaM.size(); j++) {
                Alerta m = listaM.get(j).getAlerta();
                if(distancia2Pontos(a.getLatitude(), a.getLongitude(), m.getLatitude(), m.getLongitude())) {
                    if(listaM.get(j).alerta.getePositivo() && !a.getePositivo()) {
                        Alerta aux = listaM.get(j).alerta;
                        listaM.get(j).alerta = a;
                        listaM.get(j).marcadorList.add(aux);
                    } else {
                        listaM.get(j).marcadorList.add(a);
                    }
                    controle = false;
                    break;
                }
            }
            if(controle)
                listaM.add(new Marcador(a));
        }
        return listaM;
    }

    public Marcador temReferencia(List<Marcador> marcador, Alerta alerta){
        for(int i = 0;i<marcador.size();i++){
            if(distancia2Pontos(marcador.get(i).getAlerta().getLatitude(),
                    marcador.get(i).getAlerta().getLongitude()
                    ,alerta.getLatitude(),
                    alerta.getLongitude())){
                if(marcador.get(i).alerta.getePositivo() && !alerta.getePositivo()) {
                    Alerta aux = marcador.get(i).alerta;
                    marcador.get(i).alerta = alerta;
                    marcador.get(i).marcadorList.add(aux);
                    return marcador.get(i);
                } else {
                    marcador.get(i).marcadorList.add(alerta);
                    return marcador.get(i);
                }
            }
        }
        return null;
    }

    public Marcador procuraMarcador(List<Marcador> marcador, Marker marker){
        for(int i= 0;i<marcador.size();i++){
            if(marker.getId().equals( marcador.get(i).getMarcador().getId())){
                return marcador.get(i);
            }
        }
        return null;
    }


    public Boolean distancia2Pontos(String latA, String longA, String latB, String longB) {

        double earthRadius = 6371;//kilometers
        double dLat = Math.toRadians(Double.parseDouble(latB) - Double.parseDouble(latA));
        double dLng = Math.toRadians(Double.parseDouble(longB) - Double.parseDouble(longA));
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(Double.parseDouble(latA)))
                * Math.cos(Math.toRadians(Double.parseDouble(latB)));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = (earthRadius * c) * 1000;

        Log.e("DISTANCIA", dist+"");

        if(dist <=100){
            return true;
        }
        return false;
    }

    public Marcador( Alerta alerta) {
        this.alerta = alerta;
    }

    public Alerta getAlerta() {
        return alerta;
    }

    public void setAlerta(Alerta alerta) {
        this.alerta = alerta;
    }

    public Integer getIdMarcador() {
        return idMarcador;
    }

    public void setIdMarcador(Integer idMarcador) {
        this.idMarcador = idMarcador;
    }

    public List<Alerta> getMarcadorList() {
        return marcadorList;
    }

    public void setMarcadorList(Alerta marcadorList) {
        this.marcadorList.add(marcadorList);
    }

    public Marker getMarcador() {
        return marcador;
    }

    public void setMarcador(Marker marcador) {
        this.marcador = marcador;
    }

    @Override
    public String toString() {
        return "Marcador{" +
                "alerta=" + alerta +
                ", idMarcador=" + idMarcador +
                ", marcadorList="  + teste2() +
                '}';
    }

    public String teste2() {
        String retorno = "{";
        for(int i = 0; i < marcadorList.size(); i++) {
            retorno += marcadorList.get(i) + "\n";
        }
        return retorno + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Marcador)) return false;

        Marcador marcador = (Marcador) o;

        if (alerta != null ? !alerta.equals(marcador.alerta) : marcador.alerta != null)
            return false;
        return idMarcador != null ? idMarcador.equals(marcador.idMarcador) : marcador.idMarcador == null;

    }

    @Override
    public int hashCode() {
        return 0;
    }
}
