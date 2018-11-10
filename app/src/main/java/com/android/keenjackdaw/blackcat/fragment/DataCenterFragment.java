package com.android.keenjackdaw.blackcat.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
        Drawable activateIcon = getResources().getDrawable(R.drawable.ic_launcher_background);
        Drawable inactivateIcon = getResources().getDrawable(R.drawable.ic_launcher_foreground);

        for(PictureBucket pictureBucket: pictureBuckets){
            List<Picture> pictures = pictureBucket.getPictureList();
            for(Picture picture: pictures){
                PictureView pictureView = new PictureView(getContext());
                pictureView.setActivatedIcon(activateIcon);
                pictureView.setUnactivatedIcon(inactivateIcon);
                pictureView.setPictureUrl(picture.getPicturePath());
            }

        }

        return v;
    }


}
