package com.alfeye.facedemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.alfeye.facedemo.R;
import com.alfeye.facedemo.RecyclerViewOnClickListener;
import com.alfeye.facedemo.utils.FileUtils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class AllPhotoRecyclerViewAdapter extends RecyclerView.Adapter<AllPhotoRecyclerViewAdapter.ViewHolder> {

    private Context mContext;

    private List<String> imageList;

    private RecyclerViewOnClickListener listener;

    private String rootPath;

    public void setListener(RecyclerViewOnClickListener listener) {
        this.listener = listener;
    }

    public AllPhotoRecyclerViewAdapter(Context mContext, List<String> imageList) {
        this.mContext = mContext;
        this.imageList = imageList;
        rootPath=FileUtils.getSDPath();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_allphoto2,null);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String s = imageList.get(position);
        Log.d("zhangjikai", "onBindViewHolder: "+rootPath+s);
        if (s!=null){
            Glide.with(mContext).load(Uri.fromFile(new File(s))).into(holder.imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(position);
                }
            });

            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onLongClick(position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.item_imageView);
        }
    }
}
