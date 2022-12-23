package com.example.dryruntagtofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TagListViewingGrid extends AppCompatActivity {
    DiskDB diskDB = null;
    MemoryDB memoryDB = null;
    Toolbar toolbar;
    TextView searchBarText;
    private Integer searchOperation;
    private Boolean isFilterReturn = false;

    TextView noTagsText;
    RecyclerView tagGrid;
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
    MaterialButton btnAndFilter, btnOrFilter, btnNotFilter, btnClearFilter, btnFilterResult;
    private ChipGroup andChipGroup, orChipGroup, notChipGroup;
    ChipGroup chipGroup = null;

    TextView ediTagName;
    EditText editTag;
    ImageView btnEditClose;
    MaterialButton btnApplyChanges, btnDeleteTag;

    HashSet<String> tags = new HashSet<>();
    HashSet<String> filterResult = new HashSet<>();
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

        searchOperation = 0;
        btnSearchFilter = findViewById(R.id.btn_search_filter);
        btnSearchFilter.setOnClickListener(view -> showFilterDialog());

        noTagsText = findViewById(R.id.notags_text);

        memoryDB = new MemoryDB(this);
        tags = memoryDB.getTagsSet();
        image = R.drawable.ic_baseline_edit_24;

        //Set Report Visible if no Directory items are Available
        if(tags==null || tags.isEmpty()){
            noTagsText.setVisibility(View.VISIBLE);
            return;
        }
        noTagsText.setVisibility(View.INVISIBLE);

//        if(isFilterReturn) {
//            Intent intent = getIntent();
//            searchOperation = intent.getIntExtra("searchOperation", 0);
//            switch(searchOperation) {
//                case 1:
//                    andTags = new HashSet<>(intent.getCategories());
//                    break;
//                case 2:
//                    orTags = new HashSet<>(intent.getCategories());
//                    break;
//                case 3:
//                    notTags = new HashSet<>(intent.getCategories());
//                    break;
//                default:
//                    searchOperation = 0;
//                    break;
//            }
//            showFilterDialog();
//        }

        gridadapter = new GridAdapter(this, new ArrayList<>(tags), image);
        tagGrid.setLayoutManager( new LinearLayoutManager(this));
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

    private void startFilterActivity(HashSet<String> filterTags, String searchText) {
//        Intent intent = new Intent(TagListViewingGrid.this, FilterTagsSelection.class);
//        intent.putExtra("tags", tags);
//        intent.puExtra("searchText", searchText);
//        intent.putExtra("filterTags", filterTags);
//        startActivity(intent);
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
            if(chipGroup == andChipGroup){
                andTags.remove(tagName);
            }
            else if(chipGroup == orChipGroup){
                orTags.remove(tagName);
            }
            else{
                notTags.remove(tagName);
            }
//            chipGroupTags.remove(tagName);
        });
        chipGroup.addView(chip);
        chipGroup.setVisibility(View.VISIBLE);
    }

    private void showFilterDialog() {
        Dialog filterDialog = new Dialog(TagListViewingGrid.this, R.style.dialog_theme);
        filterDialog.setContentView(R.layout.search_filter_dialog);
        filterDialog.setCancelable(true);
        filterDialog.setCanceledOnTouchOutside(true);

        btnFilterClose = filterDialog.findViewById(R.id.btn_filter_close);
        btnAndFilter = filterDialog.findViewById(R.id.btn_and_filter);
        btnOrFilter = filterDialog.findViewById(R.id.btn_or_filter);
        btnNotFilter = filterDialog.findViewById(R.id.btn_not_filter);
        btnClearFilter = filterDialog.findViewById(R.id.btn_clear_filter);
        btnFilterResult = filterDialog.findViewById(R.id.btn_filter_result);

        andChipGroup = filterDialog.findViewById(R.id.and_filter_chipGroup);
        orChipGroup = filterDialog.findViewById(R.id.or_filter_chipGroup);
        notChipGroup = filterDialog.findViewById(R.id.not_filter_chipGroup);

        btnFilterClose.setOnClickListener(view -> filterDialog.dismiss());

        btnAndFilter.setOnClickListener(view -> {
            searchOperation = 1;
            startFilterActivity(andTags, "Search (Contains Each Tags)");
//            searchBarText.setText("Search (Contains Each Tags)");
            filterDialog.dismiss();
        });

        btnOrFilter.setOnClickListener(view -> {
            searchOperation = 2;
            startFilterActivity(orTags, "Search (Contains Tags)");
//            searchBarText.setText("Search (Contains Tags)");
            filterDialog.dismiss();
        });

        btnNotFilter.setOnClickListener(view -> {
            searchOperation = 3;
            startFilterActivity(notTags, "Search (Does NOT Contains Tags)");
//            searchBarText.setText("Search (Does NOT Contains Tags)");
            filterDialog.dismiss();
        });

        btnClearFilter.setOnClickListener(view -> {
            andTags.clear();
            orTags.clear();
            notTags.clear();
            filterDialog.dismiss();
            Toast.makeText(view.getContext(),"Filters Cleared",Toast.LENGTH_LONG).show();
        });

        btnFilterResult.setOnClickListener(view -> {
            HashSet<String> andBox = intersection(memoryDB.getUIDSet(andTags));
            HashSet<String> orBox = union(memoryDB.getUIDSet(orTags));

            HashSet<String> intersectAO = new HashSet<>();
            intersectAO.retainAll(andBox);
            intersectAO.retainAll(orBox);

            filterResult.clear();
            filterResult = filterNot(intersectAO, memoryDB.getUIDSet(notTags));
            Toast.makeText(view.getContext(),"Filtered Results",Toast.LENGTH_LONG).show();
            //start Result activity
            filterDialog.dismiss();
        });


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



//    private void showEditDialog(String tagName) {
//        MemoryDB memoryDB = new MemoryDB(this);
//
//        Dialog editDialog = new Dialog(this, R.style.dialog_theme);
//        editDialog.setContentView(R.layout.edit_tag_layout);
//        editDialog.setCancelable(true);
//        editDialog.setCanceledOnTouchOutside(true);
//
//
//        ediTagName = editDialog.findViewById(R.id.edit_tag_name);
//        editTag = editDialog.findViewById(R.id.edit_name_field);
//        btnEditClose = editDialog.findViewById(R.id.btn_edit_tag_close);
//        btnApplyChanges = editDialog.findViewById(R.id.btn_apply_changes);
//        btnDeleteTag = editDialog.findViewById(R.id.btn_delete_tag);
//
//        ediTagName.setText(tagName);
//
//        btnEditClose.setOnClickListener(view -> editDialog.dismiss());
//
//        btnApplyChanges.setOnClickListener(view -> {
//            String newTagName = editTag.getText().toString();
//
//            memoryDB.updateTag( tagName,  newTagName);
//            tags.remove(tagName);
//            tags.add(newTagName);
//            Toast.makeText(view.getContext(),"Tag Changed",Toast.LENGTH_LONG).show();
//            editDialog.dismiss();
//        });
//
//        btnDeleteTag.setOnClickListener(view -> {
//            try {
//                memoryDB.removeTag(tagName);
//                tags.remove(tagName);
//                Toast.makeText(view.getContext(),"Tag Deleted",Toast.LENGTH_LONG).show();
//            } catch (Exception e) {
//                Toast.makeText(view.getContext(),"Tag Could NOT be Deleted",Toast.LENGTH_LONG).show();
//                throw new RuntimeException(e);
//            }
//            editDialog.dismiss();
//        });
//        editDialog.show();
//    }


    private HashSet<String> union(HashSet<Integer> tagsUID) {
        HashSet<String> unionSet = new HashSet<String>();
        for(Integer tagUID : tagsUID) {
            String filesString[] = diskDB.listFilePathsFor(tagUID);
            unionSet.addAll(Arrays.asList(filesString));
//            for (String filePath : filesString){
//                unionSet.add(filePath);
//            }
        }
        return unionSet;
    }

    private HashSet<String> intersection(HashSet<Integer> tagsUID) {
        HashSet<String> intersectionSet = new HashSet<>();
        for (Integer tagUID: tagsUID) {
            String filesString[] = diskDB.listFilePathsFor(tagUID);
            intersectionSet.retainAll(Arrays.asList(filesString));
        }
        return intersectionSet;
    }

    private HashSet<String> filterNot(HashSet<String> filePaths, HashSet<Integer> notTags) {
        diskDB = new DiskDB(this);
        HashSet<String> notFilePaths = new HashSet<>();
        ArrayList<String> filePathsForNotUID = new ArrayList<>();
        for (Integer uid: notTags) {
            Boolean b = Collections.addAll(filePathsForNotUID, diskDB.listFilePathsFor(uid));
        }
        notFilePaths.addAll(filePathsForNotUID);

        for(String eachFile: filePathsForNotUID){
            if (filePaths.contains(eachFile)){
                filePaths.remove(eachFile);
            }
        }
        return filePaths;
    }
}