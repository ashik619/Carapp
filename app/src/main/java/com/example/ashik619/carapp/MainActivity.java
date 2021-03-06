 package com.example.ashik619.carapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

 public class MainActivity extends AppCompatActivity {
     HttpURLConnection urlConnection = null;
     BufferedReader reader = null;
     TextView make;
     TextView car;
     List<String> Makes = new ArrayList<String>();
     List<String> Cars = new ArrayList<String>();
     Context context = MainActivity.this;
     String JsonStr = null;
     String HttpJsonStr = null;
     String brandUrlString = "http://ashik619.pythonanywhere.com/rest/brands/?format=json";
     String modelUrlString = "http://ashik619.pythonanywhere.com/rest/models/?format=json";
     String selectedBrand = null;
     String selectedModel = null;
     URL brandUrl = null;
     URL modelUrl = null;
     AlertDialog.Builder brandADB;
     AlertDialog brandADBObject;
     AlertDialog.Builder modelADB;
     AlertDialog modelADBObject;
     DownloadJson BranDownloadJson = new DownloadJson();
     DownloadJson ModelDownloadJson = new DownloadJson();
     boolean task = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        make = (TextView)findViewById(R.id.textView1);
        car = (TextView)findViewById(R.id.textView3);
        brandADB = new AlertDialog.Builder(this);
        modelADB = new AlertDialog.Builder(this);
        //this.ParseBrandJson(temp);
        brandUrl = makeUrl(brandUrlString);
        BranDownloadJson.execute(brandUrl);
        task = false;
    }
     @Override
     protected void onResume() {
         //Toast.makeText(MainActivity.this,"restarting", Toast.LENGTH_SHORT).show();
         super.onResume();
         System.out.println("restaring");
         if(selectedBrand != null)
             killactivity();






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
                 System.out.println("on post excecute");
                 doparsing(result);
             }
             //while(selectedBrand == null)
             //{continue;}



         }
     }
     String HttpReq(URL url) {
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
     void doparsing(String result){
         if(selectedBrand == null){
             System.out.println("brand parsing");

             ParseBrandJson(result);
         } else if(selectedModel == null) {
             System.out.println("model parsing");
             parseModelJson(result);
         }
     }

     void ParseBrandJson(String BrandJson) {
         try {
             JSONObject root = new JSONObject(BrandJson);
             int count = root.getInt("count");
             JSONArray brandsarray = root.getJSONArray("results");
             final int len = brandsarray.length();
             for (int i=0; i<len; i++) {
                 JSONObject brand = brandsarray.getJSONObject(i);
                 String bname = brand.getString("cname");
                 //Toast.makeText(MainActivity.this,"brand parsing"+bname, Toast.LENGTH_SHORT).show();
                 System.out.println("parsing" + bname);

                 Makes.add(bname);
             }

             final String[] barray = Makes.toArray(new String[0]);
             brandADB.setTitle("Select Make");
             brandADB.setItems(barray, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int item) {
                     if(task == true){
                         System.out.println("killing");
                         killactivity();
                     }
                     else {
                         selectedBrand = barray[item];
                         startModelTask();
                         task = true;
                         System.out.println("model started");
                         make.setText(selectedBrand);
                     }
                 }
             });
             brandADBObject = brandADB.create();
             BranDownloadJson.cancel(true);



         } catch (Exception e){
             System.out.println("exception");
             throw new RuntimeException(e);
         }
     }
     void parseModelJson(String ModelJson){
         try {
             JSONObject root = new JSONObject(ModelJson);
             int count = root.getInt("count");
             JSONArray modelsArray = root.getJSONArray("results");
             final int lenc = modelsArray.length();
             for (int i=0; i<lenc; i++) {
                 JSONObject brand = modelsArray.getJSONObject(i);
                 String carname = brand.getString("cmodel_name");
                 Cars.add(carname);
                 System.out.println(carname);
             }
             final String[] cararray = Cars.toArray(new String[0]);
             //
             //ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.select_dialog_item, barray);
             modelADB.setTitle("Select Car");
             modelADB.setItems(cararray, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int item) {
                     selectedModel = cararray[item];
                     car.setText(selectedModel);
                 }
             });
             modelADBObject = modelADB.create();

         } catch (Exception e){
             System.out.println("exception");
             throw new RuntimeException(e);

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
     public void SelectBrand(View v){
         if(selectedBrand != null) {
             System.out.println("killing in adb show");
             killactivity();
         }
         else if(brandADBObject != null){
             brandADBObject.show();
         }

     }
     public void SelectModel(View v){
         if(modelADBObject != null) {
             modelADBObject.show();
         }
     }

     public void noInternetConnection(){
         Intent in = new Intent(this, NoInternetActivity.class);
         in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         startActivity(in);
     }
     public void startDetailsActivity(View v)
     {
         Intent in1 = new Intent(this, DetailsActivity.class);
         in1.putExtra("make", selectedBrand);
         in1.putExtra("car", selectedModel);

         startActivity(in1);
     }
     public void startModelTask()
     {
         if(selectedBrand != null) {
             modelUrlString = modelUrlString + "&brandname=" + selectedBrand;
             modelUrl = makeUrl(modelUrlString);
             this.

             ModelDownloadJson.execute(modelUrl);
         }
     }
     private void killactivity(){
         this.recreate();
     }



}
