package com.example.chaitanya.lostb;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnacc, btnSignup;
    private FirebaseAuth mAuth;
    DatabaseReference reference;
    ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtEmail = (EditText)findViewById(R.id.edt_email);
        edtPassword = (EditText)findViewById(R.id.edt_pass);
        btnacc = (Button)findViewById(R.id.btn_acc);
        btnSignup = (Button)findViewById(R.id.btn_login);
        imgLogo = (ImageView) findViewById(R.id.img_logo);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        btnacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if(email.isEmpty()){
                    edtEmail.setError("Please enter email address");
                    return;
                }

                if(password.isEmpty()){
                    edtPassword.setError("Please enter password");
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edtEmail.setError("Please enter valid email address");
                    return;
                }

                if(password.length() < 6){
                    edtPassword.setError("Password should be more than 6 characters");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "Registered!",
                                    Toast.LENGTH_LONG).show();
                            final String uid = task.getResult().getUser().getUid();
                            final String email = task.getResult().getUser().getEmail();
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    DatabaseReference childReference8 = reference.child("Email");
                                    childReference8.setValue(email);
                                    DatabaseReference childReference9 = reference.child("UserId");
                                    childReference9.setValue(uid);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(SignUpActivity.this, "User already registered!",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Error, Please try again later",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

            }
        });
    }

}
