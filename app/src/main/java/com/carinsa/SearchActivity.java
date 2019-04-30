package com.carinsa;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.ListActivity;

public class SearchActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.search);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            System.out.println(query);
            //doMySearch(query);
        }
    }
}
