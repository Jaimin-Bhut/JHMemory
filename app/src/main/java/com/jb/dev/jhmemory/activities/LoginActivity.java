package com.jb.dev.jhmemory.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jb.dev.jhmemory.R;
import com.jb.dev.jhmemory.helper.AuthHelper;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonPhoneNumber;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 0;
    private String TAG = "Login Activity";
    GoogleSignInAccount account;
    FirebaseAuth mAuth;
    String email, name;
    TextInputEditText editTextUsername, editTextPassword;
    Button buttonLogin;
    private View view;
    DatabaseReference mDatabaseReference;
    Task<GoogleSignInAccount> task;
    String pass = "12345678";
    CircularImageView circularImageView;
    Uri personPhoto;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = this.getSharedPreferences("is_Set", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User_data");
        view = findViewById(android.R.id.content);
        buttonLogin = findViewById(R.id.login_btn_sign_in);
        editTextPassword = findViewById(R.id.login_edt_password);
        editTextUsername = findViewById(R.id.login_edt_username);
        buttonPhoneNumber = findViewById(R.id.login_btn_number);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        buttonPhoneNumber.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AuthHelper.isLogin(LoginActivity.this)) {
            startActivity(new Intent(LoginActivity.this, ShowData.class));
        }
//        FirebaseUser mUser = mAuth.getCurrentUser();
//        if (mUser != null) {
//            startActivity(new Intent(this, ShowData.class));
//        }
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//            startActivity(new Intent(this, ShowData.class));
//        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            Log.e("User", "Already");
        } else {
            Log.e("User", "New");
        }
        GoogleSignInAccount account = null;
        if (requestCode == RC_SIGN_IN) {
            task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                account = task.getResult(ApiException.class);
                if (account != null) {
                    personPhoto = account.getPhotoUrl();
                    email = account.getEmail();
                    name = account.getDisplayName();
                    showCustomDialog();
                    Log.e("Email", email);
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("firebaseAuthWithGoogle", "Call");
            firebaseAuthWithGoogle(account);
            handleSignInResult(task);
        }
    }

    public void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_user_password);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        circularImageView = dialog.findViewById(R.id.dialog_imageview_user_profile);
        TextView textViewUserName = dialog.findViewById(R.id.dialog_txt_user_name);
        TextView textViewUserEmail = dialog.findViewById(R.id.dialog_txt_user_email);
        textViewUserEmail.setText(email);
        textViewUserName.setText(name);
        circularImageView.setImageURI(personPhoto);
        Glide.with(this).load(personPhoto).into(circularImageView);
        final TextInputEditText edt_password = dialog.findViewById(R.id.dialog_edt_password);
        dialog.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.bt_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dialog_pass = edt_password.getText().toString().trim();
                if (dialog_pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    pass = dialog_pass;
                    Log.e("pass", pass);
                    Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();
                    handleSignInResult(task);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("task success call", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            UserRegister userRegister = new UserRegister(
                                    email,
                                    pass
                            );
                            FirebaseDatabase.getInstance().getReference("User_data")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(userRegister).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(LoginActivity.this, ShowData.class));
                                    } else {
                                        Log.e("ERROR:", task.getException().getMessage());
                                    }
                                }
                            });
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            account.getEmail();
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_number:
                startActivity(new Intent(LoginActivity.this, PhoneNumberActivity.class));
                break;
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.login_btn_sign_in:
                isLogin();
                break;
        }
    }

    private void isLogin() {
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();
        if (username.length() < 8) {
            editTextUsername.setError("Enter Valid Username");
        } else if (password.length() < 8) {
            editTextPassword.setError("Password length greater than 8");
        } else {
            Query q = mDatabaseReference.orderByChild("email").equalTo(username);
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            String pass = String.valueOf(user.child("password").getValue());
                            if (pass.equals(password)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    AuthHelper.isSetLogin(LoginActivity.this, true);
                                }
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Snackbar.make(view, "Wrong Password ", BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getColor(R.color.red_900)).show();
                            }
                        }
                    } else {
                        Snackbar.make(view, "Sorry! User Not Found ", BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getColor(R.color.red_900)).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
