package com.example.dryruntagtofile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    Context context;
    ArrayList<File> filesAndFolders = new ArrayList<>();
    MemoryDB memdb;
    Stack<String> browsePath;

    public MyAdapter(Context context, File[] filesAndFolders, Stack<String> browsePath){ //Constructor (set from FileListActivity)
        this.context = context;
        this.filesAndFolders.addAll(Arrays.asList(filesAndFolders));
        this.memdb = MemoryDB.getInstance(context);
        this.browsePath = browsePath;
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

        if(selectedFile.isDirectory()){
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_24);
        }else{
            holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
        }

        // attach long click event
        // opens a popup to add and remove tags
        if(selectedFile.isFile()){
            holder.itemView.setLongClickable(true);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    SettingPopup popup = new SettingPopup(context, holder.itemView, memdb);
                    popup.attachItem(holder);
                    popup.openPopup(selectedFile.getAbsolutePath());
                    return true;
                }
            });
        }

        //if directory - open file list recursively
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedFile.isDirectory()){
                    String path = selectedFile.getAbsolutePath();
                    browsePath.push(path);
                    updateList(selectedFile);
                }else{
                    //open the file
                    try {
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        String type = URLConnection.guessContentTypeFromName(selectedFile.getName()); // get the closest guess for file mime type and corresponding apps to open for it
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

    @Override
    public int getItemCount() {
        return filesAndFolders.size();
    }

    public boolean updateList(File dir){
        if(dir.isDirectory()){
            filesAndFolders.clear();
            filesAndFolders.addAll(Arrays.asList(dir.listFiles()));
            this.notifyDataSetChanged();
            return true;
        }
        return false;
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
                tagContainer.removeAllViews();
                String tags[] = memdb.getTagsForFile(file.getAbsolutePath());
                for(int i = 0; i < tags.length; i++){
                    TextView tg = new TextView(context);
                    tg.setText(tags[i]);
                    tg.setTextSize(14);
                    tg.setPadding(10, 5, 10, 5);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10,2,10,0);
                    tg.setLayoutParams(params);
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
