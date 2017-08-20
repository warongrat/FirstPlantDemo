package project.fertilizerandwatercontrol;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


public class weatherFragment extends Fragment  {
    TextView textViewResult, textViewTemp;
    ImageView wIcon;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_weather);
        textViewResult = (TextView) view.findViewById(R.id.result);
        textViewTemp = (TextView) view.findViewById(R.id.temp);
        wIcon = (ImageView) view.findViewById(R.id.weatherIcon);
        getActivity().setTitle("Weather");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        new OpenWeatherMapTask(wIcon,
              textViewResult).execute();
         new OpenWeatherMapTask1(
                textViewTemp).execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                new OpenWeatherMapTask(wIcon,
                        textViewResult).execute();
                new OpenWeatherMapTask1(
                        textViewTemp).execute();
            }
        });
        return view;
    }
         public class OpenWeatherMapTask extends AsyncTask<Void, Void, String> {
            TextView tvResult;
             ImageView weatherIcon;
             long sunrise, sunset, currentTime;
            String dummyAppid = "502022810de690c9f8b440a52a81ebcf";
            String queryWeather = "http://api.openweathermap.org/data/2.5/weather?q=Phuket, TH";
            String queryDummyKey = "&appid=" + dummyAppid;
            OpenWeatherMapTask(ImageView weatherIcon , TextView tvResult) {
                this.tvResult = tvResult;
                this.weatherIcon = weatherIcon;
            }
             @Override
             protected String doInBackground(Void... params) {
                 String result = "";
                 String queryReturn;

                 String query = null;
                 try {
                     query = queryWeather + queryDummyKey;
                     queryReturn = sendQuery(query);
                     result += ParseJSON(queryReturn);
                 } catch (UnsupportedEncodingException e) {
                     e.printStackTrace();
                     queryReturn = e.getMessage();
                 } catch (IOException e) {
                     e.printStackTrace();
                     queryReturn = e.getMessage();
                 }


                 return result;
            }

            @Override
            protected void onPostExecute(String s) {
                tvResult.setText(s);
                if(currentTime >= sunrise && currentTime < sunset) {
                    if(tvResult.getText().toString().equals("clear sky"))
                        weatherIcon.setImageResource(R.drawable.sunny);
                    else if(tvResult.getText().toString().equals("few clouds"))
                        weatherIcon.setImageResource(R.drawable.cloudy_sun);
                    else if(tvResult.getText().toString().equals("scattered clouds"))
                        weatherIcon.setImageResource(R.drawable.cloudy);
                    else if(tvResult.getText().toString().equals("broken clouds"))
                        weatherIcon.setImageResource(R.drawable.cloudy_broken);
                    else if(tvResult.getText().toString().equals("shower rain"))
                        weatherIcon.setImageResource(R.drawable.rainy);
                    else if(tvResult.getText().toString().equals("rain"))
                        weatherIcon.setImageResource(R.drawable.rainy);
                    else if(tvResult.getText().toString().equals("thunderstorm"))
                        weatherIcon.setImageResource(R.drawable.thunder);
                    else if(tvResult.getText().toString().equals("mist"))
                        weatherIcon.setImageResource(R.drawable.mist);
                }
                else{
                    if(tvResult.getText().toString().equals("clear sky"))
                        weatherIcon.setImageResource(R.drawable.night);
                    else if(tvResult.getText().toString().equals("few clouds"))
                        weatherIcon.setImageResource(R.drawable.cloudy_moon);
                    else if(tvResult.getText().toString().equals("scattered clouds"))
                        weatherIcon.setImageResource(R.drawable.cloudy);
                    else if(tvResult.getText().toString().equals("broken clouds"))
                        weatherIcon.setImageResource(R.drawable.cloudy_broken);
                    else if(tvResult.getText().toString().equals("shower rain"))
                        weatherIcon.setImageResource(R.drawable.rainy);
                    else if(tvResult.getText().toString().equals("rain"))
                        weatherIcon.setImageResource(R.drawable.rainy);
                    else if(tvResult.getText().toString().equals("thunderstorm"))
                        weatherIcon.setImageResource(R.drawable.thunder);
                    else if(tvResult.getText().toString().equals("mist"))
                        weatherIcon.setImageResource(R.drawable.mist);
                }

            }

            private String sendQuery(String query) throws IOException {
                String result = "";
                URL searchURL = new URL(query);
                HttpURLConnection httpURLConnection = (HttpURLConnection) searchURL.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(
                            inputStreamReader,
                            8192);

                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }

                    bufferedReader.close();
                }

                return result;
            }

            private String ParseJSON(String json) {
                String jsonResult = "";
                try {
                    JSONObject JsonObject = new JSONObject(json);
                    String cod = jsonHelperGetString(JsonObject, "cod");
                    currentTime = new Date().getTime();
                    if (cod != null) {
                        if (cod.equals("200")) {
                            JSONObject sys = jsonHelperGetJSONObject(JsonObject, "sys");
                            JSONArray weather = jsonHelperGetJSONArray(JsonObject, "weather");
                            if (weather != null) {
                                JSONObject thisWeather = weather.getJSONObject(0);
                                jsonResult += thisWeather.getString("description");
                            }
                            sunrise = sys.getLong("sunrise") * 1000;
                            sunset = sys.getLong("sunset") * 1000;

                        } else if (cod.equals("404")) {
                            String message = jsonHelperGetString(JsonObject, "message");
                            jsonResult += "cod 404: " + message;
                        }
                    } else {
                        jsonResult += "cod == null\n";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonResult += e.getMessage();
                }
                return jsonResult;
            }

            private String jsonHelperGetString(JSONObject obj, String k) {
                String v = null;
                try {
                    v = obj.getString(k);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return v;
            }

            private JSONObject jsonHelperGetJSONObject(JSONObject obj, String k) {
                JSONObject o = null;

                try {
                    o = obj.getJSONObject(k);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return o;
            }

            private JSONArray jsonHelperGetJSONArray(JSONObject obj, String k) {
                JSONArray a = null;

                try {
                    a = obj.getJSONArray(k);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return a;
            }
        }
        private class OpenWeatherMapTask1 extends AsyncTask<Void, Void, String> {
            TextView tvResult;
            String Think = "https://api.thingspeak.com/channels/228229/feeds.json?results=1";

            OpenWeatherMapTask1(TextView tvResult) {
                this.tvResult = tvResult;
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                String queryReturn;

                try {
                    queryReturn = sendQuery(Think);
                    result += ParseJSON(queryReturn);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    queryReturn = e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    queryReturn = e.getMessage();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                tvResult.setText(s);
            }

            private String sendQuery(String query) throws IOException {
                String result = "";

                URL searchURL = new URL(query);

                HttpURLConnection httpURLConnection = (HttpURLConnection) searchURL.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(
                            inputStreamReader,
                            8192);

                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }

                    bufferedReader.close();
                }

                return result;
            }

            private String ParseJSON(String json) {
                String jsonResult = "";

                try {
                    JSONObject JsonObject = new JSONObject(json);
                    JSONArray think = jsonHelperGetJSONArray(JsonObject, "feeds");
                    JSONObject Vthink = think.getJSONObject(0);
                    String temp = Vthink.getString("field1");
                    String hum = Vthink.getString("field2");
                    String hd = Vthink.getString("field3");
                    jsonResult += temp + " ℃" + "\n" + "Humidity: " + hum + " %" + "\n" + "Heat Index: " + hd + " ℃" + "\n";


                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonResult += e.getMessage();
                }

                return jsonResult;
            }

            private String jsonHelperGetString(JSONObject obj, String k) {
                String v = null;
                try {
                    v = obj.getString(k);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return v;
            }

            private JSONObject jsonHelperGetJSONObject(JSONObject obj, String k) {
                JSONObject o = null;

                try {
                    o = obj.getJSONObject(k);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return o;
            }

            private JSONArray jsonHelperGetJSONArray(JSONObject obj, String k) {
                JSONArray a = null;

                try {
                    a = obj.getJSONArray(k);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return a;
            }
        }
    }
