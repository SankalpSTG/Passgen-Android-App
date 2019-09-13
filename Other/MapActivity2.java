package com.stg.bloodbank;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import models.PlaceInfo;

import static android.content.ContentValues.TAG;
import static java.lang.Boolean.FALSE;

public class MapActivity2 extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 15f;
    private static double bigLatitude, bigLongitude;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -160), new LatLng(80, 120));
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    private Marker marker;
    private List<Marker> markers = new ArrayList<Marker>();
    private int MarkerSize;
    private PlaceInfo mplace;
    //Widgets
    private AutoCompleteTextView mSearch_Field;
    private ImageView point_gps, point_info;
    private Button getdonors;
    Spinner BGSelect2;
    //address data
    String Blood_Group2;
    String FAddress = "", FAdminarea = "", FCountryName = "", FFeatureName = "", FLocality = "", FPhone = "", FPostalCode = "", FSubAdminArea = "", FSubLocality = "";
    String LastParameter = "";
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.regmap2);
        handleSSLHandshake();
        mapFragment.getMapAsync(this);
        //SharedPreferences sharedPref = getSharedPreferences("BloodBank", Context.MODE_PRIVATE);
        //String useremail = sharedPref.getString("useremail", "");
        //Toast.makeText(getApplicationContext(), "Email : " + useremail, Toast.LENGTH_SHORT).show();
        getLocationPermission();
        mSearch_Field = findViewById(R.id.search_area2);
        point_gps = findViewById(R.id.gps_locator2);
        point_info = findViewById(R.id.info_button2);
        getdonors = findViewById(R.id.submitlocation2);
        BGSelect2 = findViewById(R.id.bg_spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Blood_Groups_Array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BGSelect2.setAdapter(adapter);
        BGSelect2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Blood_Group2 = (String) adapterView.getItemAtPosition(i);
                if(Blood_Group2.equals("A Positive")){
                    Blood_Group2 = "APavailable";
                }else if(Blood_Group2.equals("A Negative")){
                    Blood_Group2 = "ANavailable";
                }else if(Blood_Group2.equals("AB Positive")){
                    Blood_Group2 = "ABPavailable";
                }else if(Blood_Group2.equals("AB Negative")){
                    Blood_Group2 = "ABNavailable";
                }else if(Blood_Group2.equals("B Positive")){
                    Blood_Group2 = "BPavailable";
                }else if(Blood_Group2.equals("B Negative")){
                    Blood_Group2 = "BNavailable";
                }else if(Blood_Group2.equals("O Positive")){
                    Blood_Group2 = "OPavailable";
                }else if(Blood_Group2.equals("O Negative")){
                    Blood_Group2 = "ONavailable";
                }
                    Toast.makeText(getApplicationContext(), "Blood Group : " + Blood_Group2, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Blood_Group2 = "";
            }
        });
        getdonors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapActivity2.this, DonorList.class);
                i.putExtra("Admin", FAdminarea);
                i.putExtra("SubAdmin", FSubAdminArea);
                i.putExtra("Locality", FLocality);
                i.putExtra("SubLocality", FSubLocality);
                i.putExtra("PostalCode", FPostalCode);
                i.putExtra("Feature", FFeatureName);
                i.putExtra("Country", FCountryName);
                i.putExtra("BloodGroup", Blood_Group2);
                startActivity(i);
            }
        });
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mSearch_Field.setOnItemClickListener(mAutoCompleteClickListener);

        point_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
        point_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Info Button : Clicked");
                try{
                    if(marker.isInfoWindowShown()){
                        Log.e(TAG, "Info Button : Hiding Window");
                        for(int i = 0; i < MarkerSize; i++){
                            marker = markers.get(i);
                            marker.hideInfoWindow();
                        }
                    }else{
                        Log.e(TAG, "Info Button : Showing Window");
                        for(int i = 0; i < MarkerSize; i++){
                            marker = markers.get(i);
                            marker.showInfoWindow();
                        }
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, "Info Button : Null Point Exception : " + e.getMessage());
                }
            }
        });
    }
    private void init(){
        Log.d(TAG, "init: init");


        placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);
        mSearch_Field.setAdapter(placeAutoCompleteAdapter);
        mSearch_Field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int action_id, KeyEvent keyEvent) {
                if(action_id == EditorInfo.IME_ACTION_SEARCH
                        || action_id == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER){
                    //Action for Searching
                    geoLocate();
                }
                return false;
            }
        });
        HideSoftKeyboard();
    }
    private void geoLocate(){
        Log.d(TAG, "geoLocate : Geo Locating");
        String searchedString = mSearch_Field.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity2.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchedString, 1);
        }catch(IOException e){
            Log.e(TAG, "GeoLocate : IOException " + e.getMessage());
        }
        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate : Found this address : " + address.toString());
            //Toast.makeText(TAG, )
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation : Getting Device Current Location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "OnComplete : found location");
                            Location currentLocation = (Location) task.getResult();
                            LatLng latLng;
                            if (currentLocation != null) {
                                latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                moveCamera(latLng, DEFAULT_ZOOM, "Current Location");
                            }
                        } else {
                            Log.d(TAG, "OnComplete : current location is null");
                            Toast.makeText(getApplicationContext(), "Location Unknown", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation : SecurityException " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera : Moving the Camera to Lat : " + latLng.latitude + " lng : " + latLng.longitude);

        mMap.clear();
        if(marker != null){
            marker.remove();
        }
        bigLatitude = latLng.latitude;
        bigLongitude = latLng.longitude;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        /*if(!title.equals("Current Location")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            marker = mMap.addMarker(options);

        }*/
        HideSoftKeyboard();
    }
    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG, "moveCamera : Moving the Camera to Lat : " + latLng.latitude + " lng : " + latLng.longitude);

        mMap.clear();
        if(marker != null){
            marker.remove();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(placeInfo != null){
            try{
                String snippet = "Address : " + placeInfo.getAddress() +"\n" +
                        "Phone Number : " + placeInfo.getPhoneno() +"\n" +
                        "website : " + placeInfo.getWebsiteuri() +"\n";
                MarkerOptions options = new MarkerOptions().position(latLng).title(placeInfo.getName()).snippet(snippet);
                marker = mMap.addMarker(options);
            }catch(NullPointerException e){
                Log.e(TAG, "MoveCamera : Null Pointer Exception : " + e.getMessage());
            }
        }else{
            MarkerOptions options = new MarkerOptions().position(latLng);
            marker = mMap.addMarker(options);
        }
        HideSoftKeyboard();
    }
    private void moveCameraWithoutNull(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG, "moveCamera : Moving the Camera to Lat : " + latLng.latitude + " lng : " + latLng.longitude);

        mMap.clear();
        if(marker != null){
            marker.remove();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(placeInfo != null){
            try{
                String snippet = "Address : " + placeInfo.getAddress() +"\n" +
                        "Phone Number : " + placeInfo.getPhoneno() +"\n" +
                        "website : " + placeInfo.getWebsiteuri() +"\n";
                MarkerOptions options = new MarkerOptions().position(latLng).title(placeInfo.getName()).snippet(snippet);
                marker = mMap.addMarker(options);
            }catch(NullPointerException e){
                Log.e(TAG, "MoveCamera : Null Pointer Exception : " + e.getMessage());
            }
        }else{
            MarkerOptions options = new MarkerOptions().position(latLng);
            marker = mMap.addMarker(options);
        }
        HideSoftKeyboard();
    }
    private void moveCameraonIdle() {
        Log.d(TAG, "moveCameraonIdle : Triggered");

        mMap.clear();
        if(marker != null){
            marker.remove();
        }

        LatLng center = mMap.getCameraPosition().target;

        bigLatitude = center.latitude;
        bigLongitude = center.longitude;
        Toast.makeText(getApplicationContext(), "Lat : " + center.latitude + " lng : " + center.longitude, Toast.LENGTH_SHORT).show();

        MarkerOptions options = new MarkerOptions().position(center).title("This Location");
        marker = mMap.addMarker(options);
        HideSoftKeyboard();
    }

    private void initMap() {
        Log.d(TAG, "initMap : initializing Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.regmap2);
        mapFragment.getMapAsync(MapActivity2.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permissions failed.");
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permissions granted.");
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity2.this));
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d(TAG, "moveCameraonIdle : Triggered");

                LatLng center = mMap.getCameraPosition().target;
                bigLatitude = center.latitude;
                bigLongitude = center.longitude;
                Geocoder geoCoder = new Geocoder(MapActivity2.this);
                try {
                    List<Address> Addresses = geoCoder.getFromLocation(bigLatitude, bigLongitude, 1);
                    if(Addresses != null && !Addresses.isEmpty()){
                        String FSnippet = "";
                        if(Addresses.get(0).getAddressLine(0) != null){
                            FAddress = Addresses.get(0).getAddressLine(0);
                            FSnippet = "Address : " + FAddress + "\n";
                        }
                        if(Addresses.get(0).getFeatureName() != null){
                            FFeatureName = Addresses.get(0).getFeatureName();
                            FSnippet += "Landmark : " + FFeatureName;
                        }
                        if(Addresses.get(0).getLocality() != null){
                            if(Addresses.get(0).getSubLocality() != null){
                                FSubLocality = Addresses.get(0).getSubLocality();
                                FSnippet += "\nLocality : " + FSubLocality;
                                FLocality = Addresses.get(0).getLocality();
                                FSnippet += ", " + FLocality;
                            }else{
                                FLocality = Addresses.get(0).getLocality();
                                FSnippet += "\nLocality : " + FLocality;
                            }
                        }
                        if(Addresses.get(0).getAdminArea() != null){
                            if(Addresses.get(0).getSubAdminArea() != null){
                                FSubAdminArea = Addresses.get(0).getSubAdminArea();
                                FAdminarea = Addresses.get(0).getAdminArea();
                                FSnippet += "\nArea : " + FSubAdminArea + ", " + FAdminarea;
                            }else{
                                FAdminarea = Addresses.get(0).getAdminArea();
                                FSnippet += "\nArea : " + FAdminarea;
                            }
                        }
                        if(Addresses.get(0).getPostalCode() != null){
                            FPostalCode = Addresses.get(0).getPostalCode();
                            FSnippet += "\nPostal Code : " + FPostalCode;
                        }
                        if(Addresses.get(0).getCountryName() != null){
                            FCountryName = Addresses.get(0).getCountryName();
                            FSnippet += "\nCountry : " + FCountryName;
                        }
                        if(Addresses.get(0).getPhone() != null){
                            FPhone = Addresses.get(0).getPhone();
                            FSnippet += "\nPhone No : " + FPhone;
                        }
                        if(!FSubLocality.equals(LastParameter)){
                            LastParameter = FSubLocality;
                            mMap.clear();
                            if(marker != null){
                                marker.remove();
                            }
                            getbloodbanks();
                        }

                        //MarkerOptions options = new MarkerOptions().position(center).title("This Location").snippet(FSnippet);
                        //addressview.setText(FSnippet);
                        //marker = mMap.addMarker(options);
                    }else{
                        //MarkerOptions options = new MarkerOptions().position(center).title("This Location");
                        //marker = mMap.addMarker(options);
                        //addressview.setText("");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "onCameraIdle : IOexception : " + e.getMessage());
                }
                HideSoftKeyboard();
            }
        });
        Toast.makeText(getApplicationContext(), "Map Is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: ready");
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
        /*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
    private void HideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            HideSoftKeyboard();
            final AutocompletePrediction item = placeAutoCompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult : Query did not completed successfully " + places.getStatus().toString() );
                places.release();
            }else{
                mplace = new PlaceInfo();
                final Place place = places.get(0);
                try {
                    mplace.setName(place.getName().toString());
                    mplace.setId(place.getId());
                    mplace.setAddress(place.getAddress().toString());
                    mplace.setAttributions(place.getAttributions().toString());
                    mplace.setLatLng(place.getLatLng());
                    mplace.setPhoneno(place.getPhoneNumber().toString());
                    mplace.setWebsiteuri(place.getWebsiteUri());
                    mplace.setRating(place.getRating());
                    Log.d(TAG, "onResult : place : " + mplace.toString());
                }catch (NullPointerException e) {
                    Log.d(TAG, "onResult : Null Pointer Exception : " + e.getMessage());
                }
                moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mplace);
                places.release();
            }
        }
    };
    private void getbloodbanks(){
        Log.d(TAG, "ShowBloodBanks : Triggered");
        Log.d(TAG, FAddress + " " + FAdminarea + " " + FCountryName + " " + FFeatureName + " " + FLocality + " " + FSubAdminArea + " " + FSubLocality);
        String cancel_req_tag = "register";
        String IpAd = getResources().getString(R.string.IPaddress);
        String URL_FOR_REGISTRATION = "https://" + IpAd + "/NOT_OF_THIS_SITE/bloodbank/getbloodbanks.php";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_REGISTRATION,  new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), jObj.getString("numbanks"), Toast.LENGTH_LONG).show();
                        showbloodbanks(jObj);
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("Phone", FPhone );
                params.put("SubLocality", FSubLocality);
                params.put("Locality", FLocality);
                params.put("Feature", FFeatureName);
                params.put("PostalCode", FPostalCode);
                params.put("SubAdminArea", FSubAdminArea);
                params.put("AdminArea", FAdminarea);
                params.put("Country", FCountryName);
                params.put("BloodGroup", Blood_Group2);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }
    private void showbloodbanks(final JSONObject fetchedBanks) throws JSONException {
        MarkerSize = fetchedBanks.getInt("numbanks");
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity2.this));
        for(int i = 1; i <= MarkerSize; i++){

            String Nsnippet = "Name : " + fetchedBanks.getJSONObject("bank" + i).getString("name") +
                    "\nAddress : " + fetchedBanks.getJSONObject("bank" + i).getString("address") +
                    "\nPhone : " + fetchedBanks.getJSONObject("bank" + i).getString("phone") +
                    "\nEmail : " + fetchedBanks.getJSONObject("bank" + i).getString("email");
            double Nlatitude = Double.parseDouble(fetchedBanks.getJSONObject("bank" + i).getString("latitude"));
            double Nlongitude = Double.parseDouble(fetchedBanks.getJSONObject("bank" + i).getString("longitude"));
            LatLng NlatLng = new LatLng(Nlatitude, Nlongitude);
            MarkerOptions Noptions = new MarkerOptions().position(NlatLng).title(fetchedBanks.getJSONObject("bank" + i).getString("name")).snippet(Nsnippet);
            marker = mMap.addMarker(Noptions);
            markers.add(marker);
        }
        Log.d(TAG, "Marker Size : " + MarkerSize);

    }
}
