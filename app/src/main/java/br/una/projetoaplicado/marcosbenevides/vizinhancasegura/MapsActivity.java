package br.una.projetoaplicado.marcosbenevides.vizinhancasegura;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.classes.Alerta;
import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.mapaUtil.Marcador;
import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.requisicoesWS.RetrofitService;
import br.una.projetoaplicado.marcosbenevides.vizinhancasegura.requisicoesWS.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SearchView.OnQueryTextListener {

    private static final String TAG = "ERRO: ";
    private static final String TAG2 = "DEU CERTO: ";
    private GoogleMap mMap;
    private Boolean inicio = true;
    private static final int MY_PERMISSION_LOCATION = 0;
    private GoogleApiClient mGoogleApiClient = null;
    private Location inicialLocation = null;
    private Intent it;
    private Boolean marcar = false;
    private LatLng latLngMarcar;
    private SearchView barraProcurar;
    private Circle circulo;
    private Calendar hora;
    private NumberFormat formato;
    private ProgressDialog dialogo;
    private AlertDialog.Builder alertDialog, infoDialog, addMarkerDialog;
    private AlertDialog informacao,cadastro;
    private TextView textTipoAlertaCont, textDataHoraCont, textOcorrenciaCont;
    private List<Alerta> listaTeste = new ArrayList<>();
    private Switch switchPositivo;
    private EditText editorOcorrencia;
    private Spinner spinnerTipo;


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
            buscaPontos(latLng);
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
            mMap.addMarker(new MarkerOptions().position(latLngMarcar).title("LUGAR BOM").snippet("a\nb")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        } else if (resultCode == 2) {
            dados(latLngMarcar);
            mMap.addMarker(new MarkerOptions().position(latLngMarcar).title("LUGAR RUIM").snippet("a\nb"));
        }

        chuvadeListeners(latLngMarcar);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        it = new Intent(this, ActTelaParaMarcar.class);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                latLngMarcar = point;
                addMarkerDialog = new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("ADICIONAR ALERTA")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                LayoutInflater layoutInflater = MapsActivity.this.getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.addmarkerview, null);
                addMarkerDialog.setView(dialogView);

                switchPositivo = (Switch) dialogView.findViewById(R.id.switchTipo);
                editorOcorrencia = (EditText) dialogView.findViewById(R.id.editOcorrencia);
                spinnerTipo = (Spinner) dialogView.findViewById(R.id.spinnerTipo);
                List<String> lista = new ArrayList<String>();
                lista.add("1");
                lista.add("2");
                lista.add("3");

                ArrayAdapter <String> adapter = new ArrayAdapter<>(MapsActivity.this,android.R.layout.simple_list_item_1,lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                spinnerTipo.setAdapter(adapter);

                if(switchPositivo.isPressed()){

                }

                cadastro = addMarkerDialog.create();
                cadastro.show();

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

                buscaPontos(new LatLng(inicialLocation.getLatitude(), inicialLocation.getLongitude()));
            }
        }
    }

    public void buscaPontos(final LatLng ponto) {
        new Thread(new Runnable() { //por causa do Call, precisa pra rodar ele
            @Override
            public void run() { // vai rodar aqui qndo der o Start la em baixo
                runOnUiThread(new Runnable() { // O dialogo tem que ser na tread da interface, por isso
                    @Override
                    public void run() {
                        dialogo = ProgressDialog.show(MapsActivity.this, "Buscando dados no servidor ... ", "Favor Aguardar!");
                    }
                });

                String latitudeEnviar = "" + ponto.latitude;
                String longitudeEnviar = "" + ponto.longitude;

                RetrofitService service = ServiceGenerator.createService(RetrofitService.class); // inicia o gerador de servico/cria conexao com o server
                Call<List<Alerta>> alertas = service.consultaAlerta(latitudeEnviar, longitudeEnviar); // acessa os metodos do retrofit <<<LEMBRAR alterar

                alertas.enqueue(new Callback<List<Alerta>>() { // aqui que vai no servidor, precisa ser em outra tread
                    @Override
                    public void onResponse(Call<List<Alerta>> call, Response<List<Alerta>> response) { // resposta do server
                        if (!response.isSuccessful()) {
                            dialogo.dismiss();
                            Log.e(TAG, response.message());
                        } else {
                            dialogo.dismiss();
                            Log.e(TAG2, response.body().toString()); // aqui vai receber os dados, tem que tratar ainda
                            Marcador marcador = new Marcador();
                            choveMarcador(marcador.setReferencia(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Alerta>> call, Throwable t) { // se for aqui, falhou a conexao com o server
                        dialogo.dismiss();

                        alertDialog = new AlertDialog.Builder(MapsActivity.this)
                                .setMessage("Impossível conectar ao servidor!")
                                .setCancelable(true)
                                .setPositiveButton("OK", null);
                        alertDialog.create();
                        alertDialog.show();
                        Log.e(TAG, "Falha: " + t.getMessage());
                        teste();
                    }
                });
            }
        }).start();
    }

    public void choveMarcador(List<Marcador> lista) {
        Log.e("CHOVE MARCADOR", "1");
        Marker marker;
        for (int i = 0; i < lista.size(); i++) {
            LatLng latLng = new LatLng(Double.parseDouble(lista.get(i).getAlerta().getLatitude()), Double.parseDouble(lista.get(i).getAlerta().getLongitude()));
            if (lista.get(i).getAlerta().getePositivo()) {
                Log.e("CHOVE MARCADOR", "2");
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("LUGAR BOM")
                        .snippet((lista.get(i).getMarcadorList().size() + 1) + "")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            } else {
                Log.e("CHOVE MARCADOR", "3");
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("LUGAR RUIM")
                        .snippet(String.valueOf(lista.get(i).getMarcadorList().size() + 1)));
            }
            lista.get(i).setMarcador(marker);
            chuvadeListeners(latLng);
        }
    }

    public void chuvadeListeners(LatLng latLng) {

        circulo = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(100)
                .strokeWidth(2)
                .strokeColor(0xff4682B4)
                .fillColor(0x504682B4)
                .clickable(true));


        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                circle.setFillColor(0x50008000);
                Log.e("XWXW", " " + "CLICK CIRCLE");
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {


            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                System.out.println("0...");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    System.out.println("1...");
                    infoDialog = new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("INFO")
                            .setPositiveButton("OK", null);
                    LayoutInflater layoutInflater = MapsActivity.this.getLayoutInflater();
                    View dialogView = layoutInflater.inflate(R.layout.info_dialog, null);
                    infoDialog.setView(dialogView);

                    textTipoAlertaCont = (TextView) dialogView.findViewById(R.id.textTipoAlertaCont);
                    textDataHoraCont = (TextView) dialogView.findViewById(R.id.textDataHoraCont);
                    textOcorrenciaCont = (TextView) dialogView.findViewById(R.id.textOcorrenciaCont);

                    textOcorrenciaCont.setText("Mussum ipsum cacilds, vidis litro abertis Consetis " +
                            "adipiscings elitis. Pra lá , depois divoltis porris, paradis." +
                            " Paisis, filhis, espiritis santis.");

                    textTipoAlertaCont.setText("Texto do tiopo alerta");
                    textDataHoraCont.setText("Texto do data hora");
                    System.out.println("3...");

                    informacao = infoDialog.create();
                    informacao.show();

                }
            }

        });
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    public void teste() {

        listaTeste.add(new Alerta(1, "-20.065993", "-44.281507", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 2 rapazes pretos dos olhos claros bonitos", "Assalto", true, true));
        listaTeste.add(new Alerta(2, "-20.065900", "-44.281555", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 2 rapazes pretos dos olhos claros bonitos", "Tiroteio", true, true));
        listaTeste.add(new Alerta(3, "-20.065965", "-44.281522", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 2 rapazes pretos dos olhos claros bonitos", "Festa", true, true));
        listaTeste.add(new Alerta(4, "-20.065912", "-44.281566", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 2 rapazes pretos dos olhos claros bonitos", "Policiamento", true, true));
        listaTeste.add(new Alerta(5, "-20.065933", "-44.281512", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 2 rapazes pretos dos olhos claros bonitos", "Corrida Naruto", true, true));
        listaTeste.add(new Alerta(6, "-20.064242", "-44.282150", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 2 rapazes pretos dos olhos claros bonitos", "Corrida Naruto", true, false));
        listaTeste.add(new Alerta(7, "-21.064292", "-49.282120", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 2 rapazes pretos dos olhos claros bonitos", "Corrida Naruto", true, false));
        listaTeste.add(new Alerta(8, "-20.064232", "-44.282110", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 2 rapazes pretos dos olhos claros bonitos", "Corrida Naruto", true, false));
        listaTeste.add(new Alerta(9, "-20.064212", "-44.282190", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 2 rapazes pretos dos olhos claros bonitos", "Corrida Naruto", true, false));

        Marcador marcador = new Marcador();
        List<Marcador> m = marcador.setReferencia(listaTeste);
        for (int i = 0; i < m.size(); i++) {
            Log.e("DEU OU NâO", m.get(i).toString());
        }
        choveMarcador(m);

    }

}