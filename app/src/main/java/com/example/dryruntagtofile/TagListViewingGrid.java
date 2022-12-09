package com.example.dryruntagtofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SearchView;


import java.util.ArrayList;
import java.util.List;

public class TagListViewingGrid extends AppCompatActivity {
    RecyclerView tagGrid;
    List<String> tags;
    Integer image;
    GridAdapter gridadapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list_viewing_grid);
        tagGrid = findViewById(R.id.tagGrid);
        toolbar = findViewById(R.id.tbtoolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("");
        tags = new ArrayList<>();
        tags.add("Tag1");
        tags.add("Tag2");
        tags.add("Tag3");
        tags.add("Tag4");
        tags.add("Tag5");
        tags.add("Tag6");
        tags.add("Tag7");
        tags.add("Tag8");
        tags.add("Tag9");
        tags.add("Tag10");
        image = R.drawable.ic_baseline_edit_24;
        gridadapter = new GridAdapter(this, tags, image);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        //GridLayoutManager glm = new GridLayoutManager(this, 2,GridLayoutManager.VERTICAL, false);
        //tagGrid.setLayoutManager(glm);
        tagGrid.setLayoutManager(llm);
        tagGrid.setAdapter(gridadapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
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
                gridadapter.getFilter().filter(s);
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