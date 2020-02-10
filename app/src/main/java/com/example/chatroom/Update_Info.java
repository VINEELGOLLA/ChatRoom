package com.example.chatroom;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class Update_Info extends AppCompatActivity {

    EditText username,gender,city;
    Button update;
    ImageView imageView;

    ProgressDialog dialog;

    String myprofileurl;


    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    FirebaseStorage storageUpload = FirebaseStorage.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    Intent intent;
    int imagevalue = 0;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__info);

        Update_Info.this.setTitle("Edit Information");







        username = findViewById(R.id.username_update);
        gender = findViewById(R.id.gender_update);
        city = findViewById(R.id.city_update);
        imageView = findViewById(R.id.UpdateImageView);
        update = findViewById(R.id.Update);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagepicker = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imagepicker,1);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                username.setText(users.getUsername());
                gender.setText(users.getGender());
                city.setText((users.getCity()));
                Log.d("url",users.getImageurl());
                Picasso.get().load(users.getImageurl()).placeholder(R.mipmap.loading).into(imageView);
                //imageView.setImageResource(R.mipmap.ic_launcher);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(!userid.equals(firebaseUser.getUid())){
            update.setEnabled(false);
            username.setFocusableInTouchMode(false);
            gender.setFocusableInTouchMode(false);
            city.setFocusableInTouchMode(false);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(Update_Info.this);
                dialog.setMessage("Uploading into Firebase");
                dialog.show();

                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                Bitmap bitmap = imageView.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                String path =  username.getText().toString()+".png";
                StorageReference firememeref = storageUpload.getReference(path);

                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setCustomMetadata("text",username.getText().toString())
                        .build();
                UploadTask uploadTask = firememeref.putBytes(data,metadata);

                //Task <Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                uploadTask.addOnSuccessListener(Update_Info.this, new OnSuccessListener <UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.child(username + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                                myprofileurl = uri.toString();
                                Log.d("profile",myprofileurl);


                                //reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                HashMap<String,Object> hashMap = new HashMap <>();
                                hashMap.put("id",userid);
                                hashMap.put("username", username.getText().toString());
                                hashMap.put("gender", gender.getText().toString());
                                hashMap.put("city",city.getText().toString());
                                hashMap.put("imageurl", myprofileurl);


                                databaseReference.updateChildren(hashMap);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });

                        dialog.dismiss();
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        int errorCode = ((StorageException) exception).getErrorCode();
                        String errorMessage = exception.getMessage();
                        Toast.makeText(Update_Info.this, "Image upload Failed",
                                Toast.LENGTH_SHORT).show();

                    }
                });




                //databaseReference = FirebaseDatabase.getInstance().getReference("Users").updateChildren(hashMap,userid);

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
