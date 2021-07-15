package com.example.mymovies.tasks;


import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesWebService {
    /**
     * URL for movies data from the MoviesDB data-set
     */
    String BASE_REQUEST_URL = "https://api.themoviedb.org/3/movie/";

    OkHttpClient.Builder okhttpclientbuilder = new OkHttpClient.Builder();

    HttpLoggingInterceptor httplogger = new HttpLoggingInterceptor();

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_REQUEST_URL)
            .client(okhttpclientbuilder
                    .addInterceptor(httplogger.setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(new StethoInterceptor())
                    .build())
            .build();

    @Headers("Content-Type: text/html")
    @GET("{sort_type}")
    Call<String> getMovies(
            @Path("sort_type") String sort_type,
            @Query("api_key") String value
    );

    @Headers("Content-Type: text/html")
    @GET("{id}/videos")
    Call<String> getMoviesTrailers(
            @Path("id") String id,
            @Query("api_key") String value
    );

    @Headers("Content-Type: text/html")
    @GET("{id}/reviews")
    Call<String> getMovieReviews(
            @Path("id") String id,
            @Query("api_key") String value
    );
}
