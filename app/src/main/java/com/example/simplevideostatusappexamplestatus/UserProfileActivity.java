package com.example.simplevideostatusappexamplestatus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.simplevideostatusappexamplestatus.Adapters.VideoAdapters;
import com.example.simplevideostatusappexamplestatus.Models.Users;
import com.example.simplevideostatusappexamplestatus.Models.videoModels;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    CircleImageView setting_image;
    EditText setting_name, setting_email;
    FloatingActionButton setting_save;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    Uri selectedImageuri;
    RecyclerView myrecyclerview;
    VideoAdapters adapters;
    ArrayList<videoModels> modelsArrayList;
    private DatabaseReference mDatabaseRef;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        myrecyclerview=findViewById(R.id.myrecyclerview);

        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();


        myrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        myrecyclerview.setHasFixedSize(true);

        modelsArrayList=new ArrayList<>();


        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Videodata");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    videoModels modes =dataSnapshot.getValue(videoModels.class);
                    modelsArrayList.add(modes);

                }
                adapters=new VideoAdapters(UserProfileActivity.this,modelsArrayList);
                myrecyclerview.setAdapter(adapters);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //myrecycler retreive



        //id binding
        setting_image = findViewById(R.id.setting_image);
        setting_name = findViewById(R.id.setting_name);
        setting_email = findViewById(R.id.setting_email);
        setting_save = findViewById(R.id.setting_save);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();


        //data retreive
        DatabaseReference reference = database.getReference().child("User").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("imageUri").getValue().toString();

                setting_email.setText(email);
                setting_name.setText(name);
                Picasso.get().load(image).into(setting_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //close

        // data insert

        setting_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = setting_name.getText().toString();
                String email = setting_email.getText().toString();

                if (selectedImageuri != null) {

                    storageReference.putFile(selectedImageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri = uri.toString();
                                    Users users = new Users(auth.getUid(),name,email,finalImageUri);

                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(UserProfileActivity.this, "user created sucesfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(UserProfileActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri = uri.toString();
                            Users users = new Users(auth.getUid(),name,email,finalImageUri);

                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UserProfileActivity.this, "user created sucesfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(UserProfileActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }

            }
        });


        setting_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 20);
            }
        });

        getSupportActionBar().setTitle(" MyProfile ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            selectedImageuri = data.getData();
            setting_image.setImageURI(selectedImageuri);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}