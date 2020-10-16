package com.example.removebackgrdemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.removebackgrdemo.sticker.DrawableSticker;
import com.example.removebackgrdemo.sticker.Sticker;
import com.example.removebackgrdemo.sticker.StickerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ActivityGhep extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ActivityGhep.class.getSimpleName();
    private StickerView mStickerView;
    private ImageView backgr;
    Button loadback,loadfont,save;

    private static final int REQUEST_STORAGE_PERMISSION = 100;
    final int RQS_IMAGE1 = 1;
    final int RQS_IMAGE2 = 2;
    Bitmap processedBitmap, processedBitmap2;
    private Bitmap bmScale2;
    private Bitmap bmScale1;
    private float oldW1, oldH1;
    private float newW1, newH1;
    private float oldW2, oldH2;
    private float newW2, newH2;
    Uri uri1, uri2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghep);
        mStickerView = (StickerView) findViewById(R.id.sticker_view);
        backgr=(ImageView) findViewById(R.id.back_gr);
        requestPermission();
        mStickerView.setBackgroundColor(Color.WHITE);
        mStickerView.setLocked(false);
        loadback=(Button)findViewById(R.id.btn_loadback);
        loadfont=(Button)findViewById(R.id.btn_loadfont);
        save=(Button)findViewById(R.id.btn_save) ;

        loadback.setOnClickListener(this);
        loadfont.setOnClickListener(this);
        save.setOnClickListener(this);

        mStickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerClicked(Sticker sticker) {
                Log.d(TAG, "onStickerClicked");
            }

            @Override
            public void onStickerDeleted(Sticker sticker) {
                Log.d(TAG, "onStickerDeleted");
            }

            @Override
            public void onStickerDragFinished(Sticker sticker) {
                Log.d(TAG, "onStickerDragFinished");
            }

            @Override
            public void onStickerZoomFinished(Sticker sticker) {
                Log.d(TAG, "onStickerZoomFinished");
            }

            @Override
            public void onStickerFlipped(Sticker sticker) {
                Log.d(TAG, "onStickerFlipped");
            }
        });
        Intent myinten =getIntent();
          //  Bitmap bitmap = (Bitmap) myinten.getParcelableExtra("BitmapImage");
            String pathfromremove=myinten.getStringExtra("BitmapImage");
            if(pathfromremove!=null) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(pathfromremove, bmOptions);
                processedBitmap2 = Bitmap.createBitmap(bitmap);
                Drawable drawable = new BitmapDrawable(getResources(), processedBitmap2);
                mStickerView.addSticker(new DrawableSticker(drawable));
            }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_loadback:
                chooseImage();
                break;
            case R.id.btn_loadfont:
                addImage();
                break;
            case R.id.btn_save:
                saveFrameLayout();
        }
    }
    private void addImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RQS_IMAGE2);

    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RQS_IMAGE1);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RQS_IMAGE1:
                    uri1 = data.getData();
                    try {
                        processedBitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(uri1));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    processedBitmap=processedBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    this.oldW1 = processedBitmap.getWidth();
                    this.oldH1 = processedBitmap.getHeight();

//
                    if (mStickerView.getWidth() > 0 && mStickerView.getHeight() > 0) {
                        int width = processedBitmap.getWidth();
                        int height = processedBitmap.getHeight();
                        float ratioBitmap = (float) width / (float) height;
                        float ratioMax = (float) mStickerView.getWidth() / (float) mStickerView.getHeight();

                        int finalWidth = (int) mStickerView.getWidth();
                        int finalHeight = (int) mStickerView.getHeight();
                        if (ratioMax > ratioBitmap) {
                            finalWidth = (int) ((float) mStickerView.getHeight() * ratioBitmap);
                        } else {
                            finalHeight = (int) ((float) mStickerView.getWidth() / ratioBitmap);
                        }
                        this.newW1 = (float) finalWidth;
                        this.newH1 = (float) finalHeight;
                        bmScale1 = Bitmap.createScaledBitmap(processedBitmap, finalWidth, finalHeight, true);
                    }

                    backgr.setImageBitmap(bmScale1);
                    break;

                case RQS_IMAGE2:
                    uri2 = data.getData();
                    try {
                        processedBitmap2 = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(uri2));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.d("IMAGE 2", processedBitmap2.getWidth() + "" + processedBitmap2.getHeight());
//                    bmScale2 = Bitmap.createScaledBitmap(processedBitmap2,
//                            200,
//                            200,
//                            false);
//                    Log.d("IMAGE 2 SCALE ", "" + bmScale2.getWidth() + bmScale2.getHeight());
                    Drawable drawable= new BitmapDrawable(getResources(),processedBitmap2);
                    mStickerView.addSticker(new DrawableSticker(drawable));
//
                    break;
            }

        }
    }

    private void requestPermission() {

        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            } else {
                //Yeah! I want both block to do the same thing, you can write your own logic, but this works for me.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        } else {
        }

    }
    public Bitmap getBMRS(){
        Canvas ccc=new Canvas();
        processedBitmap.setConfig(Bitmap.Config.ARGB_8888);
        ccc.setBitmap(processedBitmap);
        Bitmap font= Bitmap.createScaledBitmap(processedBitmap2,(int)(mStickerView.currentX*oldH1/newH1),(int)(mStickerView.currentY*oldH1/newH1),true);
        Matrix matrix=new Matrix();
        matrix.postRotate(mStickerView.rtt);
        Bitmap rs2=Bitmap.createBitmap(font, 0, 0,font.getWidth(),font.getHeight(), matrix, true);
        //  rs2.setConfig(Bitmap.Config.ARGB_8888);
        double xbu=0;
        double ybu=0;
        if(0F<mStickerView.rtt&&mStickerView.rtt<90F){
            xbu=font.getHeight()*Math.cos(Math.toRadians(180-90-mStickerView.rtt));
            ybu=0;
        }
        else if(90F<mStickerView.rtt&&mStickerView.rtt<180F){
            xbu=font.getHeight()*Math.cos(Math.toRadians(mStickerView.rtt-90))+
            font.getWidth()*Math.sin(Math.toRadians(mStickerView.rtt-90));
            ybu=font.getHeight()*Math.sin(Math.toRadians(mStickerView.rtt-90));

        }else if(-90F<mStickerView.rtt&&mStickerView.rtt<0F){
            xbu=0;
            ybu=font.getHeight()*Math.cos(Math.toRadians(mStickerView.rtt+90));
        }


        ccc.drawBitmap(rs2,(int)(mStickerView.mX*oldH1/newH1-xbu),(int)(mStickerView.mY*oldH1/newH1-ybu),null);

        return processedBitmap;
    }
    public  void saveFrameLayout() {
        // frameLayout.setDrawingCacheEnabled(true);
        // frameLayout.buildDrawingCache();
        Bitmap cache = getBMRS();
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/DCIM/DemoSVMC");
        dir.mkdir();
        String NameFile = "" + System.currentTimeMillis();
        File newFile = new File(dir, NameFile + ".png");
        try {
            OutputStream fileOutputStream = new FileOutputStream(newFile);
            cache.setHasAlpha(true);
            cache.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(ActivityGhep.this,
                    "Save success: " + newFile.getName(),
                    Toast.LENGTH_LONG).show();
            System.out.println("Name: " + NameFile);
            //quét hình ảnh để hiển thị trong album
            MediaScannerConnection.scanFile(this,
                    new String[]{newFile.getAbsolutePath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(ActivityGhep.this,
                    "Something wrong 1: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ActivityGhep.this,
                    "Something wrong 2: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}