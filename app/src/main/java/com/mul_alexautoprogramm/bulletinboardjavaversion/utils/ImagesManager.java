package com.mul_alexautoprogramm.bulletinboardjavaversion.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImagesManager {
    private Context context;
    private final int MAX_SIZE = 1000;
    private int width;
    private int height;
    private OnBitmapLoaded onBitmapLoaded;
    private List<Bitmap> bitmapList;


    public ImagesManager(Context context, OnBitmapLoaded onBitmapLoaded) {
        this.context = context;
        this.onBitmapLoaded = onBitmapLoaded;
        bitmapList = new ArrayList<>();
    }

    public int[] getImageSize(String uri) {

        Log.d("MyLog", "Rotation: " + getImageRotation(uri));
        int[] size = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, options);

        if(getImageRotation(uri) == 0) {

            size[0] = options.outWidth;
            size[1] = options.outHeight;

        }else {

            size[1] = options.outWidth;
            size[0] = options.outHeight;

        }

        return size;
    }


    public void resizeMultiLargeImages(final List<String> uris) {
        final List<int[]> sizeList = new ArrayList<>();
        final List<int[]> realSizeList = new ArrayList<>();


        for (int i = 0; i < uris.size(); i++) {
            int[] sizes = getImageSize(uris.get(i));
            width = sizes[0];
            height = sizes[1];

            realSizeList.add(new int[]{width, height});

            float imageRatio = (float) width / (float) height;

            if (imageRatio > 1) {

                if (width > MAX_SIZE) {

                    width = MAX_SIZE;
                    height = (int) (width / imageRatio);

                }

            } else {

                if (height > MAX_SIZE) {

                    height = MAX_SIZE;
                    width = (int) (height * imageRatio);

                }
            }
            sizeList.add(new int[]{width, height});

        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    bitmapList.clear();

                    for (int i = 0; i < sizeList.size(); i++) {


                        if (!uris.get(i).equals("empty") && !uris.get(i).startsWith("http")
                                && realSizeList.get(i)[0] > MAX_SIZE || realSizeList.get(i)[1] > MAX_SIZE) {

                            Bitmap bitmap = Picasso.get().load(Uri.fromFile(new File(uris.get(i)))).resize(sizeList.get(i)[0], sizeList.get(i)[1]).get();
                            bitmapList.add(bitmap);

                        } else if (uris.get(i).startsWith("http")) {

                            Bitmap bitmap = Picasso.get().load(uris.get(i)).get();
                            bitmapList.add(bitmap);

                        } else if (!uris.get(i).equals("empty")
                                && !uris.get(i).startsWith("http")
                                && realSizeList.get(i)[0] < MAX_SIZE
                                && realSizeList.get(i)[1] < MAX_SIZE) {

                            Bitmap bitmap = Picasso.get().load(Uri.fromFile(new File(uris.get(i)))).get();
                            bitmapList.add(bitmap);

                        } else {

                            bitmapList.add(null);

                        }

                    }

                    onBitmapLoaded.onBitmapLoaded(bitmapList);

                } catch (IOException e) {
                    e.printStackTrace();

                }

            }
        }).start();

    }

    private int getImageRotation(String imagePath){
        int rotation = 0;

        File imageFile = new File(imagePath);
        try {

            ExifInterface exifInterface = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if(ExifInterface.ORIENTATION_ROTATE_90 == orientation || ExifInterface.ORIENTATION_ROTATE_270 == orientation){

                rotation = 90;

            }else {

                rotation = 0;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotation;

    }

}
