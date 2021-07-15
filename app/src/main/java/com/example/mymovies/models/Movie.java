package com.example.mymovies.models;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Movies")
public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String posterPath;
    private String overview;
    private String releaseDate;
    private String voterAverage;
    private String backdropPath;
    private String baseURL;

    public Movie(int id,
                 String title,
                 String posterPath,
                 String overview,
                 String releaseDate,
                 String voterAverage,
                 String backdropPath) {

        this.baseURL = "https://image.tmdb.org/t/p/w185";
        this.id = id;
        this.title = title;
        this.posterPath = this.baseURL + posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voterAverage = voterAverage;
        this.backdropPath = this.baseURL + backdropPath;
    }

    @Ignore
    public Movie(String title, String posterPath, String overview, String releaseDate,
                 String voterAverage, String backdropPath) {

        this.baseURL = "https://image.tmdb.org/t/p/w185";
        this.title = title;
        this.posterPath = this.baseURL + posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voterAverage = voterAverage;
        this.backdropPath = this.baseURL + backdropPath;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        voterAverage = in.readString();
        backdropPath = in.readString();
        baseURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(voterAverage);
        dest.writeString(backdropPath);
        dest.writeString(baseURL);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVoterAverage() {
        return voterAverage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}