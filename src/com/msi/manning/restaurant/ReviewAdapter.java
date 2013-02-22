package com.msi.manning.restaurant;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.msi.manning.restaurant.data.Review;

import java.util.List;

/**
 * Custom adapter for "Review" model objects.
 * 
 * @author charliecollins
 */
public class ReviewAdapter extends BaseAdapter {

    private static final String CLASSTAG = ReviewAdapter.class.getSimpleName();
    private final Context context;
    private final List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
        Log.v(Constants.LOGTAG, " " + ReviewAdapter.CLASSTAG + " reviews size - " + reviews.size());
    }

    public int getCount() {
        return reviews.size();
    }

    public Object getItem(int position) {
        return reviews.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Review review = reviews.get(position);
        if (convertView == null || 
                !(convertView instanceof ReviewListView))
        {
            return new ReviewListView(context, review.name, 
                    review.rating);
        }
        ReviewListView view = (ReviewListView)convertView;
        view.setName(review.name);
        view.setRating(review.rating);
        return view;
    }

    /**
     * ReviewListView that adapter returns as its view item per row.
     * 
     * @author charliecollins
     */
    private final class ReviewListView extends LinearLayout {

        private TextView name;
        private TextView rating;

        public ReviewListView(Context context, String itemName, 
                String itemRating) {

            super(context);
            setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 3, 5, 0);

            name = new TextView(context);
            name.setText(itemName);
            name.setTextSize(16f);
            name.setTextColor(Color.WHITE);
            addView(name, params);

            rating = new TextView(context);
            rating.setText(itemRating);
            rating.setTextSize(16f);
            rating.setTextColor(Color.GRAY);
            addView(rating, params);
        }
        
        public void setName(String itemName)
        {
            name.setText(itemName);
        }
        
        public void setRating(String itemRating)
        {
            rating.setText(itemRating);
        }
    }
}
