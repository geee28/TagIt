package com.example.dryruntagtofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TagListViewingGrid extends AppCompatActivity {
    DiskDB diskDB = null;
    Toolbar toolbar;
    TextView searchBarText;
    private Integer searchOperation;

    RecyclerView tagGrid;
    List<String> tags;
    Integer image;
    GridAdapter gridadapter;

    /*
     * Search Operation
     * And -> 1
     * OR - > 2
     * Not -> 3
     */
    FloatingActionButton btnSearchFilter;
    // Dialog components
    ImageView btnFilterClose;
    MaterialButton btnAndFilter, btnOrFilter, btnNotFilter;
    private ChipGroup andChipGroup, orChipGroup, notChipGroup;
    ChipGroup chipGroup = null;
    HashSet<String> andTags = new HashSet<>();
    HashSet<String> orTags = new HashSet<>();
    HashSet<String> notTags = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list_viewing_grid);
        tagGrid = findViewById(R.id.tagGrid);
        toolbar = findViewById(R.id.tbtoolbar);
        searchBarText = findViewById(R.id.search_bar_text);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("");

        searchOperation = 1;
        btnSearchFilter = findViewById(R.id.btn_search_filter);
        btnSearchFilter.setOnClickListener(view -> showDialog());

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
//        syncTags();
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

    private void syncTags() {
        diskDB = new DiskDB(this);
        Set<String> tagsName = diskDB.getTags().keySet();
        HashSet<String> tagsSet = new HashSet<>(tagsName);
    }

    private void showDialog() {
        Dialog filterDialog = new Dialog(TagListViewingGrid.this, R.style.filter_dialog_theme);
        filterDialog.setContentView(R.layout.search_filter_dialog);
        filterDialog.setCancelable(true);
        filterDialog.setCanceledOnTouchOutside(true);

        btnFilterClose = filterDialog.findViewById(R.id.btn_filter_close);
        btnAndFilter = filterDialog.findViewById(R.id.btn_and_filter);
        btnOrFilter = filterDialog.findViewById(R.id.btn_or_filter);
        btnNotFilter = filterDialog.findViewById(R.id.btn_not_filter);

        andChipGroup = filterDialog.findViewById(R.id.and_filter_chipGroup);
        orChipGroup = filterDialog.findViewById(R.id.or_filter_chipGroup);
        notChipGroup = filterDialog.findViewById(R.id.not_filter_chipGroup);

        btnFilterClose.setOnClickListener(view -> filterDialog.dismiss());

        btnAndFilter.setOnClickListener(view -> {
            searchOperation = 1;
            searchBarText.setText("Search (Contains Each Tags)");
            filterDialog.dismiss();
        });

        btnOrFilter.setOnClickListener(view -> {
            searchOperation = 2;
            searchBarText.setText("Search (Contains Tags)");
            filterDialog.dismiss();
        });

        btnNotFilter.setOnClickListener(view -> {
            searchOperation = 3;
            searchBarText.setText("Search (Does NOT Contains Tags)");
            filterDialog.dismiss();
        });

//        andTags.add("Tag1");
//        andTags.add("Tag2");
//        andTags.add("Tag3");
//        andTags.add("Tag4");
//        andTags.add("Tag5");
//        orTags.add("Tag6");
//        orTags.add("Tag7");
//        orTags.add("Tag8");
//        notTags.add("Tag9");

        switch(searchOperation) {
            case 1:
                chipGroup = andChipGroup;
                break;
            case 2:
                chipGroup = orChipGroup;
                break;
            case 3:
                chipGroup = notChipGroup;
                break;
            default:
                searchOperation = 1;
                chipGroup = andChipGroup;
                break;
        }

        fillChipGroup(andTags, andChipGroup);
        fillChipGroup(orTags, orChipGroup);
        fillChipGroup(notTags, notChipGroup);

        filterDialog.show();
    }

    private void fillChipGroup(HashSet<String> chipGroupTags, ChipGroup chipGroup) {
        for (String tag: chipGroupTags) {
            insertChip(tag, chipGroupTags, chipGroup);
        }
    }

    private void insertChip(String tagName, HashSet<String> chipGroupTags, ChipGroup chipGroup) {
        Chip chip = new Chip(this);
        chip.setText(tagName);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setOnCloseIconClickListener(view -> {
            Chip chip1 = (Chip) view;
            chipGroup.removeView(chip1);
            chipGroupTags.remove(tagName);
        });
        chipGroup.addView(chip);
        chipGroup.setVisibility(View.VISIBLE);
    }

//    private HashSet<String> union(HashSet<Integer> tagsUID) {
//        HashSet<String> unionSet = new HashSet<String>();
//        for(Integer tagUID : tagsUID) {
//            //getFilesForATag()
//            String filesString[] = diskDB.listFilePathsFor(tagUID);
//            for (String filePath : filesString){
//                unionSet.add(filePath);
//            }
//        }
//        return unionSet;
//    }

//    private HashSet<Integer> intersection(HashSet<Integer> tags) {
//        HashSet<Integer> result = new HashSet<>();
//
//        Iterator<Integer> itr = tags.iterator();
//        Integer tag1 = itr.next();
//        while (itr.hasNext()) {
//            //itr.next();
//        }
//        return result;
//    }
}