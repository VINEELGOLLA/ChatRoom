package com.example.chatroom;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatroom.directionhelpers.FetchURL;
import com.example.chatroom.directionhelpers.TaskLoadedCallback;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class riderhome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, TaskLoadedCallback {

    Button request;
    private Users u;
    private String currentUserID,currentGroupName,source_name,dest_name;
    private FirebaseAuth mAuth;
    TextView requesting,noactive;
    LatLng new1,new2,latLng;
    GoogleMap mMap;
    DatabaseReference databaseReference,databaseReference2,databaseReference1,databaseReference3,databaseReference4;
    ProgressBar progressBar;
    private Polyline currentPolyline;
    MarkerOptions place1, place2;
    private String trip;
    private int flag = -1;
    private static final int REQUEST_FINE_LOCATION = 101;
    private LocationRequest mLocationRequest;

    public static String group_key="trip";


    private long UPDATE_INTERVAL = 1000 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 100000; /* 2 sec */


    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riderhome);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.out.println("inside oncreate");

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        currentGroupName = getIntent().getExtras().get("group").toString();

        requesting = findViewById(R.id.requesting);
        noactive = findViewById(R.id.noactive);





        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        request = findViewById(R.id.request);

        databaseReference = FirebaseDatabase.getInstance().getReference("Trips");
        databaseReference1 = FirebaseDatabase.getInstance().getReference("Trips");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Trips");
        databaseReference3 = FirebaseDatabase.getInstance().getReference("Groups").child(currentGroupName);
        databaseReference4 = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        /*fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener <Location>() {
            @Override
            public void onSuccess(Location location) {
                System.out.println("Inside location on success");
                if(location!=null){
                    System.out.println("Lat "+location.getLatitude());
                }
                else{
                    System.out.println("Inside else condition");
                }
            }
        });*/


        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(riderhome.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(riderhome.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(riderhome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        List <String> providers = lm.getProviders(true);
        for (String prov:providers) {
            Location location = lm.getLastKnownLocation(prov);
            if (location != null){
                System.out.println("current location:" + location.getLatitude());
            }
        }


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDgj8b7j4zDZvmG9OZdqJ5nL7nE3GCgqto");
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_location);


        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //Log.i( "Place: " + place.getName() + ", " + place.getId());
                Log.d("place", String.valueOf(place.getLatLng()));
                //latlng = place.getLatLng().toString();
                new1 = place.getLatLng();
                source_name = place.getName();
                setMarker1();
            }


            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //Log.i(TAG, "An error occurred: " + status);
            }
        });

        AutocompleteSupportFragment autocompleteFragment1 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_destination);

        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME));

        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //Log.i( "Place: " + place.getName() + ", " + place.getId());
                Log.d("place", String.valueOf(place.getLatLng()));
                new2 = place.getLatLng();
                dest_name = place.getName();
                setMarker2();


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //Log.i(TAG, "An error occurred: " + status);
            }
        });

        databaseReference4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                u = users;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageKey = databaseReference1.push().getKey();
                trip = messageKey;
                databaseReference = databaseReference.child(messageKey);
                Trips t = new Trips(source_name,new1.latitude,new1.longitude,
                        new2.latitude,new2.longitude,
                        dest_name,messageKey,"pending","NA",currentUserID);


                databaseReference.setValue(t);

                databaseReference2.child(messageKey).setValue("");

                System.out.println("user trips:" + u.getTripid());
                if(u.getTripid() == null){
                    u.setTripid(t.getTrip_id());
                }
                else{
                    String trip = u.getTripid();
                    trip = trip + "," + t.getTrip_id();
                    u.setTripid(trip);
                }
                databaseReference4.setValue(u);


                Intent intent = new Intent(riderhome.this,RiderAccept.class);
                intent.putExtra(group_key,messageKey);
                startActivity(intent);

                /*databaseReference3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("ActiveMembers")){
                            Sprite doubleBounce = new DoubleBounce();
                            Sprite wave = new Wave();
                            progressBar.setVisibility(View.VISIBLE);
                            requesting.setVisibility(View.VISIBLE);
                            progressBar.setIndeterminateDrawable(doubleBounce);
                        }else{

                            CountDownTimer timer = new CountDownTimer(5000, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {
                                    noactive.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onFinish() {
                                    noactive.setVisibility(View.INVISIBLE); //(or GONE)
                                }
                            }.start();


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/

                DatabaseReference tripdr = FirebaseDatabase.getInstance().getReference("Trips").child(trip);
                /*tripdr.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        flag +=1;
                        if(flag == 1) {
                            Trips t = dataSnapshot.getValue(Trips.class);
                            System.out.println("jgvhbnk fgh");
                            System.out.println(t);
                            Intent intent = new Intent(riderhome.this, Routing.class);
                            intent.putExtra(group_key, t);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/
                /*tripdr.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        flag  +=1;
                        if(flag == 1){
                            Log.d("flag","sdjnsj");
                            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Trips");
                            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot data:dataSnapshot.getChildren()){
                                    Trips t1 = data.getValue(Trips.class);
                                    if(t1.getTrip_id() == messageKey){
                                        System.out.println("jgvhbnk fgh");
                                        System.out.println(t1);
                                        Intent intent = new Intent(riderhome.this, Routing.class);
                                        intent.putExtra(group_key, t);
                                        startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    }


                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/



                /*Sprite doubleBounce = new DoubleBounce();
                Sprite wave = new Wave();
                progressBar.setVisibility(View.VISIBLE);
                requesting.setVisibility(View.VISIBLE);
                progressBar.setIndeterminateDrawable(doubleBounce);*/
            }
        });

        startLocationUpdates();

    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);


        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }


    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("marker", String.valueOf(latLng));

       mp1();
        /*mMap.addMarker(new MarkerOptions().position(latLng)
                .title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));*/
    }

    private void mp1() {

        String name ="";
        Geocoder geocoder = new Geocoder(riderhome.this,Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            name = addresses.get(0).getLocality();
            Log.d("address",address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng)
                .title("I am here"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude,latLng.longitude),15));

    }


    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener <Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }









    private void setMarker1(){
        MarkerOptions options= new MarkerOptions()
                .title("pickup")
                .icon(BitmapDescriptorFactory.defaultMarker())
                .position(new LatLng(new1.latitude,new1.longitude));
        mMap.addMarker(options);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(new1.latitude,new1.longitude),15));




    }

    private void setMarker2(){
        MarkerOptions options= new MarkerOptions()
                .title("drop")
                .icon(BitmapDescriptorFactory.defaultMarker())
                .position(new LatLng(new2.latitude,new2.longitude));
        mMap.addMarker(options);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(new2.latitude,new2.longitude),15));

        /*Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(new1.latitude, new1.longitude), new LatLng(new2.latitude, new2.longitude))
                .width(5)
                .color(Color.RED));*/

        place1 = new MarkerOptions().position(new LatLng(new1.latitude, new1.longitude)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(new2.latitude, new2.longitude)).title("Location 2");

        String url = getUrl(place1.getPosition(), place2.getPosition(), "driving");

        new FetchURL(riderhome.this).execute(url,"driving");
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
        getMenuInflater().inflate(R.menu.riderhome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            googleMap.setMyLocationEnabled(true);
        }

    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }





    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }


}
