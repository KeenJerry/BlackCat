package com.android.keenjackdaw.blackcat.fragment;

import android.content.Context;
import android.content.Intent;
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

public class DataCenterFragment extends Fragment {

    private PictureProvider pictureProvider = PictureProvider.getInstance();
    private List<PictureBucket> pictureBuckets = null;
    private BlackCatRunnable loadPictureRunnable = null;
    private CitrusFaceManager citrusFaceManager = null;
    private ExecutorService threadPool = null;
    private GridViewAdaptor gridViewAdaptor = null;
    private Context context = null;

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

        GridView gridView = v.findViewById(R.id.picture_grid_layout);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        threadPool = Executors.newCachedThreadPool();

        citrusFaceManager = CitrusFaceManager.getInstance();
        citrusFaceManager.setUpAppInfo();
        try {
            citrusFaceManager.initCitrusFaceSDK(0, 0);
        }catch (BlackCatException e){
            e.printStackTrace();
        }

        try{
            pictureProvider.loadPictureData(context);
        }catch (BlackCatException e){
            e.printStackTrace();
        }
        pictureBuckets = pictureProvider.getPictureBucketList();
        List<Picture> pictures = new ArrayList<>();

        for(PictureBucket pictureBucket: pictureBuckets){
            List<Picture> picturesList = pictureBucket.getPictureList();
            Picture[] picturesInBucket = picturesList.toArray(new Picture[picturesList.size()]);
            Collections.addAll(pictures, picturesInBucket);
        }
        Log.i(Settings.TAG, "pictures id: " + pictures.get(0).getPictureId());

        GridViewAdaptor gridViewAdaptor = new GridViewAdaptor(context, 0, pictures.toArray(new Picture[pictures.size()]), gridView);
        gridView.setAdapter(gridViewAdaptor);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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
