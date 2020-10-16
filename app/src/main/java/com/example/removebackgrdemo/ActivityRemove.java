package com.example.removebackgrdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.removebackgrdemo.Util.FileUtil;
import com.example.removebackgrdemo.Util.PermissionUtil;
import com.example.removebackgrdemo.removebg.PaintView;
import com.example.removebackgrdemo.rmtensorflow.ImageSegmentationModelExecutor;
import com.example.removebackgrdemo.rmtensorflow.ImageUtils;
import com.example.removebackgrdemo.rmtensorflow.ModelExecutionResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import static android.graphics.PorterDuff.Mode.DST_IN;
import static android.graphics.PorterDuff.Mode.DST_OUT;

public class ActivityRemove extends AppCompatActivity implements View.OnClickListener {
    private PaintView paintView;
    private Button btnLoad,btnAuto,btnOK;
    private FrameLayout fmLayout;
    int DefaultColor;
    private Bitmap bm;
    SeekBar size;
    private static final int GET_FILE_REQUEST_CODE = 101;
    private static final int REQUEST_PERMISSION = 102;
    private String mCameraPhotoPath;
    private String pathtoghep;

    ///auto
    private String path;
    //    private MLExecutionViewModel viewModel;
    private ImageSegmentationModelExecutor imageSegmentationModel;
    private Boolean useGPU =false;
    private ModelExecutionResult modelExecutionResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);
        paintView = findViewById(R.id.paint_view);
        //  circleView=findViewById(R.id.circle);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        paintView.normal();
        //  circleView.init(metrics);
        init();
        DefaultColor=paintView.DEFAULT_COLOR;
        imageSegmentationModel = new ImageSegmentationModelExecutor(this, useGPU);

    }
    public void onClickSelectPhoto(View view) {
        checkPermissionOS6();
    }


    private void checkPermissionOS6() {
        if (PermissionUtil.isCameraPermissionOn(this)
                && PermissionUtil.isReadExternalPermissionOn(this)
                && PermissionUtil.isWriteExternalPermissionOn(this)) {
            getPhoto();
            return;
        }
        String[] permissions = {
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
    }

    private void getPhoto() {
        // Camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = FileUtil.createImageFile(this);
            path="file:" + photoFile.getAbsolutePath();
            mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
        } catch (IOException e) {
            mCameraPhotoPath = null;
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        // Gallery
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.addCategory(Intent.CATEGORY_OPENABLE);
        gallery.setType("image/*");

        Intent[] intents;
        if (cameraIntent != null && mCameraPhotoPath != null) {
            intents = new Intent[]{cameraIntent};
        } else {
            intents = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, gallery);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);

        startActivityForResult(chooserIntent, GET_FILE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            onClickSelectPhoto(null);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != GET_FILE_REQUEST_CODE || resultCode != RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            mCameraPhotoPath = null;
            return;
        }
        Uri[] results = null;

        if (data == null || data.getData() == null) {
            // If there is not data, then we may have taken a photo
            if (mCameraPhotoPath != null) {
                results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                try {
                    bm=MediaStore.Images.Media.getBitmap(this.getContentResolver(), results[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCameraPhotoPath = null;
            }
        } else {
            Uri dataUri = data.getData();
            Hashtable<String, Object> info = FileUtil.getFileInfo(this, dataUri);
            path = (String) info.get(FileUtil.ARG_PATH);
            ///Check rotate
            try {
                ExifInterface exif = null;
                exif = new ExifInterface(path);
                Matrix transformation =
                        decodeExifOrientation(
                                exif.getAttributeInt(
                                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90
                                )
                        );
                BitmapFactory.Options options = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(path,options);
                bm=Bitmap.createBitmap(
                        bm, 0, 0, bm.getWidth(), bm.getHeight(), transformation, true
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            // results = new Uri[]{Uri.fromFile(new File(imagePath))};


        }
        paintView.setbm(bm,fmLayout.getWidth(),fmLayout.getHeight());

    }

    ///check Rotate
    private static Matrix decodeExifOrientation(int orientation ) {
        Matrix matrix = new Matrix();

        // Apply transformation corresponding to declared EXIF orientation
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                break;
            case ExifInterface.ORIENTATION_UNDEFINED:
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90F);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180F);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270F);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1F, 1F);
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1F, -1F);
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.postScale(-1F, 1F);
                matrix.postRotate(270F);
                break;

            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.postScale(-1F, 1F);
                matrix.postRotate(90F);
                break;

            // Error out if the EXIF orientation is invalid
            default:
                throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
        return matrix;
    }




    private void init() {
        size= findViewById(R.id.size);
        btnLoad= findViewById(R.id.btn_load);
        btnAuto = findViewById(R.id.btn_auto);
        btnOK=findViewById(R.id.btn_ok);
        fmLayout=findViewById(R.id.frm_paint);

        btnAuto.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        size.setMax(100);
        size.setProgress(25);
        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                paintView.resize(i - 25);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // int i = size.getProgress();
                //  paintView.resize(i - 25);
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                Bitmap cache = paintView.getBMRS();
                saveFrameLayout(cache);
                Intent itghep=new Intent(ActivityRemove.this,ActivityGhep.class);
                itghep.putExtra("BitmapImage",pathtoghep);

                startActivity(itghep);
                break;
            case R.id.btn_auto:
                try {
                    modelExecutionResult=imageSegmentationModel.execute(ImageUtils.decodeBitmap(new File(path)));
                    Bitmap bmsrauto=Bitmap.createScaledBitmap(modelExecutionResult.bitmapResult, bm.getWidth(), bm.getHeight(), true);
                    Canvas canvas =new Canvas();
                    bm=bm.copy(Bitmap.Config.ARGB_8888, true);
                    canvas.setBitmap(bm);
                    Paint paint=new Paint();
                    paint.setXfermode(new PorterDuffXfermode(DST_IN));
                    canvas.drawBitmap(bmsrauto,0,0,paint);
                    paintView.setbm(bm,fmLayout.getWidth(),fmLayout.getHeight());

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


        //setChipsToLogView(modelExecutionResult.itemsFound)
        //  enableControls(true)

    //Save

    public  void saveFrameLayout(Bitmap cache) {
        // frameLayout.setDrawingCacheEnabled(true);
        // frameLayout.buildDrawingCache();
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
            Toast.makeText(ActivityRemove.this,
                    "Save success: " + newFile.getName(),
                    Toast.LENGTH_LONG).show();
            System.out.println("Name: " + NameFile);
            //quét hình ảnh để hiển thị trong album
            pathtoghep=newFile.getAbsolutePath();
            MediaScannerConnection.scanFile(this,
                    new String[]{newFile.getAbsolutePath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(ActivityRemove.this,
                    "Something wrong 1: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ActivityRemove.this,
                    "Something wrong 2: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}