package com.briatka.pavol.closertostars;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ItemModel> mArrayList;
    private Context context;

    OnImageClickListener imageListener;

    public interface OnImageClickListener {
        void onImageClicked(ItemModel clickedImage);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.picture_of_day)
        ImageView picture;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public CustomRecyclerViewAdapter(ArrayList<ItemModel> arrayList, Context context1) {
        this.mArrayList = arrayList;
        //this.context = context1;
        this.imageListener = (OnImageClickListener) context1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.grid_layout_item, parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        final ItemModel currentObject = mArrayList.get(position);

        int customSize = MainActivity.getCustomSize();

        holder.picture.getLayoutParams().width = customSize;
        holder.picture.getLayoutParams().height = customSize;

        if (!currentObject.mMediaType.equals("image")) {
            Picasso.with(context)
                    .load(R.drawable.skyvector)
                    .into(holder.picture);
        } else {
            Picasso.with(context)
                    .load(currentObject.mMediaSource)
                    .into(holder.picture);
        }

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageListener.onImageClicked(currentObject);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mArrayList) return 0;
        return mArrayList.size();
    }

    public void setGalleryData(ArrayList<ItemModel> passedGalleryArray) {
        mArrayList = passedGalleryArray;
        notifyDataSetChanged();
    }
}
