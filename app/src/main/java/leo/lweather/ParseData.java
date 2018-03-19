package leo.lweather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ParseData extends AsyncTask<String, Void, String> {
    public MainActivity activity;
    private String notFound;
    private String iconURL;

    public ParseData(MainActivity mainActivity) {
        this.activity = mainActivity;
        notFound = mainActivity.getResources().getString(R.string.locationNotFound);
        iconURL = mainActivity.getResources().getString(R.string.iconURLStart);
    }

    @Override
    protected String doInBackground(String... urls) {
        String jSonData = "";
        URL url;
        HttpURLConnection urlConnection = null;


        try {
            url = new URL(urls[0]);

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();

            while (data != -1) {

                char current = (char) data;

                jSonData += current;
                data = reader.read();

            }

            return jSonData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String jSonData) {
        super.onPostExecute(jSonData);

        if (jSonData == null) {
            populateFieldsMainActivity(notFound);
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(jSonData);
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weatherData = weatherArray.getJSONObject(0);
            JSONObject mainData = new JSONObject(jsonObject.getString("main"));
            JSONObject windData = new JSONObject(jsonObject.getString("wind"));
            JSONObject coordinates = new JSONObject(jsonObject.getString("coord"));

            String latitude = coordinates.getString("lat");
            String longitude = coordinates.getString("lon");

            String locationName = jsonObject.getString("name");
            if (locationName.length() < 1) {
                locationName = "Lat: " + latitude + ", " + "Lon: " + longitude;
            }

            String description = weatherData.getString("description");

            String icon = weatherData.getString("icon");
            String fullIconURL = iconURL + icon + ".png";
            new DownloadImageTask(MainActivity.icon).execute(fullIconURL);

            int tempCelcius = getTempCelcius(mainData);

            String windSpeed = windData.getString("speed");
            String windDirectionDegrees = windData.getString("deg");


            populateFieldsMainActivity(String.valueOf(tempCelcius), locationName, description, windSpeed, windDirectionDegrees);
            setTextViewColor(Color.BLACK);

        } catch (Exception e) {

            e.printStackTrace();
        }


    }

    private String convertDegreeToCardinalDirection(String stringWindDegrees) {
        String cardinalDirection = null;
        double directionInDegrees = Double.parseDouble(stringWindDegrees);

        if ((directionInDegrees >= 348.75) && (directionInDegrees <= 360) ||
                (directionInDegrees >= 0) && (directionInDegrees <= 11.25)) {
            cardinalDirection = "N";
        } else if ((directionInDegrees >= 11.25) && (directionInDegrees <= 33.75)) {
            cardinalDirection = "NNE";
        } else if ((directionInDegrees >= 33.75) && (directionInDegrees <= 56.25)) {
            cardinalDirection = "NE";
        } else if ((directionInDegrees >= 56.25) && (directionInDegrees <= 78.75)) {
            cardinalDirection = "ENE";
        } else if ((directionInDegrees >= 78.75) && (directionInDegrees <= 101.25)) {
            cardinalDirection = "E";
        } else if ((directionInDegrees >= 101.25) && (directionInDegrees <= 123.75)) {
            cardinalDirection = "ESE";
        } else if ((directionInDegrees >= 123.75) && (directionInDegrees <= 146.25)) {
            cardinalDirection = "SE";
        } else if ((directionInDegrees >= 146.25) && (directionInDegrees <= 168.75)) {
            cardinalDirection = "SSE";
        } else if ((directionInDegrees >= 168.75) && (directionInDegrees <= 191.25)) {
            cardinalDirection = "S";
        } else if ((directionInDegrees >= 191.25) && (directionInDegrees <= 213.75)) {
            cardinalDirection = "SSW";
        } else if ((directionInDegrees >= 213.75) && (directionInDegrees <= 236.25)) {
            cardinalDirection = "SW";
        } else if ((directionInDegrees >= 236.25) && (directionInDegrees <= 258.75)) {
            cardinalDirection = "WSW";
        } else if ((directionInDegrees >= 258.75) && (directionInDegrees <= 281.25)) {
            cardinalDirection = "W";
        } else if ((directionInDegrees >= 281.25) && (directionInDegrees <= 303.75)) {
            cardinalDirection = "WNW";
        } else if ((directionInDegrees >= 303.75) && (directionInDegrees <= 326.25)) {
            cardinalDirection = "NW";
        } else if ((directionInDegrees >= 326.25) && (directionInDegrees <= 348.75)) {
            cardinalDirection = "NNW";
        } else {
            cardinalDirection = "?";
        }

        return cardinalDirection;
    }

    private void populateFieldsMainActivity(String notFound) {
        populateFieldsMainActivity(null, notFound, null, null, null);
        MainActivity.icon.setImageResource(0);
    }

    private void populateFieldsMainActivity(String tempCelcius, String locationName, String description, String windSpeed, String windDirectionDegrees) {
        MainActivity.cityNameTextView.setText(locationName);
        MainActivity.editCityText.setText(locationName);

        setDescriptionText(description);

        setWindAndTempText(tempCelcius, windSpeed, windDirectionDegrees);
    }

    private void setWindAndTempText(String tempCelcius, String windSpeed, String windDirectionDegrees) {
        if (tempCelcius == null && windSpeed == null) {
            MainActivity.temperatureTextView.setText("");
            MainActivity.windTextView.setText("");

        } else {
            String windDirection = convertDegreeToCardinalDirection(windDirectionDegrees);
            MainActivity.temperatureTextView.setText(tempCelcius + "\u00b0" + "C");
            MainActivity.windTextView.setText(windSpeed + " m/s --> " + windDirection);
        }
    }

    private void setDescriptionText(String description) {
        if(activity.getResources().getString(R.string.privateAPIKey).length() < 1){
            MainActivity.descriptionTextView.setText(activity.getResources().getString(R.string.insertAPIKey));
            MainActivity.icon.setVisibility(View.GONE);
        }
        else{
            MainActivity.descriptionTextView.setText(description);
        }
    }

    private void setTextViewColor(int color) {
        MainActivity.cityNameTextView.setTextColor(color);
        MainActivity.descriptionTextView.setTextColor(color);
        MainActivity.temperatureTextView.setTextColor(color);
        MainActivity.windTextView.setTextColor(color);
    }


    private int getTempCelcius(JSONObject mainData) throws JSONException {
        double tempKelvin = Double.parseDouble(mainData.getString("temp"));
        return (int) (tempKelvin - 273.15);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bitmapImageView;

        public DownloadImageTask(ImageView bmImage) {
            this.bitmapImageView = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bitmapImageView.setImageBitmap(result);
            bitmapImageView.setVisibility(View.VISIBLE);
        }
    }
}
