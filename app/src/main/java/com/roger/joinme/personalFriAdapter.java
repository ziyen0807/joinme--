package com.roger.joinme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class personalFriAdapter extends RecyclerView.Adapter<personalFriAdapter.ViewHolder>  {
    private Context context;
    private List<personal> personalFriList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserID,currentUserName;

    public personalFriAdapter(Context context, List<personal> personalFriList){
        this.context = context;
        this.personalFriList = personalFriList;
    }

    public personalFriAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new personalFriAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull personalFriAdapter.ViewHolder holder, int position) {
        personal personal = personalFriList.get(position);
        holder.textName.setText(personal.getName());
        holder.circleImageViewid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = personal.getID();
                Intent myIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("visit_user_id", userID);
                myIntent.putExtras(bundle);
                myIntent.setClass(holder.circleImageViewid.getContext(), personalpage.class);
                context.startActivity(myIntent);
            }
        });
        Glide.with(holder.itemView.getContext())
                .load(personal.getImage())
                .circleCrop()
                .into(holder.circleImageViewid);
    }

    @Override
    public int getItemCount() {
        return personalFriList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView circleImageViewid;
        TextView textName;
        ViewHolder(View itemView) {
            super(itemView);
            circleImageViewid= (ImageView) itemView.findViewById(R.id.friendImg);
            textName = (TextView) itemView.findViewById(R.id.friendName);
        }
    }
}
