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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class TagListViewingGrid extends AppCompatActivity {
    RecyclerView tagGrid;
    List<String> tags;
    Integer image;
    GridAdapter gridadapter;
    Toolbar toolbar;
    private Integer searchOperation;
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
// this is a comment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list_viewing_grid);
        tagGrid = findViewById(R.id.tagGrid);
        toolbar = findViewById(R.id.tbtoolbar);
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
        btnAndFilter.setOnClickListener(view -> searchOperation = 1);
        btnOrFilter.setOnClickListener(view -> searchOperation = 2);
        btnNotFilter.setOnClickListener(view -> searchOperation = 3);
        filterDialog.show();
    }

    private void onSelectTag(String tagName, ChipGroup chipGroup) {
        Chip chip = new Chip(this);
        chip.setText(tagName);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);
        // chip.setOnCloseIconClickListener(this);
        chip.setOnCloseIconClickListener(view -> {
            Chip chip1 = (Chip) view;
            chipGroup.removeView(chip1);
        });
        chipGroup.addView(chip);
        chipGroup.setVisibility(View.VISIBLE);
    }


    /*
    private HashSet<String> union(ArrayList<Integer> tags) {
        HashSet<String> unionSet = new HashSet<String>();
        for(Integer tagUID : tags) {
            //getFilesForATag()
            String filesString = getFilesForATag(tagUID);
            ArrayList<String> files = (ArrayList<String>) Arrays.asList(filesString.split(";"));
            for (String filePath : files){
                unionSet.add(filePath);
            }
        }
        return unionSet;
    }

    private HashSet<String> intersection(ArrayList<Integer> tags) {}
    */
}