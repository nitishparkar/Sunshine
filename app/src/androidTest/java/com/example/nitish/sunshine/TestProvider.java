package com.example.nitish.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.nitish.sunshine.data.WeatherContract.LocationEntry;
import com.example.nitish.sunshine.data.WeatherContract.WeatherEntry;

public class TestProvider extends AndroidTestCase {

    final String LOG_TAG = "Inside Tests";

    public static String TEST_CITY_NAME = "Mumbai";
    public static String TEST_PINCODE = "400027";
    public static String TEST_DATE = "20141208";

    public void testGetType() {

        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);

        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocation(TEST_PINCODE));

        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);


        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(TEST_PINCODE, TEST_DATE));

        // vnd.android.cursor.item/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);

        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));

        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testInsertReadProvider() {

        double lat = 18.9902;
        double lng = 72.8314;

        /*WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();*/

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_PINCODE, TEST_PINCODE);
        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_LAT, lat);
        values.put(LocationEntry.COLUMN_LNG, lng);

        // long locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        // assertTrue(locationRowId != -1);

        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, values);
        assertNotNull(locationUri);
        long  locationRowId = ContentUris.parseId(locationUri);

        // Now see if we can successfully query if we include the row id
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order
        );

        if (cursor.moveToFirst()) {
            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_PINCODE);
            String location = cursor.getString(locationIndex);
            int nameIndex = cursor.getColumnIndex((LocationEntry.COLUMN_CITY_NAME));
            String name = cursor.getString(nameIndex);
            assertEquals(TEST_CITY_NAME, name);
            assertEquals(TEST_PINCODE, location);
        }


        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
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
            assertEquals(TEST_CITY_NAME, name);
            assertEquals(TEST_PINCODE, location);
            assertEquals(lat, latitude);
            assertEquals(lng, longitude);

            // Fantastic. Now that we have a location, add some weather!
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
            weatherValues.put(WeatherEntry.COLUMN_DATETEXT, TEST_DATE);
            weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
            weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
            weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
            weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
            weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
            weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
            weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

            // long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

            // Verify we got a row back.
            // assertTrue(weatherRowId != -1);

            Uri weatherUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);
            assertNotNull(weatherUri);

            // A cursor is your primary interface to the query results.
            Cursor weather_cursor = mContext.getContentResolver().query(WeatherEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            if(weather_cursor.moveToFirst()) {

                int dateIndex = weather_cursor.getColumnIndex((WeatherEntry.COLUMN_DATETEXT));
                String date_text = weather_cursor.getString(dateIndex);

                assertEquals(TEST_DATE, date_text);
            } else {
                fail("No Weather data returned :(");
            }

            weather_cursor.close();

            weather_cursor = mContext.getContentResolver().query(WeatherEntry.buildWeatherLocation(TEST_PINCODE),
                    null,
                    null,
                    null,
                    null);

            if(weather_cursor.moveToFirst()) {

                int dateIndex = weather_cursor.getColumnIndex((WeatherEntry.COLUMN_DATETEXT));
                String date_text = weather_cursor.getString(dateIndex);

                assertEquals(TEST_DATE, date_text);
            } else {
                fail("No Weather data returned :(");
            }

            weather_cursor.close();

            weather_cursor = mContext.getContentResolver().query(WeatherEntry.buildWeatherLocationWithStartDate(TEST_PINCODE, TEST_DATE),
                    null,
                    null,
                    null,
                    null);

            if(weather_cursor.moveToFirst()) {

                int dateIndex = weather_cursor.getColumnIndex((WeatherEntry.COLUMN_DATETEXT));
                String date_text = weather_cursor.getString(dateIndex);

                assertEquals(TEST_DATE, date_text);
            } else {
                fail("No Weather data returned :(");
            }

            weather_cursor.close();


            weather_cursor = mContext.getContentResolver().query(WeatherEntry.buildWeatherLocationWithDate(TEST_PINCODE, TEST_DATE),
                    null,
                    null,
                    null,
                    null);

            if(weather_cursor.moveToFirst()) {

                int dateIndex = weather_cursor.getColumnIndex((WeatherEntry.COLUMN_DATETEXT));
                String date_text = weather_cursor.getString(dateIndex);

                assertEquals(TEST_DATE, date_text);
            } else {
                fail("No Weather data returned :(");
            }

            weather_cursor.close();

        } else {
            fail("No values returned :(");
        }

    }

    // brings our database to an empty state
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                WeatherEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }
    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testUpdateLocation() {
        double lat = 18.9902;
        double lng = 72.8314;

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_PINCODE, TEST_PINCODE);
        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_LAT, lat);
        values.put(LocationEntry.COLUMN_LNG, lng);

        Uri locationUri = mContext.getContentResolver().
                insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(LocationEntry._ID, locationRowId);
        updatedValues.put(LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        int count = mContext.getContentResolver().update(
                LocationEntry.CONTENT_URI, updatedValues, LocationEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_PINCODE);
            String location = cursor.getString(locationIndex);
            int nameIndex = cursor.getColumnIndex((LocationEntry.COLUMN_CITY_NAME));
            String name = cursor.getString(nameIndex);
            // Hooray, data was returned! Assert that it's the right data, and that the database
            // creation code is working as intended.
            // Then take a break. We both know that wasn't easy.
            assertEquals("Santa's Village", name);
            assertEquals(TEST_PINCODE, location);

        } else {
            fail();
        }

    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }
}
