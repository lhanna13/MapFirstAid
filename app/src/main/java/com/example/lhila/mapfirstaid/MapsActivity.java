package com.example.lhila.mapfirstaid;

import org.json.JSONObject;
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View.OnClickListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/*
* MapsActivity Class, displays maps to find emergency locations
* Holds Call 911 button and Emergency Information Page button
* Contains search location, for locating places through specific addresses
* Created by Lauren Hiland on 6/06/2016.
 */
public class MapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback {


    private static final String GOOGLE_API_KEY = "AIzaSyDqJUraplnrdTAn-XoVoolwmp_ys749N1E ";
    GoogleMap mMap;

    Spinner spinner;
    String[] LocationType = null;
    String[] LocationName = null;

    double latitude;
    double longitude;

    int i = 0; //Keeps track to the # of clicks on call 911 button


    //    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Array of place types
        LocationType = getResources().getStringArray(R.array.place_type);

        // Array of place type names
        LocationName = getResources().getStringArray(R.array.place_type_name);

        // Creating an array adapter with an array of Place types
        // to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, LocationName);

        // Getting reference to the Spinner
        spinner = (Spinner) findViewById(R.id.spr_place_type);

        // Setting adapter on Spinner to set place types
        spinner.setAdapter(adapter);

        Button btnFind;

        // Getting reference to Find Button
        btnFind = (Button) findViewById(R.id.btn_find);

        Button btnCall911;

        // Getting reference to call 911 Button
        btnCall911 = (Button) findViewById(R.id.button4);

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment
            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Google Map
            mMap = fragment.getMap();

            // Enabling MyLocation in Google Map
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            Criteria criteria = new Criteria();

            String provider = locationManager.getBestProvider(criteria, true);

            // Get last known location from GPS
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            }

            locationManager.requestLocationUpdates(provider, 20000, 0, this);


            // Setting click event lister for the find button
            btnFind.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(MapsActivity.this, "Finding Locations...", Toast.LENGTH_LONG).show();
                    int selectedPosition = spinner.getSelectedItemPosition();
                    String typeOfLocation = LocationType[selectedPosition]; //where we select what place we are looking for.

                    StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                    sb.append("location=" + latitude + "," + longitude);
                    sb.append("&radius=7000");
                    sb.append("&types=" + typeOfLocation);
                    sb.append("&sensor=true");
                    sb.append("&key=AIzaSyDqJUraplnrdTAn-XoVoolwmp_ys749N1E");

                    PlacesTask placesTask = new PlacesTask();

                    placesTask.execute(sb.toString());

                }

            });
        }
        //Setting click event listener for the call 911 button
        btnCall911.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                i++; //button was clicked
                Toast.makeText(MapsActivity.this, "Call 911 Clicked " + i + " Times!", Toast.LENGTH_LONG).show();

                if (i == 1) {
                    //Single click do nothing.

                } else if (i == 2) {
                    //Double click, do nothing.
                }
                //triple click call 911 this time.
                else if (i == 3) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:5036252847")); //call this number
                    try {
                        startActivity(callIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getApplicationContext(), "Cannot make call : ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String Url) throws IOException {
        String data = "";
        InputStream inputInfo = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(Url);

            // Making an Http Url Connection, so Url can open connection
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            inputInfo = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputInfo));

            StringBuffer stringBuffer = new StringBuffer();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();

            bufferedReader.close();

        } catch (Exception exception) {
            Log.d("Exception: ", exception.toString());
        }
        //Close out and disconnect from the url
        finally {
            inputInfo.close();
            urlConnection.disconnect();
        }

        return data;
    }

    /*
    Class to download Google Places data.
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String info = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                info = downloadUrl(url[0]);
            } catch (Exception exception) {

                Log.d("Background Task", exception.toString());
            }
            return info;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }

    /*
      Parses the Google Places in JSON format.
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jsonObject;

        // Invoked by execute() method of this object
        // @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> locations = null;
            JSONParser placeJsonParser = new JSONParser();

            try {
                jsonObject = new JSONObject(jsonData[0]);

                /*Getting the parsed data as a List construct */
                locations = placeJsonParser.parseJsonObj(jsonObject);

            } catch (Exception exception) {
                Log.d("Exception", exception.toString());

            }
            return locations;
        }

        // Executed after the complete execution of doInBackground() method
        //@Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            mMap.clear();  // Clears all the existing markers

            for (int j = 0; j < list.size(); j++) {

                // Create a new marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(j);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                //This will be displayed on taping the marker
                markerOptions.title(name + " : " + vicinity);

                //marker cannot be moved
                markerOptions.draggable(false);

                //markers should be Azure (blue) color
                markerOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));


                // Add marker to position
                mMap.addMarker(markerOptions);
            }
        }
    }

    // @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu); // the R.menu.menu points to the menu resource file in the menu folder
        return true;
    }

    // @Override Location changed, move to new position.
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }


    /*
    Check is Google Play Services is even avaliable
     */
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    // @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }//

    //@Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    // Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    //@Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

    }

    /*
    Search for specific locations, ex(addresses, cities, etc...)
     */
    public void onSearch(View view) {
        EditText location_tf = (EditText) findViewById(R.id.editText);
        String location = location_tf.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }


    /*
    Personal Emergency Contact Information
     */
    public void GoToEmergencyInformation(View view) {
        String button_text;
        button_text = ((Button) view).getText().toString();
        if (button_text.equals("Emergency Info")) {
            Intent intent = new Intent(this, EmergencyInfo.class);
            startActivity(intent);
        }
    }

}