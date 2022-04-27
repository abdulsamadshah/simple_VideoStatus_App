package com.example.simplevideostatusappexamplestatus.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplevideostatusappexamplestatus.Models.CategoryModels;
import com.example.simplevideostatusappexamplestatus.R;
import com.example.simplevideostatusappexamplestatus.loveActivity;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.headerViewholder> {

    Context context;
    ArrayList<CategoryModels>categoryModels;

    public CategoryAdapter(Context context, ArrayList<CategoryModels> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public headerViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.category_layout,parent,false);
        return new headerViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull headerViewholder holder, int position) {
        CategoryModels category=categoryModels.get(position);
        String url;
        url=category.getImagestatus();
        Picasso.get().load(url)
                .fit()
                .centerCrop()
                .into(holder.categorystatus);



    }



    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class  headerViewholder extends RecyclerView.ViewHolder{
        ImageView categorystatus;
        public headerViewholder(@NonNull View itemView) {
            super(itemView);

            categorystatus=itemView.findViewById(R.id.headerstatus);


//            categorystatus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (getLayoutPosition()==0){
//                        Toast.makeText(context, "is cliked", Toast.LENGTH_SHORT).show();
//                        Intent intent =new Intent(context,loveActivity.class);
//                        context.startActivity(intent);
//                    }
//                }
//            });
        }
    }
}
