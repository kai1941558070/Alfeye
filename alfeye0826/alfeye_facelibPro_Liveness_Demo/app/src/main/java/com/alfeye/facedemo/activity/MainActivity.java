package com.alfeye.facedemo.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alfeye.a1io.DeviceControl;
import com.alfeye.facedemo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_verify_activity)
    Button btnVerifyActivity;
    @BindView(R.id.btn_face11_recog)
    Button btnFace11Recog;
    @BindView(R.id.btn_load_bmpfeature)
    Button btnLoadBmpfeature;
    @BindView(R.id.btn_face1n_recog)
    Button btnFace1nRecog;
    @BindView(R.id.btn_track_face)
    Button btnTrackFace;
    @BindView(R.id.btn_takePhoto)
    Button btnTakePhoto;
    @BindView(R.id.iv_showImage)
    ImageView ivShowImage;
    @BindView(R.id.btn_back)
    Button btnBack;
    private Uri photoUri;
    public String sdCardDir = Environment.getExternalStorageDirectory() + "/" + "A1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_track_face, R.id.btn_verify_activity, R.id.btn_face11_recog, R.id.btn_load_bmpfeature, R.id.btn_face1n_recog, R.id.btn_takePhoto,R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_track_face:
                startActivity(TrackFaceActivity.class);
                break;

            case R.id.btn_verify_activity:
                startActivity(VerifyActivity.class);
                break;

            case R.id.btn_face11_recog:
                startActivity(Face11RecogActivity.class);
                break;

            case R.id.btn_load_bmpfeature:
                startActivity(LoadBmpFeatureActivity.class);
                break;

            case R.id.btn_face1n_recog:
                startActivity(Face1NRecogActivity.class);
                break;

            case R.id.btn_back:
                finish();
                break;
                /*
                拍照存储
                 */
            case R.id.btn_takePhoto:
//                Toast.makeText(this, "take photo test", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory() + "/Pitctures", System.currentTimeMillis() + ".jpg");
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.TITLE, file.getAbsolutePath());
                photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                /**
                 * 打开宽动态状态
                 */
                cameraHdrManager(true);
                startActivityForResult(intent, CAMERA);
                boolean cameraHdrStatus = DeviceControl.getCameraHdrStatus(this);
//                Toast.makeText(this, "宽动态状态:" + cameraHdrStatus, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("zhangjikai", "requestCode:" + requestCode);
        Log.d("zhangjikai", "resultCode:" + resultCode);
        Log.d("zhangjikai", "data:" + data);

        if (requestCode == CAMERA && resultCode == -1) {
            Uri uri = photoUri;
            Log.d("zhangjikai", "保存Uri：" + uri);
            String imagePath = getImagePath(uri, null);
            Log.d("zhangjikai", "保存path：" + imagePath);
            displayImage(imagePath);
            /*
            拍照成功或取消后，关闭宽动态
             */
            cameraHdrManager(false);
        } else if (resultCode == 0) {
            cameraHdrManager(false);
            Toast.makeText(this, "您已取消拍照", Toast.LENGTH_SHORT).show();
        }
    }

    private void startActivity(Class<?> clazz) {
        startActivity(new Intent(this, clazz));
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            ivShowImage.setImageBitmap(bitmap);
            Log.d("zhangjikai", "图片路径：" + imagePath);
            saveBitmap(bitmap);
        } else {
            Toast.makeText(this, "加载出错", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBitmap(Bitmap bitmap) {
        try {
            File dirFile = new File(sdCardDir);
            if (!dirFile.exists()) {    //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }
            String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            File file = new File(sdCardDir, name + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.d("zhangjikai", "保存成功！----路径：" + file.getAbsolutePath());
            Toast.makeText(this, "保存成功!", Toast.LENGTH_SHORT).show();
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void cameraHdrManager(boolean open){
        if (open){
            DeviceControl.configCameraHdr(this, true);
            Log.d("zhangjikai", "cameraHdrManager: "+"宽动态开启");
        }else {
            DeviceControl.configCameraHdr(this, false);
            Log.d("zhangjikai", "cameraHdrManager: "+"宽动态关闭");
        }
    }
}
