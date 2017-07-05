package com.example.android.googlelistingexplorer;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.R.attr.resource;

/**
 * Created by georgeD on 29/06/2017.
 */

public class BookItemAdapter extends ArrayAdapter<BookItem> {

    //Constructs a new BookItemAdapter.
    // bookItems is the list of books, which is the data source of the adapter
    public BookItemAdapter(Context context, ArrayList<BookItem> bookItems) {
        super(context,0, bookItems);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new book list item layout.
        View bookItemView = convertView;
        if (bookItemView == null) {
            bookItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        // Find the book at the given position in the list of books
        BookItem currentBookItem = getItem(position);

        // Find the TextView for book title
        TextView title_TextView = (TextView) bookItemView.findViewById(R.id.Title_TextView);
         // Find the TextView for book author
        TextView author_TextView = (TextView) bookItemView.findViewById(R.id.Author_TextView);
         // Find the TextView for book language
        TextView language_TextView = (TextView) bookItemView.findViewById(R.id.Language_TextView);
         // Find the TextView for book description
        TextView description_TextView = (TextView) bookItemView.findViewById(R.id.Description_TextView);

        // Find the TextView for book image
        ImageView thumbnailImageView = (ImageView) bookItemView.findViewById(R.id.Image_View);

        // Check if there is a link to an image, otherwise use a NO image placeholder.
        if (currentBookItem.getImage() != null) {
            Picasso.with(getContext()).load(currentBookItem.getImage()).into(thumbnailImageView);
        } else {
            thumbnailImageView.setImageResource(R.drawable.no_image_available);
        }

        // Display the time of the book title in that TextView
        title_TextView.setText(currentBookItem.getTitle());
        // Display the book author in that TextView
        author_TextView.setText(currentBookItem.getAuthor());
        // Display the book language in that TextView
        language_TextView.setText(currentBookItem.getLanguage());
        // Display the book description in that TextView
        description_TextView.setText(currentBookItem.getDescription());

        // Return the list item view that is now showing the appropriate data
        return bookItemView;
    }
}
