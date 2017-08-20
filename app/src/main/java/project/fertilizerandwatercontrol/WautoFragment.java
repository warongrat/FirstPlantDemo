package project.fertilizerandwatercontrol;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;


public class WautoFragment extends Fragment {
    private Button On, Off;
    private ImageView Auto;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wauto, container, false);
        Auto = (ImageView) view.findViewById(R.id.auto);
        On = (Button)view.findViewById(R.id.auto_On);
        Off = (Button)view.findViewById(R.id.auto_Off);
        getActivity().setTitle("Auto Irrigation");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        On.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String serverAdress = "192.168.104.104" + ":" + "80";
                WautoFragment.HttpRequestTask requestTask = new WautoFragment.HttpRequestTask(serverAdress);
                requestTask.execute("1");
                Auto.setImageResource(R.drawable.o_irrigation);
                Toast.makeText(WautoFragment.this.getActivity(), "ON", Toast.LENGTH_SHORT).show();
            }
        });

        Off.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String serverAdress = "192.168.104.104" + ":" + "80";
                WautoFragment.HttpRequestTask requestTask = new WautoFragment.HttpRequestTask(serverAdress);
                requestTask.execute("1");
                Auto.setImageResource(R.drawable.f_irrigation);
                Toast.makeText(WautoFragment.this.getActivity(), "OFF", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    private class HttpRequestTask extends AsyncTask<String, Void, String> {

        private String serverAdress;
        private String serverResponse = "";
        private AlertDialog dialog;

        public HttpRequestTask(String serverAdress) {
            this.serverAdress = serverAdress;

        }

        @Override
        protected String doInBackground(String... params) {

            String val = params[0];
            final String url = "http://" + serverAdress + "/led/" + val;

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet getRequest = new HttpGet();
                getRequest.setURI(new URI(url));
                HttpResponse response = client.execute(getRequest);

                InputStream inputStream = null;
                inputStream = response.getEntity().getContent();
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream));

                serverResponse = bufferedReader.readLine();
                inputStream.close();

            } catch (URISyntaxException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
            }

            return serverResponse;
        }

        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected void onPreExecute() {

        }
    }
}
