package com.example.chatroom;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Password extends AppCompatActivity {

    EditText email;
    Button btn,btn2;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot__password);

        email = findViewById(R.id.forgot_email);
        btn = findViewById(R.id.btn_forgot);
        btn2 = findViewById(R.id.btn_forgotcancel);

        mAuth = FirebaseAuth.getInstance();

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Forgot_Password.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emil = email.getText().toString();

                if(TextUtils.isEmpty(emil)){
                    Toast.makeText(Forgot_Password.this,"enter email",Toast.LENGTH_LONG).show();
                }else{
                    mAuth.sendPasswordResetEmail(emil).addOnCompleteListener(new OnCompleteListener <Void>() {
                        @Override
                        public void onComplete(@NonNull Task <Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Forgot_Password.this," password reset email sent",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Forgot_Password.this,MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(Forgot_Password.this,"error",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
