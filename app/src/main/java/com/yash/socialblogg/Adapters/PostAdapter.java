package com.yash.socialblogg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yash.socialblogg.Activities.PostDetailActivity;
import com.yash.socialblogg.Activities.RegisterActivity;
import com.yash.socialblogg.Model.Post;
import com.yash.socialblogg.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(mData.get(position).getTitle());

        Glide.with(mContext).load(mData.get(position).getPicture())
                .placeholder(R.drawable.placeholder_image).into(holder.imgPost);

        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPost;
        ImageView imgPostProfile;
        TextView tvTitle;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPost = itemView.findViewById(R.id.row_post_image);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile);
            tvTitle = itemView.findViewById(R.id.row_post_title);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetail = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetail.putExtra("title", mData.get(position).getTitle());
                    postDetail.putExtra("postImage", mData.get(position).getPicture());
                    postDetail.putExtra("description", mData.get(position).getDescription());
                    postDetail.putExtra("postKey", mData.get(position).getPostKey());
                    postDetail.putExtra("userPhoto", mData.get(position).getUserPhoto());
                    //postDetail.putExtra("userName",mData.get(position).get)

                    long timeStamp = (long) mData.get(position).getTimeStamp();
                    postDetail.putExtra("postDate", timeStamp);
                    mContext.startActivity(postDetail);
                    CustomIntent.customType(mContext, "fadein-to-fadeout");


                }
            });
        }
    }
}
