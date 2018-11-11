package com.android.keenjackdaw.blackcat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.ui.PictureLayout;

import java.io.File;

public class GridViewAdaptor extends ArrayAdapter<Picture> {
    private int firstVisiblePicture = 0;
    private int visiblePicturesCount = 0;
    private int totalPictureCount = 0;
    private GridView gridView = null;

    public GridViewAdaptor(@NonNull Context context, int textViewResourceId, @NonNull Picture[] objects, GridView gridView) {
        super(context, textViewResourceId, objects);
        this.gridView = gridView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Picture picture = getItem(position);
        PictureLayout pictureLayout;
        if(convertView == null){
            pictureLayout = new PictureLayout(getContext());
        }
        else
        {
            pictureLayout = (PictureLayout)convertView;
        }

        ImageView pictureView = new ImageView(getContext());
        RelativeLayout.LayoutParams layoutParams= new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        pictureView.setLayoutParams(layoutParams);

        ImageView iconView = new ImageView(getContext());
        iconView.setLayoutParams(layoutParams);
        iconView.setImageResource(R.drawable.ic_launcher_background);

        if(picture != null){
            File pictureThumbnail = new File(picture.getThumbnail());
            pictureView.setImageBitmap(BitmapFactory.decodeFile(pictureThumbnail.getAbsolutePath()));
            pictureLayout.setId(picture.getPictureId());
        }

        pictureLayout.setPictureView(pictureView);
        pictureLayout.setIconView(iconView);

        return pictureLayout;
    }
}
