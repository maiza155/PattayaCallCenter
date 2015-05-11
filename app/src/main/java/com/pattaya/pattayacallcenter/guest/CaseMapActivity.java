package com.pattaya.pattayacallcenter.guest;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pattaya.pattayacallcenter.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CaseMapActivity extends ActionBarActivity implements GoogleMap.OnMapLongClickListener {
    LatLng myLatLng;
    LatLng markPos;
    ImageButton btn;
    TextView titleTextView;
    double latitude = 17.385044;
    double longitude = 78.486671;
    MapView mMapView;
    private GoogleMap googleMap;
    private Location location;
    private LatLng userLocation;
    private String addressdetail = "";
    List<Address> addresses = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_map);


        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_back, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);


        titleTextView.setText(getResources().getString(R.string.profile_place));
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setClickListener();


        GPSTracker mGPS = new GPSTracker(this);
        userLocation = new LatLng(latitude, longitude);
        if (mGPS.canGetLocation) {
            location = mGPS.getLocation();
            if (location != null) {
                Log.e("TAG LOCATION", "" + location);

                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                markPos = userLocation;
                new TaskGetAddress(userLocation).execute();
                //addressdetail = getAddress(userLocation);
                myLatLng = userLocation;

            } else {
                //LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                //locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,1000,10, (android.location.LocationListener) this);
                //mGPS.showSettingsAlert();
                Log.d("TAG", "NULL LOCATION");
            }
        } else {
            mGPS.showSettingsAlert();
        }
        //googleMap = mMapView.getMap();
        // latitude and longitude

        //googleMap.setMyLocationEnabled(true);
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        googleMap.addMarker(new MarkerOptions().position(userLocation)
                .title(String.format("%.3f,%.3f", userLocation.latitude, userLocation.longitude)));


       /* Marker kiel = googleMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("Kiel")
                .snippet("Kiel is cool")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_launcher)));*/

        // Move the camera instantly to hamburg with a zoom of 15.


        try {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        } catch (Exception e) {
            System.out.println("savedInstanceState = [" + savedInstanceState + "]");
        }


        googleMap.setOnMapLongClickListener(this);
    }


    public String getAddress(final LatLng userLocation) {
        String addressdetail = "";
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            addresses = gcd.getFromLocation(userLocation.latitude, userLocation.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                public void run() {
                    // some code #3 (Write your code here to run in UI thread)
                    Toast.makeText(getApplicationContext(),
                            "Google Play Service not Available",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });

        }

        if (addresses == null) {
            System.out.println("null");
            addresses = getFromLocation(userLocation.latitude, userLocation.longitude, 1);
        }
        if (addresses != null) {
            if (addresses.size() > 0) {
                addressdetail = (addresses.get(0).getThoroughfare() == null) ? "" : addressdetail + " " + addresses.get(0).getThoroughfare();
                addressdetail = (addresses.get(0).getSubLocality() == null) ? "" : addressdetail + " " + addresses.get(0).getSubLocality();
                addressdetail = (addresses.get(0).getLocality() == null) ? "" : addressdetail + " " + addresses.get(0).getLocality();
                addressdetail = (addresses.get(0).getAdminArea() == null) ? "" : addressdetail + " " + addresses.get(0).getAdminArea();
                addressdetail = (addresses.get(0).getCountryCode() == null) ? "" : addressdetail + " " + addresses.get(0).getCountryCode();
                addressdetail = (addresses.get(0).getPostalCode() == null) ? "" : addressdetail + " " + addresses.get(0).getPostalCode();
                addressdetail = (addresses.get(0).getCountryName() == null) ? "" : addressdetail + " " + addresses.get(0).getCountryName();

                //System.out.println(addressdetail);

            }
        }

        return addressdetail;
    }


    public static List<Address> getFromLocation(double lat, double lng, int maxResult) {

        String address = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language=" + Locale.getDefault().getCountry(), lat, lng);
        HttpGet httpGet = new HttpGet(address);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        List<Address> retList = null;

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(stringBuilder.toString());


            retList = new ArrayList<Address>();


            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    String indiStr = result.getString("formatted_address");
                    Address addr = new Address(Locale.ITALY);
                    addr.setAddressLine(0, indiStr);
                    retList.add(addr);
                }
            }


        } catch (ClientProtocolException e) {
            Log.e("MyGeocoder", "Error calling Google geocode webservice.", e);
        } catch (IOException e) {
            Log.e("MyGeocoder", "Error calling Google geocode webservice.", e);
        } catch (JSONException e) {
            Log.e("MyGeocoder", "Error parsing Google geocode webservice response.", e);
        }

        return retList;
    }


    private void setClickListener() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng point) {
        googleMap.clear();
        markPos = point;
        addressdetail = "";

        new TaskGetAddress(point).execute();
        googleMap.addMarker(new MarkerOptions().position(point).title(
                String.format("%.3f,%.3f", point.latitude, point.longitude)));

        String text = (addressdetail.matches("")) ? "" : "Add marker Address :" + addressdetail + "\n";
        Toast.makeText(getApplicationContext(),
                text + " point :" + String.format("%.3f,%.3f", point.latitude, point.longitude),
                Toast.LENGTH_LONG)
                .show();


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_case_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent i = new Intent();
            //i.putExtra("loc", "<lat>" + markPos.latitude + "<lat>\n" + "<lng>" + markPos.longitude + "<lng>");
            i.putExtra("detail", addressdetail);
            setResult(Activity.RESULT_OK, i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class TaskGetAddress extends AsyncTask<Void, Void, String> {

        private LatLng mLoc;

        TaskGetAddress(LatLng mLoc) {
            this.mLoc = mLoc;
        }

        @Override
        protected String doInBackground(Void... params) {

            return getAddress(mLoc);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            addressdetail = s;
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>" + addressdetail);
            if (!s.matches("")) {
                googleMap.addMarker(new MarkerOptions().position(mLoc)
                        .title("" + addressdetail));
                Toast.makeText(getApplicationContext(),
                        "Your location :" + addressdetail,
                        Toast.LENGTH_LONG)
                        .show();
            }


        }
    }
}
