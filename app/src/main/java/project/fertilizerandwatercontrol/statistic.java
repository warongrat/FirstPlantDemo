package project.fertilizerandwatercontrol;

import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class statistic extends Fragment {
    ImageView imageView;
    WebView webView;
    SwipeRefreshLayout swipeRefreshLayout;
    //String url = "https://s3.amazonaws.com/images.thingspeak.com/plugins/167881/BBsloXkl6HH1NW139Idbkw.png";
    //String html = "<iframe width=\"450\" height=\"260\" style=\"border: 1px solid #cccccc;\" src=\"http://api.thingspeak.com/channels/31592/charts/1?width=450&height=260&results=60&dynamic=true\" ></iframe>";
    String html = "<iframe width=\"450\" height=\"260\" style=\"border: 0px solid #cccccc;\" src=\"https://thingspeak.com/apps/matlab_visualizations/167881\"></iframe>\n";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_statistic, container , false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.stat);
        getActivity().setTitle("Statistics");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //imageView = (ImageView) view.findViewById(R.id.imageStat);
        //Picasso.with(getActivity()).load(url).into(imageView);
        webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(html, "text/html", null);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                //Picasso.with(getActivity()).load(url).into(imageView);
                webView.loadData(html, "text/html", null);
            }
        });
        return view;
    }
}
