package com.example.nitish.sunshine;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nitish.sunshine.sync.SunshineSyncAdapter;


public class MainActivity extends Activity implements ForecastFragment.Callback {

    boolean mTowPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.weather_detail_container) != null) {
            mTowPane = true;
            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTowPane = false;
        }
        SunshineSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if(id == R.id.action_map) {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW);

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String location = pref.getString(getString(R.string.pref_pincode_key), "400004");
            Uri geolocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", location).build();
            mapIntent.setData(geolocation);

            if(mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(getApplicationContext(), "No suitable applications found.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String date) {
        if(mTowPane) {
            Log.i(">>>>>>>>>>>", "Two pane");

            DetailFragment df = new DetailFragment();
            Bundle args = new Bundle();
            args.putString(DetailFragment.DATE_KEY, date);
            df.setArguments(args);

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.weather_detail_container, df);
            ft.commit();
        } else {
            Log.i(">>>>>>>>>>>", "One Pane");
            Intent intent = new Intent(this, DetailActivity.class)
                            .putExtra(DetailFragment.DATE_KEY, date);
            startActivity(intent);
        }
    }
}
