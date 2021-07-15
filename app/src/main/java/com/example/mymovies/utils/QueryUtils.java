package com.example.mymovies.utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.mymovies.models.Movie;
import com.example.mymovies.models.Review;
import com.example.mymovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    public static ArrayList<Movie> fetchMoviesData(String requestUrl) {
        String jsonResponse = fetchData(requestUrl);
        // Return the {@link Event}
        return extractMoviesFromJson(jsonResponse);
    }

    private static String fetchData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        return jsonResponse;
    }


    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Return an {@link Movie} object by parsing out information
     * about the first movie from the input moviesJSON string.
     */
    static ArrayList<Movie> extractMoviesFromJson(String moviesJSON) {
        ArrayList<Movie> movies = new ArrayList<>();

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(moviesJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(moviesJSON);
            JSONArray moviesArray = baseJsonResponse.getJSONArray("results");

            // If there are results in the features array
            if (moviesArray.length() > 0) {
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movie = moviesArray.getJSONObject(i);
                    int id = movie.optInt("id");
                    String posterPath = movie.optString("poster_path");
                    String title = movie.optString("title");
                    String releaseDate = movie.optString("release_date");
                    String overview = movie.optString("overview");
                    Double voterAverage = movie.optDouble("vote_average");
                    String backdropPath = movie.optString("backdrop_path");

                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(releaseDate);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy");
                    String MovieYear = dateFormat.format(date1);

                    String voterAverageStr = voterAverage + "/10";

                    Movie movieObject = new Movie(
                            id,
                            title,
                            posterPath,
                            overview,
                            MovieYear,
                            voterAverageStr,
                            backdropPath);
                    movies.add(movieObject);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movies JSON results", e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return movies;
    }


    static ArrayList<Trailer> extractMovieTrailersFromJson(String trailersJSON) {
        ArrayList<Trailer> movieTrailers = new ArrayList<>();

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(trailersJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(trailersJSON);
            JSONArray movieTrailersArray = baseJsonResponse.getJSONArray("results");

            /*{
                "id": "5d894d21d9f4a6000e4dc169",
                    "iso_639_1": "en",
                    "iso_3166_1": "US",
                    "key": "stOVFXuyyWQ",
                    "name": "Moon Rover",
                    "site": "YouTube",
                    "size": 1080,
                    "type": "Clip"
            }*/
            // If there are results in the features array
            if (movieTrailersArray.length() > 0) {
                for (int i = 0; i < movieTrailersArray.length(); i++) {
                    JSONObject movieTrailer = movieTrailersArray.getJSONObject(i);
                    String id = movieTrailer.optString("id");
                    String key = movieTrailer.optString("key");
                    String name = movieTrailer.optString("name");
                    String site = movieTrailer.optString("site");
                    Double size = movieTrailer.optDouble("size");
                    String type = movieTrailer.optString("type");

                    Trailer trailerObject = new Trailer(
                            id,
                            key,
                            name,
                            site,
                            size,
                            type);
                    movieTrailers.add(trailerObject);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movies JSON results", e);
        }
        return movieTrailers;
    }


    static ArrayList<Review> extractMovieReviewsFromJson(String reviewsJSON) {
        ArrayList<Review> movieReviews = new ArrayList<>();

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(reviewsJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(reviewsJSON);
            JSONArray movieReviewsArray = baseJsonResponse.getJSONArray("results");

            /*{
               "author": "markuspm",
                "content": "Since watching this movie I think I feel ...",
                "id": "52b7db5c760ee367060af443",
                "url": "https://www.themoviedb.org/review/52b7db5c760ee367060af443"
            }*/
            // If there are results in the features array
            if (movieReviewsArray.length() > 0) {
                for (int i = 0; i < movieReviewsArray.length(); i++) {
                    JSONObject movieReview = movieReviewsArray.getJSONObject(i);
                    String id = movieReview.optString("id");
                    String content = movieReview.optString("content");
                    String author = movieReview.optString("author");
                    String url = movieReview.optString("url");

                    Review reviewObject = new Review(
                            id,
                            content,
                            author,
                            url);
                    movieReviews.add(reviewObject);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movies JSON results", e);
        }
        return movieReviews;
    }

    /**
     * No Network Condition handled based on link below
     * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     */

    public static boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public static class CheckOnlineStatus extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            //This is a background thread, when it finishes executing will return the result from your function.
            Boolean isOffline;
            isOffline = isOnline();
            return isOffline;
        }
    }
}
