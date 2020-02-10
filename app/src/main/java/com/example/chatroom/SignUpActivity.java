package com.example.chatroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText username,email,password,gender,city;
    Button register,cancel;
    ImageView imageView;
    int imagevalue = 0;

    String downloadImageUrl,myprofileurl;
    FirebaseStorage ProductImagesRef;
    Uri ImageUri;


    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseStorage storageUpload = FirebaseStorage.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    //StorageReference filePath,mStorageReference;
    StorageReference storageRef = storage.getReference();


    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Register");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username_signup);
        gender = findViewById(R.id.gender_signup);
        city = findViewById(R.id.city_signup);
        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);
        register = findViewById(R.id.btn_register);
        imageView = findViewById(R.id.ImageView);
        cancel = findViewById(R.id.btn_cancel);

        auth = FirebaseAuth.getInstance();



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usrname = username.getText().toString();
                String gender1 = gender.getText().toString();
                String city1 = city.getText().toString();
                String email1 = email.getText().toString();
                String pwd = password.getText().toString();

                if(TextUtils.isEmpty(usrname) || TextUtils.isEmpty(email1) || TextUtils.isEmpty(pwd)  || TextUtils.isEmpty(gender1) || TextUtils.isEmpty(city1) || imagevalue == 0){
                    Toast.makeText(SignUpActivity.this,"all fields are required",Toast.LENGTH_LONG).show();
                }else if(pwd.length()<8){
                    Toast.makeText(SignUpActivity.this,"password must be greater than 8",Toast.LENGTH_LONG).show();

                }else{
                    register(usrname,email1,pwd,gender1,city1);
                }

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagepicker = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imagepicker,1);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    private void register(final String username, String email, String passsword,final String gender, final String city){

        auth.createUserWithEmailAndPassword(email,passsword)
                .addOnCompleteListener(new OnCompleteListener <AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task <AuthResult> task) {
                        System.out.println("Task"+task.getException());
                        if(task.isSuccessful()){
                            dialog = new ProgressDialog(SignUpActivity.this);
                            dialog.setMessage("Creating the Account");
                            dialog.show();

                            imageView.setDrawingCacheEnabled(true);
                            imageView.buildDrawingCache();
                            Bitmap bitmap = imageView.getDrawingCache();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] data = baos.toByteArray();

                            String path =  username+".png";
                            StorageReference firememeref = storageUpload.getReference(path);

                            StorageMetadata metadata = new StorageMetadata.Builder()
                                    .setCustomMetadata("text",username)
                                    .build();
                            final UploadTask uploadTask = firememeref.putBytes(data,metadata);

                            uploadTask.addOnSuccessListener(SignUpActivity.this, new OnSuccessListener <UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String op;
                                    //StorageReference pathReference = storageRef.child(username + ".png");
                                    Log.d("profile","hi");

                                    storageRef.child(username + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Got the download URL for 'users/me/profile.png'
                                            myprofileurl = uri.toString();
                                            Log.d("profile",myprofileurl);

                                            FirebaseUser firebaseUser = auth.getCurrentUser();
                                            assert firebaseUser != null;
                                            String userid = firebaseUser.getUid();

                                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                            HashMap<String,String> hashMap = new HashMap <>();
                                            hashMap.put("id",userid);
                                            hashMap.put("username", username);
                                            hashMap.put("gender", gender);
                                            hashMap.put("city",city);
                                            hashMap.put("imageurl", myprofileurl);


                                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener <Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task <Void> task) {
                                                    if(task.isSuccessful()){
                                                        Log.d("profile","bye");

                                                        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                        }
                                    });

                                    //downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                                    //String myprofileurl  = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                                    //op = downloadUrl.toString();

                                   /* FirebaseUser firebaseUser = auth.getCurrentUser();
                                    assert firebaseUser != null;
                                    String userid = firebaseUser.getUid();

                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                    HashMap<String,String> hashMap = new HashMap <>();
                                    hashMap.put("id",userid);
                                    hashMap.put("username", username);
                                    hashMap.put("gender", gender);
                                    hashMap.put("city",city);
                                    hashMap.put("imageurl", myprofileurl);*/

                                    /*reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener <Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task <Void> task) {
                                            if(task.isSuccessful()){
                                                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });*/


                                    dialog.dismiss();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    int errorCode = ((StorageException) exception).getErrorCode();
                                    String errorMessage = exception.getMessage();
                                    Toast.makeText(SignUpActivity.this, "Image upload Failed",
                                            Toast.LENGTH_SHORT).show();

                                }
                            });

                        }else{
                            Toast.makeText(SignUpActivity.this,"you cant register",Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(requestCode == 1){
            Uri SelectedImage  = imageReturnedIntent.getData();
            Log.d("hi3", String.valueOf(+requestCode));
            Log.i("Add","SelectedImage"+SelectedImage);
            this.imageView.setImageURI(SelectedImage);
            imagevalue = 1;
        }
    }


}
