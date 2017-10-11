package br.una.zisc.security;

import android.util.Base64;

/**
 * Created by Marcos Benevides on 02/09/2017.
 */

public class Security {

    public String criptografar(String var){
        byte[] data = var.getBytes();
        return Base64.encodeToString(data,Base64.DEFAULT);
    }

}
