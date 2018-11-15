package com.android.keenjackdaw.blackcat.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.android.keenjackdaw.blackcat.BlackCatApplication;
import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.activity.CameraActivity;
import com.android.keenjackdaw.blackcat.controller.CitrusFaceManager;
import com.android.keenjackdaw.blackcat.controller.PictureProvider;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.utils.BlackCatRunnable;
import com.android.keenjackdaw.blackcat.utils.GridViewAdaptor;
import com.android.keenjackdaw.blackcat.utils.Picture;
import com.android.keenjackdaw.blackcat.utils.PictureBucket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.util.Log.i;

public class DataCenterFragment extends Fragment {

    private PictureProvider pictureProvider = PictureProvider.getInstance();
    private List<PictureBucket> pictureBuckets = null;
    private BlackCatRunnable loadPictureRunnable = null;
    private CitrusFaceManager citrusFaceManager = null;
    private ExecutorService threadPool = null;
    private GridViewAdaptor gridViewAdaptor = null;
    private String nameFilePath = null;
    private List<String> nameList = null;
    private int faceListNum = 0;
    private int faceNum = 0;
    private int idNow = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_data_center, container, false);
        Button backButton = v.findViewById(R.id.back_to_camera_activity_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });

        final GridView gridView = v.findViewById(R.id.picture_grid_layout);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        // TODO Delete below after debug
        i(Settings.TAG, "grid view init.");

        threadPool = Executors.newCachedThreadPool();

        citrusFaceManager = CitrusFaceManager.getInstance();
        citrusFaceManager.setUpAppInfo();
        try {
            citrusFaceManager.initCitrusFaceSDKWithoutBuffer();
        }catch (BlackCatException e){
            // TODO Delete after debug
            Log.i(Settings.TAG, "External storage not allowed.");
            e.printStackTrace();
        }
        citrusFaceManager.reset();
        nameFilePath = citrusFaceManager.getNameFilePath();
        nameList = citrusFaceManager.readNames(nameFilePath);


        i(Settings.TAG, "citrusFace Manager init");

        try{
            pictureProvider.setUpInfo();
            pictureProvider.loadPictureData(BlackCatApplication.getCurrentActivity().get());
        }catch (BlackCatException e){
            e.printStackTrace();
        }

        i(Settings.TAG, "pictureProvider init");
        pictureBuckets = pictureProvider.getPictureBucketList();

        i(Settings.TAG, "pictureBuckets init");
        List<Picture> pictures = new ArrayList<>();

        for(PictureBucket pictureBucket: pictureBuckets){
            List<Picture> picturesList = pictureBucket.getPictureList();
            Picture[] picturesInBucket = picturesList.toArray(new Picture[picturesList.size()]);
            Collections.addAll(pictures, picturesInBucket);
        }

        final GridViewAdaptor gridViewAdaptor = new GridViewAdaptor(BlackCatApplication.getCurrentActivity().get(), 0, pictures.toArray(new Picture[pictures.size()]), gridView);
        i(Settings.TAG, "gridView adaptor init");
        gridView.setAdapter(gridViewAdaptor);

        Button addButton = v.findViewById(R.id.data_center_button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = gridViewAdaptor.getCount();
                for(int i = 0; i < count; i++){
                    Picture picture = gridViewAdaptor.getItem(i);
                    // TODO Delete below after debug
                    i(Settings.TAG, "picture is " + picture.getPicturePath());
                    if(picture != null){
                        if(picture.isSelected()){
                            // TODO Delete below after debug
                            i(Settings.TAG, "pic " + picture.getPicturePath() + " is selected.");
                            Bitmap bitmap = BitmapFactory.decodeFile(picture.getPicturePath());
                            Bitmap scaledBitmap;

                            int h = bitmap.getHeight();
                            int w = bitmap.getWidth();

                            int max = h > w? h: w;
                            float scale;
                            if (max > 800) {
                                scale = (float)800/(float)max;

                                Matrix matrix = new Matrix();
                                matrix.postScale(scale, scale);
                                scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
                            } else {
                                scaledBitmap = bitmap;
                            }


                            // TODO Delete below after debug
                            Log.i(Settings.TAG, "bitmap width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight());
                            Log.i(Settings.TAG, "Scaled bitmap width = " + scaledBitmap.getWidth() + ", height = " + scaledBitmap.getHeight());

                            citrusFaceManager.reset();

                            citrusFaceManager.detectWithImage(scaledBitmap, 3);
                            faceListNum = citrusFaceManager.getResListNum();
                            faceNum = citrusFaceManager.getResFaceNum();

                            // TODO Delete below after debug
                            Log.i(Settings.TAG, "face number is " + faceNum);
                            Log.i(Settings.TAG, "face list number is " + faceListNum);

                            if(faceNum > 0){
                                idNow = 0;
                                citrusFaceManager.faceRecognition(idNow);
                                // TODO Delete after debug
                                Log.i(Settings.TAG, "add to db return " + citrusFaceManager.addToDB(idNow));
                                if(citrusFaceManager.addToDB(idNow) == 0){
                                    // TODO Delete after debug
                                    Log.i(Settings.TAG, "id is " + idNow);
                                    Log.i(Settings.TAG, "name list is " + nameList.toString());
                                    nameList.add("trump");
                                    try{
                                        citrusFaceManager.writeNames(citrusFaceManager.getNameFilePath(), nameList);
                                    }
                                    catch (BlackCatException e){
                                        e.printStackTrace();
                                    }
                                }else
                                {
                                    Toast.makeText(getContext(), "add To DB failed.", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else {
                                Toast.makeText(getContext(), "Add face failed. Please check the direction of the picture.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });

        return v;
    }

    public void setLoadPictureRunnable(){
        loadPictureRunnable = new BlackCatRunnable() {
            @Override
            protected void blackCatRun() {
                while(isRunning()){

                    try{
                        throw new InterruptedException();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
    }

}
