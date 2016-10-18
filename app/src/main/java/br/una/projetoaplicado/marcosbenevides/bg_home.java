package br.una.projetoaplicado.marcosbenevides;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.R;

public class bg_home extends Activity implements SurfaceHolder.Callback {

    private MediaPlayer mp = null;
    SurfaceView mSurfaceView = null;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.bg_video);

        try {
            mp.setDataSource(String.valueOf(video));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Get the dimensions of the video
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();

        //Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        //Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();

        //Set the width of the SurfaceView to the width of the screen
        lp.width = screenWidth;

        //Set the height of the SurfaceView to match the aspect ratio of the video
        //be sure to cast these as floats otherwise the calculation will likely be 0
        lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);

        //Commit the layout parameters
        mSurfaceView.setLayoutParams(lp);

        //Start video
        mp.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mp = new MediaPlayer();
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);

    }
}
