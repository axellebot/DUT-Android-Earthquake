package xyz.lebot.projet;

import android.graphics.Color;

import java.io.Serializable;

public class Earthquake implements Serializable {
    private String mId;
    private String mTitle;
    private Double mMagnitude;
    private String mPlace;
    private Long mTime;
    private Long mTimeUpdate;
    private String mStatus;
    private Integer mTsunami;
    private String mSources;
    private Double mLocation[];
    private int mColor;
    private boolean fav;

    public Earthquake(String Id,String mTitle, Double mMagnitude, String mPlace, Long mTime, Long mTimeUpdate, String mStatus, Integer mTsunami, String mSources, Double mLocation[]) {
        this.mId=Id;
        this.mTitle = mTitle;
        this.mMagnitude = mMagnitude;
        this.mPlace = mPlace;
        this.mTime = mTime;
        this.mTimeUpdate = mTimeUpdate;
        this.mStatus = mStatus;
        this.mTsunami = mTsunami;
        this.mSources = mSources;
        this.mLocation = mLocation;
        this.fav=false;

        if (this.mMagnitude >= 6) {
            this.mColor = Color.rgb(244, 67, 54);
        } else if (this.mMagnitude >= 5) {
            this.mColor = Color.rgb(255, 152, 0);
        } else if (this.mMagnitude >= 4) {
            this.mColor = Color.rgb(255, 193, 7);
        } else if (this.mMagnitude >= 3) {
            this.mColor = Color.rgb(255, 235, 59);
        } else if (this.mMagnitude >= 2) {
            this.mColor = Color.rgb(205, 220, 57);
        } else if (this.mMagnitude >= 1) {
            this.mColor = Color.rgb(139, 195, 74);
        } else if (this.mMagnitude >= 0) {
            this.mColor = Color.rgb(255, 255, 255);
        }
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Double getmMagnitude() {
        return mMagnitude;
    }

    public void setmMagnitude(Double mMagnitude) {
        this.mMagnitude = mMagnitude;
    }

    public String getmPlace() {
        return mPlace;
    }

    public void setmPlace(String mPlace) {
        this.mPlace = mPlace;
    }

    public Long getmTime() {
        return mTime;
    }

    public void setmTime(Long mTime) {
        this.mTime = mTime;
    }

    public Long getmTimeUpdate() {
        return mTimeUpdate;
    }

    public void setmTimeUpdate(Long mTimeUpdate) {
        this.mTimeUpdate = mTimeUpdate;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public Integer getmTsunami() {
        return mTsunami;
    }

    public void setmTsunami(Integer mTsunami) {
        this.mTsunami = mTsunami;
    }

    public String getmSources() {
        return mSources;
    }

    public void setmSources(String mSources) {
        this.mSources = mSources;
    }

    public Double[] getmLocation() {
        return mLocation;
    }

    public void setmLocation(Double[] mLocation) {
        this.mLocation = mLocation;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
}
