package project.fertilizerandwatercontrol;

import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.ThingSpeakLineChart;
import com.macroyau.thingspeakandroid.model.ChannelFeed;

import java.util.Calendar;
import java.util.Date;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
public class THGraph extends Fragment {
    private ThingSpeakChannel tsChannel;
    private ThingSpeakLineChart tsChart, tsChart1;
    private LineChartView chartView, chartView1;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.th_graph, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_graph_th);
        getActivity().setTitle("Temp./Humid.");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Connect to ThinkSpeak Channel 9
        tsChannel = new ThingSpeakChannel(9);
        // Set listener for Channel feed update events
        tsChannel.setChannelFeedUpdateListener(new ThingSpeakChannel.ChannelFeedUpdateListener() {
            @Override
            public void onChannelFeedUpdated(long channelId, String channelName, ChannelFeed channelFeed) {
                // Show Channel ID and name on the Action Bar
                //((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle("Channel " + channelId);
                // Notify last update time of the Channel feed through a Toast message
                Date lastUpdate = channelFeed.getChannel().getUpdatedAt();
                Toast.makeText(THGraph.this.getActivity(), lastUpdate.toString(), Toast.LENGTH_LONG).show();
            }
        });
        // Fetch the specific Channel feed
        tsChannel.loadChannelFeed();

        // Create a Calendar object dated 5 minutes ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);

        // Configure LineChartView
        chartView = (LineChartView) view.findViewById(R.id.chart_th);
        chartView.setZoomEnabled(false);
        chartView.setValueSelectionEnabled(true);
        chartView1 = (LineChartView) view.findViewById(R.id.chart1_th);
        chartView1.setZoomEnabled(false);
        chartView1.setValueSelectionEnabled(true);

        tsChart = new ThingSpeakLineChart(9, 1);
        tsChart1 = new ThingSpeakLineChart(9, 2);
        // Get 200 entries at maximum
        tsChart.setNumberOfEntries(100);
        // Set value axis labels on 10-unit interval
        tsChart.setValueAxisLabelInterval(10);
        // Set date axis labels on 5-minute interval
        tsChart.setDateAxisLabelInterval(1);
        // Show the line as a cubic spline
        tsChart.useSpline(true);
        // Set the line color
        tsChart.setLineColor(Color.parseColor("#D32F2F"));
        // Set the axis color
        tsChart.setAxisColor(Color.parseColor("#455a64"));
        // Set the starting date (5 minutes ago) for the default viewport of the chart
        tsChart.setChartStartDate(calendar.getTime());
        // Set listener for chart data update
        tsChart.setListener(new ThingSpeakLineChart.ChartDataUpdateListener() {
            @Override
            public void onChartDataUpdated(long channelId, int fieldId, String title, LineChartData lineChartData, Viewport maxViewport, Viewport initialViewport) {
                // Set chart data to the LineChartView
                chartView.setLineChartData(lineChartData);
                // Set scrolling bounds of the chart
                chartView.setMaximumViewport(maxViewport);
                // Set the initial chart bounds
                chartView.setCurrentViewport(initialViewport);
            }
        });
        // Load chart data asynchronously
        tsChart.loadChartData();



        // Get 200 entries at maximum
        tsChart1.setNumberOfEntries(100);
        // Set value axis labels on 10-unit interval
        tsChart1.setValueAxisLabelInterval(10);
        // Set date axis labels on 5-minute interval
        tsChart1.setDateAxisLabelInterval(1);
        // Show the line as a cubic spline
        tsChart1.useSpline(true);
        // Set the line color
        tsChart1.setLineColor(Color.parseColor("#33b5e5"));
        // Set the axis color
        tsChart1.setAxisColor(Color.parseColor("#455a64"));
        // Set the starting date (5 minutes ago) for the default viewport of the chart
        tsChart1.setChartStartDate(calendar.getTime());
        // Set listener for chart data update
        tsChart1.setListener(new ThingSpeakLineChart.ChartDataUpdateListener() {
            @Override
            public void onChartDataUpdated(long channelId, int fieldId, String title, LineChartData lineChartData, Viewport maxViewport, Viewport initialViewport) {
                // Set chart data to the LineChartView
                chartView1.setLineChartData(lineChartData);
                // Set scrolling bounds of the chart
                chartView1.setMaximumViewport(maxViewport);
                // Set the initial chart bounds
                chartView1.setCurrentViewport(initialViewport);
            }
        });
        // Load chart data asynchronously
        tsChart1.loadChartData();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, -5);
                tsChart.setChartStartDate(calendar.getTime());
                tsChart1.setChartStartDate(calendar.getTime());
                tsChart.loadChartData();
                tsChart1.loadChartData();
            }
        });
        return view;
    }
}
