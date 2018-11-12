package com.android.keenjackdaw.blackcat.controller;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.keenjackdaw.blackcat.BlackCatApplication;
import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.activity.CameraActivity;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.utils.Picture;
import com.android.keenjackdaw.blackcat.utils.PictureBucket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PictureProvider{

    private static List<PictureBucket> pictureBucketList = null;
    private static HashMap<String, PictureBucket> pictureBucketMap = null;
    private static LruCache<String, Bitmap> pictureCache = null;
    private Activity activity = null;
    private Context context = null;

    private PictureProvider() { }

    private static PictureProvider instance = null;

    public static PictureProvider getInstance() {
        if(instance == null){
            pictureBucketMap = new HashMap<>();
            pictureBucketList = new ArrayList<>();

            int maxMemory = (int) Runtime.getRuntime().maxMemory() / 4;
            pictureCache = new LruCache<String, Bitmap>(maxMemory){
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }
            };

            return new PictureProvider();
        }
        return instance;
    }

    public void setUpInfo(){
        activity = BlackCatApplication.getCurrentActivity().get();
        context = activity.getApplicationContext();
    }

    public void loadPictureData(Context context) throws BlackCatException{

        if(pictureBucketMap.size() == 0){
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
            else {
                if (cursor.moveToFirst()) {

                    int tempPictureId = cursor.getColumnIndex(Settings.PROJECTION[0]);
                    int tempPicturePath = cursor.getColumnIndex(Settings.PROJECTION[1]);
                    int tempBucketId = cursor.getColumnIndex(Settings.PROJECTION[2]);
                    int tempBucketName = cursor.getColumnIndex(Settings.PROJECTION[3]);
                    int tempThumbnail = cursor.getColumnIndex(Settings.PROJECTION[4]);

                    do {

                        String pictureId = cursor.getString(tempPictureId);
                        String picturePath = cursor.getString(tempPicturePath);
                        String bucketId = cursor.getString(tempBucketId);
                        String bucketName = cursor.getString(tempBucketName);
                        String thumbnail = cursor.getString(tempThumbnail);

                        PictureBucket pictureBucket = pictureBucketMap.get(bucketId);
                        Log.i(Settings.TAG, "Bucket name is " + bucketName);

                        if (pictureBucket == null) {
                            pictureBucket = new PictureBucket(bucketId, bucketName);
                            pictureBucket.setPictureList(new ArrayList<Picture>());
                            pictureBucketMap.put(bucketId, pictureBucket);
                            pictureBucketList.add(pictureBucket);
                        }

                        Picture picture = new Picture(pictureId, picturePath, pictureBucket, thumbnail);
                        pictureBucket.AddToPictureList(picture);

                    } while (cursor.moveToNext());

                }
            }
            cursor.close();
        }
    }

    public List<PictureBucket> getPictureBucketList() {
        return pictureBucketList;
    }

    public boolean hasPictureInCache(String thumbnail){
        Bitmap bitmap = pictureCache.get(thumbnail);
        return bitmap != null;
    }

    public Bitmap getCachedBitmap(String thumbnail){
        return pictureCache.get(thumbnail);
    }

    public void addToCachedBitmap(String thumbnail, Bitmap bitmap){
        pictureCache.put(thumbnail, bitmap);
    }

    public void clear(){
        pictureBucketMap.clear();
    }
}
