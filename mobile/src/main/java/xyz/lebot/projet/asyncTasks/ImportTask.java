package xyz.lebot.projet.asyncTasks;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xyz.lebot.projet.Earthquake;
import xyz.lebot.projet.adapters.EarthquakeAdapter;

public class ImportTask extends AsyncTask<Object, Integer, String> {

    private ArrayList<Earthquake> mEarthquakes;
    private ArrayList<String> mFavs;
    private EarthquakeAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected String doInBackground(Object[] arg) {
        String text = "";
        URL url = null;
        try {
            url = new URL((String) arg[0]);
            mEarthquakes = (ArrayList<Earthquake>) arg[1];
            mFavs = (ArrayList<String>) arg[2];
            mAdapter = (EarthquakeAdapter) arg[3];
            mRecyclerView = (RecyclerView) arg[4];


            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                //ensuite, on récupère le contenu du flux avec in.readLine(), tant qu’il y a des
                //données dans le flux d’entrée
                String str;
                while ((str = in.readLine()) != null) {
                    text += str;
                }
                in.close(); // et on ferme le flux
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    // Méthode exécutée à la fin de l'execution de la tâche asynchrone
    @Override
    protected void onPostExecute(String result) {
        mEarthquakes.clear();
        try {
            JSONObject reader = new JSONObject(result);
            JSONArray features = reader.getJSONArray("features");

            String _id, _title, _place, _status, _sources;
            Long _time, _timeUpdate;
            Integer _tsunamiCounter;
            Double _magn;
            Double _location[];

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.optJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");

                _id = feature.getString("id");
                _title = properties.getString("title");
                _place = properties.getString("place");
                _status = properties.getString("status");

                _time = properties.getLong("time");
                _timeUpdate = properties.getLong("updated");

                _tsunamiCounter = properties.getInt("tsunami");

                _magn = properties.getDouble("mag");

                _sources = properties.getString("sources");

                JSONObject geometry = feature.getJSONObject("geometry");

                _location = new Double[2];


                _location[0] = geometry.optJSONArray("coordinates").getDouble(0);
                _location[1] = geometry.optJSONArray("coordinates").getDouble(1);

                mEarthquakes.add(new Earthquake(_id, _title, _magn, _place, _time, _timeUpdate, _status, _tsunamiCounter, _sources, _location));
            }
        } catch (JSONException ex) {
            Log.d("Importation : ", ex.getMessage());
        }

        for (String _tmp : mFavs) {
            for (Earthquake _earthquake : mEarthquakes) {
                if (_tmp.equalsIgnoreCase(_earthquake.getmId())) {
                    _earthquake.setFav(true);
                }
            }
        }

        mAdapter = new EarthquakeAdapter(mEarthquakes, mFavs);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}