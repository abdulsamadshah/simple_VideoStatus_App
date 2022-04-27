package com.example.simplevideostatusappexamplestatus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.car.ui.AlertDialogBuilder;
import com.example.simplevideostatusappexamplestatus.Adapters.CategoryAdapter;
import com.example.simplevideostatusappexamplestatus.Adapters.VideoAdapters;
import com.example.simplevideostatusappexamplestatus.Models.CategoryModels;
import com.example.simplevideostatusappexamplestatus.Models.Users;
import com.example.simplevideostatusappexamplestatus.Models.videoModels;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton upload;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Users users;
    RecyclerView recyclerView;
    VideoAdapters adapters;
    ArrayList<videoModels> modelsArrayList;
    private DatabaseReference mDatabaseRef;
    FirebaseUser firebaseUser;
    RecyclerView categoryrecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upload = findViewById(R.id.upload);
        recyclerView = findViewById(R.id.recyclerview);
        categoryrecyclerview=findViewById(R.id.categoryrecyclerview);

        progressbar();
        mainrecyclerfetchdata();
        LoadLocale();
        networkconnection();


        ArrayList<CategoryModels>categoryModels=new ArrayList<>();
        categoryrecyclerview.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
       String Love="https://i.pinimg.com/564x/d4/6e/6a/d46e6a8187ebbce58857b264a9af3b5c.jpg";
       String Romantic="https://www.bonobology.com/wp-content/uploads/2018/04/couple-expressing-love.jpg";
       String sad="https://www.cohenmedical.com/wp-content/uploads/2020/01/sad-seasonal-depression-1.jpg";
       String Animated="https://static01.nyt.com/images/2021/07/20/arts/20burst-luca/20burst-luca-blog480.jpg";
       String Goodmorning="https://akm-img-a-in.tosshub.com/indiatoday/images/story/201801/goodmorning_message.jpeg?3qVf91YyRe_jhFadrOy.qpkoPZnbaiVz&size=770:433";
       String breakup="https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/gettyimages-1202062704.jpg?resize=640:*";
       String attitude="https://i.pinimg.com/originals/04/f9/62/04f9628710f1f7e9e0d3acf30b1ebceb.jpg";
       String freindship="https://nextlevelgents.com/wp-content/uploads/2019/10/Signs-of-a-Strong-Friendship-1.jpg";

       categoryModels.add(new CategoryModels(Love));
       categoryModels.add(new CategoryModels(Romantic));
       categoryModels.add(new CategoryModels(sad));
       categoryModels.add(new CategoryModels(Animated));
       categoryModels.add(new CategoryModels(Goodmorning));
       categoryModels.add(new CategoryModels(breakup));
       categoryModels.add(new CategoryModels(attitude));
       categoryModels.add(new CategoryModels(freindship));
       CategoryAdapter categoryAdapter =new CategoryAdapter(getApplicationContext(),categoryModels);
        categoryrecyclerview.setAdapter(categoryAdapter);


        //images upload
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUser != null) {
                    Intent intent = new Intent(MainActivity.this, VideoUploadActivity.class);
                    startActivity(intent);

                } else {
                    showBottomSheetDialog();
                }
            }
        });

        // 2nd step:   Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

    }

    private void networkconnection() {


        ConnectivityManager manager =(ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activenetwork =manager.getActiveNetworkInfo();

        if (null!=activenetwork){
            if (activenetwork.getType() == ConnectivityManager.TYPE_WIFI) {
               // Toast.makeText(this, "Wifi Enable", Toast.LENGTH_SHORT).show();
            }
            else if (activenetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
              //  Toast.makeText(this, "Data Network Enable", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
          //  message.setText("No Internet Connection");

        }
    }

    private void mainrecyclerfetchdata() {

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        modelsArrayList = new ArrayList<>();


        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Videodata");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    videoModels modes = dataSnapshot.getValue(videoModels.class);
                    modelsArrayList.add(modes);

                }
                adapters = new VideoAdapters(MainActivity.this, modelsArrayList);
                recyclerView.setAdapter(adapters);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void progressbar() {
        //progressbar
        final ProgressBar simpleProgressBar = (ProgressBar) findViewById(R.id.progressBar3);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                simpleProgressBar.setVisibility(View.INVISIBLE);
            }
        }, 5000);
        simpleProgressBar.setVisibility(View.VISIBLE);
    }


    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.login_dialog_layout);

        Button google = bottomSheetDialog.findViewById(R.id.google);
        Button later = bottomSheetDialog.findViewById(R.id.later);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });


        //google signup
        //xml file button onclick method
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        bottomSheetDialog.show();

    }


    int RC_SIGN_IN = 65;

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }


    //5th step:
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Users users = new Users();
                            users.setUid(user.getUid());
                            users.setName(user.getDisplayName());
                            users.setEmail(user.getEmail());
                            users.setImageUri(user.getPhotoUrl().toString());
                            database.getReference().child("User").child(user.getUid()).setValue(users);

                            Toast.makeText(MainActivity.this, "User Created Succesfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, VideoUploadActivity.class);
                            intent.putExtra("sendimage", users.getImageUri());
                            startActivity(intent);


                            //  updateUI(user);
                        } else {
                            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                            // If sign in fails, display a message to the user.
                            //  Log.w("TAG", "signInWithCredential:failure", task.getException());
                            // updateUI(null);
                        }
                    }
                });
    }

    //All menu itme
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchmenu, menu);
        inflater.inflate(R.menu.share, menu);
        inflater.inflate(R.menu.language, menu);

        MenuItem item = menu.findItem(R.id.searchmenus);

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapters.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plan");

                String sharesub = "Status Video Share";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
                startActivity(Intent.createChooser(shareIntent, "share using"));
                break;

            case R.id.mylanguage:
                Toast.makeText(this, "created users", Toast.LENGTH_SHORT).show();
                ChanLanguage();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void ChanLanguage() {
        final String language[] = {"Englist", "Hindi", "Marathi"};
        AlertDialogBuilder mbuilder = new AlertDialogBuilder(getApplicationContext());
        mbuilder.setTitle("Choose Language");
        mbuilder.setSingleChoiceItems(language, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {
                    setLocale("");
                    recreate();
//                    recreate();
                } else if (i == 1) {
                    setLocale("hi");
                    recreate();
//                    recreate();
                } else if (i == 2) {
                    setLocale("mr");
                    recreate();
//                    recreate();
                }
            }
        });
        mbuilder.create();
        mbuilder.show();


    }


    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getApplication().getResources().updateConfiguration(configuration, getApplicationContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Setting", Context.MODE_PRIVATE).edit();
        editor.putString("app_lang", language);
        editor.apply();
    }


    private void LoadLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        String languagess = sharedPreferences.getString("app_lang", "");
        setLocale(languagess);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//       finish();
//    }
}