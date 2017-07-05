package com.example.android.googlelistingexplorer;

import android.media.Image;

/**
 * Created by georgeD on 29/06/2017.
 */

public class BookItem {

    /** Url for the book's image */
    private String mImage;
    /** book's title */
    private String mTitle;
    /** book's author */
    private String mAuthor;
    /** book's language */
    private String mLanguage;
    /** book's description */
    private String mDescription;
    /**  book's url for the preview link */
    private String mUrl;

    /**
     * Create a new BookItem object
     * @param image is the image which will be shown as the book's image
     * @param title is the book's title
     * @param author is the book's author
     * @param language is the book's language
     * @param description is a short description of the book
     * @param url is the link to the book's previewURL page
     */

    public BookItem(String image, String title, String author, String language, String description, String url) {
        mImage = image;
        mTitle = title;
        mAuthor = author;
        mLanguage = language;
        mDescription = description;
        mUrl=url;
    }

    /** getter methods */
    public String getImage(){
        return mImage;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public String getLanguage(){
        return mLanguage;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getUrl(){
        return mUrl;
    }
}
