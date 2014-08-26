package com.example.nitish.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.nitish.sunshine.data.WeatherContract;
import com.example.nitish.sunshine.data.WeatherContract.LocationEntry;
import com.example.nitish.sunshine.data.WeatherContract.WeatherEntry;
import com.example.nitish.sunshine.data.WeatherDbHelper;

import java.util.Set;

public class TestDb extends AndroidTestCase {

    final String LOG_TAG = "Inside Tests";

    public void testCreateDb() throws Throwable {
        Log.d(LOG_TAG, "Checkpoint 1");
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
        Log.d(LOG_TAG, "Checkpoint 2");
    }

    public void testInsertReadDb() {
        Log.d(LOG_TAG, "Checkpoint 3");
        String city_name = "Mumbai";
        String pincode = "400027";
        double lat = 18.9902;
        double lng = 72.8314;

        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_PINCODE, pincode);
        values.put(LocationEntry.COLUMN_CITY_NAME, city_name);
        values.put(LocationEntry.COLUMN_LAT, lat);
        values.put(LocationEntry.COLUMN_LNG, lng);
        long locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);


        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME, // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_PINCODE);
            String location = cursor.getString(locationIndex);
            int nameIndex = cursor.getColumnIndex((LocationEntry.COLUMN_CITY_NAME));
            String name = cursor.getString(nameIndex);
            int latIndex = cursor.getColumnIndex((LocationEntry.COLUMN_LAT));
            double latitude = cursor.getDouble(latIndex);
            int longIndex = cursor.getColumnIndex((LocationEntry.COLUMN_LNG));
            double longitude = cursor.getDouble(longIndex);
            // Hooray, data was returned! Assert that it's the right data, and that the database
            // creation code is working as intended.
            // Then take a break. We both know that wasn't easy.
            assertEquals(city_name, name);
            assertEquals(pincode, location);
            assertEquals(lat, latitude);
            assertEquals(lng, longitude);

            // Fantastic. Now that we have a location, add some weather!
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
            weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
            weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
            weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
            weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
            weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
            weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
            weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
            weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

            long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

            // Verify we got a row back.
            assertTrue(weatherRowId != -1);


            // A cursor is your primary interface to the query results.
            Cursor weather_cursor = db.query(
                    WeatherEntry.TABLE_NAME, // Table to Query
                    null,
                    null, // Columns for the "where" clause
                    null, // Values for the "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null // sort order
            );

            if(weather_cursor.moveToFirst()) {

                int dateIndex = weather_cursor.getColumnIndex((WeatherEntry.COLUMN_DATETEXT));
                String date_text = weather_cursor.getString(dateIndex);

                assertEquals("20141205", date_text);
            } else {
                fail("No Weather data returned :(");
            }

            dbHelper.close();
        } else {
            fail("No values returned :(");
        }

    }
}
