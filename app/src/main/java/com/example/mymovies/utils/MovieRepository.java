package com.example.mymovies.utils;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mymovies.database.MoviesDatabase;
import com.example.mymovies.models.Movie;
import com.example.mymovies.models.Review;
import com.example.mymovies.models.Trailer;
import com.example.mymovies.tasks.MoviesWebService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {

    private MoviesWebService moviesWebService;
    private MoviesDatabase mDatabase;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    private MovieRepository(Context context) {
        moviesWebService = MoviesWebService.retrofit.create(MoviesWebService.class);
        mDatabase = MoviesDatabase.getInstance(context);
    }

    public static MovieRepository getInstance(Context context) {
        return new MovieRepository(context);
    }

    public LiveData<List<Movie>> getMovies(String sort_type, String api_key) {
        final MutableLiveData<List<Movie>> data = new MutableLiveData<>();

        Call<String> call = moviesWebService.getMovies(sort_type, api_key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String movieListResponse = response.body();
                    if (movieListResponse != null) {
                        data.setValue(processMovieListResponse(movieListResponse));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Tangho", "Failure happened fetching movies");
            }
        });
        return data;
    }

    private ArrayList<Movie> processMovieListResponse(String movieListResponse) {
        return QueryUtils.extractMoviesFromJson(movieListResponse);
    }

    private ArrayList<Trailer> processMovieTrailersResponse(String movieTrailersResponse) {
        return QueryUtils.extractMovieTrailersFromJson(movieTrailersResponse);
    }

    private ArrayList<Review> processMovieReviewsResponse(String movieReviewsResponse) {
        return QueryUtils.extractMovieReviewsFromJson(movieReviewsResponse);
    }

    public LiveData<List<Movie>> getMovieListFromDB() {
        return mDatabase.moviesDAO().loadAllMovies();
    }

    public LiveData<List<Trailer>> getMovieTrailers(String id, String api_key) {
        final MutableLiveData<List<Trailer>> data = new MutableLiveData<>();

        Call<String> call = moviesWebService.getMoviesTrailers(id, api_key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String movieTrailerResponse = response.body();
                    if (movieTrailerResponse != null) {
                        data.setValue(processMovieTrailersResponse(movieTrailerResponse));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Tangho", "Failure happened fetching movies");
            }
        });
        return data;
    }

    public LiveData<List<Review>> getMovieReviews(String id, String api_key) {
        final MutableLiveData<List<Review>> data = new MutableLiveData<>();

        Call<String> call = moviesWebService.getMovieReviews(id, api_key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String movieReviewResponse = response.body();
                    if (movieReviewResponse != null) {
                        data.setValue(processMovieReviewsResponse(movieReviewResponse));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Tangho", "Failure happened fetching movies");
            }
        });
        return data;
    }

    public void addMovieToFavorite(Movie movie) {

        if (movie != null) {
            String baseURL = movie.getBaseURL();

            String posterPath = movie.getPosterPath();
            movie.setPosterPath(posterPath.replace(baseURL, ""));

            String backdropPath = movie.getBackdropPath();
            movie.setBackdropPath(backdropPath.replace(baseURL, ""));
        }

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.moviesDAO().insertMovie(movie);
            }
        });
    }

    public Movie getMovieById(int id) {
        return mDatabase.moviesDAO().getMovieById(id);
    }

    public void deleteMovieFromFavorite(Movie movie) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.moviesDAO().deleteMovie(movie);
            }
        });
    }
}
