package leo.lweather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    static TextView cityNameTextView;
    static EditText editCityText;
    static TextView descriptionTextView;
    static TextView temperatureTextView;
    static TextView windTextView;
    static ImageView icon;
    static Button goButton;

    private double lat;
    private double lng;

    private String apiKey;
    private String uRLStart;
    private String noInternet;
    private String noGPS;
    private String allowLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        apiKey = getString(R.string.apiKeyPrefix) + getString(R.string.privateAPIKey);
        uRLStart = getString(R.string.weatherURLStart);
        noInternet = getString(R.string.noInternet);
        noGPS = getString(R.string.activateGPS);
        allowLocation = getString(R.string.allowLocation);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSwipeRefresh();

        identifyViews();

        setBottomNavigation();

        setCurrentLocationData();
    }

    private void setSwipeRefresh() {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainActivity);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void identifyViews() {
        cityNameTextView = (TextView) findViewById(R.id.cityName);
        editCityText = (EditText) findViewById(R.id.cityNameEdit);
        descriptionTextView = (TextView) findViewById(R.id.description);
        temperatureTextView = (TextView) findViewById(R.id.temperature);
        windTextView = (TextView) findViewById(R.id.wind);
        icon = (ImageView) findViewById(R.id.icon);
        goButton = (Button) findViewById(R.id.goButton);

        editCityText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                editCityText.setText("");
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                populateFields(0, 0, R.id.cityBN);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private void setBottomNavigation() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.locationBN:
                                setCurrentLocationData();
                                return true;
                            case R.id.cityBN:
                                setCitySelectFields();
                                return true;
                            case R.id.randomBN:
                                setRandomLocationData();
                                return true;
                        }
                        return false;
                    }
                });
    }


    private void setCurrentLocationData() {
        hideEditTextAndButton();
        DefineLatAndLng defineLatAndLng = new DefineLatAndLng().invoke();
        lat = defineLatAndLng.getLat();
        lng = defineLatAndLng.getLng();
        populateFields(lat, lng, R.id.locationBN);
    }

    private void hideEditTextAndButton() {
        cityNameTextView.setVisibility(View.VISIBLE);
        editCityText.setVisibility(View.GONE);
        goButton.setVisibility(View.GONE);
    }

    private void showEditTextAndButton() {
        cityNameTextView.setVisibility(View.GONE);
        editCityText.setVisibility(View.VISIBLE);
        goButton.setVisibility(View.VISIBLE);
    }

    private void setCitySelectFields() {
        showEditTextAndButton();
        clearViews();
    }

    private void setRandomLocationData() {
        lat = (-90) + (int) (Math.random() * ((90 - (-90)) + 1));
        lng = (-180) + (int) (Math.random() * ((180 - (-180)) + 1));
        hideEditTextAndButton();
        populateFields(lat, lng, R.id.randomBN);
    }


    private void populateFields(double lat, double lng, int selectedItemId) {
        if (isNetworkConnected()) {
            String URLCenter = getURLCenter(lat, lng, selectedItemId);

            String fullURL = uRLStart + URLCenter + apiKey;

            ParseData task = new ParseData(this);

            task.execute(fullURL);
        } else {
            Toast.makeText(getApplicationContext(), noInternet, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private String getURLCenter(double lat, double lng, int selectedItemId) {
        switch (selectedItemId) {
            case R.id.locationBN:
                String coordinatesInUrlString = "lat=" + String.valueOf(lat) + "&lon=" + String.valueOf(lng);
                return coordinatesInUrlString;

            case R.id.cityBN:
                return "q=" + String.valueOf(editCityText.getText());

            case R.id.randomBN:
                String randomCoordinates = "lat=" + String.valueOf(lat) + "&lon=" + String.valueOf(lng);
                return randomCoordinates;
        }
        return "q=Warsaw";
    }


    private void refresh() {
        finish();
        startActivity(getIntent());
    }

    private void clearViews() {
        cityNameTextView.setText("");
        editCityText.setText("");
        descriptionTextView.setText("");
        temperatureTextView.setText("");
        windTextView.setText("");
        icon.setImageResource(0);
        icon.setVisibility(View.GONE);
    }


    private class DefineLatAndLng {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }

        public DefineLatAndLng invoke() {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), allowLocation, Toast.LENGTH_SHORT).show();

                // Consider calling
                // ActivityCompat#requestPermissions

                return this;
            }

            Location location = null;
            List<String> providers = locationManager.getProviders(true);

            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);

                if (l == null) {
                    continue;
                } else {
                    location = l;
                }
            }

            try {
                lat = location.getLatitude();
                lng = location.getLongitude();

            } catch (Exception e) {
                // keep in mind -- emulators may not be able to access location
                // Consider launching google maps first
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), noGPS, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                lat = 53.3869;
                lng = -2.3489;
            }
            return this;
        }
    }
}
