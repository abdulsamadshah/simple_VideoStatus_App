package com.example.simplevideostatusappexamplestatus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.simplevideostatusappexamplestatus.Models.Users;
import com.example.simplevideostatusappexamplestatus.Models.videoModels;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoUploadActivity extends AppCompatActivity {
    EditText videotitle;
    FloatingActionButton insertdata;
    private Uri videouri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    CircleImageView profile_image;
    VideoView profile_video;
    private StorageTask mUploadTask;
    CheckBox lovechekbox,romanticecheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);
        getSupportActionBar().setTitle(" Video Upload ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressbar();

        profile_image = findViewById(R.id.profile_image);
        profile_video=findViewById(R.id.profile_video);
        videotitle = findViewById(R.id.videotitle);
        insertdata = findViewById(R.id.insertdata);
        lovechekbox=findViewById(R.id.lovechekbox);
        romanticecheckbox=findViewById(R.id.romanticecheckbox);

        Users users=new Users();





        databaseReference= FirebaseDatabase.getInstance().getReference("Videodata");
        storageReference= FirebaseStorage.getInstance().getReference("Videodata");

        insertdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimage();
                Intent intent =new Intent(VideoUploadActivity.this,MainActivity.class);
                Toast.makeText(VideoUploadActivity.this, "Video Uploaded Successfully", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                if (lovechekbox.isChecked()){
                    uploadimage();
                    Intent lovecheckintent =new Intent(VideoUploadActivity.this,MainActivity.class);
                    startActivity(lovecheckintent);
                }else if (romanticecheckbox.isChecked()){
                    uploadimage();
                    Intent romanticeintent =new Intent(VideoUploadActivity.this,MainActivity.class);
                    startActivity(romanticeintent);
                }

            }
        });



        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");

                startActivityForResult(intent, 7);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            videouri = data.getData();
           profile_video.setVideoURI(videouri);
           profile_video.start();
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadimage(){
        if (videouri != null) {
            final StorageReference filerefence =storageReference.child(System.currentTimeMillis()+"."+getFileExtension(videouri));

            mUploadTask=filerefence.putFile(videouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filerefence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            videoModels models=new videoModels(uri.toString(),videotitle.getText().toString().trim());
                            String uploadId =databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(models);
                            Toast.makeText(VideoUploadActivity.this, "uploaded sussesfully", Toast.LENGTH_SHORT).show();
                            videotitle.setText("");

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void progressbar() {
        //progressbar
        final ProgressBar uploadprogressbar = (ProgressBar) findViewById(R.id.uploadprogressbar);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                uploadprogressbar.setVisibility(View.INVISIBLE);
            }
        }, 3000);
        uploadprogressbar.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}