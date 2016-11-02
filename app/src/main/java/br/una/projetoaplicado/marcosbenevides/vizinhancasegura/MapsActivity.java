package br.una.projetoaplicado.marcosbenevides.vizinhancasegura;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.*;
import android.content.*;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.classes.Alerta;
import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.requisicoesWS.RetrofitService;
import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.requisicoesWS.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SearchView.OnQueryTextListener,
        GoogleMap.OnCircleClickListener {

    private static final String TAG = "ERRO: ";
    private static final String TAG2 = "DEU CERTO: ";
    private GoogleMap mMap;
    private Boolean inicio = true;
    private static final int MY_PERMISSION_LOCATION = 0;
    GoogleApiClient mGoogleApiClient = null;
    Location inicialLocation = null;
    Intent it;
    boolean marcar = false;
    LatLng latLngMarcar;
    SearchView barraProcurar;
    Circle circulo;
    Calendar hora;
    NumberFormat formato;
    private ProgressDialog dialogo;
    private AlertDialog.Builder alertDialog;

    //Alerta alerta;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /*ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSION_LOCATION);*/

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        barraProcurar = (SearchView) findViewById(R.id.barraProcurar);
        barraProcurar.setOnQueryTextListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        formato = new DecimalFormat("00");


    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //está aqui porque implementou SearchView.OnQueryTextListener
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        List<Address> locaisList = null;
        if (query != null && !query.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                locaisList = geocoder.getFromLocationName(query, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address;
            try {
                address = locaisList.get(0);
            } catch (IndexOutOfBoundsException e) {
                Context context = getApplicationContext();
                CharSequence text = "Local digitado não existe!";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                return false;
            }

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }

        return false;
    }

    public void dados(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> address = null;
        try {
            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cidade = "";
        String bairro = "";
        String estado = "";
        try {
            cidade = address.get(0).getLocality();
            bairro = address.get(0).getSubLocality();
            estado = address.get(0).getAdminArea();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        Log.e("ESTADO", " " + estado);
        Log.e("CIDADE", " " + cidade);
        Log.e("BAIRRO", " " + bairro);
        String lat = "" + latLng.latitude;
        Log.e("LATITUDE", lat);
        String slong = "" + latLng.longitude;
        Log.e("LONGITUDE", slong);

        hora = Calendar.getInstance();
        String horario = "" + hora.get(Calendar.YEAR) + "-" + formato.format((hora.get(Calendar.MONTH) + 1))
                + "-" + formato.format(hora.get(Calendar.DAY_OF_MONTH)) + " "
                + formato.format(hora.get(Calendar.HOUR_OF_DAY)) + ":" + formato.format(hora.get(Calendar.MINUTE)) +
                ":" + formato.format(hora.get(Calendar.SECOND));
        Log.e("HORA: ", horario);

        //alerta = new Alerta(estado, cidade, bairro, lat, slong, horario);

        //Log.e("TIPO ALERTA: ", alerta.toString());
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            dados(latLngMarcar);
            mMap.addMarker(new MarkerOptions().position(latLngMarcar).title("LUGAR BOM")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            circulo = mMap.addCircle(new CircleOptions()
                    .center(latLngMarcar)
                    .radius(300)
                    .strokeWidth(2)
                    .strokeColor(0xff4682B4)
                    .fillColor(0x504682B4)
                    .clickable(true));
        } else if (resultCode == 2) {
            dados(latLngMarcar);
            mMap.addMarker(new MarkerOptions().position(latLngMarcar).title("LUGAR RUIM"));
        }
    }

    public void onCircleClick(Circle circle) {
        circulo.setFillColor(0x50008000);
        Log.e("XWXW", " " + "CLICK CIRCLE");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        it = new Intent(this, ActTelaParaMarcar.class);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                marcar = true;
                latLngMarcar = point;
                it.putExtra("TESTE", "teste aqui"); // trecho apenas para um teste de passagem dos parametros
                startActivityForResult(it, 0);
            }
        }); //chama a outra janela

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            if (inicio) {
                onConnected(Bundle.EMPTY);
            }
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION);
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // We are not connected anymore!
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // We tried to connect but failed!
    }

    public void onConnected(Bundle connectionHint) { // passa a posicao inicial
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && inicio) {
            inicialLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (inicialLocation != null) {
                LatLng lugar = new LatLng(inicialLocation.getLatitude(), inicialLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lugar, 14));

                Geocoder geocoder = new Geocoder(this);
                List<Address> address = null;
                try {
                    address = geocoder.getFromLocation(inicialLocation.getLatitude(), inicialLocation.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String cidade = "";
                String bairro = "";
                String estado = "";
                try {
                    cidade = address.get(0).getLocality();
                    bairro = address.get(0).getSubLocality();
                    estado = address.get(0).getAdminArea();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                //enviar para o banco os dados a seguir mais a lat e a long
                final String bairro2 = bairro;
                final String cidade2 = cidade;
                final String estado2 = estado;
                Log.e("CIDADE", " " + cidade);
                Log.e("BAIRRO", " " + bairro);
                Log.e("ESTADO", " " + estado);
                inicio = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogo = ProgressDialog.show(MapsActivity.this, "Buscando dados no servidor ... ", "Favor Aguardar!");
                            }
                        });
                        String latitudeEnviar = "" + inicialLocation.getLatitude();
                        String longitudeEnviar = "" + inicialLocation.getLongitude();
                        RetrofitService service = ServiceGenerator.createService(RetrofitService.class);
                        Call<Alerta> alertas = service.consultaAlerta(bairro2, cidade2, estado2);
                        alertas.enqueue(new Callback<Alerta>() {
                            @Override
                            public void onResponse(Call<Alerta> call, Response<Alerta> response) {
                                if (!response.isSuccessful()) {
                                    dialogo.dismiss();
                                    Log.e(TAG, response.message());
                                } else {
                                    dialogo.dismiss();
                                    Log.e(TAG2, response.body().toString());
                                }
                            }

                            @Override
                            public void onFailure(Call<Alerta> call, Throwable t) {
                                dialogo.dismiss();

                                alertDialog = new AlertDialog.Builder(MapsActivity.this)
                                        .setMessage("Impossível conectar ao servidor!")
                                        .setCancelable(true)
                                        .setPositiveButton("OK", null);
                                alertDialog.create();
                                alertDialog.show();
                                Log.e(TAG, "Falha: " + t.getMessage());
                            }
                        });

                    }
                });

            }
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}