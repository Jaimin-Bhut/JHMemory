package com.jb.dev.jhmemory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.jb.dev.jhmemory.R;
import com.jb.dev.jhmemory.helper.GenericTextWatcher;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Phone Verification";
    String phoneNumber, pass;
    EditText e1, e2, e3, e4, e5, e6;
    TextView textViewCountDown;
    TextInputEditText editTextPhoneNumber;
    private CountDownTimer countDownTimer;
    FirebaseAuth mAuth;
    String verificationId, code;
    Button buttonVerify, buttonResend;
    TextInputLayout textInputLayout;
    TextInputEditText editTextPassword;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        mAuth = FirebaseAuth.getInstance();
        view = findViewById(android.R.id.content);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phonenumber");
        Log.e(TAG, phoneNumber);
        textViewCountDown = findViewById(R.id.phone_verification_txt_coundown);
        editTextPhoneNumber = findViewById(R.id.phone_verification_edt_phone_number);
        editTextPhoneNumber.setText(phoneNumber.substring(3));
        buttonResend = findViewById(R.id.phone_verification_btn_resend);
        buttonVerify = findViewById(R.id.phone_verification_btn_verify);
        e1 = findViewById(R.id.e1);
        e2 = findViewById(R.id.e2);
        e3 = findViewById(R.id.e3);
        e4 = findViewById(R.id.e4);
        e5 = findViewById(R.id.e5);
        e6 = findViewById(R.id.e6);

        e1.addTextChangedListener(new GenericTextWatcher(e2, e1));
        e2.addTextChangedListener(new GenericTextWatcher(e3, e1));
        e3.addTextChangedListener(new GenericTextWatcher(e4, e2));
        e4.addTextChangedListener(new GenericTextWatcher(e5, e3));
        e5.addTextChangedListener(new GenericTextWatcher(e6, e4));
        e6.addTextChangedListener(new GenericTextWatcher(e6, e5));

        buttonVerify.setOnClickListener(this);
        buttonResend.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    verifyCode(verificationId, code);
                    showCustomDialog(phoneAuthCredential);
                }
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                mResendToken = forceResendingToken;
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("Error", e.getMessage());
            }
        };
        sendVerificationCode();
        countDownTimer();
    }

    public void showCustomDialog(final PhoneAuthCredential credential) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_user_password);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView textViewUserEmail = dialog.findViewById(R.id.dialog_txt_user_email);
        textViewUserEmail.setText(phoneNumber);
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
                    signInWithPhoneAuthCredential(credential);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            if (task.isSuccessful()) {
                                UserRegister userRegister = new UserRegister(
                                        editTextPhoneNumber.getText().toString(),
                                        pass
                                );
                                FirebaseDatabase.getInstance().getReference("User_data")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(userRegister).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(PhoneVerificationActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(PhoneVerificationActivity.this, LoginActivity.class));
                                        } else {
                                            Log.e("ERROR:", task.getException().getMessage());
                                        }
                                    }
                                });
                            } else {
                                // Sign in failed, display a message and update the UI
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // The verification code entered was invalid
                                }
                            }
                        }
                    }
                });
    }

    private void countDownTimer() {
        countDownTimer = new CountDownTimer(1000 * 60, 1000) {
            @Override
            public void onTick(long l) {
                String text = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(l) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(l) % 60);
                textViewCountDown.setText(text);
            }

            @Override
            public void onFinish() {
                textViewCountDown.setText("00:00");
            }
        };
        countDownTimer.start();
    }

    void sendVerificationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
    }

    void verifyCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        showCustomDialog(credential);
    }

    void resendVerificationCode(String phoneNumber,
                                PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_verification_btn_resend:
                resendVerificationCode(phoneNumber, mResendToken);
                break;
            case R.id.phone_verification_btn_verify:
                code = e1.getText().toString() + e2.getText().toString() + e3.getText().toString() + e4.getText().toString() + e5.getText().toString() + e6.getText().toString();
                if (code.length() < 6) {
                    Snackbar.make(view, "Please Insert Valid Code", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    Log.e("Code", code);
                    verifyCode(verificationId, code);
                }
                break;
        }
    }
}


