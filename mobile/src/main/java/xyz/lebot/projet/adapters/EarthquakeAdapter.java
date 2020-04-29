package xyz.lebot.projet.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import xyz.lebot.projet.Earthquake;
import xyz.lebot.projet.R;
import xyz.lebot.projet.activities.InfoEarthquakeActivity;


public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.EarthquakeViewHolder> {

    protected ArrayList<Earthquake> mEarthquakes;
    protected ArrayList<String> mFavs;
    protected Context mContext;

    //tweets est la liste des models à afficher
    public EarthquakeAdapter(ArrayList<Earthquake> earthquakes, ArrayList<String> favs) {
        mEarthquakes = earthquakes;
        mFavs = favs;
    }


    @Override
    public EarthquakeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.row_main_earthquake, parent, false);
        mContext = parent.getContext();
        return new EarthquakeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EarthquakeViewHolder holder, int position) {
        final Earthquake earthquake = mEarthquakes.get(position);
        holder.mMagnitude.setText(earthquake.getmMagnitude().toString());
        holder.mMagnitude.getBackground().setColorFilter(earthquake.getmColor(), PorterDuff.Mode.SRC_ATOP);
        holder.mTitle.setText(earthquake.getmTitle());
        holder.mPlace.setText(earthquake.getmPlace());

        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (earthquake.isFav()) {
                    earthquake.setFav(false);
                    ((ImageView) v).setImageResource(R.drawable.ic_star_border_black);
                    mFavs.remove(earthquake.getmId());
                    Toast.makeText(mContext, R.string.delete_fav_message, Toast.LENGTH_SHORT).show();
                } else {
                    earthquake.setFav(true);
                    ((ImageView) v).setImageResource(R.drawable.ic_star_black);
                    mFavs.add(earthquake.getmId());
                    Toast.makeText(mContext, R.string.add_fav_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (earthquake.isFav()) {
            holder.mImage.setImageResource(R.drawable.ic_star_black);
        } else {
            holder.mImage.setImageResource(R.drawable.ic_star_border_black);
        }
    }

    @Override
    public int getItemCount() {
        return mEarthquakes.size();
    }


    public class EarthquakeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mMagnitude;
        public RelativeLayout mRowRelativeLayout;
        public LinearLayoutCompat mRowLinearLayout;
        public TextView mTitle;
        public TextView mPlace;
        public ImageView mImage;


        public EarthquakeViewHolder(View v) {
            super(v);
            mMagnitude = (TextView) v.findViewById(R.id.rowMagnitude);
            mRowRelativeLayout = (RelativeLayout) v.findViewById(R.id.rowRelativeLayout);
            mRowLinearLayout = (LinearLayoutCompat) v.findViewById(R.id.rowLinearLayout);
            mTitle = (TextView) v.findViewById(R.id.rowTitle);
            mPlace = (TextView) v.findViewById(R.id.rowPlace);
            mImage = (ImageView) v.findViewById(R.id.rowFav);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mContext != null) {
                Intent intent = new Intent(mContext, InfoEarthquakeActivity.class);
                intent.putExtra("EARTHQUAKE", mEarthquakes.get(getLayoutPosition()));
                mContext.startActivity(intent);
            }
        }
    }
}
