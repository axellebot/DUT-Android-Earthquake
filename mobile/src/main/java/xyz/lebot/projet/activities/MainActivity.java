package xyz.lebot.projet.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import xyz.lebot.projet.Earthquake;
import xyz.lebot.projet.adapters.EarthquakeAdapter;
import xyz.lebot.projet.R;
import xyz.lebot.projet.asyncTasks.ImportTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
        , NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 1;
    private static final int MY_PERMISSIONS_ACCESS_NETWORK_STATE = 2;
    private static final int MY_PERMISSIONS_CHANGE_NETWORK_STATE = 3;
    private static final int MY_PERMISSIONS_REQUEST_READ_FILES = 4;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_FILES = 5;
    private static final String FILE_NAME_FAV = "favs";
    private static final String FILE_NAME_SAVE = "save";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DrawerLayout mDrawer;

    private FloatingActionButton mBtnFab;
    private ArrayList<Earthquake> mEarthquakes;
    private ArrayList<Earthquake> mFilterEarthquakes;

    private ArrayList<String> mFavs;
    private SwipeRefreshLayout mSwipeLayout;
    private String mLink = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_month.geojson";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mBtnFab = (FloatingActionButton) findViewById(R.id.btn_fab_main);
        mBtnFab.setOnClickListener(this);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mBtnFab.isShown())
                    mBtnFab.hide();
                else if (dy < 0 && !mBtnFab.isShown())
                    mBtnFab.show();
            }
        });

        //Liste des favoris
        mFavs = new ArrayList<>();
        importFavs();

        mEarthquakes = new ArrayList<>();
        mFilterEarthquakes = new ArrayList<>();

        mAdapter = new EarthquakeAdapter(mEarthquakes, mFavs);

        //mDrawer
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (mBtnFab != null) {
                    mBtnFab.setTranslationX(slideOffset * 300);
                }
            }
        };
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        importData();
    }

    //OptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_filter_favs:
                favFilter();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    //on resume from other activity StartForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_fab_main:
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("EARTHQUAKES", mEarthquakes);
                startActivity(intent);
                break;
        }
    }


    public boolean hasPermission(String manifestPermission, int code) {
        int permissionCode = ContextCompat.checkSelfPermission(this, manifestPermission);

        if (permissionCode == PackageManager.PERMISSION_GRANTED) {
            Log.v("Projet", "Got " + manifestPermission + " permission");
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{manifestPermission}, code);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
            case MY_PERMISSIONS_REQUEST_READ_FILES: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
            case MY_PERMISSIONS_REQUEST_WRITE_FILES: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mDrawer.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.nav_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Watch this apps";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Earthquake");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            case R.id.nav_info:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://axel.lebot.xyz"));
                startActivity(browserIntent);
                break;
            case R.id.action_last_month:
                mLink = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_month.geojson";
                importData();
                noFilter();
                return true;

            case R.id.action_last_day:
                mLink = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_day.geojson";
                importData();
                noFilter();
                return true;
            case R.id.action_last_hour:
                mLink = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_hour.geojson";
                importData();
                noFilter();
                return true;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
                importData();
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveFavs();
        saveBackup();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveFavs();
        saveBackup();
    }

    //-----------------Usual Methods-----------------
    private void importData() {
        if (hasPermission(Manifest.permission.INTERNET, MY_PERMISSIONS_REQUEST_INTERNET) && hasPermission(Manifest.permission.ACCESS_NETWORK_STATE, MY_PERMISSIONS_ACCESS_NETWORK_STATE)) {
            if (isNetworkAvailable()) {
                new ImportTask().execute(mLink, mEarthquakes, mFavs, mAdapter, mRecyclerView);
                saveBackup();
            } else {
                Snackbar.make(mRecyclerView, R.string.no_internet_info, Snackbar.LENGTH_LONG).show();
                importBackup();
            }
        }
    }


    //BACKUP
    private void saveBackup() {
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_FILES)) {
            try {
                FileOutputStream out = openFileOutput(FILE_NAME_SAVE, MODE_PRIVATE);
                ObjectOutput oos = new ObjectOutputStream(out);
                oos.writeObject(mEarthquakes);
                oos.close();
                out.close();
            } catch (Exception ex) {
                Log.e("Projet", ex.getMessage());
            }
        }
    }

    private void importBackup() {
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_READ_FILES)) {
            try {
                FileInputStream in = openFileInput(FILE_NAME_SAVE);
                ObjectInput ois = new ObjectInputStream(in);

                ArrayList<Object> aList = (ArrayList<Object>) ois.readObject();
                mEarthquakes.clear();
                for (int i = 0; i < aList.size(); i++) {
                    mEarthquakes.add((Earthquake) aList.get(i));
                }
                ois.close();
                in.close();

            } catch (Exception ex) {
                Log.e("Projet", ex.getMessage());
            }
        }
    }

    //FAVS
    private void saveFavs() {
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_FILES)) {
            try {
                FileOutputStream out = openFileOutput(FILE_NAME_FAV, MODE_PRIVATE);
                ObjectOutput oos = new ObjectOutputStream(out);
                oos.writeObject(mFavs);
                oos.close();
                out.close();
            } catch (Exception ex) {
                Log.e("Projet", ex.getMessage());
            }
        }
    }

    private void importFavs() {
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_READ_FILES)) {

            try {
                FileInputStream in = openFileInput(FILE_NAME_FAV);
                ObjectInput ois = new ObjectInputStream(in);

                ArrayList<Object> aList = (ArrayList<Object>) ois.readObject();
                mFavs.clear();
                for (int i = 0; i < aList.size(); i++) {
                    mFavs.add((String) aList.get(i));
                }
                ois.close();
                in.close();
            } catch (Exception ex) {
                Log.e("Projet", ex.getMessage());
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //SEARCH
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mFilterEarthquakes.clear();
        newText = newText.toLowerCase();

        for (Earthquake earthquake : mEarthquakes) {
            final String text = earthquake.getmTitle().toLowerCase();
            if (text.contains(newText)) {
                mFilterEarthquakes.add(earthquake);
            }
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mAdapter = new EarthquakeAdapter(mFilterEarthquakes, mFavs);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();  // data set changed
        return true;
    }

    public void favFilter() {
        mFilterEarthquakes.clear();
        for (Earthquake earthquake : mEarthquakes) {
            if (earthquake.isFav()) {
                mFilterEarthquakes.add(earthquake);
            }
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mAdapter = new EarthquakeAdapter(mFilterEarthquakes, mFavs);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();  // data set changed
    }

    public void noFilter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mAdapter = new EarthquakeAdapter(mEarthquakes, mFavs);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();  // data set changed
    }
}