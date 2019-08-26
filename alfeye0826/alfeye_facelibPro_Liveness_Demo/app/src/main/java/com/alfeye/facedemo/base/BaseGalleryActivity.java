package com.alfeye.facedemo.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.alfeye.facedemo.R;
import com.alfeye.facedemo.utils.AlertError;
import com.facelib.activity.BaseFaceRecogActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 相册、拍照 选择图片 的基础类
 */
public abstract class BaseGalleryActivity extends BaseFaceRecogActivity {
    protected PopupWindow mPopupWindow = null;
    protected String mPhotoFilePath = null;
    // 打开相机拍照的请求码
    // the request code for opening camera app to take picture
    protected int PHOTO_REQUEST_CAREMA = 1;
    // 从相册中选择的请求码
    // the request code for choosing from gallery
    protected int PHOTO_REQUEST_GALLERY = 2;
    // 裁剪请求码
    // the request code for cropping
    protected static final int CROP_REQUEST_IMAGE = 3;
    private String TAKEPIC_DIR_NAME = "images";

    @TargetApi(Build.VERSION_CODES.M)
    protected void requestAllPermissionsIfNeed() {
        List<String> permissionList = new ArrayList<String>();
        // 申请相机权限
        // Camera permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // 用户拒绝过权限申请，下一次再进入的时候给出的解释
                // If the permission have been refused, explain here
                new AlertError().showDialog(this, getResources().getString(R.string.error_title),
                        getResources().getString(R.string.no_camera_perm_hint));
            } else {
                permissionList.add(Manifest.permission.CAMERA);
            }
        }
        // 我们需要从应用外的目录获取照片，所以需要申请读取外部存储权限
        // read external storage permission, for we need to read the photos
        // outside application-specific directories
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertError().showDialog(this, getResources().getString(R.string.error_title),
                        getResources().getString(R.string.no_file_perm_hint));
            } else {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        if (permissionList.size() > 0) {
            requestPermissions(
                    permissionList.toArray(new String[permissionList.size()]), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPopupWindow = new PopupWindow(this);
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setContentView(view);

        Button cameraBtn = (Button) view.findViewById(R.id.item_camera);
        Button galleryBtn = (Button) view.findViewById(R.id.item_gallery);
        Button cancelBtn = (Button) view.findViewById(R.id.item_cancel);

        cameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                openCamera(BaseGalleryActivity.this);
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                openGallery();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
            }
        });
    }

    protected void popupChoosePictureWindows(View mainView) {
        if (mainView != null) {
            mPopupWindow.showAtLocation(mainView, Gravity.BOTTOM, 0, 0);
        }
    }

    private String generateFilepath(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String newPicPath = makeFileDirs(context, TAKEPIC_DIR_NAME);
        return newPicPath + File.separator + "IMG_" + timeStamp + ".jpg";
    }

    private String makeFileDirs(Context context, String dirname) {
        String newPath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + dirname;
        File file = new File(newPath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return newPath;
    }

    protected abstract void openGallery();

    private void openCamera(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoFilePath = generateFilepath(context);
        File file = new File(mPhotoFilePath);
        Uri imageUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        try {
            startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
        } catch (SecurityException e) {
            new AlertError().showDialog(this, getString(R.string.error_title), getString(R.string.no_camera_perm_hint));
        }
    }

    public String getImagePath(Context context, Uri uri) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

}
