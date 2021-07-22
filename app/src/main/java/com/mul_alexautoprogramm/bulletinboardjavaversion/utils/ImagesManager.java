package com.mul_alexautoprogramm.bulletinboardjavaversion.utils;

import android.graphics.Bitmap;

public class ImagesManager {

    public static Bitmap resizeImage(Bitmap image, int maxSize){

        int width = image.getWidth();
        int height = image.getHeight();
        float imageRatio = (float) (width /  height);
        if(imageRatio > 1){

            if(width > maxSize){

                width = maxSize;
                height = (int) (width / imageRatio);

            }



        }else {

            if(height > maxSize) {

                height = maxSize;
                width = (int) (height * imageRatio);

            }
        }

        return Bitmap.createScaledBitmap(image, width, height,true);
    }

}
