package com.alfeye.facedemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alfeye.facedemo.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class AllPhotoRecyclerViewAdapter extends RecyclerView.Adapter<AllPhotoRecyclerViewAdapter.ViewHolder> {

    private Context mContext;

    private List<String> imageList;

    public AllPhotoRecyclerViewAdapter(Context mContext, List<String> imageList) {
        this.mContext = mContext;
        this.imageList = imageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_allphoto,null);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String s = imageList.get(position);
        Log.d("zhangjikai", "onBindViewHolder: "+s);
        if (s!=null){
//            Glide.with(mContext).load(s).into(holder.imageView);

            Bitmap bitmap = BitmapFactory.decodeFile(s);
            holder.imageView.setImageBitmap(bitmap);
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