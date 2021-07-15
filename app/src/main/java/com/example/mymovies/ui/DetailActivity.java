package com.example.mymovies.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.R;
import com.example.mymovies.adapters.ReviewAdapter;
import com.example.mymovies.adapters.TrailerAdapter;
import com.example.mymovies.models.Movie;
import com.example.mymovies.models.Review;
import com.example.mymovies.models.Trailer;
import com.example.mymovies.utils.QueryUtils;
import com.example.mymovies.viewmodels.MovieDetailsViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DetailActivity extends AppCompatActivity {

    MovieDetailsViewModel movieDetailsViewModel;
    RecyclerView trailersRecyclerView, reviewsRecyclerView;
    TextView ratingTV, releaseDateTV, descriptionTV, movieTitleTV, reviewsLabelTV, trailersLabelTV;
    View divider1;
    ImageView posterIV, backdropIV, favoriteIV;
    AppBarLayout appBarLayout;
    Observer<List<Trailer>> trailerObserver;
    Observer<List<Review>> reviewObserver;
    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;
    boolean isFavorite = false;
    String api_key;
    private List<Trailer> trailerList;
    private List<Review> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.setBackgroundResource(R.drawable.backdrop);

        initDetailViewModel();
        trailerList = new ArrayList<>();
        reviewList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        trailersRecyclerView = findViewById(R.id.trailersRecyclerView);
        reviewsRecyclerView = findViewById(R.id.reviewsGridRecyclerView);
        ratingTV = findViewById(R.id.movie_rating_tv);
        releaseDateTV = findViewById(R.id.release_date_tv);
        descriptionTV = findViewById(R.id.movie_description_tv);
        posterIV = findViewById(R.id.movie_poster_iv);
        movieTitleTV = findViewById(R.id.movie_title_tv);
        favoriteIV = findViewById(R.id.favorite_iv);
        backdropIV = findViewById(R.id.backdrop_poster_iv);
        reviewsLabelTV = findViewById(R.id.reviews_label_tv);
        trailersLabelTV = findViewById(R.id.trailers_label_tv);
        divider1 = findViewById(R.id.divider1);
        api_key = getString(R.string.api_key);

        try {
            boolean isOffline = new QueryUtils.CheckOnlineStatus().execute().get();
            if (isOffline) {
                reviewsLabelTV.setVisibility(View.GONE);
                trailersLabelTV.setVisibility(View.GONE);
                divider1.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Movie movie = intent.getParcelableExtra("movie");

        if (movie == null) {
            closeOnError();
        } else {
            setTitle(movie.getTitle());
            Picasso.get()
                    .load(movie.getBackdropPath())
                    .into(backdropIV);
            backdropIV.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(movie.getPosterPath())
                    .fit()
                    .error(R.drawable.ic_videocam_black_48dp)
                    .placeholder(R.drawable.ic_videocam_black_48dp)
                    .into(posterIV);

            movieTitleTV.setText(movie.getTitle());
            ratingTV.setText(String.valueOf(movie.getVoterAverage()));
            releaseDateTV.setText(movie.getReleaseDate().trim());
            descriptionTV.setText(movie.getOverview());

            movieDetailsViewModel
                    .getMovieTrailers(String.valueOf(movie.getId()), api_key)
                    .observe(this, trailerObserver);
            movieDetailsViewModel
                    .getMovieReviews(String.valueOf(movie.getId()), api_key)
                    .observe(this, reviewObserver);

            movieDetailsViewModel.mLiveMovie.observe(this, new Observer<Movie>() {
                @Override
                public void onChanged(Movie movie) {
                    if (movie != null) {
                        favoriteIV.setImageResource(R.drawable.like);
                        isFavorite = true;
                    } else {
                        favoriteIV.setImageResource(R.drawable.unlike);
                    }
                }
            });

            trailerAdapter = new TrailerAdapter(this, trailerList);
            trailersRecyclerView.setAdapter(trailerAdapter);

            reviewAdapter = new ReviewAdapter(this, reviewList);
            reviewsRecyclerView.setAdapter(reviewAdapter);

            RecyclerView.LayoutManager trailerLayoutManager
                    = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            trailersRecyclerView.setLayoutManager(trailerLayoutManager);

            RecyclerView.LayoutManager reviewLayoutManager
                    = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            reviewsRecyclerView.setLayoutManager(reviewLayoutManager);

            movieDetailsViewModel.getMovieById(movie.getId());

            favoriteIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isFavorite) {
                        Toast.makeText(DetailActivity.this,
                                "I like this movie", Toast.LENGTH_LONG).show();
                        movieDetailsViewModel.addMovieToFavorite(movie);
                        favoriteIV.setImageResource(R.drawable.like);
                        isFavorite = true;
                    } else {
                        Toast.makeText(DetailActivity.this,
                                "Alright! Not a Favorite", Toast.LENGTH_SHORT).show();
                        movieDetailsViewModel.deleteMovieFromFavorite(movie);
                        favoriteIV.setImageResource(R.drawable.unlike);
                        isFavorite = false;
                    }
                }
            });
        }
    }

    private void initDetailViewModel() {
        trailerObserver =
                trailers -> {
                    trailerList.clear();
                    trailerList.addAll(trailers);

                    if (trailers.size() < 1) {
                        divider1.setVisibility(View.GONE);
                        trailersLabelTV.setVisibility(View.GONE);
                    }

                    if (trailerAdapter == null) {
                        trailerAdapter = new
                                TrailerAdapter(DetailActivity.this, trailerList);
                    } else {
                        trailerAdapter.notifyDataSetChanged();
                    }
                };
        reviewObserver =
                reviews -> {
                    reviewList.clear();
                    reviewList.addAll(reviews);

                    if (reviews.size() < 1) {
                        reviewsLabelTV.setVisibility(View.GONE);
                    }

                    if (reviewAdapter == null) {
                        reviewAdapter = new
                                ReviewAdapter(DetailActivity.this, reviewList);
                    } else {
                        reviewAdapter.notifyDataSetChanged();
                    }
                };
        movieDetailsViewModel = ViewModelProviders.of(this)
                .get(MovieDetailsViewModel.class);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this,
                "Something went wrong.", Toast.LENGTH_SHORT).show();
    }
}
