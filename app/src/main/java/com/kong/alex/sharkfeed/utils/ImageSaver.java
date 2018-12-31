package com.kong.alex.sharkfeed.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageSaver {

    private final Bitmap image;

    private final File file;

    private static final String DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

    public ImageSaver(Bitmap image, String imageName) {
        this.image = image;
        this.file = new File(DIRECTORY, imageName + ".jpg");
    }

    public String saveImage() {
        String savedImagePath = null;
        File storageDir = new File(DIRECTORY);
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = file;
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return savedImagePath;
    }

}