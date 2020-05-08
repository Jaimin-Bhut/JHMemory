package com.jb.dev.jhmemory.firestore;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.jb.dev.jhmemory.R;

import java.util.ArrayList;
import java.util.List;

public class GridListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    GridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_list);
//        ArrayList arrayList = isSetData();
        initToolbar();
        prepareData();
    }


    ArrayList isSetData() {
        List<String> strings = new ArrayList<>();
        strings.add("https://firebasestorage.googleapis.com/v0/b/jh-memory.appspot.com/o/857412270_393806.jpg?alt=media&token=2460a823-d405-4890-a545-4668c2dbaa4d");
        strings.add("https://firebasestorage.googleapis.com/v0/b/jh-memory.appspot.com/o/857412247_392731.jpg?alt=media&token=f36d70b2-8aea-4cec-9b78-e23f3f07288b");
        strings.add("https://firebasestorage.googleapis.com/v0/b/jh-memory.appspot.com/o/857412016_392660.jpg?alt=media&token=2ea3e538-29d3-49fe-b4a7-bfab8b1bb56c");
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < strings.size(); i++) {
            ImageListModel imageListModel = new ImageListModel();
            imageListModel.setName(strings.get(i));
            arrayList.add(imageListModel);
        }
        Log.e("arrayList>>", arrayList.size() + "");
        return arrayList;
    }

    private void prepareData() {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference listRef = storage.getReference();
        final List<String> strings = new ArrayList<>();
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(final ListResult listResult) {
                        for (final StorageReference item : listResult.getItems()) {
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    strings.add(uri + "");
                                    isSetList(strings);
                                }
                            });
                        }
                    }
                });
    }

    void isSetList(List<String> strings) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < strings.size(); i++) {
            ImageListModel imageListModel = new ImageListModel();
            imageListModel.setName(strings.get(i));
            arrayList.add(imageListModel);
        }
        recyclerView = findViewById(R.id.activity_grid_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        gridAdapter = new GridAdapter(getApplicationContext(), arrayList);
        recyclerView.setAdapter(gridAdapter);
    }

    void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notif_setting, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
