package com.example.nitish.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nitish.sunshine.data.WeatherContract;

/**
 * Created by nitish on 9/9/14.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;
    private boolean mUseTodayLayout = true;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mUseTodayLayout = context.getResources().getBoolean(R.bool.useTodayLayout);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /**
     * Copy/paste note: Replace existing newView() method in ForecastAdapter with this one.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = viewType == VIEW_TYPE_TODAY ? R.layout.list_item_forecast_today : R.layout.list_item_forecast;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder)view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        boolean isMetric = Utility.isMetric(context);
        float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);

//        viewHolder.iconView.setImageResource(R.mipmap.ic_launcher);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));
        viewHolder.descriptionView.setText(description);

        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));

        if(getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY) {
            int artResource = Utility.getArtResourceForWeatherCondition(cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID)));
            viewHolder.iconView.setImageResource(artResource);
        } else {
            int iconResource = Utility.getIconResourceForWeatherCondition(cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID)));
            viewHolder.iconView.setImageResource(iconResource);
        }
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_image);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}
