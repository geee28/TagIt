package com.example.dryruntagtofile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TagsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_list);

        //Find Components
        TextView noTagsText = findViewById(R.id.notags_text);
        RecyclerView recyclerView = findViewById(R.id.tags_recycler_view);

        /*
        // String of User Tags from UID<->Tags relation
        String[] userTags = getUserTags();

        //Set Report Visible if no User Tags are Available or Created
        if(userTags==null || userTags.length == 0){
            noTagsText.setVisibility(View.VISIBLE);
            return;
        }
        noTagsText.setVisibility(View.INVISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter();
         */

    }

}