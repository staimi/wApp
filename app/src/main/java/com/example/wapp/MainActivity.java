package com.example.wapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button button;
    TextView tempInfo, textView, textView2, cityDescription;
    EditText cityName;
    Bitmap myBitmap;
    ImageView imageView2;
    String result = "";
    String wPic2 = "";
    String setCity = "";

    public void buttonDownload(View view) {
        try {
            dwnl download = new dwnl();
            setCityName(view);
            download.execute(setCity);
            ///
            imageReader task = new imageReader();
            task.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class imageReader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            Log.i("In buttonDownload", wPic2); 
            try {
                URL url = new URL(wPic2);
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                connect.connect();
                InputStream inStream = connect.getInputStream();
                myBitmap = BitmapFactory.decodeStream(inStream);

                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView2.setImageBitmap(myBitmap);
        }
    }


    public void setCityName(View view)
    {
        if(cityName.getText().toString().isEmpty() == false){
            String cityNameReplaced = cityName.getText().toString().replace(" ", "_");
            setCity = ("http://api.weatherstack.com/current?access_key=e0fc0deb036daafc30de759fddfd2973&query=").concat(cityNameReplaced);
        }
        else{
            setCity = "http://api.weatherstack.com/current?access_key=e0fc0deb036daafc30de759fddfd2973&query=london";
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        tempInfo = findViewById(R.id.tempInfo);
        cityName = findViewById(R.id.getCity);
        textView = findViewById(R.id.textView);
        imageView2 = findViewById(R.id.imageView2);
        textView2 = findViewById(R.id.textView2);
        cityDescription = findViewById(R.id.cityDescription);


    }

    public class dwnl extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            result = "";
            URL url;
            HttpURLConnection connection = null;

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader read = new InputStreamReader(in);
                int data = read.read();

                while (data != -1) {
                    char current = (char) data;
                    result = result + current;
                    data = read.read();
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Info  : ", "Error");
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            try {
                 jsonObject = new JSONObject(result);
                 String weatherInfo = jsonObject.getString("current");
                 String request  = jsonObject.getString("request");
                 JSONObject req = new JSONObject(request);
                 JSONObject arr = new JSONObject(weatherInfo);
                 String temperature = arr.getString("temperature");
                 String weatherPicture = arr.getString("weather_icons");
                 String wDescription = arr.getString("weather_descriptions");
                 String obserwationTime = arr.getString("observation_time");
                 String cityName = req.getString("query");

                 // set temp

                tempInfo.setText(temperature + "°");

                //set weather pic

                String wPic = weatherPicture.replace("[\"" , "");
                wPic2 = wPic.replace("\"]", "");
                Log.i("Repleaced", wPic2);

                //set weather description
                String w1 = wDescription.replace("[\"", "");
                String w2 = w1.replace("\"]", "");
                textView.setText(w2);

                //set obserwation time
                textView2.setText(obserwationTime);

                // set cityDescription
                cityDescription.setText(cityName);


                 } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("Error", "Wrong JSON file");
            }
        }
    }
}