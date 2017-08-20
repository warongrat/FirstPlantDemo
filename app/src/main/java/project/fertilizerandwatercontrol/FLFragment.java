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

/**
 * Created by waron on 7/4/2560.
 */

public class FLFragment extends Fragment {

    TextView FLResult, textViewFL;
    ImageView FLIcon;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fl, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fl_monitor);
        FLResult = (TextView) view.findViewById(R.id.FL_result);
        textViewFL = (TextView) view.findViewById(R.id.fertilizer_level);
        FLIcon = (ImageView) view.findViewById(R.id.FLIcon);
        getActivity().setTitle("Fertilizer Level");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        new FLFragment.OpenWeatherMapTask(FLResult,
                textViewFL).execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                new FLFragment.OpenWeatherMapTask(FLResult,
                        textViewFL).execute();
            }
        });
        return view;
    }

    private class OpenWeatherMapTask extends AsyncTask<Void, Void, String> {
        TextView tvResult, tvFL;
        String Think = "https://api.thingspeak.com/channels/228229/feeds.json?results=1";

        OpenWeatherMapTask(TextView tvFL, TextView tvResult) {
            this.tvResult = tvResult;
            this.tvFL = tvFL;
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
            tvFL.setText("Full");       //>1000
            FLIcon.setImageResource(R.drawable.measure_full);
            //tvWT.setText("3/4");      //768-1000
            //tvWT.setText("1/2");      //512-768
            //tvWT.setText("1/4");      //256-511
            //tvWT.setText("Refill");   //0-255

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

