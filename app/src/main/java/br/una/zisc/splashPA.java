package br.una.zisc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import br.una.projetoaplicado.marcosbenevides.zisc.R;

public class splashPA extends Activity {

    //tempo da splash screen
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_p);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splashPA.this,LoginActivity.class);
                startActivity(intent);

                finish();
            }
        },SPLASH_TIME_OUT);

    }
}
