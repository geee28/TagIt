package com.example.dryruntagtofile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class FileListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        //Find Components
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView noFilesText = findViewById(R.id.nofiles_text);

        // Get Directory items
        String filepath = getIntent().getStringExtra("path");
        File root = new File(filepath);
        File[] filesAndFolders = root.listFiles();

        //Set Report Visible if no Directory items are Available
        if(filesAndFolders==null || filesAndFolders.length == 0){
            noFilesText.setVisibility(View.VISIBLE);
            return;
        }
        noFilesText.setVisibility(View.INVISIBLE);

        MyAdapter fileBrowser = new MyAdapter(getApplicationContext(), filesAndFolders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fileBrowser);

    }
}