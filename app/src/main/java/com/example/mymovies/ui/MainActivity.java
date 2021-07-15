package com.example.mymovies.ui;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.R;
import com.example.mymovies.adapters.MoviesGridAdapter;
import com.example.mymovies.models.Movie;
import com.example.mymovies.utils.QueryUtils;
import com.example.mymovies.viewmodels.MoviesListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_OF_COLUMNS = 2;
    String sort_type = "popular";
    Observer<List<Movie>> movieObserver;
    ProgressBar mProgressBar;
    String api_key;
    private RecyclerView moviesGridRecyclerView;
    private TextView noInternetTV, currentCategoryTV;
    private Button retryInternetBtn;
    private MoviesGridAdapter moviesGridAdapter;
    private MoviesListViewModel moviesListViewModel;
    private List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesGridRecyclerView = findViewById(R.id.MoviesGridRecyclerView);
        noInternetTV = findViewById(R.id.noInternetTV);
        retryInternetBtn = findViewById(R.id.retryInternetBtn);
        mProgressBar = findViewById(R.id.progress_bar);
        currentCategoryTV = findViewById(R.id.current_category_tv);
        currentCategoryTV.setText(R.string.popular_movie_list_caption);
        api_key = getString(R.string.api_key);

        initMoviesViewModel();
        movieList = new ArrayList<>();
        setTitle(getString(R.string.app_name));

        retryInternetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
    }

    private void initMoviesViewModel() {
        movieObserver =
                movies -> {
                    movieList.clear();
                    movieList.addAll(movies);

                    if (moviesGridAdapter == null) {
                        moviesGridAdapter = new
                                MoviesGridAdapter(MainActivity.this, movieList);
                    } else {
                        moviesGridAdapter.notifyDataSetChanged();
                    }
                };
        moviesListViewModel = ViewModelProviders.of(this)
                .get(MoviesListViewModel.class);
        moviesListViewModel.getMovieList(sort_type, api_key)
                .observe(MainActivity.this, movieObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            boolean isOffline = new QueryUtils.CheckOnlineStatus().execute().get();
            if (isOffline && !TextUtils.equals(sort_type, "favorites")) {
                noInternetTV.setVisibility(View.VISIBLE);
                retryInternetBtn.setVisibility(View.VISIBLE);
                moviesGridRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            } else {
                noInternetTV.setVisibility(View.GONE);
                retryInternetBtn.setVisibility(View.GONE);
                moviesGridRecyclerView.setVisibility(View.VISIBLE);

                moviesGridAdapter = new MoviesGridAdapter(this, movieList);
                moviesGridRecyclerView.setAdapter(moviesGridAdapter);
                RecyclerView.LayoutManager mLayoutManager
                        = new GridLayoutManager(this, NUM_OF_COLUMNS);
                moviesGridRecyclerView.setLayoutManager(mLayoutManager);
                moviesGridRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.srt_pprty:
                sort_type = "popular";
                currentCategoryTV.setText(R.string.popular_movie_list_caption);
                moviesListViewModel.getMovieList(sort_type, api_key)
                        .observe(MainActivity.this, movieObserver);
                break;
            case R.id.srt_top_rated:
                sort_type = "top_rated";
                currentCategoryTV.setText(R.string.top_rated_movie_list_caption);
                moviesListViewModel.getMovieList(sort_type, api_key)
                        .observe(MainActivity.this, movieObserver);
                break;
            case R.id.srt_favorites:
                sort_type = "favorites";
                currentCategoryTV.setText(R.string.favorites_movie_list_caption);
                moviesListViewModel.getMovieListFromDB()
                        .observe(MainActivity.this, movieObserver);
                break;
        }
        onResume();
        return true;
    }
}
