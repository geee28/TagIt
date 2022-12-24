package com.example.dryruntagtofile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class FilterTagListSelection extends AppCompatActivity {

    CheckedTagsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_tag_list_selection);

        Toolbar toolbar = findViewById(R.id.tbtoolbar);
        TextView searchBarText = findViewById(R.id.search_bar_text);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("");

        ArrayList<String> availableTags = this.getIntent().getStringArrayListExtra("tags");
        adapter = new CheckedTagsAdapter(this, availableTags);
        RecyclerView availableTagsView = findViewById(R.id.tag_selection_list);
        availableTagsView.setLayoutManager(new LinearLayoutManager(this));
        availableTagsView.setAdapter(adapter);

        findViewById(R.id.done_selection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> selectedTags = new ArrayList<>();
                selectedTags.addAll(adapter.getSelectedTags());
                Intent intent = new Intent();
                intent.putStringArrayListExtra("selected_tags", selectedTags);
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

class CheckedTagsAdapter extends RecyclerView.Adapter<CheckedTagsAdapter.ViewHolder> {

    Context ctx;
    List<String> tags;
    List<String> availableTags;
    HashSet<String> tagsFiltered = new HashSet<>();
    LayoutInflater inflater;

    public CheckedTagsAdapter(Context ctx, ArrayList<String> tags){
        this.ctx = ctx;
        this.tags = tags;
        this.availableTags = tags;
        this.inflater = LayoutInflater.from(ctx);
    }

    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence.length()==0 || charSequence == null){
                    filterResults.values = availableTags;
                    filterResults.count = availableTags.size();
                }
                else{
                    String searchChar = charSequence.toString().toLowerCase();
                    List<String> filteredResults = new ArrayList<>();
                    for(String tagName: tags){
                        if(tagName.toLowerCase().contains(searchChar)){
                            filteredResults.add(tagName);
                        }
                    }
                    filterResults.values = filteredResults;
                    filterResults.count = filteredResults.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                tags = (List<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tagName;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.tag_checkbox);
            tagName = itemView.findViewById(R.id.grid_tname);
            itemView.findViewById(R.id.grid_image).setVisibility(View.GONE);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(view.getContext(),"Selected "+ tags.get(getAdapterPosition()),Toast.LENGTH_LONG).show();
//                }
//            });
        }
    }


    @NonNull
    @Override
    public CheckedTagsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_tag, parent, false);
        return new CheckedTagsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckedTagsAdapter.ViewHolder holder, int position) {
        holder.tagName.setText(tags.get(position));
        holder.checkBox.setVisibility(View.VISIBLE);
        if(tags != null && tags.size() > 0) {
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.checkBox.isChecked()) {
//                        filteredTags.add(tags.get(position));
                        tagsFiltered.add(tags.get(holder.getAdapterPosition()));
                    }
                    else {
//                        filteredTags.remove(tags.get(position));
                        tagsFiltered.remove(tags.get(holder.getAdapterPosition()));
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public HashSet<String> getSelectedTags(){
        return tagsFiltered;
    }
}
