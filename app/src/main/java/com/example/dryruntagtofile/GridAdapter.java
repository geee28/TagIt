package com.example.dryruntagtofile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    List<String> tags;
    Integer image;
    LayoutInflater inflater;

    public GridAdapter(Context ctx, List<String> tags, Integer image){
        this.tags = tags;
        this.image = image;
        this.inflater = LayoutInflater.from(ctx);
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
        View view = inflater.inflate(R.layout.custom_grid_layout, parent, false);
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
