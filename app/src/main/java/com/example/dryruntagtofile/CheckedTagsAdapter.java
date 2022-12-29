package com.example.dryruntagtofile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CheckedTagsAdapter extends RecyclerView.Adapter<CheckedTagsAdapter.ViewHolder> implements Filterable {

    Context ctx;
    List<String> tags;
    List<String> tagsFiltered;
    List<String> availableTags;
    ArrayList<String> presentTags;
    HashSet<String> tagsFilteredSet = new HashSet<>();
    LayoutInflater inflater;

    public CheckedTagsAdapter(Context ctx, List<String> tags, ArrayList<String> availableTags, ArrayList<String> presentTags) {
        this.ctx = ctx;
        this.tags = tags;
        this.presentTags = presentTags;
        this.availableTags = availableTags;
        this.tagsFiltered = tags;
        this.inflater = LayoutInflater.from(ctx);
        tagsFiltered.addAll(presentTags);
    }

    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence.length() == 0 || charSequence == null) {
                    filterResults.values = availableTags;
                    filterResults.count = availableTags.size();
                } else {
                    String searchChar = charSequence.toString().toLowerCase();
                    List<String> filteredResults = new ArrayList<>();
                    for (String tagName : availableTags) {
                        if (tagName.toLowerCase().contains(searchChar)) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tagName;
        CheckBox checkBox;
        androidx.cardview.widget.CardView tagHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.tag_checkbox);
            tagName = itemView.findViewById(R.id.grid_tname);
            tagHolder = itemView.findViewById(R.id.tag_cont);
            itemView.findViewById(R.id.grid_image).setVisibility(View.GONE);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tagName.setText(tags.get(position));
        holder.checkBox.setVisibility(View.VISIBLE);
        if (presentTags.size() > 0 && presentTags.contains(tags.get(position))) {
            holder.checkBox.setChecked(true);
        }
        if (tags != null && tags.size() > 0) {
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.checkBox.isChecked()) {
//                      filteredTags.add(tags.get(position));
                        tagsFiltered.add(tags.get(holder.getAdapterPosition()));

                    } else {
//                      filteredTags.remove(tags.get(position));
                        tagsFiltered.remove(tags.get(holder.getAdapterPosition()));

                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.availableTags.size();
    }

    public HashSet<String> getSelectedTags() {
        tagsFilteredSet.addAll(tagsFiltered);
        return tagsFilteredSet;
    }
}
