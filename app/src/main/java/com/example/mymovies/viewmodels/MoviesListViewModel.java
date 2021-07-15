package com.example.mymovies.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mymovies.models.Movie;
import com.example.mymovies.utils.MovieRepository;

import java.util.List;

public class MoviesListViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movieList;
    private MovieRepository movieRepository;

    public MoviesListViewModel(@NonNull Application application) {
        super(application);
        movieRepository = MovieRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Movie>> getMovieList(String sort_type, String api_key) {
        return movieRepository.getMovies(sort_type, api_key);
    }

    public LiveData<List<Movie>> getMovieListFromDB() {
        return movieRepository.getMovieListFromDB();
    }
}
