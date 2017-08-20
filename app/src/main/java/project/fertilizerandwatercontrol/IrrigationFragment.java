package project.fertilizerandwatercontrol;

import android.app.DownloadManager;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
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

public class IrrigationFragment extends Fragment {
    Button On, Off;
    ImageView Grow;
    DownloadManager downloadManager;
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.irrigation, container , false);
        Grow = (ImageView) view.findViewById(R.id.grow);
        On = (Button)view.findViewById(R.id.btn_On);
        Off = (Button)view.findViewById(R.id.btn_Off);
        getActivity().setTitle("Irrigation Control");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        On.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String serverAdress = "192.168.104.104" + ":" + "80";
                HttpRequestTask requestTask = new HttpRequestTask(serverAdress);
                requestTask.execute("1");
                Grow.setImageResource(R.drawable.grow_normal);
                /*downloadManager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri=Uri.parse("https://thingspeak.com/channels/228229/feed.csv");
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                Long reference = downloadManager.enqueue(request);*/
                Toast.makeText(IrrigationFragment.this.getActivity(), "ON", Toast.LENGTH_SHORT).show();
            }
        });

        Off.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String serverAdress = "192.168.104.104" + ":" + "80";
                HttpRequestTask requestTask = new HttpRequestTask(serverAdress);
                requestTask.execute("1");
                Grow.setImageResource(R.drawable.growdie);
                Toast.makeText(IrrigationFragment.this.getActivity(), "OFF", Toast.LENGTH_SHORT).show();
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
