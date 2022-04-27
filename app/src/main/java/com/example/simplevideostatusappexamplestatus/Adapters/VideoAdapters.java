package com.example.simplevideostatusappexamplestatus.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.MediaController;
import android.widget.Toast;
//like button library
import com.example.simplevideostatusappexamplestatus.UserProfileActivity;
import com.google.firebase.auth.FirebaseUser;
import com.like.LikeButton;
import com.like.OnLikeListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.simplevideostatusappexamplestatus.MainActivity;
import com.example.simplevideostatusappexamplestatus.Models.Users;
import com.example.simplevideostatusappexamplestatus.Models.videoModels;
import com.example.simplevideostatusappexamplestatus.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoAdapters extends RecyclerView.Adapter<VideoAdapters.Viewholder> implements Filterable {

    Context context;
    ArrayList<videoModels> arrayList;
    ArrayList<videoModels> backup;
    Users users = new Users();
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
//    ProgressDialog progressDialog;


    public VideoAdapters(Context context, ArrayList<videoModels> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        backup = new ArrayList<>(arrayList);
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);

        return new Viewholder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        videoModels mode = arrayList.get(position);
        holder.video_title.setText(mode.getVideotitle());

//        progressDialog=new ProgressDialog(context);
//        progressDialog.setCancelable(false);
//        progressDialog.show();


        String videouri;
        videouri = mode.getVideo();
        holder.video_view.setVideoURI(Uri.parse(videouri));

//        FirebaseUser firebaseUser=auth.getCurrentUser();
//        String userid=firebaseUser.getUid();

        //google userprofiel read code
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


       if (firebaseUser !=null) {
           DatabaseReference reference = database.getReference().child("User");
           reference.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   String image = snapshot.child("imageUri").getValue(String.class);
                   Picasso.get().load(image).
                           placeholder(R.drawable.avatar).
                           into(holder.imgprofile);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                   Toast.makeText(context, "errornot", Toast.LENGTH_SHORT).show();
               }
           });
       }
//
        else if (auth.getCurrentUser() !=null ){
           DatabaseReference reference = database.getReference().child("User").child(auth.getUid());
           reference.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   String image = snapshot.child("imageUri").getValue(String.class);
                   Picasso.get().load(image).
                           placeholder(R.drawable.avatar).
                           into(holder.imgprofile);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                   Toast.makeText(context, "errornot", Toast.LENGTH_SHORT).show();
               }
           });

        }else {
           Toast.makeText(context, "errrors", Toast.LENGTH_SHORT).show();
       }






        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaController mediaController = new MediaController(context);
                mediaController.setAnchorView(holder.video_view);
                holder.video_view.setMediaController(mediaController);
                holder.video_view.start();



            }
        });


        //like Button Heart OnLikeListener
        holder.likeButtonHeart.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                //sowing simple Toast when liked
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                //sowing simple Toast when unLiked

            }
        });

        holder.imgwhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plan");
                shareIntent.setPackage("com.whatsapp");

                String videotitle = holder.video_title.getText().toString();
                String sharebody = videotitle;
                String sharesub = "Status Video Share";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
                shareIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
                context.startActivity(Intent.createChooser(shareIntent, "share using"));
            }
        });

        holder.imgshareview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plan");

                String videotitle = holder.video_title.getText().toString();
                String sharebody = videotitle;
                String sharesub = "Status Video Share";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
                shareIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
                context.startActivity(Intent.createChooser(shareIntent, "share using"));

            }
        });

        holder.imgprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        // background thread
        protected FilterResults performFiltering(CharSequence keyword) {
            ArrayList<videoModels> filtereddata = new ArrayList<>();

            if (keyword.toString().isEmpty())
                filtereddata.addAll(backup);

            else {
                for (videoModels obj : backup) {
                    if (obj.getVideotitle().toString().toLowerCase().contains(keyword.toString().toLowerCase()))
                        filtereddata.add(obj);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filtereddata;
            return results;
        }

        @Override  // main UI thread
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((ArrayList<videoModels>) results.values);
            notifyDataSetChanged();
        }
    };


    public class Viewholder extends RecyclerView.ViewHolder{
        TextView video_title;
        VideoView video_view;
        public CircleImageView imgprofile;
        private LikeButton likeButtonHeart;
        ImageView imgwhatsapp, imgshareview;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            video_title = itemView.findViewById(R.id.video_title);
            video_view = itemView.findViewById(R.id.video_view);
            imgprofile = itemView.findViewById(R.id.imgprofile);
            likeButtonHeart = itemView.findViewById(R.id.likeButtonHeart);
            imgwhatsapp = itemView.findViewById(R.id.imgwhatsapp);
            imgshareview = itemView.findViewById(R.id.imgshareview);



        }


    }

}
