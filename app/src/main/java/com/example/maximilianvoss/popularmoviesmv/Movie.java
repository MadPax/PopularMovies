package com.example.maximilianvoss.popularmoviesmv;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ruedigervoss on 17/03/16.
 */
public class Movie implements Parcelable{

    private String image_path;
    private String movie_name;
    private String user_rating;
    private String overview;
    private String release_date;

    public Movie(String path, String name, String rating, String synopsis, String release){
        this.image_path = path;
        this.movie_name = name;
        this.user_rating = rating;
        this.overview = synopsis;
        this.release_date = release;
    }

    protected Movie(Parcel in) {

        image_path = in.readString();
        movie_name = in.readString();
        user_rating = in.readString();
        overview = in.readString();
        release_date = in.readString();
    }

    public String getImage_path(){
        return image_path;
    }

    public String getMovie_name(){
        return movie_name;
    }

    public String getUser_rating(){
        return user_rating;
    }

    public String getOverview(){
        return overview;
    }

    public String getRelease_date(){
        return release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image_path);
        dest.writeString(movie_name);
        dest.writeString(user_rating);
        dest.writeString(overview);
        dest.writeString(release_date);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel source) {

            return new Movie(source);

        }

        @Override
        public Movie[] newArray(int size) {

            return new Movie[size];

        }
    };

}
