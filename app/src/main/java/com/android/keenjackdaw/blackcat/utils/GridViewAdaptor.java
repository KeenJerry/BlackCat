package com.android.keenjackdaw.blackcat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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

        final Picture picture = getItem(position);
        Log.i(Settings.TAG, "picture id = " + picture.getPictureId());

        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.picture, null);
        }
        else {
            view = convertView;
        }
        ImageView thumbnailView = view.findViewById(R.id.picture_thumbnail);
        final ImageView selectIconView = view.findViewById(R.id.select_icon);
        final EditText editText = view.findViewById(R.id.picture_name);
        editText.removeTextChangedListener(editText.getTag() == null? null: (TextWatcher) editText.getTag());

        Bitmap thumbnail;

        if(PictureProvider.getInstance().hasPictureInCache(picture.getThumbnail())){
            // TODO Delete after debug
            Log.i(Settings.TAG, "has picture in cache.");
            // Log.i(Settings.TAG, "not has bitmap in cache");
            thumbnail = PictureProvider.getInstance().getCachedBitmap(picture.getThumbnail());
        }
        else
        {
            Log.i(Settings.TAG, "not has picture in cache.");
            Log.i(Settings.TAG, "picture thumbnail is " + picture.getThumbnail());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
            BitmapFactory.decodeFile(picture.getThumbnail(), options);
            int height = options.outHeight;
            int width= options.outWidth;
            int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
            int minLen = Math.min(height, width); // 原图的最小边长
            if(minLen > 250) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
                float ratio = (float)minLen / 250; // 计算像素压缩比例
                inSampleSize = (int)ratio;
            }
            options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
            options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
            thumbnail = BitmapFactory.decodeFile(picture.getThumbnail(), options); // 解码文件

            Log.i(Settings.TAG, "" + thumbnail.getByteCount());
            PictureProvider.getInstance().addToCachedBitmap(picture.getThumbnail(), thumbnail);

        }
        if(thumbnail != null){
            thumbnailView.setImageBitmap(thumbnail);
            editText.setText(picture.getPictureName());
        }
        // thumbnail.recycle();

        selectIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picture.isSelected()){
                    picture.setSelected(false);
                    selectIconView.setImageResource(R.drawable.icon_deactivated);
                }
                else
                {
                    picture.setSelected(true);
                    selectIconView.setImageResource(R.drawable.icon_activated);
                }
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // picture.setPictureName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                picture.setPictureName(s.toString());
            }
        };
        editText.addTextChangedListener(textWatcher);
        editText.setTag(textWatcher);


        return view;
    }
}
