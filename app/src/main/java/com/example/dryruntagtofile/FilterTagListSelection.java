package com.example.dryruntagtofile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class FilterTagListSelection extends AppCompatActivity {

    CheckedTagsAdapter adapter;
    ArrayList<String> presentTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_tag_list_selection);

        Toolbar toolbar = findViewById(R.id.tbtoolbar);
        TextView searchBarText = findViewById(R.id.search_bar_text);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("");

        ArrayList<String> tags = this.getIntent().getStringArrayListExtra("tags");
        ArrayList<String> availableTags = tags;
        Integer searchOperation = this.getIntent().getIntExtra("searchOperation", 0);

        presentTags = this.getIntent().getStringArrayListExtra("presentTags");
        ArrayList<String> exclusiveTags = this.getIntent().getStringArrayListExtra("exclusiveTags");;
        if (exclusiveTags != null && exclusiveTags.size() > 0) {
            availableTags.removeAll(exclusiveTags);
        }

        adapter = new CheckedTagsAdapter(this, tags, availableTags, presentTags);
        RecyclerView availableTagsView = findViewById(R.id.tag_selection_list);
        availableTagsView.setLayoutManager(new LinearLayoutManager(this));
        availableTagsView.setAdapter(adapter);

        findViewById(R.id.done_selection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> selectedTags = new ArrayList<>();
//                selectedTags.addAll(filteredTags);
                selectedTags.addAll(adapter.getSelectedTags());
                Intent intent = new Intent();
                intent.putStringArrayListExtra("selected_tags", selectedTags);
                intent.putExtra("searchOperation", searchOperation);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}

