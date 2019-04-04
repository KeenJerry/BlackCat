package com.android.keenjackdaw.blackcat.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

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

import com.rokid.facelib.api.ImageFaceCallback;
import com.rokid.facelib.db.UserInfo;
import com.rokid.facelib.face.FaceDbHelper;
import com.rokid.facelib.input.BitmapInput;
import com.rokid.facelib.model.FaceDO;
import com.rokid.facelib.model.FaceModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.util.Log.i;

public class DataCenterFragment extends Fragment {

    private PictureProvider pictureProvider = null;
    private List<PictureBucket> pictureBuckets = null;
    private BlackCatRunnable loadPictureRunnable = null;
    private CitrusFaceManager citrusFaceManager = null;

    private FaceDbHelper dbCreator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_data_center, container, false);
        pictureProvider = PictureProvider.getInstance();

        Button backButton = v.findViewById(R.id.back_to_camera_activity_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                pictureProvider.destory();
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

        citrusFaceManager = CitrusFaceManager.getInstance();
        citrusFaceManager.setUpAppInfo(getActivity());

        dbCreator = new FaceDbHelper(getContext());
        dbCreator.clearDb();
        dbCreator.configDb("user.db");

        try{
            pictureProvider.initPictureProvider();
            pictureProvider.setUpInfo(getActivity());
            pictureProvider.loadPictureData(getActivity());
        }catch (BlackCatException e){
            e.printStackTrace();
        }

        i(Settings.TAG, "pictureProvider init complete.");
        pictureBuckets = pictureProvider.getPictureBucketList();

        i(Settings.TAG, "pictureBuckets init");
        List<Picture> pictures = new ArrayList<>();

        for(PictureBucket pictureBucket: pictureBuckets){
            List<Picture> picturesList = pictureBucket.getPictureList();
            Picture[] picturesInBucket = picturesList.toArray(new Picture[picturesList.size()]);
            Collections.addAll(pictures, picturesInBucket);
        }

        final GridViewAdaptor gridViewAdaptor = new GridViewAdaptor(getActivity(), 0, pictures.toArray(new Picture[pictures.size()]), gridView);
        i(Settings.TAG, "gridView adaptor init");
        gridView.setAdapter(gridViewAdaptor);

        Button addButton = v.findViewById(R.id.data_center_button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = gridViewAdaptor.getCount();
                for(int i = 0; i < count; i++){
                    final Picture picture = gridViewAdaptor.getItem(i);

                    if(picture != null){
                        if(picture.isSelected()){
                            FileInputStream fis = null;
                            try{
                                fis = new FileInputStream(picture.getPicturePath());
                            }
                            catch (FileNotFoundException e){
                                e.printStackTrace();
                            }

                            final Bitmap bitmap = BitmapFactory.decodeFile(picture.getPicturePath());

                            citrusFaceManager.getImageFace().setImageFaceCallback(new BitmapInput(bitmap), new ImageFaceCallback() {
                                @Override
                                public void onFaceModel(FaceModel faceModel) {
                                    if(false){
                                        Toast.makeText(getContext(), "Add face failed.", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        // FaceDO faceDO = faceModel.getFaceList().get(0);
                                        UserInfo info = new UserInfo(picture.getPictureName().split("\\.")[0], picture.getPictureId());
                                        dbCreator.add(bitmap, info);
                                        dbCreator.save();
                                        Toast.makeText(getContext(), "Add face success.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                }
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

}
