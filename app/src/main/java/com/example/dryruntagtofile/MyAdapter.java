package com.example.dryruntagtofile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    Context context;
    ArrayList<File> filesAndFolders = new ArrayList<>();
    MyAdapter self = this;
    MemoryDB memDB = null;

    public MyAdapter(Context context, File[] filesAndFolders){ //Constructor (set from FileListActivity)
        this.context = context;
        this.filesAndFolders.addAll(Arrays.asList(filesAndFolders));
        memDB = MemoryDB.getInstance(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {

        File selectedFile = filesAndFolders.get(position);
        holder.populateData(selectedFile);

        //if directory - open file list recursively
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedFile.isDirectory()){
                    Intent intent = new Intent(context, FileListActivity.class);
                    String path = selectedFile.getAbsolutePath();
                    intent.putExtra("path", path);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    //open the file
                    try {
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        String type = "image/*";
                        intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()), type);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(context.getApplicationContext(),"Cannot open the file",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public boolean updateList(File dir){
        if(dir.isDirectory()){
            filesAndFolders.clear();
            filesAndFolders.addAll(Arrays.asList(dir.listFiles()));
            self.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return filesAndFolders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;
        LinearLayout tagContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name_text_view);
            imageView = itemView.findViewById(R.id.icon_view);
            tagContainer = itemView.findViewById(R.id.tag_cont);
        }

        public void populateData(File file){
            textView.setText(file.getName());
            if(file.isDirectory()){
                imageView.setImageResource(R.drawable.ic_baseline_folder_24);
            }else{
                imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
            }

            try{
                String tags[] = memDB.getTagsForFile(file.getAbsolutePath());
                for(int i = 0; i < tags.length; i++){
                    TextView tg = new TextView(context);
                    tg.setText(tags[i]);
                    tg.setTextSize(14);
                    tg.setPadding(5, 2, 5, 2);
                    tg.setTextColor(context.getColor(R.color.white));
                    tg.setBackgroundColor(context.getColor(R.color.teal_700));
                    tagContainer.addView(tg);
                }
            } catch(Exception e){
                return;
            }
        }
    }
}
