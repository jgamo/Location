package com.example.t410.foursquare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Location location;
    private final int REQUEST_LOCATION = 1;
    private GoogleApiClient googleApiClient;
    TextView tvLat, tvLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLat = (TextView) findViewById(R.id.tv_lat);
        tvLon = (TextView) findViewById(R.id.tv_lon);
        Button boton1 = (Button)findViewById(R.id.b_connect);

        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null){
                    buildGoogleApiClient();
                }else {
                    googleApiClient.disconnect();
                    googleApiClient.connect();
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
