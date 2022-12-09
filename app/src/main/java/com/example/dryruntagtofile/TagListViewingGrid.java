package com.example.dryruntagtofile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class TagListViewingGrid extends AppCompatActivity {
    RecyclerView tagGrid;
    List<String> tags;
    Integer image;
    GridAdapter gridadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list_viewing_grid);
        tagGrid = findViewById(R.id.tagGrid);
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

        GridLayoutManager glm = new GridLayoutManager(this, 2,GridLayoutManager.VERTICAL, false);
        tagGrid.setLayoutManager(glm);
        tagGrid.setAdapter(gridadapter);



    }
}