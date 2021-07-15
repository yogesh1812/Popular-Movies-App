package com.example.mymovies.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.R;
import com.example.mymovies.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private final LayoutInflater inflater;
    private final Context mContext;
    private List<Trailer> trailers = Collections.emptyList();

    public TrailerAdapter(Context mContext, List<Trailer> trailers) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.trailers = trailers;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (View) inflater.inflate(R.layout.trailer_list_item, parent, false);
        TrailerAdapter.TrailerViewHolder viewHolder = new TrailerAdapter.TrailerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        final Trailer current = trailers.get(position);

        String THUMBNAIL_URL = holder.itemView.getContext().getString(R.string.thumbnail_url);
        String THUMBNAIL_SUFFIX = holder.itemView.getContext().getString(R.string.thumbnail_suffix);
        String url = TextUtils.concat(THUMBNAIL_URL, current.getKey(), THUMBNAIL_SUFFIX).toString();

        Picasso.get()
                .load(url)
                .fit()
                .error(R.drawable.ic_videocam_black_48dp)
                .placeholder(R.drawable.ic_videocam_black_48dp)
                .into(holder.movieThumbnailIV);

        holder.ytButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(holder.itemView.getContext(),
                //        "Playing the videos", Toast.LENGTH_SHORT).show();

                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=" + current.getKey()));
                try {
                    holder.itemView.getContext().startActivity(webIntent);
                } catch (ActivityNotFoundException ex) {
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailers == null ? 0 : trailers.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        ImageView movieThumbnailIV;
        View ytButton;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            movieThumbnailIV = itemView.findViewById(R.id.trailer_poster_iv);
            ytButton = itemView.findViewById(R.id.trailer_play_button);
        }
    }
}