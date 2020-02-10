package com.example.chatroom;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText email,password;
    Button login,signup,forgotpassword;

    FirebaseAuth auth;

    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            Intent intent = new Intent(MainActivity.this,Main2Activity.class);
            startActivity(intent);
            finish();
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Login");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_Main);
        password = findViewById(R.id.password_Main);
        login = findViewById(R.id.btn_SignIn);
        signup = findViewById(R.id.btn_SignUp);
        forgotpassword = findViewById(R.id.btn_forgotpassword);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,Forgot_Password.class);
                startActivity(intent);
                finish();

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email2 = email.getText().toString();
                String pwd = password.getText().toString();

                if(TextUtils.isEmpty(email2) || TextUtils.isEmpty(pwd)){
                    Toast.makeText(MainActivity.this,"fields must not be empty",Toast.LENGTH_LONG).show();
                }else{
                    auth.signInWithEmailAndPassword(email2,pwd)
                            .addOnCompleteListener(new OnCompleteListener <AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task <AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                                        startActivity(intent);
                                        finish();

                                    }else{
                                        Toast.makeText(MainActivity.this,"authentication failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
