package xyz.lebot.projet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;

import xyz.lebot.projet.Earthquake;
import xyz.lebot.projet.R;


public class InfoEarthquakeActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton mBtnFab;
    private Earthquake mEarthquake;
    private LinearLayoutCompat mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);


        Bundle extras = getIntent().getExtras();
        mEarthquake = (Earthquake) extras.get("EARTHQUAKE");


        Toolbar toolbar = (Toolbar) findViewById(R.id.info_toolbar);
        toolbar.setTitle(mEarthquake.getmTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBtnFab = (FloatingActionButton) findViewById(R.id.btn_fab_info);
        mBtnFab.setOnClickListener(this);

        mLayout = (LinearLayoutCompat) findViewById(R.id.layoutInfo);

        View v = LayoutInflater.from(this).inflate(R.layout.row_info_earthquake, null, false);
        AppCompatTextView _title = (AppCompatTextView) v.findViewById(R.id.row_info_title);
        AppCompatTextView _descritipion = (AppCompatTextView) v.findViewById(R.id.row_info_description);

        _title.setText(R.string.info_id);
        _descritipion.setText(mEarthquake.getmId());
        mLayout.addView(v);

        v = LayoutInflater.from(this).inflate(R.layout.row_info_earthquake, null, false);
        _title = (AppCompatTextView) v.findViewById(R.id.row_info_title);
        _descritipion = (AppCompatTextView) v.findViewById(R.id.row_info_description);
        _title.setText(R.string.info_place);
        _descritipion.setText(mEarthquake.getmPlace());
        mLayout.addView(v);

        v = LayoutInflater.from(this).inflate(R.layout.row_info_earthquake, null, false);
        _title = (AppCompatTextView) v.findViewById(R.id.row_info_title);
        _descritipion = (AppCompatTextView) v.findViewById(R.id.row_info_description);
        _title.setText(R.string.info_date);
        _descritipion.setText(new Date(mEarthquake.getmTime()).toString());
        mLayout.addView(v);

        v = LayoutInflater.from(this).inflate(R.layout.row_info_earthquake, null, false);
        _title = (AppCompatTextView) v.findViewById(R.id.row_info_title);
        _descritipion = (AppCompatTextView) v.findViewById(R.id.row_info_description);
        _title.setText(R.string.info_update);
        _descritipion.setText(new Date(mEarthquake.getmTimeUpdate()).toString());
        mLayout.addView(v);

        v = LayoutInflater.from(this).inflate(R.layout.row_info_earthquake, null, false);
        _title = (AppCompatTextView) v.findViewById(R.id.row_info_title);
        _descritipion = (AppCompatTextView) v.findViewById(R.id.row_info_description);
        _title.setText(R.string.info_tsunami);
        _descritipion.setText(mEarthquake.getmTsunami().toString());
        mLayout.addView(v);

        v = LayoutInflater.from(this).inflate(R.layout.row_info_earthquake, null, false);
        _title = (AppCompatTextView) v.findViewById(R.id.row_info_title);
        _descritipion = (AppCompatTextView) v.findViewById(R.id.row_info_description);
        _title.setText(R.string.info_sources);
        _descritipion.setText(mEarthquake.getmSources());
        mLayout.addView(v);

        v = LayoutInflater.from(this).inflate(R.layout.row_info_earthquake, null, false);
        _title = (AppCompatTextView) v.findViewById(R.id.row_info_title);
        _descritipion = (AppCompatTextView) v.findViewById(R.id.row_info_description);
        _title.setText(R.string.info_magnitude);
        _descritipion.setText(mEarthquake.getmMagnitude().toString());
        mLayout.addView(v);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            if (v == mBtnFab) {

                ArrayList<Earthquake> listEarthquake = new ArrayList<Earthquake>();
                listEarthquake.add(mEarthquake);

                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("EARTHQUAKES", listEarthquake);
                startActivity(intent);
            }
        }
    }
}
