package br.una.zisc;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.una.projetoaplicado.marcosbenevides.zisc.R;
import br.una.zisc.entidades.Alerta;
import br.una.zisc.entidades.CallHandler;
import br.una.zisc.entidades.DptoPolicia;
import br.una.zisc.entidades.Usuario;
import br.una.zisc.mapaUtil.Marcador;
import br.una.zisc.requisicoesWS.RetrofitService;
import br.una.zisc.requisicoesWS.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuLateral extends AppCompatActivity
        implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SearchView.OnQueryTextListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ERRO: ";
    private static final String TAG2 = "DEU CERTO: ";
    private GoogleMap mMap;
    private Boolean inicio = true;
    private static final int MY_PERMISSION_LOCATION = 0;
    private GoogleApiClient mGoogleApiClient = null;
    private Location inicialLocation = null;
    private Intent it;
    private Boolean marcar = false, callHanderFailure = false;
    private LatLng latLngMarcar;
    private SearchView barraProcurar;
    private Circle circulo;
    private Calendar hora;
    private NumberFormat formato;
    private ProgressDialog dialog;
    private AlertDialog.Builder alertDialog, infoDialog, addMarkerDialog, confirmaDialog;
    private AlertDialog informacao, cadastro;
    private TextView textTipoAlertaCont, textDataHoraCont, textOcorrenciaCont, nomeUserMenu, emailUserMenu;
    private List<Alerta> listaTeste = new ArrayList<>();
    private final List<String> listaPositiva = new ArrayList<>();
    private final List<String> listaNegativa = new ArrayList<>();
    private List<Marcador> mListMarcador = new ArrayList<>(); // lista que armazena todos os objetos do tipo Marcador
    private ToggleButton switchNegPos;
    private EditText editorOcorrencia;
    private Spinner spinnerTipo, spinnerAlerta;
    private String cidade = "", estado = "", bairro = "";
    private Integer controle = 0, contFalha = 0;
    private DialogInterface.OnClickListener dialogInterface;
    private Base64 base64;
    private Object[] alertas = new Object[2];
    private Marcador marcador = new Marcador();
    private Thread thread1, thread2;
    private CallHandler callHandlerAtivo;
    private List<DptoPolicia> listaDpto;
    private Marker dpto;
    private RadioButton negativo, positivo;
    private Button emergencia, menu;
    private Toolbar actionBar;
    private GoogleApiClient client;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_menu_lateral);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        emergencia = findViewById(R.id.btnEmergencia);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        formato = new DecimalFormat("00");

        listaPositiva.add("Bem iluminado.");
        listaPositiva.add("Bem movimentado.");
        listaPositiva.add("Bom policiamento.");
        listaNegativa.add("Assalto.");
        listaNegativa.add("Mal iluminado.");
        listaNegativa.add("Local deserto.");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuario = new Gson().fromJson(bundle.getString("USUARIO"), Usuario.class);
        }

        emergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG2, "BOTAO EMERGÊNCIA");
                setCallHandler(1, getCallHandler());
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle("Mapa de Alertas");
        } catch (NullPointerException e) {
            callDialog(3, e.getMessage());
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerview = navigationView.getHeaderView(0);


                /* Setando no menu o nome do usuario e o email */
        nomeUserMenu = headerview.findViewById(R.id.nomeUserMenu);
        emailUserMenu = headerview.findViewById(R.id.emailUserMenu);
        nomeUserMenu.setText(usuario.getNome());
        emailUserMenu.setText(usuario.getEmail());


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lateral, menu);

        barraProcurar = (SearchView) menu.findItem(R.id.procurar).getActionView();
        barraProcurar.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            // Handle the camera action
        } else if (id == R.id.profile) {

            Intent intent = new Intent(MenuLateral.this, ProfileActivity.class);
            intent.putExtra("USUARIO", new Gson().toJson(usuario));
            startActivity(intent);

        } else if (id == R.id.my_alerts) {

            Intent intent = new Intent(MenuLateral.this, CardViewActivity.class);
            intent.putExtra("USUARIO", new Gson().toJson(usuario));

            startActivity(intent);

        } else if (id == R.id.logout) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public CallHandler getCallHandler() {
        String cidade = "", bairro = "", estado = "";
        LatLng latlng = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        Log.e(TAG2, latlng.toString());
        Geocoder geocoder = new Geocoder(this);
        List<Address> address;
        try {
            address = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
            cidade = address.get(0).getLocality();
            bairro = address.get(0).getSubLocality();
            estado = address.get(0).getAdminArea();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return new CallHandler(String.valueOf(latlng.latitude), String.valueOf(latlng.longitude), cidade, estado, bairro, true, new Date());
    }

    public void dados(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> address;
        try {
            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            cidade = address.get(0).getLocality();
            bairro = address.get(0).getSubLocality();
            estado = address.get(0).getAdminArea();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void longClickListener() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {


                latLngMarcar = point;
                addMarkerDialog = new AlertDialog.Builder(MenuLateral.this)
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
                                                MenuLateral.this.dialog = ProgressDialog.show(MenuLateral.this, "Aguarde..", "Enviando ao servidor...");
                                            }
                                        });
                                        dados(latLngMarcar);
                                        final Alerta alerta_temporario = new Alerta(getDataHoraAgora(),
                                                String.valueOf(latLngMarcar.longitude),
                                                String.valueOf(latLngMarcar.latitude),
                                                bairro,
                                                cidade,
                                                estado,
                                                editorOcorrencia.getText().toString(),
                                                spinnerTipo.getSelectedItem().toString(),
                                                switchNegPos.isChecked());

                                        RetrofitService service = ServiceGenerator.createService(RetrofitService.class); // inicia o gerador de servico/cria conexao com o server
                                        Call<Alerta> call = service.cadastraralerta(
                                                usuario.getId(),
                                                alerta_temporario.getLongitude(),
                                                alerta_temporario.getLatitude(),
                                                alerta_temporario.getBairro(),
                                                alerta_temporario.getCidade(),
                                                alerta_temporario.getEstado(),
                                                alerta_temporario.getObservacao(),
                                                alerta_temporario.getTipo(),
                                                alerta_temporario.getePositivo()); // acessa os metodos do retrofit <<<LEMBRAR alterar

                                        callCreateMarker(call, alerta_temporario);
                                    }
                                }).start();

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                LayoutInflater layoutInflater = MenuLateral.this.getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.add_marker_view, null);
                addMarkerDialog.setView(dialogView);

                switchNegPos = dialogView.findViewById(R.id.buttonNegPos);
                editorOcorrencia = dialogView.findViewById(R.id.editOcorrencia);
                spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MenuLateral.this, android.R.layout.simple_list_item_1, listaNegativa);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                spinnerTipo.setAdapter(adapter);

                switchNegPos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(MenuLateral.this, android.R.layout.simple_list_item_1, listaPositiva);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            spinnerTipo.setAdapter(adapter);
                        } else {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(MenuLateral.this, android.R.layout.simple_list_item_1, listaNegativa);
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

    public void callCreateMarker(Call call, final Alerta alerta_temporario) {
        call.clone().enqueue(new Callback<Alerta>() {
            @Override
            public void onResponse(Call<Alerta> call, Response<Alerta> response) {
                if (!response.isSuccessful()) {
                    contFalha++;
                    if (contFalha > 5) {
                        contFalha = 0;
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        callDialog(1, response.code() + " - " + response.message());
                    } else {
                        callCreateMarker(call, alerta_temporario);
                    }
                } else {
                    contFalha = 0;
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Marcador marcador = new Marcador();
                    Alerta alerta = response.body();
                    Marcador aux = marcador.temReferencia(mListMarcador, alerta);
                    if (aux != null) {
                        if (!aux.getAlerta().getePositivo()) {
                            aux.getMarcador().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            aux.getMarcador().setTitle("LUGAR RUIM");
                        }
                        aux.getMarcador().setSnippet("" + (Integer.parseInt(aux.getMarcador().getSnippet()) + 1));
                        dialog.dismiss();
                        Toast.makeText(MenuLateral.this, "Alerta adicionado ao marcador selecionado!", Toast.LENGTH_LONG).show();
                        aux.getMarcador().showInfoWindow();
                    } else {
                        marcador.setAlerta(alerta);
                        mListMarcador.add(marcador);
                        choveMarcador();
                    }
                }
            }

            @Override
            public void onFailure(Call<Alerta> call, Throwable t) {
                contFalha++;
                if (contFalha > 3) {
                    contFalha = 0;
                    dialog.dismiss();
                    alertDialog = new AlertDialog.Builder(MenuLateral.this)
                            .setMessage("OPS! Falha ao conectar.\n" + t.getMessage())
                            .setCancelable(true)
                            .setPositiveButton("OK", null);
                    alertDialog.create();
                    alertDialog.show();
                } else {
                    callCreateMarker(call, alerta_temporario);
                }
            }
        });
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

            runOnUiThread(new Runnable() { // O dialog tem que ser na tread da interface, por isso
                @Override
                public void run() {
                    dialog = ProgressDialog.show(MenuLateral.this, "Buscando dados no servidor", "Por favor, aguarde...");
                }
            });
            buscaAlertas(latLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }

        return false;
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

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // setCallHandler(2,null);
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

                callProgressDialog("Buscando dados no servidor", "Por favor, aguarde!");
                buscaAlertas(new LatLng(inicialLocation.getLatitude(), inicialLocation.getLongitude()));
                buscaDpto();
            }
        }
    }

    public void setCallHandler(Integer tipo, CallHandler callHandler) {

        RetrofitService service = ServiceGenerator.createService(RetrofitService.class);

        if (tipo == 1) {
            Log.e(TAG2, callHandler.toString());
            final Call<CallHandler> callHandlerCall = service.setCallHandler(usuario.getId(),
                    callHandler.getLatitude(),
                    callHandler.getLongitude(),
                    callHandler.getCidade(),
                    callHandler.getBairro(),
                    callHandler.getEstado());
            confirmaDialog = new AlertDialog.Builder(MenuLateral.this)
                    .setTitle("Ligação Direta?")
                    .setMessage("Deseja abrir o Dial-up para fazer uma chamada na polícia?")
                    .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        /**
                         * This method will be invoked when a button in the dialog is clicked.
                         *
                         * @param dialog The dialog that received the click.
                         * @param which  The button that was clicked (e.g.
                         *               {@link DialogInterface#BUTTON1}) or the position
                         */
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    callHandlerCall.enqueue(new Callback<CallHandler>() {
                                        /**
                                         * Invoked for a received HTTP response.
                                         * <p>
                                         * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
                                         * Call {@link Response#isSuccessful()} to determine if the response indicates success.
                                         *
                                         * @param call
                                         * @param response
                                         */
                                        @Override
                                        public void onResponse(Call<CallHandler> call, Response<CallHandler> response) {
                                            if (!response.isSuccessful()) {
                                                callHanderFailure = true;
                                            } else {
                                                callHanderFailure = false;
                                                callHandlerAtivo = response.body();
                                            }

                                            if (dialog != null) {
                                                dialog.dismiss();
                                            }
                                            Intent dialup = new Intent(Intent.ACTION_DIAL);
                                            dialup.setData(Uri.parse("tel:" + 190));
                                            startActivity(dialup);
                                        }

                                        /**
                                         * Invoked when a network exception occurred talking to the server or when an unexpected
                                         * exception occurred creating the request or processing the response.
                                         *
                                         * @param call
                                         * @param t
                                         */
                                        @Override
                                        public void onFailure(Call<CallHandler> call, Throwable t) {
                                            if (dialog != null) {
                                                dialog.dismiss();
                                            }
                                            Intent dialup = new Intent(Intent.ACTION_DIAL);
                                            dialup.setData(Uri.parse("tel:" + 190));
                                            startActivity(dialup);
                                        }
                                    });
                                }
                            }).start();

                        }
                    })
                    .setNegativeButton("Cancelar", null);
            confirmaDialog.create();
            confirmaDialog.show();

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MenuLateral.this.dialog = ProgressDialog.show(MenuLateral.this, "Aguarde..", "Enviando ao servidor...");
                }
            });

            if (!callHanderFailure) {
                final Call<CallHandler> callHandlerCall = service.cancelCallHandler(callHandlerAtivo.getId());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callHandlerCall.enqueue(new Callback<CallHandler>() {
                            @Override
                            public void onResponse(Call<CallHandler> call, Response<CallHandler> response) {
                                if (!response.isSuccessful()) {
                                    callDialog(1, response.code() + " - " + response.message());
                                } else {
                                    callHandlerAtivo = response.body();
                                }
                            }

                            @Override
                            public void onFailure(Call<CallHandler> call, Throwable t) {
                                callDialog(1, t.getCause() + " - " + t.getMessage());
                            }
                        });
                    }
                }).start();
            }
        }
    }

    public void buscaAlertas(final LatLng ponto) {

        new Thread(new Runnable() { //por causa do Call, precisa pra rodar ele
            @Override
            public void run() { // vai rodar aqui qndo der o Start la em baixo
                String latitude = "" + ponto.latitude;
                String longitude = "" + ponto.longitude;

                RetrofitService service = ServiceGenerator.createService(RetrofitService.class);
                Call<List<Alerta>> alertas = service.consultaAlerta(latitude, longitude);

                alertas.enqueue(new Callback<List<Alerta>>() {
                    @Override
                    public void onResponse(Call<List<Alerta>> call, Response<List<Alerta>> response) {
                        if (!response.isSuccessful()) {
                            contFalha++;
                            if (contFalha >= 3) {

                                callDialog(1, response.code() + " - " + response.message());
                            } else {
                                buscaAlertas(ponto);
                            }
                        } else if (response.body() != null) {
                            contFalha = 0;
                            barraProcurar.clearFocus(); // teclado não aparecer novamente qndo pesquisa
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            List<Alerta> listAlertas = response.body();
                            mListMarcador.addAll(marcador.setReferencia(listAlertas));
                            choveMarcador();
                        } else {
                            callDialog(2, "");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Alerta>> call, Throwable t) {
                        callDialog(1, t.getMessage() + " - " + t.getCause());
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
            if (mListMarcador.get(i).getAlerta().getePositivo()) {
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
        controle = mListMarcador.size();
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
                /*
                  Chamando o InfoDialog com as informações do alerta
                 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    System.out.println("1...");
                    infoDialog = new AlertDialog.Builder(MenuLateral.this)
                            .setTitle("INFO")
                            .setPositiveButton("OK", null);
                    LayoutInflater layoutInflater = MenuLateral.this.getLayoutInflater();
                    View dialogView = layoutInflater.inflate(R.layout.info_dialog, null);
                    infoDialog.setView(dialogView);

                    /*
                      Elementos na tela de informações do alerta
                     */
                    textTipoAlertaCont = dialogView.findViewById(R.id.textTipoAlertaCont);
                    textDataHoraCont = dialogView.findViewById(R.id.textDataHoraCont);
                    textOcorrenciaCont = dialogView.findViewById(R.id.textOcorrenciaCont);
                    spinnerAlerta = dialogView.findViewById(R.id.spinnerAlertas);
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd HH:mm");
                    final SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Marcador marcador = new Marcador();
                    marcador = marcador.procuraMarcador(mListMarcador, marker);
                    /**
                     * Implementação dos itens do spinner baseado na lista de alerta em cada objeto marcador
                     */
                    if (marcador != null) {
                        List<String> listaDrop = new ArrayList<>();
                        listaDrop.add(marcador.getAlerta().getTipo() + " ( Dia " + String.valueOf(dateFormat.format(marcador.getAlerta().getLogHora())) + ")");

                        if (marcador.getMarcadorList().size() >= 1) {
                            for (int i = 0; i < marcador.getMarcadorList().size(); i++) {
                                listaDrop.add(marcador.getMarcadorList().get(i).getTipo()
                                        + " ( Dia " + String.valueOf(dateFormat.format(marcador.getMarcadorList().get(i).getLogHora())) + ")");
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MenuLateral.this, android.R.layout.simple_list_item_1, listaDrop);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        spinnerAlerta.setAdapter(adapter);

                        /*
                          Função para alterar os itens dentro do dialog de informações de cada marcador.
                          Os valores irão alterar para cada tipo de alerta selecionado na lista de dropdown
                         */
                        final Marcador finalMarcador = marcador;
                        spinnerAlerta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    textOcorrenciaCont.setText(finalMarcador.getAlerta().getObservacao());
                                    textTipoAlertaCont.setText(finalMarcador.getAlerta().getTipo());
                                    textDataHoraCont.setText(String.valueOf(dateFormat2.format(finalMarcador.getAlerta().getLogHora())));

                                } else {
                                    textOcorrenciaCont.setText(finalMarcador.getMarcadorList().get(position - 1).getObservacao());
                                    textTipoAlertaCont.setText(finalMarcador.getMarcadorList().get(position - 1).getTipo());
                                    textDataHoraCont.setText(String.valueOf(dateFormat2.format(finalMarcador.getMarcadorList().get(position - 1).getLogHora())));
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                textOcorrenciaCont.setText(finalMarcador.getAlerta().getObservacao());

                                textTipoAlertaCont.setText(finalMarcador.getAlerta().getTipo());
                                textDataHoraCont.setText(String.valueOf(dateFormat2.format(finalMarcador.getAlerta().getLogHora())));
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

        listaTeste.add(new Alerta(1, getDataHoraAgora(), "-20.065993", "-44.281507", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 1 rapazes bonitos dos olhos claros", "Assalto", false));
        listaTeste.add(new Alerta(2, getDataHoraAgora(), "-20.065900", "-44.281555", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 2 rapazes bonitos dos olhos claros", "Tiroteio", false));
        listaTeste.add(new Alerta(3, getDataHoraAgora(), "-20.065965", "-44.281522", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 3 rapazes bonitos dos olhos claros", "Festa", true));
        listaTeste.add(new Alerta(4, getDataHoraAgora(), "-20.065912", "-44.281566", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 4 rapazes bonitos dos olhos claros", "Policiamento", true));
        listaTeste.add(new Alerta(5, getDataHoraAgora(), "-20.065933", "-44.281512", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 5 rapazes bonitos dos olhos claros", "Corrida Naruto1", true));
        listaTeste.add(new Alerta(6, getDataHoraAgora(), "-20.064242", "-44.282150", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 6 rapazes bonitos dos olhos claros", "Corrida Naruto2", true));
        listaTeste.add(new Alerta(7, getDataHoraAgora(), "-21.064292", "-49.282120", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 7 rapazes bonitos dos olhos claros", "Corrida Naruto3", true));
        listaTeste.add(new Alerta(8, getDataHoraAgora(), "-20.064232", "-44.282110", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 8 rapazes bonitos dos olhos claros", "Corrida Naruto4", true));
        listaTeste.add(new Alerta(9, getDataHoraAgora(), "-20.064212", "-44.282190", "Marques Canadá", "São Joaquim de Bicas", "Minas Gerais", "Fui abordado por 9 rapazes bonitos dos olhos claros", "Corrida Naruto5", true));

        Marcador marcador = new Marcador();
        mListMarcador = marcador.setReferencia(listaTeste);
        for (int i = 0; i < mListMarcador.size(); i++) {
            Log.e("DEU OU NâO", mListMarcador.get(i).toString());
        }
        choveMarcador();

    }

    /**
     * Chama o dialogMessage com o tipo especificado nos parametros
     *
     * @param tipoErro -> 1 = Erro ao conectar, 2 = Sem Alertas
     * @param response
     */
    public void callDialog(final Integer tipoErro, final String response) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String title;
                String msg;
                if (tipoErro == 1) {
                    title = getResources().getString(R.string.ERRO_AO_CONECTAR);
                    msg = response;
                } else if (tipoErro == 2) {
                    title = "OPS!";
                    msg = getResources().getString(R.string.SEM_ALERTAS);
                } else {
                    title = getResources().getString(R.string.ERRO_AO_CONECTAR);
                    msg = response;
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
                alertDialog = new AlertDialog.Builder(MenuLateral.this)
                        .setTitle(title)
                        .setMessage(msg)
                        .setCancelable(true)
                        .setPositiveButton("OK", null);
                alertDialog.create();
                alertDialog.show();
            }
        });
    }

    public Date getDataHoraAgora() {

        return new Date(System.currentTimeMillis());

    }

    public void buscaDpto() {
        RetrofitService service = ServiceGenerator.createService(RetrofitService.class);
        Call<List<DptoPolicia>> call = service.buscaDpto();
        callProgressDialog("Por favor, aguarde!", "Estamos carregando os pontos policiais!");
        call.enqueue(new Callback<List<DptoPolicia>>() {
            @Override
            public void onResponse(Call<List<DptoPolicia>> call, Response<List<DptoPolicia>> response) {
                if (!response.isSuccessful()) {
                    callDialog(1, response.code() + " - " + response.message());
                } else {
                    listaDpto = response.body();
                    for (DptoPolicia policia : listaDpto) {
                        dpto = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(policia.getLatitude()), Double.parseDouble(policia.getLongitude())))
                                .title(policia.getId() + " - " + policia.getNome())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.police_marker))
                                .snippet("(" + policia.getDdd() + ") " + policia.getTelefone()));
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DptoPolicia>> call, Throwable t) {
                callDialog(1, t.getCause() + " - " + t.getMessage());
            }
        });


    }

    public void callProgressDialog(final String title, final String message) {
        if (dialog != null) {
            dialog.dismiss();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MenuLateral.this.dialog = ProgressDialog.show(MenuLateral.this, title, message);
            }
        });
    }
}
