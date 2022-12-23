package com.example.dryruntagtofile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> implements Filterable {

    List<String> tags;
    List<String> tagsFiltered;
    Integer image;
    LayoutInflater inflater;

    public GridAdapter(Context ctx, List<String> tags, Integer image){
        this.tags = tags;
        this.tagsFiltered = tags;
        this.image = image;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence.length()==0 || charSequence == null){
                    filterResults.values = tagsFiltered;
                    filterResults.count = tagsFiltered.size();
                }
                else{
                    String searchChar = charSequence.toString().toLowerCase();
                    List<String> filteredResults = new ArrayList<>();
                    for(String tagname:tagsFiltered){
                        if(tagname.toLowerCase().contains(searchChar)){
                            filteredResults.add(tagname);
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
        TextView tagname;
        ImageView editIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagname = itemView.findViewById(R.id.grid_tname);
            editIcon = itemView.findViewById(R.id.grid_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),"displaying contents of"+ tags.get(getAdapterPosition()),Toast.LENGTH_LONG).show();
                }
            });
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
        holder.tagname.setText(tags.get(position));
        holder.editIcon.setImageResource(image);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }


}
