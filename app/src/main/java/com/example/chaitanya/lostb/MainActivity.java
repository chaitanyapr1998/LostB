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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button login, signup;
    EditText edtEmail, edtPassword;
    static FirebaseAuth mAuth;
    ImageView imgLogo;
    //static String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button) findViewById(R.id.btn_login);
        signup = (Button) findViewById(R.id.btn_newuser);

        imgLogo = (ImageView) findViewById(R.id.img_logo);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_pass);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if(email.isEmpty()){
                    edtEmail.setError("Please enter email address");
                }

                if(password.isEmpty()){
                    edtPassword.setError("Please enter password");
                }


                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edtEmail.setError("Please enter valid email address");
                }

                if(password.length() < 6){
                    edtPassword.setError("Password should be more than 6 characters");
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Successful!",
                                    Toast.LENGTH_LONG).show();

                            finishAffinity();

                            Intent intent = new Intent(MainActivity.this, LostActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(MainActivity.this, "Wrong Email or Password",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Error",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            finishAffinity();
            Intent intent = new Intent(MainActivity.this, LostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
