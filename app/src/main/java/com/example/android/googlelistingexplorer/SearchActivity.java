package com.example.android.googlelistingexplorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity {
    // initialize variable to hold string to be attached to google api URL
    String finalString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        // Find Search button
        Button SearchButton = (Button) findViewById(R.id.Search_button);

        // set a click listener on button
        SearchButton.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the search button is clicked on.
            @Override
            public void onClick(View view) {
                //creates the final URL
                QueryCreator();
                // pass final URL string to MainActivity
                Intent buttonIntent = new Intent(SearchActivity.this, MainActivity.class);
                buttonIntent.putExtra("query", finalString);
                //starts the MainActivity
                startActivity(buttonIntent);
            }
        });
    }

    public String QueryCreator () {
        //find and get user's input from EditText
        EditText searchKey = (EditText) findViewById(R.id.Search_key);
        String stringToAdd = searchKey.getText().toString();
        //remove spaces from user's input
        stringToAdd = stringToAdd.replace(" ","%20").trim();
        //attach user's search key to Google Book API Url
        finalString = "https://www.googleapis.com/books/v1/volumes?q=" + stringToAdd;
        Log.v("SearchActivity",  finalString);
        return finalString;
    }


}
