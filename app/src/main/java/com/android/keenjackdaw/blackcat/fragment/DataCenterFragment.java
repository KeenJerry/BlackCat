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
import com.rokid.facelib.ImageRokidFace;
import com.rokid.facelib.api.IImageRokidFace;
import com.rokid.facelib.api.ImageFaceCallback;
import com.rokid.facelib.db.UserInfo;
import com.rokid.facelib.input.BitmapInput;
import com.rokid.facelib.model.FaceDO;
import com.rokid.facelib.model.FaceModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.util.Log.i;

public class DataCenterFragment extends Fragment {

    private PictureProvider pictureProvider = null;
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

                            final Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            //final Bitmap bitmap1 = BitmapFactory.
                            final Bitmap scaledBitmap;

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
                            citrusFaceManager.getImageFace().setImageFaceCallback(new BitmapInput(bitmap), new ImageFaceCallback() {
                                @Override
                                public void onFaceModel(FaceModel faceModel) {
                                    FaceDO faceDO = faceModel.getFaceList().get(0);

                                    if(faceDO == null){
                                        Toast.makeText(getContext(), "Add face failed.", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        UserInfo info = new UserInfo(picture.getPictureName(), picture.getPictureId());
                                        citrusFaceManager.addUser(bitmap, info);
                                        citrusFaceManager.save();
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
