package com.jb.dev.jhmemory.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.jb.dev.jhmemory.R;
import com.jb.dev.jhmemory.helper.AuthHelper;

public class ShowData extends AppCompatActivity {
    ImageView imageView;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        imageView = findViewById(R.id.show_data_img);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        isLoadImage();
    }

    void isLoadImage() {
        String url = "https://firebasestorage.googleapis.com/v0/b/jh-memory.appspot.com/o/IMG_0154.JPG?alt=media&token=8fb2a168-31fb-41ad-84af-f35cbe700d3e";
        Glide.with(this /* context */)
                .load(url)
                .into(imageView);
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
            AuthHelper.isClear(ShowData.this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }
}
