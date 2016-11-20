package br.una.zisc;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.una.projetoaplicado.marcosbenevides.zisc.R;
import br.una.zisc.classes.Alerta;
import br.una.zisc.mapaUtil.Marcador;
import br.una.zisc.requisicoesWS.RetrofitService;
import br.una.zisc.requisicoesWS.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.DialogInterface.BUTTON_POSITIVE;

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
    private AlertDialog informacao, cadastro;
    private TextView textTipoAlertaCont, textDataHoraCont, textOcorrenciaCont;
    private List<Alerta> listaTeste = new ArrayList<>();
    private final List<String> listaPositiva = new ArrayList<String>();
    private final List<String> listaNegativa = new ArrayList<String>();
    private List<Marcador> mListMarcador = new ArrayList<>(); // lista que armazena todos os objetos do tipo Marcador
    private ToggleButton switchNegPos;
    private EditText editorOcorrencia;
    private Spinner spinnerTipo, spinnerAlerta;
    private String cidade = "", estado = "", bairro = "", emailUsuario = "mariaajp@gmail.com";
    private Integer controle = 0, contFalha = 0;
    private DialogInterface.OnClickListener dialogInterface;


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

        Bundle bundle = getIntent().getExtras();
        Log.e("BUNDLE: ", emailUsuario);
        if (bundle != null) {
            emailUsuario = bundle.getString("EMAIL");
            Log.e("BUNDLE: ", emailUsuario);
        }
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
            Log.e("LATITUDE E LONGITUDE", address.getLatitude() + "/" + address.getLongitude());

            runOnUiThread(new Runnable() { // O dialogo tem que ser na tread da interface, por isso
                @Override
                public void run() {
                    dialogo = ProgressDialog.show(MapsActivity.this, "Buscando dados no servidor", "Por favor, aguarde...");
                }
            });
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

    }

    public void longClickListener() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {


                latLngMarcar = point;
                addMarkerDialog = new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("ADICIONAR ALERTA")
                        .setPositiveButton("Confirmar", dialogInterface = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialogo = ProgressDialog.show(MapsActivity.this, "Aguarde..", "Enviando ao servidor...");
                                            }
                                        });
                                        dados(latLngMarcar);
                                        final Alerta alerta = new Alerta(55,
                                                getDataHoraAgora(),
                                                String.valueOf(latLngMarcar.latitude),
                                                String.valueOf(latLngMarcar.longitude),
                                                bairro,
                                                cidade,
                                                estado,
                                                editorOcorrencia.getText().toString(),
                                                spinnerTipo.getSelectedItem().toString(),
                                                switchNegPos.isChecked());

                                        RetrofitService service = ServiceGenerator.createService(RetrofitService.class); // inicia o gerador de servico/cria conexao com o server
                                        Call<String> call = service.cadastraralerta(
                                                emailUsuario,
                                                alerta.getLoghora(),
                                                alerta.getLatitude(),
                                                alerta.getLongitude(),
                                                alerta.getBairro(),
                                                alerta.getCidade(),
                                                alerta.getEstado(),
                                                alerta.getObservacao(),
                                                alerta.getTipo(),
                                                alerta.getEpositivo()); // acessa os metodos do retrofit <<<LEMBRAR alterar

                                        callCreateMarker(call,alerta);
                                        /*call.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if (!response.isSuccessful()) {
                                                    contFalha++;
                                                    if (contFalha > 3) {
                                                        dialogo.dismiss();
                                                        alertDialog = new AlertDialog.Builder(MapsActivity.this)
                                                                .setMessage("OPS! Algo deu errado.\n" + response.message())
                                                                .setCancelable(true)
                                                                .setPositiveButton("OK", null);
                                                        alertDialog.create();
                                                        alertDialog.show();
                                                    } else {
                                                        call.enqueue();
                                                    }
                                                } else {
                                                    dialogo.dismiss();
                                                    Marcador marcador = new Marcador();
                                                    Marker marker = marcador.temReferencia(mListMarcador, alerta);
                                                    if (marker != null) {
                                                        dialogo.dismiss();
                                                        Toast.makeText(MapsActivity.this, "Alerta adicionado ao marcador selecionado!", Toast.LENGTH_LONG).show();
                                                        marker.showInfoWindow();
                                                    } else {
                                                        marcador.setAlerta(alerta);
                                                        mListMarcador.add(marcador);
                                                        choveMarcador();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                dialogo.dismiss();
                                                alertDialog = new AlertDialog.Builder(MapsActivity.this)
                                                        .setMessage("OPS! Falha ao conectar.\n" + t.getMessage())
                                                        .setCancelable(true)
                                                        .setPositiveButton("OK", null);
                                                alertDialog.create();
                                                alertDialog.show();
                                            }
                                        });
*/
                                    }
                                }).start();

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                LayoutInflater layoutInflater = MapsActivity.this.getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.add_marker_view, null);
                addMarkerDialog.setView(dialogView);

                switchNegPos = (ToggleButton) dialogView.findViewById(R.id.buttonNegPos);
                editorOcorrencia = (EditText) dialogView.findViewById(R.id.editOcorrencia);
                spinnerTipo = (Spinner) dialogView.findViewById(R.id.spinnerTipo);

                listaPositiva.add("Trecho bem iluminado.");
                listaPositiva.add("Local bem movimentado.");
                listaPositiva.add("Local com bom policiamento.");
                listaNegativa.add("Assalto.");
                listaNegativa.add("Trecho mau iluminado.");
                listaNegativa.add("Local deserto.");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MapsActivity.this, android.R.layout.simple_list_item_1, listaNegativa);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                spinnerTipo.setAdapter(adapter);

                switchNegPos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(MapsActivity.this, android.R.layout.simple_list_item_1, listaPositiva);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            spinnerTipo.setAdapter(adapter);
                        } else {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(MapsActivity.this, android.R.layout.simple_list_item_1, listaNegativa);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            spinnerTipo.setAdapter(adapter);
                        }
                    }
                });

                cadastro = addMarkerDialog.create();
                cadastro.show();

            }
        });

    }

    public void callCreateMarker(Call call, final Alerta alerta) {
        call.clone().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    contFalha++;
                    if (contFalha > 5) {
                        contFalha = 0;
                        dialogo.dismiss();
                        alertDialog = new AlertDialog.Builder(MapsActivity.this)
                                .setMessage("OPS! Algo deu errado.\n" + response.message())
                                .setCancelable(true)
                                .setPositiveButton("OK", null);
                        alertDialog.create();
                        alertDialog.show();
                    } else {
                        callCreateMarker(call,alerta);
                    }
                } else {
                    contFalha = 0;
                    dialogo.dismiss();
                    Marcador marcador = new Marcador();
                    Marker marker = marcador.temReferencia(mListMarcador, alerta);
                    if (marker != null) {
                        dialogo.dismiss();
                        Toast.makeText(MapsActivity.this, "Alerta adicionado ao marcador selecionado!", Toast.LENGTH_LONG).show();
                        marker.showInfoWindow();
                    } else {
                        marcador.setAlerta(alerta);
                        mListMarcador.add(marcador);
                        choveMarcador();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                contFalha++;
                if (contFalha > 3) {
                    contFalha = 0;
                    dialogo.dismiss();
                    alertDialog = new AlertDialog.Builder(MapsActivity.this)
                            .setMessage("OPS! Falha ao conectar.\n" + t.getMessage())
                            .setCancelable(true)
                            .setPositiveButton("OK", null);
                    alertDialog.create();
                    alertDialog.show();
                }else{
                    callCreateMarker(call,alerta);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        it = new Intent(this, ActTelaParaMarcar.class);

        longClickListener();

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

                runOnUiThread(new Runnable() { // O dialogo tem que ser na tread da interface, por isso
                    @Override
                    public void run() {
                        dialogo = ProgressDialog.show(MapsActivity.this, "Buscando dados no servidor", "Por favor, aguarde...");
                    }
                });

                buscaPontos(new LatLng(inicialLocation.getLatitude(), inicialLocation.getLongitude()));

                teste();
            }
        }
    }

    public void buscaPontos(final LatLng ponto) {
        new Thread(new Runnable() { //por causa do Call, precisa pra rodar ele
            @Override
            public void run() { // vai rodar aqui qndo der o Start la em baixo

                String latitudeEnviar = "" + ponto.latitude;
                String longitudeEnviar = "" + ponto.longitude;

                RetrofitService service = ServiceGenerator.createService(RetrofitService.class); // inicia o gerador de servico/cria conexao com o server
                Call<List<Alerta>> alertas = service.consultaAlerta(latitudeEnviar, longitudeEnviar); // acessa os metodos do retrofit <<<LEMBRAR alterar

                alertas.enqueue(new Callback<List<Alerta>>() { // aqui que vai no servidor, precisa ser em outra tread
                    @Override
                    public void onResponse(Call<List<Alerta>> call, Response<List<Alerta>> response) { // resposta do server
                        if (!response.isSuccessful()) {
                            if (contFalha > 3) {
                                contFalha = 0;
                                dialogo.dismiss();
                                alertDialog = new AlertDialog.Builder(MapsActivity.this)
                                        .setMessage("Impossível conectar ao servidor!\n" + response.message())
                                        .setCancelable(true)
                                        .setPositiveButton("OK", null);
                                alertDialog.create();
                                alertDialog.show();

                                Log.e(TAG, response.message() + " " + response.code() + " " + response.errorBody());
                            } else {
                                buscaPontos(ponto);
                            }
                        } else {
                            contFalha = 0;
                            dialogo.dismiss();
                            Log.e(TAG2, response.body().toString()); // aqui vai receber os dados, tem que tratar ainda
                            Marcador marcador = new Marcador();
                            List<Alerta> lista = response.body();
                            if (lista.size() == 0) {
                                alertDialog = new AlertDialog.Builder(MapsActivity.this)
                                        .setMessage("Não existe Alertas nesta \nproximidade!")
                                        .setCancelable(true)
                                        .setPositiveButton("OK", null);
                                alertDialog.create();
                                alertDialog.show();
                            } else {
                                mListMarcador.addAll(marcador.setReferencia(lista));
                                choveMarcador();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Alerta>> call, Throwable t) { // se for aqui, falhou a conexao com o server
                        contFalha++;
                        if (contFalha > 3) {
                            contFalha = 0;
                            Log.e("onFailure", (contFalha + 1) + " tentativa.");
                            dialogo.dismiss();

                            alertDialog = new AlertDialog.Builder(MapsActivity.this)
                                    .setMessage("Impossível conectar ao servidor! Fui lá três vezes já!!")
                                    .setCancelable(true)
                                    .setPositiveButton("OK", null);
                            alertDialog.create();
                            alertDialog.show();
                            Log.e(TAG, "Falha: " + t.getMessage());
                        } else {
                            buscaPontos(ponto);
                        }
                    }
                });
            }
        }).start();
    }

    public void choveMarcador() {
        Log.e("CHOVE MARCADOR", "1");
        Marker marker;
        for (int i = controle; i < mListMarcador.size(); i++) {
            LatLng latLng = new LatLng(Double.parseDouble(mListMarcador.get(i).getAlerta().getLatitude())
                    , Double.parseDouble(mListMarcador.get(i).getAlerta().getLongitude()));
            if (mListMarcador.get(i).getAlerta().getEpositivo()) {
                Log.e("CHOVE MARCADOR", "2");
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("LUGAR BOM")
                        .snippet((mListMarcador.get(i).getMarcadorList().size() + 1) + "")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            } else {
                Log.e("CHOVE MARCADOR", "3");
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("LUGAR RUIM")
                        .snippet(String.valueOf(mListMarcador.get(i).getMarcadorList().size() + 1)));
            }
            mListMarcador.get(i).setMarcador(marker);
            choveListener(latLng);
        }
        controle = mListMarcador.size() - 1;
    }

    public void choveListener(LatLng latLng) {

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
/**
 * Chamando o InfoDialog com as informações do alerta
 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    System.out.println("1...");
                    infoDialog = new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("INFO")
                            .setPositiveButton("OK", null);
                    LayoutInflater layoutInflater = MapsActivity.this.getLayoutInflater();
                    View dialogView = layoutInflater.inflate(R.layout.info_dialog, null);
                    infoDialog.setView(dialogView);

                    /**
                     * Elementos na tela de informações do alerta
                     */
                    textTipoAlertaCont = (TextView) dialogView.findViewById(R.id.textTipoAlertaCont);
                    textDataHoraCont = (TextView) dialogView.findViewById(R.id.textDataHoraCont);
                    textOcorrenciaCont = (TextView) dialogView.findViewById(R.id.textOcorrenciaCont);
                    spinnerAlerta = (Spinner) dialogView.findViewById(R.id.spinnerAlertas);
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd HH:mm");
                    final SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Marcador marcador = new Marcador();
                    marcador = marcador.procuraMarcador(mListMarcador, marker);
                    /**
                     * Implementação dos itens do spinner baseado na lista de alerta em cada objeto marcador
                     */
                    if (marcador != null) {
                        List<String> listaDrop = new ArrayList<String>();
                        listaDrop.add(marcador.getAlerta().getTipo() + " ( Dia " + String.valueOf(dateFormat.format(marcador.getAlerta().getLoghora())) + ")");

                        if (marcador.getMarcadorList().size() >= 1) {
                            for (int i = 0; i < marcador.getMarcadorList().size(); i++) {
                                listaDrop.add(marcador.getMarcadorList().get(i).getTipo()
                                        + " ( Dia " + String.valueOf(dateFormat.format(marcador.getMarcadorList().get(i).getLoghora())) + ")");
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MapsActivity.this, android.R.layout.simple_list_item_1, listaDrop);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        spinnerAlerta.setAdapter(adapter);

                        /**
                         * Função para alterar os itens dentro do dialogo de informações de cada marcador.
                         * Os valores irão alterar para cada tipo de alerta selecionado na lista de dropdown
                         */
                        final Marcador finalMarcador = marcador;
                        spinnerAlerta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    textOcorrenciaCont.setText(finalMarcador.getAlerta().getObservacao());
                                    textTipoAlertaCont.setText(finalMarcador.getAlerta().getTipo());
                                    textDataHoraCont.setText(String.valueOf(dateFormat2.format(finalMarcador.getAlerta().getLoghora())));

                                } else {
                                    textOcorrenciaCont.setText(finalMarcador.getMarcadorList().get(position - 1).getObservacao());
                                    textTipoAlertaCont.setText(finalMarcador.getMarcadorList().get(position - 1).getTipo());
                                    textDataHoraCont.setText(String.valueOf(dateFormat2.format(finalMarcador.getMarcadorList().get(position - 1).getLoghora())));
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                textOcorrenciaCont.setText(finalMarcador.getAlerta().getObservacao());

                                textTipoAlertaCont.setText(finalMarcador.getAlerta().getTipo());
                                textDataHoraCont.setText(String.valueOf(dateFormat2.format(finalMarcador.getAlerta().getLoghora())));
                                System.out.println("3...");
                            }
                        });


                        informacao = infoDialog.create();
                        informacao.show();
                    }
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

        listaTeste.add(new Alerta(1, getDataHoraAgora(), "-20.065993", "-44.281507", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 1 rapazes bonitos dos olhos claros", "Assalto", true));
        listaTeste.add(new Alerta(2, getDataHoraAgora(), "-20.065900", "-44.281555", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 2 rapazes bonitos dos olhos claros", "Tiroteio", true));
        listaTeste.add(new Alerta(3, getDataHoraAgora(), "-20.065965", "-44.281522", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 3 rapazes bonitos dos olhos claros", "Festa", true));
        listaTeste.add(new Alerta(4, getDataHoraAgora(), "-20.065912", "-44.281566", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 4 rapazes bonitos dos olhos claros", "Policiamento", true));
        listaTeste.add(new Alerta(5, getDataHoraAgora(), "-20.065933", "-44.281512", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 5 rapazes bonitos dos olhos claros", "Corrida Naruto1", true));
        listaTeste.add(new Alerta(6, getDataHoraAgora(), "-20.064242", "-44.282150", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 6 rapazes bonitos dos olhos claros", "Corrida Naruto2", false));
        listaTeste.add(new Alerta(7, getDataHoraAgora(), "-21.064292", "-49.282120", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 7 rapazes bonitos dos olhos claros", "Corrida Naruto3", false));
        listaTeste.add(new Alerta(8, getDataHoraAgora(), "-20.064232", "-44.282110", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 8 rapazes bonitos dos olhos claros", "Corrida Naruto4", false));
        listaTeste.add(new Alerta(9, getDataHoraAgora(), "-20.064212", "-44.282190", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 9 rapazes bonitos dos olhos claros", "Corrida Naruto5", false));

        Marcador marcador = new Marcador();
        mListMarcador = marcador.setReferencia(listaTeste);
        for (int i = 0; i < mListMarcador.size(); i++) {
            Log.e("DEU OU NâO", mListMarcador.get(i).toString());
        }
        choveMarcador();

    }

    public java.sql.Date getDataHoraAgora() {
        Date data = new Date();
        java.sql.Date dataSql = new java.sql.Date(data.getTime());
/*        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        hora = Calendar.getInstance();
        String horario = "" + hora.get(Calendar.YEAR) + "-" + formato.format((hora.get(Calendar.MONTH) + 1))
                + "-" + formato.format(hora.get(Calendar.DAY_OF_MONTH)) + " "
                + formato.format(hora.get(Calendar.HOUR_OF_DAY)) + ":" + formato.format(hora.get(Calendar.MINUTE)) +
                ":" + formato.format(hora.get(Calendar.SECOND));
        Log.e("HORA: ", horario);


        try {
            data = dateFormat.parse(horario);
            dataSql = new java.sql.Date(data.getTime());

            Log.e("HORA DATE", data.toString());
            Log.e("HORA DATE SQL", dataSql.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }*/


        return dataSql;
    }

}