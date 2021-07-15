package com.example.mymovies.adapters;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.R;
import com.example.mymovies.models.Review;

import java.util.Collections;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final LayoutInflater inflater;
    private final Context mContext;
    private List<Review> reviews = Collections.emptyList();
    private Dialog reviewDialog;
    private String HYPHEN = "-";
    private String SPACE = " ";

    public ReviewAdapter(Context mContext, List<Review> reviews) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.reviews = reviews;
        reviewDialog = new Dialog(mContext);
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (View) inflater.inflate(R.layout.review_list_item, parent, false);
        ReviewAdapter.ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        reviewDialog.setContentView(R.layout.detailed_review_dialog);

        viewHolder.reviewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String authorText = HYPHEN + SPACE + reviews.get(viewHolder.getAdapterPosition()).getAuthor();
                TextView reviewDialogAuthorTV = reviewDialog.findViewById(R.id.review_dialog_author_tv);
                TextView reviewDialogTextTV = reviewDialog.findViewById(R.id.review_dialog_content_tv);

                reviewDialogAuthorTV.setText(authorText);
                reviewDialogTextTV.setText(reviews.get(viewHolder.getAdapterPosition()).getContent());

                reviewDialog.show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        final Review current = reviews.get(position);
        String authorText = HYPHEN + SPACE + current.getAuthor();
        holder.reviewTextTV.setText(current.getContent());
        holder.reviewAuthorTV.setText(authorText);

        if (current.getContent().length() < 200) {
            holder.readMoreTV.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView reviewTextTV, reviewAuthorTV, readMoreTV;
        RelativeLayout reviewContainer;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewContainer = itemView.findViewById(R.id.review_container);
            reviewTextTV = itemView.findViewById(R.id.review_text_tv);
            reviewAuthorTV = itemView.findViewById(R.id.review_author_tv);
            readMoreTV = itemView.findViewById(R.id.read_more_tv);
        }
    }
}