package com.jb.dev.jhmemory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jb.dev.jhmemory.R;

public class PhoneNumberActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputEditText editTextPhoneNumber;
    TextView textViewCountryCode;
    Button buttonContinue, buttonOtherTime;
    String phoneNumber, countryCode;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User_data");
        mAuth = FirebaseAuth.getInstance();
        editTextPhoneNumber = findViewById(R.id.phone_number_edt_phone_number);
        textViewCountryCode = findViewById(R.id.phone_number_txt_country_code);
        buttonContinue = findViewById(R.id.phone_number_btn_continue);
        buttonOtherTime = findViewById(R.id.phone_number_btn_other_time);
        buttonOtherTime.setOnClickListener(this);
        buttonContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_number_btn_continue:
                countryCode = textViewCountryCode.getText().toString();
                phoneNumber = editTextPhoneNumber.getText().toString();
                if (!validPhoneNumber(phoneNumber)) {
                    editTextPhoneNumber.setError("Enter Valid Phone Number");
                    editTextPhoneNumber.setFocusable(true);
                } else {
                    phoneNumber = countryCode + phoneNumber;
                    Log.e("phonenumber", editTextPhoneNumber.getText().toString());
                    Query q = mDatabaseReference.orderByChild("email").equalTo(editTextPhoneNumber.getText().toString());
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                startActivity(new Intent(PhoneNumberActivity.this, LoginActivity.class));
                            } else {
                                Intent intent = new Intent(PhoneNumberActivity.this, PhoneVerificationActivity.class);
                                intent.putExtra("phonenumber", phoneNumber);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                break;
            case R.id.phone_number_btn_other_time:
                finish();
                break;
        }

    }

    public static boolean validPhoneNumber(String editText) throws NumberFormatException {
        if (editText.length() < 10) {
            return false;
        } else {
            String postCodePattrn = "^[7-9]\\d{9}$";
            String postCodeInput = editText.trim();
            return postCodeInput.matches(postCodePattrn);
        }
    }
}
