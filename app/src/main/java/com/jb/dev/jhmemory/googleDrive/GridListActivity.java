package com.jb.dev.jhmemory.googleDrive;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.jb.dev.jhmemory.R;
import com.jb.dev.jhmemory.activities.LoginActivity;
import com.jb.dev.jhmemory.helper.AuthHelper;

import java.util.ArrayList;
import java.util.List;

public class GridListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Uri> imageListModels;
    private DatabaseReference mDatabase;
    GridAdapter gridAdapter;
    List list;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_list);
        recyclerView = findViewById(R.id.activity_grid_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        imageListModels = new ArrayList<>();
        initToolbar();
        init();
    }

    void init() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference listRef = storage.getReference();
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(final ListResult listResult) {
                        for (final StorageReference item : listResult.getItems()) {
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageListModels.add(uri);
                                    gridAdapter = new GridAdapter(getApplicationContext(), imageListModels);
                                    recyclerView.setAdapter(gridAdapter);
                                    SpacesItemDecoration decoration = new SpacesItemDecoration(16);
                                    recyclerView.addItemDecoration(decoration);
                                }
                            });
                            String s = item.getName();
                            Log.e("a=>>", s);
                        }
                    }
                });

    }

    // for (final StorageReference item : listResult.getItems()) {
//                            item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Uri> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.e("task->>", task.getResult() + "");
//                                        imageListModels.add(task.getResult());
//                                        gridAdapter = new GridAdapter(getApplicationContext(), imageListModels);
//                                        recyclerView.setAdapter(gridAdapter);
//                                        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
//                                        recyclerView.addItemDecoration(decoration);
//                                    }
//                                }
//                            });
//                            String s = item.getName();
//                            Log.e("a=>>", s);
//                        }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notif) {
            FirebaseAuth.getInstance().signOut();
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(GridListActivity.this, LoginActivity.class));
                        }
                    });
            AuthHelper.isClear(GridListActivity.this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

}
