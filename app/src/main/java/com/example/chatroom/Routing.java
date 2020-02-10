package com.example.chatroom;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatroom.directionhelpers.FetchURL;
import com.example.chatroom.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class Routing extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener, TaskLoadedCallback {

    private GoogleMap mMap;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    LatLng latLng,latLng1,latLng2,latLng3;
    MarkerOptions place1, place2;
    private Polyline currentPolyline;

    PolylineOptions polylineOptions;


    Button pickedup, cancel;
    Trips t1;
    int flag = 0;



    FirebaseUser firebaseUser;
    DatabaseReference databaseReference, databaseReference1, databaseReference2;
    String useruid;
    Trips t;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routing);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        useruid = firebaseUser.getUid();
        pickedup = findViewById(R.id.finish);
        cancel = findViewById(R.id.cancel);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        t = (Trips) getIntent().getExtras().get("trip");

        latLng1 = new LatLng(t.getSourcelat(), t.getSourcelng());
        latLng2 = new LatLng(t.getDestlat(), t.getDestlng());

        if (useruid.equals(t.getDriverid())) {
            Routing.this.setTitle("Driver");
            cancel.setVisibility(View.GONE);

            System.out.println("wrong" + t.getSourcelat());


        } else {


            Routing.this.setTitle("Rider");
            pickedup.setVisibility(View.GONE);
        }

        databaseReference1 = FirebaseDatabase.getInstance().getReference("Trips").child(t.getTrip_id());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(Routing.this);
                builder1.setMessage("Going back will End the Ride");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mGoogleApiClient.disconnect();
                                t.setStatus("cancel");
                                System.out.println("test" + t);
                                databaseReference1.setValue(t);

                                Intent intent = new Intent(Routing.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        pickedup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.setStatus("Done");
                databaseReference1.setValue(t);

                mGoogleApiClient.disconnect();


                Intent intent = new Intent(Routing.this, Main2Activity.class);
                startActivity(intent);
                finish();

            }
        });


        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Trips t = dataSnapshot.getValue(Trips.class);
                if (t.getStatus().equals("Done")) {
                    mGoogleApiClient.disconnect();
                    Toast.makeText(Routing.this, "completed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Routing.this, Main2Activity.class);
                    startActivity(intent);
                    finish();


                } else if (t.getStatus().equals("cancel")) {
                    mGoogleApiClient.disconnect();
                    Intent intent = new Intent(Routing.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (useruid.equals(t.getRiderid())) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            t1 = dataSnapshot.getValue(Trips.class);
                            if (t1.getDriverlat() != null) {
                                latLng3 = new LatLng(Double.parseDouble(t1.getDriverlat()), Double.parseDouble(t1.getDriverlong()));
                            }


                            place1 = new MarkerOptions().position(new LatLng(latLng3.latitude, latLng3.longitude)).title("Location 1");
                            place2 = new MarkerOptions().position(new LatLng(t.getSourcelat(), t.getSourcelng())).title("Location 2");

                            String url = getUrl(place1.getPosition(), place2.getPosition(), "driving");

                            new FetchURL(Routing.this).execute(url, "driving");

                            mMap.clear();


                            mMap.addMarker(new MarkerOptions().position(latLng3)
                                    .title("I am here"))
                                    .setIcon(bitmapDescriptorFromVector(Routing.this, R.drawable.car2));
                            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng3.latitude, latLng3.longitude), 15));


                            /*mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng3));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));*/

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();

                            builder.include(latLng3);
                            builder.include(latLng1);

                            LatLngBounds bounds = builder.build();

                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);

                            mMap.moveCamera(cu);

                            mMap.addMarker(new MarkerOptions().position(latLng1)
                                    .title("pickup"));

                            mMap.addMarker(new MarkerOptions().position(latLng2)
                                    .title("Drop"));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }


            }, 3000);
        }

    }









    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(15000);
        //mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(10);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

         latLng = new LatLng(location.getLatitude(),location.getLongitude());



                if (useruid.equals(t.getDriverid())) {

            /*if(String.valueOf(latLng.latitude).equals(t.getSourcelat()) && String.valueOf(latLng.longitude).equals(t.getSourcelng())){
                Log.d("vineel","lol");

                mGoogleApiClient.disconnect();
            }*/

                    t.setDriverlat(String.valueOf(latLng.latitude));
                    t.setDriverlong(String.valueOf(latLng.longitude));
                    databaseReference1.setValue(t);



            /*mdriver = mMap.addMarker(new MarkerOptions().position(latLng).title("I am here")
                    .icon(bitmapDescriptorFromVector(Routing.this, R.drawable.car2)));*/


            //mdriver.remove();

                    driverfunc();




                    /*mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
*/

                }
            }


            public void driverfunc(){

                place1 = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title("Location 1");
                place2 = new MarkerOptions().position(new LatLng(t.getSourcelat(), t.getSourcelng())).title("Location 1");


                String url = getUrl(place1.getPosition(), place2.getPosition(), "driving");

                new FetchURL(Routing.this).execute(url, "driving");


                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title("I am here"))
                        .setIcon(bitmapDescriptorFromVector(Routing.this, R.drawable.car2));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                builder.include(latLng1);
                builder.include(latLng);

                LatLngBounds bounds = builder.build();

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);

                mMap.moveCamera(cu);



                //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));




                //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                /*if(flag == 0) {

                    mMap.animateCamera(cu);
                }else{
                    flag += 1;
                }*/
                //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));

                mMap.addMarker(new MarkerOptions().position(latLng1)
                        .title("pickup"));

                mMap.addMarker(new MarkerOptions().position(latLng2)
                        .title("Drop"));







            }






    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        //mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);


    }
    protected synchronized void buildGoogleApiClient(){

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyCjQlEN9SKDCtC30zy7grp-lyhPjEv792Q";
        Log.d("url",url);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        currentPolyline.setColor(Color.BLACK);

    }

    @Override
    public void onBackPressed() {

        if(useruid.equals(t.getRiderid())) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(Routing.this);
            builder1.setMessage("Going back will End the Ride");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            t.setStatus("cancel");
                            databaseReference.setValue(t);

                            Intent intent = new Intent(Routing.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }


    }
}
