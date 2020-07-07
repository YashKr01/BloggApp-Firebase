package com.yash.socialblogg.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yash.socialblogg.Model.Comment;
import com.yash.socialblogg.R;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        Glide.with(mContext).load(mData.get(position).getUimg()).into(holder.imgUser);
        holder.tvName.setText(mData.get(position).getUname());
        holder.tvContent.setText(mData.get(position).getContent());

        holder.tvDate.setText(timestampToString((Long) mData.get(position).getTimeStamp()));


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView tvName, tvContent, tvDate;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.comment_user_img);
            tvContent = itemView.findViewById(R.id.comment_content);
            tvName = itemView.findViewById(R.id.comment_username);
            tvDate = itemView.findViewById(R.id.comment_date);

        }
    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm", calendar).toString();
        return date;
    }

}
