package com.example.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    private URL imgUrl;
    private Bitmap imgBitmap;
    ImageView carImage;

    //constructor
    public ImageDownloader(ImageView carImg){
        carImage = carImg;
    }

    //downloads image in background and returns as bitmap
    @Override
    protected Bitmap doInBackground(String... strings) {

        try{
            this.imgUrl = new URL(strings[0]);
            imgBitmap = BitmapFactory.decodeStream(this.imgUrl.openConnection().getInputStream());
        }catch(Exception e){
            Log.e("ImageDownloader error", e.toString());
        }
        return imgBitmap;
    }

    //put the image into ImageView when finished downloading
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        carImage.setImageBitmap(bitmap);
    }
}
