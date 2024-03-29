package project.fertilizerandwatercontrol.Thing;

/**
 * Created by waron on 4/4/2560.
 */


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import project.fertilizerandwatercontrol.model.ChannelFeed;
import project.fertilizerandwatercontrol.model.Feed;

/***
 * Representation of a field chart in a ThingSpeak Channel using the HelloCharts for Android library.
 *
 * @author Macro Yau
 */
public class ThingSpeakLineChart implements ThingSpeakChannel.ChannelFieldFeedUpdateListener {

    /***
     * Listener for chart data update events.
     */
    public interface ChartDataUpdateListener {

        /***
         * Chart data is successfully fetched from the ThingSpeak API.
         *
         * @param channelId The ID of this specific Channel.
         * @param fieldId The ID of this specific field in the Channel.
         * @param title The title of this specific field in the Channel.
         * @param lineChartData The line chart data of this specific field in the Channel.
         * @param maxViewport The maximum {@link lecho.lib.hellocharts.model.Viewport} bounds of the chart.
         * @param initialViewport The initial {@link lecho.lib.hellocharts.model.Viewport} bounds of the chart.
         */
        void onChartDataUpdated(long channelId, int fieldId, String title, LineChartData lineChartData, Viewport maxViewport, Viewport initialViewport);

    }

    private ChartDataUpdateListener mListener;

    private ThingSpeakChannel mChannel;
    private int mFieldId;
    private String mTitle;

    private Date mChartStartDate, mChartEndDate;
    private boolean mSpline = false;
    private boolean mFilled = false;
    private String mXAxisName = "Date";
    private String mYAxisName;
    private String mDateAxisLabelFormat = "HH:mm";
    private int mDateAxisLabelInterval = 10;
    private float mValueAxisLabelInterval = 10.0f;
    private int mLineColor = ChartUtils.COLOR_RED;
    private int mAxisColor = ChartUtils.DEFAULT_COLOR;

    private LineChartData mLineChartData;

    /***
     * Constructor for public Channels.
     *
     * @param channelId The ID of this specific Channel.
     * @param fieldId The ID of the specific field in the Channel.
     */
    public ThingSpeakLineChart(long channelId, int fieldId) {
        this(channelId, fieldId, null);
    }

    /***
     * Constructor for private Channels.
     *
     * @param channelId The ID of this specific Channel.
     * @param fieldId The ID of the specific field in the Channel.
     * @param readApiKey The Read API Key for this specific Channel.
     */
    public ThingSpeakLineChart(long channelId, int fieldId, String readApiKey) {
        this.mChannel = new ThingSpeakChannel(channelId, readApiKey);
        this.mFieldId = fieldId;
    }

    public ThingSpeakLineChart(ThingSpeakChannel channel, int fieldId) {
        this.mChannel = channel;
        this.mFieldId = fieldId;
    }

    /***
     * Load chart data asynchronously from ThingSpeak API.
     */
    public void loadChartData() {
        if (mChannel != null) {
            mChannel.setChannelFieldFeedUpdateListener(this);
            mChannel.loadChannelFieldFeed(mFieldId);
        }
    }

    @Override
    public void onChannelFieldFeedUpdated(long channelId, int fieldId, ChannelFeed channelFieldFeed) {
        // Get field name from Channel Feed
        mTitle = channelFieldFeed.getChannel().getFieldName(mFieldId);

        // Initialize line chart
        List<Line> lines = new ArrayList<Line>();
        List<PointValue> values = new ArrayList<PointValue>();
        List<AxisValue> dateAxisValues = new ArrayList<AxisValue>();
        List<AxisValue> valueAxisValues = new ArrayList<AxisValue>();
        final DateFormat df = new SimpleDateFormat(mDateAxisLabelFormat);
        List<Feed> feeds = channelFieldFeed.getFeeds();
        long reference = feeds.get(0).getCreatedAt().getTime();
        long index = 0;
        long startDateIndex = -1, endDateIndex = -1;
        long prevDate = -1;
        float minValue = Float.parseFloat(feeds.get(0).getField(mFieldId));
        float maxValue = -1;

        // Inflate line chart
        for (Feed f : feeds) {
            Date createdAt = f.getCreatedAt();
            long date = createdAt.getTime();
            index = date - reference;

            // Check start and end date for default chart viewport
            if (mChartStartDate != null && startDateIndex == -1 && createdAt.after(mChartStartDate))
                startDateIndex = index;
            if (mChartEndDate != null && createdAt.before(mChartEndDate))
                endDateIndex = index;

            // Insert data points
            float value = Float.parseFloat(f.getField(mFieldId));
            values.add(new PointValue(index, value));

            // Configure date labels
            date /= 1000;
            long currentMinute = date - (date % 60);
            if ((currentMinute % (mDateAxisLabelInterval * 60) == 0) && currentMinute != prevDate) {
                prevDate = currentMinute;
                currentMinute *= 1000;
                dateAxisValues.add(new AxisValue(index).setLabel(df.format(new Date(currentMinute))));
            }

            // Check minimum and maximum in data set
            if (value < minValue)
                minValue = value;
            else if (value > maxValue)
                maxValue = value;
        }

        // Adjust line chart Y-axis bounds
        float axisMinValue = minValue - (minValue % mValueAxisLabelInterval);
        float axisMaxValue = maxValue - (maxValue % mValueAxisLabelInterval) + mValueAxisLabelInterval;
        float axisValue = axisMinValue;
        while (axisValue <= axisMaxValue) {
            System.out.println(axisValue);
            valueAxisValues.add(new AxisValue(axisValue));
            axisValue += mValueAxisLabelInterval;
        }

        // Configure line parameters
        Line line = new Line(values);
        line.setCubic(mSpline);
        line.setFilled(mFilled);
        line.setColor(mLineColor);
        line.setHasLabelsOnlyForSelected(true);
        lines.add(line);

        // Create LineChartData instance
        mLineChartData = new LineChartData(lines);
        mLineChartData.setAxisYLeft(new Axis()
                .setValues(valueAxisValues)
                .setTextColor(mAxisColor)
                .setHasLines(true)
                .setName(mYAxisName == null ? mTitle : mYAxisName));
        mLineChartData.setAxisXBottom(new Axis()
                .setTextColor(mAxisColor)
                .setValues(dateAxisValues)
                .setName(mXAxisName));

        // Configure maximum and default viewport
        Viewport maxViewport = new Viewport(0, (float) (axisMaxValue + mValueAxisLabelInterval * 0.25), index, (float) (axisMinValue - mValueAxisLabelInterval * 0.25));
        if (startDateIndex == -1)
            startDateIndex = 0;
        if (endDateIndex == -1)
            endDateIndex = index;
        Viewport defaultViewport = new Viewport(maxViewport);
        defaultViewport.left = startDateIndex;
        defaultViewport.right = endDateIndex;

        // Notify listener
        if (mListener != null) {
            mListener.onChartDataUpdated(mChannel.getChannelId(), mFieldId, mTitle, mLineChartData, maxViewport, defaultViewport);
        }
    }

    public void setListener(ChartDataUpdateListener listener) {
        this.mListener = listener;
    }


    public void setReadApiKey(String readApiKey) {
        mChannel.setReadApiKey(readApiKey);
    }

    public void setTimezone(String timezone) {
        mChannel.setTimezone(timezone);
    }

    public void setNumberOfEntries(int results) {
        mChannel.setNumberOfEntries(results);
    }

    public void setDaysToInclude(int days) {
        mChannel.setDaysToInclude(days);
    }

    public void setDataStartDate(Date start) {
        mChannel.setStartDate(start);
    }

    public void setDataEndDate(Date end) {
        mChannel.setEndDate(end);
    }

    public void setTimescale(int timescale) {
        mChannel.setTimescale(timescale);
    }

    public void setChartStartDate(Date start) {
        this.mChartStartDate = start;
    }

    public void setChartEndDate(Date end) {
        this.mChartEndDate = end;
    }

    public void useSpline(boolean spline) {
        this.mSpline = spline;
    }

    public void setFilled(boolean filled) {
        this.mFilled = filled;
    }

    public void setYAxisName(String name) {
        this.mYAxisName = name;
    }

    public void setXAxisName(String name) {
        this.mXAxisName = name;
    }

    /***
     * Set the date and time format of the date axis labels using the given pattern.
     *
     * @param pattern The pattern describing the date and time format.
     */
    public void setDateAxisLabelFormat(String pattern) {
        this.mDateAxisLabelFormat = pattern;
    }

    /***
     * Set the interval of the date axis labels in minutes. The default interval is 10 minutes.
     *
     * @param interval The interval in minutes.
     */
    public void setDateAxisLabelInterval(int interval) {
        this.mDateAxisLabelInterval = interval;
    }

    /***
     * Set the interval of the value axis labels. The default interval is 10.0 units.
     *
     * @param interval The interval.
     */
    public void setValueAxisLabelInterval(float interval) {
        this.mValueAxisLabelInterval = interval;
    }

    /***
     * Set the line color of the chart. The default color is light gray.
     *
     * @param color The line color.
     */
    public void setLineColor(int color) {
        this.mLineColor = color;
    }

    /***
     * Set the axis color of the chart. The default color is red.
     *
     * @param color The axis color.
     */
    public void setAxisColor(int color) {
        this.mAxisColor = color;
    }

}