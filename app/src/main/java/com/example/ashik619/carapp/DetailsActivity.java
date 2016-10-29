package com.example.ashik619.carapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailsActivity extends AppCompatActivity {
    String make = null;
    String car = null;
    TextView makeView;
    TextView carView;
    TextView typeView;
    TextView pengineView;
    TextView ppowerView;
    TextView ptorqueView;
    TextView dengineView;
    TextView dpowerView;
    TextView dtorqueView;
    TextView dtransView;
    TextView ptransView;
    TextView reviewView;

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String JsonStr = null;
    String HttpJsonStr = null;
    DownloadJson DetailDownloadJson = new DownloadJson();
    URL detailsUrl = null;
    String detailsUrlString = "http://ashik619.pythonanywhere.com/rest/cardetails/?format=json&carname=";
    String type = null;
    String pengine = null;
    String ppower = null;
    String ptorque = null;
    String ptransmission = null;
    String dengine = null;
    String dpower = null;
    String dtorque = null;
    String dtransmission = null;
    String review = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        makeView = (TextView)findViewById(R.id.makeView);
        carView = (TextView)findViewById(R.id.carView);
        typeView = (TextView)findViewById(R.id.typeView);
        pengineView = (TextView)findViewById(R.id.pengineView);
        ppowerView = (TextView)findViewById(R.id.ppowerView);
        ptorqueView = (TextView)findViewById(R.id.ptorqueView);
        ptransView = (TextView)findViewById(R.id.ptansView);
        dengineView = (TextView)findViewById(R.id.dengineView);
        dpowerView = (TextView)findViewById(R.id.dpowerView);
        dtorqueView = (TextView)findViewById(R.id.dtorqueView);
        dtransView = (TextView)findViewById(R.id.dtransView);
        reviewView = (TextView)findViewById(R.id.reviewView);
        Intent intent = getIntent();
        make = intent.getStringExtra("make");
        car = intent.getStringExtra("car");
        car = intent.getStringExtra("car");
        detailsUrlString = detailsUrlString + car;
        detailsUrl = makeUrl(detailsUrlString);
        DetailDownloadJson.execute(detailsUrl);
        makeView.setText("Make :" + make);


    }

    private class DownloadJson extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... url) {
            JsonStr = HttpReq(url[0]);


            return  JsonStr;

        }
        @Override
        protected void onPostExecute(String result) {
            if(result == null){
                noInternetConnection();
            }
            else {

                doparsing(result);
            }

        }
    }

    private void noInternetConnection(){
        Intent in = new Intent(this, NoInternetActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
    }

    private String HttpReq(URL url) {
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            HttpJsonStr = buffer.toString();
            return HttpJsonStr;
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

    }
    URL makeUrl(String urlString){
        try

        {
            URL url = new URL(urlString);
            return url;
        }catch (MalformedURLException e){
            return null;
        }
    }
    void doparsing(String DetailsJson){
        try {
            System.out.println(DetailsJson);
            JSONObject root = new JSONObject(DetailsJson);
            JSONArray detailArray = root.getJSONArray("results");
            int len = detailArray.length();
            JSONObject  details = detailArray.getJSONObject(0);
            type = details.getString("type");
            dengine = details.getString("dengine");
            dpower = details.getString("dpower");
            dtorque = details.getString("dtorque");
            dtransmission = details.getString("dtransmission");
            pengine = details.getString("pengine");
            ppower = details.getString("ppower");
            ptorque =details.getString("ptorque");
            ptransmission = details.getString("ptransmission");
            review = details.getString("text");
            filldetails();

        }catch (Exception e){
            System.out.println("exception");
            throw new RuntimeException(e);
        }
    }
    void filldetails(){
        carView.append(car);
        typeView.setText("Type :"+type);
        pengineView.setText(pengine + "Engine");
        ppowerView.setText(ppower + "Bhp power");
        ptorqueView.setText(ptorque+"Nm Torque");
        ptransView.setText(ptransmission+" Transmission");
        dengineView.setText(dengine + "Engine");
        dpowerView.setText(dpower + "Bhp power");
        dtorqueView.setText(dtorque+"Nm Torque");
        dtransView.setText(dtransmission+" Transmission");
        reviewView.setText(review);


    }
}
