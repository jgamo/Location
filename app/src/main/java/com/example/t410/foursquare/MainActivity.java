package com.example.t410.foursquare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_FSQ_CONNECT = 200; // Código de conexión exitosa
    private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 201; // Código de intercambio de token exitosa
    private static final String CLIENT_ID = "PF3J0MAV5W0R2H0MKLY4QM5IMAVZLE05VHY5DE4AEF1PHBNT"; // clave ID
    private static final String CLIENT_SECRET = "NZQBQIEFKIP34CA2V0XIFUF32H5NCXRCD5WA5MTZ35USFVMU"; // clave secreta

    private Context con;
    private Location location;
    private final int REQUEST_LOCATION = 1;
    private GoogleApiClient googleApiClient;

    private TextView tvLat, tvLon;
    private Button bLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLat = (TextView) findViewById(R.id.tv_lat);
        tvLon = (TextView) findViewById(R.id.tv_lon);
        bLogin = (Button)findViewById(R.id.b_login);
        Button boton1 = (Button)findViewById(R.id.b_connect);
        con= this;
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (compruebaConexion(con)) {
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
                Intent intent = FoursquareOAuth.getConnectIntent(MainActivity.this, CLIENT_ID);
                // Si el dispositivo no tiene instalada la app de Fousquare se le va a pedir que
                // la instale, usando un intent hacia la Play Store
                if(FoursquareOAuth.isPlayStoreIntent(intent)){
                    Toast.makeText(MainActivity.this, "La app no está instalada", Toast.LENGTH_SHORT).show();
                    //startActivity(intent);
                }else{
                    // Si está la app instalada empieza el proceso de autenticación
                    //Toast.makeText(MainActivity.this, "ELSEEE", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
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
                Intent act = new Intent(MainActivity.this, RecyclerActivity.class);
                act.putExtra("latitude", String.valueOf(location.getLatitude()));
                act.putExtra("longitude", String.valueOf(location.getLongitude()));
                startActivity(act);
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
            Toast.makeText(this, "Token " + accessToken, Toast.LENGTH_SHORT).show();

            //Se hacen las siguientes operaciones
        }else{
            Toast.makeText(this, "Error " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onCompleteConnect(int resultCode, Intent data) {
        AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
        Exception exception = codeResponse.getException();
        if (exception == null) {
// Si no hay excepción se obtiene el código para cambiarlo por un token
// y se llama la función para hacer el intercambio
            String code = codeResponse.getCode();
            performTokenExchange(code);
            Toast.makeText(this, "Entre 1 " + exception.getMessage(), Toast.LENGTH_SHORT).show();
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
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                }catch (SecurityException e) {
                    // lets the user know there is a problem with the gps
                }
                if (location != null) {
                    updateLocationUI();
                } else {
                    Toast.makeText(this, "Ubicacion no encontrada", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void processLocation() {

        getLocation();
        if (location != null) {
            updateLocationUI();
        }
    }

    private void getLocation() {
        if (isLocationPermissionGranted()) {
            try {
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            } catch (SecurityException e) {
                 // lets the user know there is a problem with the gps
            }
        } else {
            requestPermission();
        }
    }

    private boolean isLocationPermissionGranted(){
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this,"No quisiste dar accesso a tu ubicacion", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        }
    }

    private void updateLocationUI(){
        tvLat.setText("n"+String.valueOf(location.getLatitude()));
        tvLon.setText("n"+String.valueOf(location.getLongitude()));
        Log.d("george", String.valueOf(location.getLatitude()));
        Log.d("george", String.valueOf(location.getLongitude()));
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d("onLocationChanged", "cambiÃ³ ubicaciÃ³n");
        updateLocationUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }*/
}
