package com.example.dryruntagtofile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Context;
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
import java.util.HashSet;

public class TagListViewingGrid extends AppCompatActivity {
    DiskDB diskDB = null;
    MemoryDB memoryDB = null;

    Context ctx;
    Toolbar toolbar;
    TextView searchBarText;
    private Integer searchOperation;

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


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("TAG", "onActivity Result");
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Integer searchOperationResult = data.getIntExtra("searchOperation", 0);
                        HashSet<String> selectedTags = new HashSet<>(data.getStringArrayListExtra("selected_tags"));
//                        for (String selectedTag: selectedTags) {
//                            Log.d("FILTER_RESULT", selectedTag);
//                        }
                        switch (searchOperationResult) {
                            case 1: {
//                                andTags.addAll(selectedTags);
                                andTags = selectedTags;
                                Log.d("FILTER_RESULT", "AND Result");
                                break;
                            }
                            case 2: {
//                                orTags.addAll(selectedTags);
                                orTags = selectedTags;
                                Log.d("FILTER_RESULT", "OR Result");
                                break;
                            }
                            case 3: {
//                                notTags.addAll(selectedTags);
                                notTags = selectedTags;
                                Log.d("FILTER_RESULT", "NOT Result");
                                break;
                            }
                            default: {
//                                Intent intent = getIntent();
                                finish();
//                                startActivity(intent);
                                Log.d("FILTER_RESULT", "Activity Restarted");
                                break;
                            }
                        }
                        showFilterDialog();
                    }
                    else {
                        Log.d("FILTER_RESULT", "TAGS Selection ERROR");
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list_viewing_grid);
        tagGrid = findViewById(R.id.tagGrid);
        toolbar = findViewById(R.id.tbtoolbar);
        searchBarText = findViewById(R.id.search_bar_text);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("");

        ctx = TagListViewingGrid.this;

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

    private void startFilterActivity(HashSet<String> presentTags, HashSet<String> exclusiveTags, String searchText, Integer searchOperation) {
        ArrayList<String> availableTags = new ArrayList<>(tags);
        Intent intent = new Intent(TagListViewingGrid.this, FilterTagListSelection.class);
        intent.putExtra("searchOperation", searchOperation);
        intent.putStringArrayListExtra("tags", availableTags);
        intent.putExtra("presentTags", presentTags);
        intent.putExtra("exclusiveTags", exclusiveTags);
//        intent.putExtra("searchText", searchText);
        activityResultLauncher.launch(intent);

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

        HashSet<String> exclusiveTags = new HashSet<>(tags);
        btnAndFilter.setOnClickListener(view -> {
            searchOperation = 1;
            Boolean b = exclusiveTags.removeAll(andTags);
            startFilterActivity(andTags, exclusiveTags, "Search (Contains Each Tags)", 1);
//            searchBarText.setText("Search (Contains Each Tags)");
            filterDialog.dismiss();
        });

        btnOrFilter.setOnClickListener(view -> {
            searchOperation = 2;
            Boolean b = exclusiveTags.removeAll(orTags);
            startFilterActivity(orTags, exclusiveTags, "Search (Contains Tags)", 2);
//            searchBarText.setText("Search (Contains Tags)");
            filterDialog.dismiss();
        });

        btnNotFilter.setOnClickListener(view -> {
            searchOperation = 3;
            Boolean b = exclusiveTags.removeAll(notTags);
            startFilterActivity(notTags, exclusiveTags, "Search (Does NOT Contains Tags)", 3);
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
            Toast.makeText(this, "Want Result", Toast.LENGTH_LONG).show();

            HashSet<String> andBox = intersection(memoryDB.getUIDSet(andTags));

            int i = 0;
            for (String filePath : andBox) {
                Log.d("filePath", String.valueOf(i++));
                Log.d("filePath", filePath);
            }

            Toast.makeText(this, "Want Result 1", Toast.LENGTH_LONG).show();

            HashSet<String> orBox = union(memoryDB.getUIDSet(orTags));

            i = 100;
            for (String filePath : orBox) {
                Log.d("filePath", String.valueOf(i++));
                Log.d("filePath", filePath);
            }

            Toast.makeText(this, "Want Result 2", Toast.LENGTH_LONG).show();

            HashSet<String> intersectAO = new HashSet<>();
            intersectAO.retainAll(andBox);
            intersectAO.retainAll(orBox);

            filterResult.clear();
            filterResult = filterNot(intersectAO, memoryDB.getUIDSet(notTags));
            Toast.makeText(view.getContext(),"Filtered Results",Toast.LENGTH_LONG).show();

            Toast.makeText(this, "Want Result 3", Toast.LENGTH_LONG).show();

            for (String resultTag : filterResult) {
                Log.d("TAG_RESULT", resultTag);
            }
            //start Result activity
//            Intent intent = new Intent(TagListViewingGrid.this, FilterResult.class);
//            intent.putExtra("filterResult", filterResult);
//            startActivity(intent);

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
                searchOperation = 0;
                filterDialog.dismiss();
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
            String[] filesString = diskDB.getFilePathsFor(tagUID);
            unionSet.addAll(Arrays.asList(filesString));
//            for (String filePath : filesString){
//                unionSet.add(filePath);
//            }
        }
        return unionSet;
    }

    private HashSet<String> intersection(HashSet<Integer> tagsUID) {
        HashSet<String> intersectionSet = new HashSet<>();
        for (Integer tagUID: tagsUID)
            Log.d("TAGUID", String.valueOf(tagUID));

        for (Integer tagUID: tagsUID) {
            String[] filesString = diskDB.getFilePathsFor(tagUID);
//            Arrays.toString(fileString);
            intersectionSet.retainAll(Arrays.asList(filesString));
//            intersectionSet.retainAll(Arrays.asList(diskDB.getFilePathsFor(tagUID)));
        }
        return intersectionSet;
    }

    private HashSet<String> filterNot(HashSet<String> filePaths, HashSet<Integer> notTags) {
        diskDB = new DiskDB(this);
        HashSet<String> notFilePaths = new HashSet<>();
        ArrayList<String> filePathsForNotUID = new ArrayList<>();
        for (Integer uid: notTags) {
            Boolean b = Collections.addAll(filePathsForNotUID, diskDB.getFilePathsFor(uid));
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