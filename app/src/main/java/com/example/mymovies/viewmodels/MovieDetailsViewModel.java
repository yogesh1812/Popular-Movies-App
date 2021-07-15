package com.example.mymovies.viewmodels;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mymovies.models.Movie;
import com.example.mymovies.models.Review;
import com.example.mymovies.models.Trailer;
import com.example.mymovies.utils.MovieRepository;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MovieDetailsViewModel extends AndroidViewModel {

    public MutableLiveData<Movie> mLiveMovie = new MutableLiveData<>();
    private MovieRepository movieRepository;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public MovieDetailsViewModel(@NonNull Application application) {
        super(application);
        movieRepository = MovieRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Trailer>> getMovieTrailers(String id, String api_key) {
        return movieRepository.getMovieTrailers(id, api_key);
    }

    public LiveData<List<Review>> getMovieReviews(String id, String api_key) {
        return movieRepository.getMovieReviews(id, api_key);
    }

    public void addMovieToFavorite(Movie movie) {
        movieRepository.addMovieToFavorite(movie);
    }

    public void getMovieById(int movieId) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Movie movie = movieRepository.getMovieById(movieId);
                mLiveMovie.postValue(movie);
            }
        });
    }

    public void deleteMovieFromFavorite(Movie movie) {
        movieRepository.deleteMovieFromFavorite(movie);
    }
}
