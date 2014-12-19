package type.proy.com.json;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {
    /**
     *
     */
    public String readJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        //Para conectarse a un servicio web, la aplicación necesita en primer lugar conectarse al servidor utilizando HTTP
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }
    /*****************************************************************/

    private class ReadWeatherJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject weatherObservationItems =
                        new JSONObject(jsonObject.getString("weatherObservation"));

                Toast.makeText(getBaseContext(),
                        weatherObservationItems.getString("clouds") +
                                " - " + weatherObservationItems.getString("stationName"),
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }
        }
    }


    /*****************************************************************/
    private class ReadPlacesFeedTask extends AsyncTask
            <String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray postalCodesItems = new
                        JSONArray(jsonObject.getString("postalCodes"));

                //---print out the content of the json feed---
                for (int i = 0; i < postalCodesItems.length(); i++) {
                    JSONObject postalCodesItem =
                            postalCodesItems.getJSONObject(i);
                    Toast.makeText(getBaseContext(),
                            postalCodesItem.getString("postalCode") + " - " +
                                    postalCodesItem.getString("placeName") + ", " +
                                    postalCodesItem.getString("countryCode"),
                            Toast.LENGTH_SHORT).show();
                   // Toast.makeText(getBaseContext(),"Vueltaaaaaa",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
            }
        }
    }

    /*****************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    /*****************************************************************/

    public void btnGetWeather(View view) {
        EditText txtLat = (EditText) findViewById(R.id.txtLatitud);
        EditText txtLong = (EditText) findViewById(R.id.txtLong);

        new ReadWeatherJSONFeedTask().execute(
                "http://ws.geonames.org/findNearByWeatherJSON?lat=" +
                        txtLat.getEditableText().toString() + "&lng=" +
                        txtLong.getText().toString());
    }
    /*****************************************************************/
    public void btnGetPlaces(View view) {
        EditText txtPostalCode = (EditText) findViewById(R.id.txtPostalCode);
        new ReadPlacesFeedTask().execute(
                "http://api.geonames.org/postalCodeSearchJSON?postalcode=" +
                        txtPostalCode.getEditableText().toString() +
                        "&maxRows=10&username=demo");
    }
    /*****************************************************************/
}
