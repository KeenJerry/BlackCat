package com.android.keenjackdaw.blackcat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.activity.CameraActivity;
import com.android.keenjackdaw.blackcat.controller.PictureProvider;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.PictureView;
import com.android.keenjackdaw.blackcat.utils.Picture;
import com.android.keenjackdaw.blackcat.utils.PictureBucket;

import java.util.List;

public class DataCenterFragment extends Fragment {

    private PictureProvider pictureProvider = PictureProvider.getInstance();
    private List<PictureBucket> pictureBuckets = null;

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

        try{
            pictureProvider.loadPictureData(getContext());
        }catch (BlackCatException e){
            e.printStackTrace();
        }

        pictureBuckets = pictureProvider.getPictureBucketList();

        GridLayout gridLayout = v.findViewById(R.id.picture_grid_layout);

        for(PictureBucket pictureBucket: pictureBuckets){
            PictureView pictureView = new PictureView(getContext(), new AttributeSet() {
                @Override
                public int getAttributeCount() {
                    return 0;
                }

                @Override
                public String getAttributeName(int index) {
                    return null;
                }

                @Override
                public String getAttributeValue(int index) {
                    return null;
                }

                @Override
                public String getAttributeValue(String namespace, String name) {
                    return null;
                }

                @Override
                public String getPositionDescription() {
                    return null;
                }

                @Override
                public int getAttributeNameResource(int index) {
                    return 0;
                }

                @Override
                public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
                    return 0;
                }

                @Override
                public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
                    return false;
                }

                @Override
                public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
                    return 0;
                }

                @Override
                public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
                    return 0;
                }

                @Override
                public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
                    return 0;
                }

                @Override
                public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
                    return 0;
                }

                @Override
                public int getAttributeListValue(int index, String[] options, int defaultValue) {
                    return 0;
                }

                @Override
                public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
                    return false;
                }

                @Override
                public int getAttributeResourceValue(int index, int defaultValue) {
                    return 0;
                }

                @Override
                public int getAttributeIntValue(int index, int defaultValue) {
                    return 0;
                }

                @Override
                public int getAttributeUnsignedIntValue(int index, int defaultValue) {
                    return 0;
                }

                @Override
                public float getAttributeFloatValue(int index, float defaultValue) {
                    return 0;
                }

                @Override
                public String getIdAttribute() {
                    return null;
                }

                @Override
                public String getClassAttribute() {
                    return null;
                }

                @Override
                public int getIdAttributeResourceValue(int defaultValue) {
                    return 0;
                }

                @Override
                public int getStyleAttribute() {
                    return 0;
                }
            });
            pictureView.
        }

        return v;
    }


}
