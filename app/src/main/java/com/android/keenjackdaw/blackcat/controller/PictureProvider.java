package com.android.keenjackdaw.blackcat.controller;

import android.content.Context;
import android.database.Cursor;

import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.utils.Picture;
import com.android.keenjackdaw.blackcat.utils.PictureBucket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PictureProvider {

    private static List<PictureBucket> pictureBucketList = null;

    private static HashMap<String, PictureBucket> pictureBucketMap = null;

    private PictureProvider() { }

    private static PictureProvider instance = null;

    public static PictureProvider getInstance() {
        if(instance == null){
            pictureBucketMap = new HashMap<>();
            pictureBucketList = new ArrayList<>();
            return new PictureProvider();
        }
        return instance;
    }

    public void loadPictureData(Context context) throws BlackCatException{

        pictureBucketList.clear();
        Cursor cursor = context.getContentResolver().query(
                Settings.QUERY_URI,
                Settings.PROJECTION,
                Settings.SELECTION,
                Settings.SELECTION_ARGS,
                Settings.QUERY_ORDER
        );

        if(cursor == null){
            throw new BlackCatException();
        }
        else{
            if(cursor.moveToFirst()){
                int tempPictureId = cursor.getColumnIndex(Settings.PROJECTION[0]);
                int tempPicturePath = cursor.getColumnIndex(Settings.PROJECTION[1]);
                int tempBucketId = cursor.getColumnIndex(Settings.PROJECTION[2]);
                int tempBucketName = cursor.getColumnIndex(Settings.PROJECTION[3]);

                do{
                    String pictureId = cursor.getString(tempPictureId);
                    String picturePath = cursor.getString(tempPicturePath);
                    String bucketId = cursor.getString(tempBucketId);
                    String bucketName = cursor.getString(tempBucketName);

                    PictureBucket pictureBucket = pictureBucketMap.get(bucketId);

                    if(pictureBucket == null){
                        pictureBucket = new PictureBucket(bucketId, bucketName);
                        pictureBucket.setPictureList(new ArrayList<Picture>());
                        pictureBucketMap.put(bucketId, pictureBucket);
                    }

                    Picture picture = new Picture(pictureId, picturePath, pictureBucket);
                    pictureBucket.AddToPictureList(picture);
                    pictureBucketList.add(pictureBucket);

                }while(cursor.moveToNext());
            }
        }

        cursor.close();
    }

    public List<PictureBucket> getPictureBucketList() {
        return pictureBucketList;
    }
}
