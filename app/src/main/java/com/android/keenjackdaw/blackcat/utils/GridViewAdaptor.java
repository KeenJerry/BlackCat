package com.android.keenjackdaw.blackcat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.controller.PictureProvider;

import java.io.File;

public class GridViewAdaptor extends ArrayAdapter<Picture> {
    private int firstVisiblePicture = 0;
    private int visiblePicturesCount = 0;
    private int totalPictureCount = 0;
    private GridView gridView;

    public GridViewAdaptor(@NonNull Context context, int textViewResourceId, @NonNull Picture[] objects, GridView gridView) {
        super(context, textViewResourceId, objects);
        this.gridView = gridView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // TODO Delete after debug
        Log.i(Settings.TAG, "in getView, position is " + position);

        Picture picture = getItem(position);
        Log.i(Settings.TAG, "picture id = " + picture.getPictureId());

        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.picture, null);
        }
        else {
            view = convertView;
        }
        ImageView thumbnailView = view.findViewById(R.id.picture_thumbnail);

        Bitmap thumbnail;


        if(PictureProvider.getInstance().hasPictureInCache(picture.getThumbnail())){
            Log.i(Settings.TAG, "not has bitmap in cache");
            thumbnail = PictureProvider.getInstance().getCachedBitmap(picture.getThumbnail());
        }
        else
        {
            File pictureThumbnail = new File(picture.getThumbnail());
            thumbnail = BitmapFactory.decodeFile(pictureThumbnail.getAbsolutePath());
            PictureProvider.getInstance().addToCachedBitmap(picture.getThumbnail(), thumbnail);
        }
        thumbnailView.setImageBitmap(thumbnail);
        return view;
    }
}
