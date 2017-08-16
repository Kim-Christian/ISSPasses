package com.skyevent.isspasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skyevent.isspasses.data.Geometry;
import com.skyevent.isspasses.data.Time;
import com.skyevent.isspasses.service.GeocodingServiceCallback;
import com.skyevent.isspasses.service.GoogleMapsGeocodingService;
import com.skyevent.isspasses.service.ISSPassTimesService;
import com.skyevent.isspasses.service.PassTimesServiceCallback;

public class ISSPassesActivity extends AppCompatActivity implements GeocodingServiceCallback, PassTimesServiceCallback {

    private GoogleMapsGeocodingService locationService;
    private ISSPassTimesService passTimesService;
    private ProgressDialog dialog;
    private String address;
    private EditText addressField;
    private Button submitButton;
    private TextView longitudeView;
    private TextView latitudeView;
    private TextView durationView;
    private TextView risetimeView;
    private int numberOfPasses = 1;
    private InputMethodManager inputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isspasses);

        addressField = (EditText) findViewById(R.id.address);
        submitButton = (Button) findViewById(R.id.submit);
        longitudeView = (TextView) findViewById(R.id.longitude);
        latitudeView = (TextView) findViewById(R.id.latitude);
        durationView = (TextView) findViewById(R.id.duration);
        risetimeView = (TextView) findViewById(R.id.risetime);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        addressField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });

        inputManager =
                (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        locationService = new GoogleMapsGeocodingService(this);
        passTimesService = new ISSPassTimesService(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
    }

    private void search() {
        hideKeyboard();
        dialog.show();
        longitudeView.setText("Longitude:");
        latitudeView.setText("Latitude:");
        durationView.setText("Duration:");
        risetimeView.setText("Risetime:");
        address = addressField.getText().toString();
        locationService.refreshLocation(address);
    }

    private boolean hideKeyboard() {
        try {
            inputManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void serviceSuccess(Geometry geometry) {
        double latitude = geometry.getLocation().getLatitude();
        double longitude = geometry.getLocation().getLongitude();
        longitudeView.setText("Longitude: " + Double.toString(longitude));
        latitudeView.setText("Latitude: " + Double.toString(latitude));
        passTimesService.refreshTime(latitude, longitude, numberOfPasses);
    }

    @Override
    public void serviceSuccess(Time passTime) {
        dialog.hide();
        int timeStamp = passTime.getRisetime();
        java.util.Date time = new java.util.Date((long)timeStamp*1000);

        durationView.setText("Duration: " + Double.toString(passTime.getDuration() / 60) + " min");
        risetimeView.setText("Risetime: " + time.toString());
    }

    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
    }
}