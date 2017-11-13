package com.justin.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yuchen.lib.R;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class UILUtils {
    private static DisplayImageOptions options;
    private static DisplayImageOptions optionsCorner;
    private static ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    public static DisplayImageOptions getCircelOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_default_man_circle)
                .showImageForEmptyUri(R.drawable.icon_default_man_circle)
                .showImageOnFail(R.drawable.icon_default_man_circle).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .displayer(new CircleBitmapDisplayer())
                .build();
        return options;
    }

    public static DisplayImageOptions getNoCacheOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_default_man_circle)
                .showImageForEmptyUri(R.drawable.icon_default_man_circle)
                .showImageOnFail(R.drawable.icon_default_man_circle).cacheInMemory(false)
                .cacheOnDisk(false).considerExifParams(true)
                .displayer(new CircleBitmapDisplayer())
                .build();
        return options;
    }



    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 3000);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    public static void displayImage(String imageUrls, ImageView mImageView) {
        initOptions();
        ImageLoader.getInstance().displayImage(imageUrls, mImageView, options,
                animateFirstListener);
    }
    public static void displayCacheImage(String imageUrls, ImageView mImageView) {

        ImageLoader.getInstance().displayImage(imageUrls, mImageView, getNoCacheOptions(),
                animateFirstListener);
    }

    public static void displayCircleImage(String imageUrls, ImageView mImageView) {
        ImageLoader.getInstance().displayImage(imageUrls, mImageView, getCircelOptions(),
                animateFirstListener);
    }

    public static void displayImageWithRounder(String imageUrls,
                                               ImageView mImageView, int cornerRadiusPixels) {
        initOptions(cornerRadiusPixels);
        ImageLoader.getInstance().displayImage(imageUrls, mImageView, optionsCorner,
                animateFirstListener);
    }


    private static void initOptions() {
        if (options == null) {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.icon_default_man)
                    .showImageForEmptyUri(R.drawable.icon_default_man)
                    .showImageOnFail(R.drawable.icon_default_man).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true)
                    .build();
        }
    }


    private static void initOptions(int cornerRadiusPixels) {
        if (optionsCorner == null) {
            optionsCorner = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.icon_default_man)
                    .showImageForEmptyUri(R.drawable.icon_default_man)
                    .showImageOnFail(R.drawable.icon_default_man).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true)
                    .displayer(new RoundedBitmapDisplayer(cornerRadiusPixels))
                    .build();
        }
    }

}
