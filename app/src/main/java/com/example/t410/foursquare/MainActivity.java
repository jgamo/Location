package com.example.t410.foursquare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.R.attr.description;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_FSQ_CONNECT = 200; // Código de conexión exitosa
    private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 201; // Código de intercambio de token exitosa
    private static final String CLIENT_ID = "PF3J0MAV5W0R2H0MKLY4QM5IMAVZLE05VHY5DE4AEF1PHBNT"; // clave ID
    private static final String CLIENT_SECRET = "NZQBQIEFKIP34CA2V0XIFUF32H5NCXRCD5WA5MTZ35USFVMU"; // clave secreta
    private static boolean found;

    private OkHttpClient okHttp;
    private Request request;
    private String url;

    private Context con;
    private Location location;
    private final int REQUEST_LOCATION = 1;
    private GoogleApiClient googleApiClient;

    private TextView tvLat, tvLon;
    private Button bLogin;

    public static ArrayList<FQ> lista = new ArrayList<>();
    private ListView listView;
    private static final double LAT = 40.7142700; // Ubicacion centro New York
    private static final double LON = -74.0059700;// Ubicacion centro New York

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);

        tvLat = (TextView) findViewById(R.id.tv_lat);
        tvLon = (TextView) findViewById(R.id.tv_lon);
        bLogin = (Button)findViewById(R.id.b_login);
        Button boton1 = (Button)findViewById(R.id.b_connect);

        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (compruebaConexion(MainActivity.this)) {
                    if (googleApiClient == null) {
                        buildGoogleApiClient();
                    } else {
                        googleApiClient.disconnect();
                        googleApiClient.connect();
                    }
                } else {
                    Toast.makeText(getBaseContext(),"No hay conexión a Internet ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (compruebaConexion(MainActivity.this)) {
                    if(location==null){
                        Toast.makeText(getBaseContext(),"Obtenga la ubicacion", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = FoursquareOAuth.getConnectIntent(MainActivity.this, CLIENT_ID);
                        // Si el dispositivo no tiene instalada la app de Fousquare se le va a pedir que
                        // la instale, usando un intent hacia la Play Store
                        if (FoursquareOAuth.isPlayStoreIntent(intent)) {
                            Toast.makeText(MainActivity.this, "La app no está instalada", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else {
                            // Si está la app instalada empieza el proceso de autenticacion
                            startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
                        }
                    }
                } else {
                    Toast.makeText(getBaseContext(),"No hay conexión a Internet ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //processLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Aquí muestras confirmación explicativa al usuario
                // por si rechazó los permisos anteriormente
                Toast.makeText(this,"Rechazaste el permiso", Toast.LENGTH_SHORT);
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
        } else {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                tvLat.setText(String.valueOf(location.getLatitude()));
                tvLon.setText(String.valueOf(location.getLongitude()));
            } else {
                Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Fallo en la conexion", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FSQ_CONNECT: // Se pudo hacer la conexión
                onCompleteConnect(resultCode, data);
                break;
            case REQUEST_CODE_FSQ_TOKEN_EXCHANGE: // Ya se dio el permiso, hace falta obtener el token
                onCompleteTokenExchange(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onCompleteTokenExchange(int resultCode, Intent data) {
        // Se lleva a cabo la solicitud de token
        AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode,data);
        Exception exception = tokenResponse.getException();
        //Si no hay ninguna excepcion o problema quiere decir que ya se puede obtener el token de acceso
        if(exception==null){
            String accessToken = tokenResponse.getAccessToken();
            //Guardamos el token para usarlo posteriormente
            //Toast.makeText(this, "Token " + accessToken, Toast.LENGTH_SHORT).show();
            lista = new ArrayList<>();
            makeCall(accessToken);
            //Se hacen las siguientes operaciones
        }else {
            Toast.makeText(this, "Error " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void makeCall(final String accessToken) {
        url = "https://api.foursquare.com/v2/venues/search?ll="+String.valueOf(location.getLatitude())+
                ","+String.valueOf(location.getLongitude())+"&oauth_token="+accessToken+"&v=20170926";

        okHttp = new OkHttpClient();
        request = new Request.Builder().url(url).build();
        okHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                final String myResponse = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(myResponse);
                            if (json.has("response")) {
                                    if (json.getJSONObject("response").getJSONArray("venues").length()>0) {
                                        JSONArray jsonArray = json.getJSONObject("response").getJSONArray("venues");
                                        String id, name, distance, address;
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            if(jsonArray.getJSONObject(i).getJSONObject("location").has("distance")){
                                                distance = jsonArray.getJSONObject(i).getJSONObject("location").getString("distance");
                                            } else {
                                                distance="Distance not found";
                                            }
                                            if(jsonArray.getJSONObject(i).getJSONObject("location").has("address")){
                                                address = jsonArray.getJSONObject(i).getJSONObject("location").getString("address");
                                            } else{
                                                address="Address not found";
                                            }
                                            lista.add(new FQ(jsonArray.getJSONObject(i).getString("id"), jsonArray.getJSONObject(i).getString("name"),
                                                distance, address));
                                            getUrl(lista.get(i).getId(),accessToken, i);
                                        }
                                        SystemClock.sleep(2000); // ponemos a dormir el hilo para dar tiempo
                                        //a que se terminen de cargar las urls que estaban en otros hilos
                                        Intent act = new Intent(MainActivity.this, RecyclerActivity.class);
                                        startActivity(act);
                                    }else{
                                        Toast.makeText(getApplicationContext(), "No se encontraron lugares", Toast.LENGTH_SHORT).show();
                                    }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void getUrl(String id, String accessToken, final int indice) {
        url = "https://api.foursquare.com/v2/venues/"+id+"/photos/?oauth_token="+accessToken+"&v=20170926";

        okHttp = new OkHttpClient();
        request = new Request.Builder().url(url).build();
        okHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                final String myResponse = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(myResponse);
                            JSONArray jsonArray = json.getJSONObject("response").getJSONObject("photos").getJSONArray("items");
                            String prefix = jsonArray.getJSONObject(0).getString("prefix");
                            String sufix = jsonArray.getJSONObject(0).getString("suffix");
                            int width = jsonArray.getJSONObject(0).getInt("width");
                            int height = jsonArray.getJSONObject(0).getInt("height");
                            lista.get(indice).setUrlPict(prefix+width+"x"+height+sufix);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    private void onCompleteConnect(int resultCode, Intent data) {
        AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
        Exception exception = codeResponse.getException();
        if (exception == null) {
            // Si no hay excepción se obtiene el código para cambiarlo por un token
            // y se llama la función para hacer el intercambio
            String code = codeResponse.getCode();
            performTokenExchange(code);
        } else {
                Toast.makeText(this, "Error 2" + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void performTokenExchange(String code) {
        Intent intent = FoursquareOAuth.getTokenExchangeIntent(this, CLIENT_ID, CLIENT_SECRET, code);
        startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
    }



    public static boolean compruebaConexion(Context context) {

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }
}
