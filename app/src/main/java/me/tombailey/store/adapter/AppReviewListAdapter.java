package me.tombailey.store.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import me.tombailey.store.R;
import me.tombailey.store.model.Review;


/**
 * Created by Tom on 28/11/2016.
 */

public class AppReviewListAdapter extends RecyclerView.Adapter<AppReviewListAdapter.AppReviewViewHolder> {

    private Review[] mReviews;
    private RequestManager mRequestManager;
    private AdapterItemSelectedListener<Review> mReviewSelectedListener;

    public AppReviewListAdapter(Review[] reviews, RequestManager requestManager, AdapterItemSelectedListener<Review> reviewSelectedListener) {
        mReviews = reviews;
        mRequestManager = requestManager;
        mReviewSelectedListener = reviewSelectedListener;
    }

    @Override
    public AppReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = ((Activity) parent.getContext()).getLayoutInflater();
        return new AppReviewViewHolder(layoutInflater.inflate(R.layout.list_review_view, parent, false));
    }

    @Override
    public void onBindViewHolder(AppReviewViewHolder holder, int position) {
        holder.setReview(mReviews[position]);
    }

    @Override
    public int getItemCount() {
        return mReviews.length;
    }

    public void setReviews(Review[] reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    public class AppReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDescription;
        private SimpleRatingBar srbRating;

        private Review mReview;

        public AppReviewViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mReviewSelectedListener.onSelected(mReview);
                }
            });

            tvDescription = (TextView) itemView.findViewById(R.id.list_review_view_text_view_description);
            srbRating = (SimpleRatingBar) itemView.findViewById(R.id.list_review_view_simple_rating_bar_rating);
        }

        public void setReview(Review review) {
            mReview = review;
            tvDescription.setText(review.getDescription());
            srbRating.setRating(review.getRating());
        }
    }
}
