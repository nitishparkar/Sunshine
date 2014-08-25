package com.example.nitish.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.nitish.sunshine.data.WeatherContract.WeatherEntry;
import com.example.nitish.sunshine.data.WeatherContract.LocationEntry;

public class WeatherDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 1;

    public WeatherDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations. A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
        LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        LocationEntry.COLUMN_LOCATION_ID + " INTEGER NOT NULL, " +
        LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
        LocationEntry.COLUMN_PINCODE + " TEXT NOT NULL, " +
        LocationEntry.COLUMN_LAT + " REAL NOT NULL, " +
        LocationEntry.COLUMN_LNG + " REAL NOT NULL, " +


        // To assure the application have just one weather entry per day
        // per location, it's created a UNIQUE constraint with REPLACE strategy
        " UNIQUE (" + LocationEntry.COLUMN_CITY_NAME + ") ON CONFLICT IGNORE);";

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
        // Why AutoIncrement here, and not above?
        // Unique keys will be auto-generated in either case. But for weather
        // forecasting, it's reasonable to assume the user will want information
        // for a certain date and all dates *following*, so the forecast data
        // should be sorted accordingly.
        WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

        // the ID of the location entry associated with this weather data
        WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
        WeatherEntry.COLUMN_DATETEXT + " TEXT NOT NULL, " +
        WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
        WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

        WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
        WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

        WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
        WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
        WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
        WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +

        // Set up the location column as a foreign key to location table.
        " FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
        LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

        // To assure the application have just one weather entry per day
        // per location, it's created a UNIQUE constraint with REPLACE strategy
        " UNIQUE (" + WeatherEntry.COLUMN_DATETEXT + ", " +
        WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +  LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
