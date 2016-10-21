package br.una.projetoaplicado.marcosbenevides.vizinhancasegura.operacoes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.classes.Usuario;

/**
 * Created by marcos.benevides on 21/10/2016.
 */

public class OperacoesGerais {

    public Usuario getInformacao(String end) {
        String json;
        Usuario retorno;
        json = OperacoesWS.getJSONfromWS(end);
        Log.i("Resultado", json);
        retorno = parseJson(json);

        return retorno;
    }

    private Usuario parseJson(String json) {
        try {
            Usuario pessoa = new Usuario();

            JSONObject jsonObj = new JSONObject(json);
            JSONArray array = jsonObj.getJSONArray("results");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date data;

            JSONObject objArray = array.getJSONObject(0);

            JSONObject obj = objArray.getJSONObject("user");
            //Atribui os objetos que estão nas camadas mais altas
            pessoa.setEmail(obj.getString("email"));
            data = new Date(obj.getLong("dob") * 1000);

            //Nome da pessoa é um objeto, instancia um novo JSONObject
            JSONObject nome = obj.getJSONObject("name");
            pessoa.setNome(nome.getString("first"));

            //Endereco tambem é um Objeto
            JSONObject endereco = obj.getJSONObject("location");

            //Imagem eh um objeto
            JSONObject foto = obj.getJSONObject("picture");

            return pessoa;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
