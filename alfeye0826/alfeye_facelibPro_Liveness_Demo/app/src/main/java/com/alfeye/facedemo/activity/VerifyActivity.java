package com.alfeye.facedemo.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfeye.facedemo.BuildConfig;
import com.alfeye.facedemo.R;
import com.alfeye.facedemo.base.BaseGalleryActivity;
import com.alfeye.facedemo.utils.AlertError;
import com.alfeye.facedemo.utils.ImageFilePath;
import com.facelib.LicenseST.DialogUtil;
import com.facelib.LicenseST.LicenseResultListener;
import com.facelib.utils.FaceUtils;
import com.sensetime.faceapi.StFace;
import com.sensetime.faceapi.StFaceFeature;
import com.sensetime.faceapi.StFaceOrientation;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class VerifyActivity extends BaseGalleryActivity implements LicenseResultListener, OnClickListener {
    private Bitmap mLeftBitmap = null;
    private Bitmap mRightBitmap = null;
    private ImageView mLeftImageView = null;
    private ImageView mRightImageView = null;
    private TextView mResultTextView = null;
    private LinearLayout mResultLayout = null;
    private Button mVerifyBtn = null;
    private VerifyAsyncTask mVerifyAsyncTask = null;
    private String mLeftCroppedImagePath = null;
    private String mRightCroppedImagePath = null;
    private static final int CROP_WIDTH = 500;
    private static final int CROP_HEIGHT = 500;
    private RelativeLayout mMainView = null;
    private static final int STATE_CHOOSE_LEFT_IMAGE = 1;
    private static final int STATE_CHOOSE_RIGHT_IMAGE = 2;
    private int mCurrentState = 0;
    private String PROVIDER_NAME = BuildConfig.APPLICATION_ID + ".provider";
    private AlertError alertError = new AlertError();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        // Android 6.0 及以上需要申请运行权限
        // Request permissions at runtime on Android 6.0 or later
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            requestAllPermissionsIfNeed();
        }
        initViews();
    }

    @Override
    public void onLicenseInitFailed(String errorMessage) {
        hideVerifyButton();
    }

    private void hideVerifyButton() {
        if (mVerifyBtn != null) {
            mVerifyBtn.setVisibility(View.GONE);
        }
    }

    /**
     * 异步比对线程<br>
     * the async task for face verify
     */
    class VerifyAsyncTask extends AsyncTask<Void, Void, String> {
        private String mErrorMessage = null;
        private Dialog mVerifyProcessDialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mVerifyProcessDialog = DialogUtil.createProgressDialog(VerifyActivity.this, getString(R.string.main_init));
            mVerifyProcessDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            StFaceFeature feature1 = null;
            StFaceFeature feature2 = null;
            // 默认图片中人脸方向是朝上的
            // set the default face orientation to UP
            int orientation = StFaceOrientation.ST_FACE_UP;
            float result = 0;
            // 分别检测左右两侧的图片对象中的人脸，如果检测到的人脸不为空，则获取人脸特征
            // detect the faces in left and right image objects respectively, if
            // the face is detected, get the feature of the face
            try {
                StFace[] face = FaceUtils.queryFace(mLeftBitmap, orientation);       //mDetector.detect(mLeftBitmap, orientation);
                if (face != null && face.length > 0) {
                    feature1 = FaceUtils.getFaceFeature(mLeftBitmap, face[0]);
                }
                StFace[] face2 = FaceUtils.queryFace(mRightBitmap, orientation);
                if (face2 != null && face2.length > 0) {
                    feature2 = FaceUtils.getFaceFeature(mRightBitmap, face2[0]);
                }
            } catch (Exception e) {
                mErrorMessage = e.getLocalizedMessage();
            }

            // 如果两张图片中的人脸特征都获取成功，那么比对这两个特征，得出最后的得分
            // if the features of two faces are got successfully, compare the
            // features, got the score of two feature
            if (feature1 != null && feature2 != null) {
                result = FaceUtils.compareFeature(feature1, feature2);

            } else {
                mErrorMessage = getString(R.string.no_face_hint);
            }
            // 释放人脸特征对象
            // release the face feature object
            if (feature1 != null) {
                feature1.recycle();
            }
            if (feature2 != null) {
                feature2.recycle();
            }
            return String.format("%.3f", result);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mVerifyProcessDialog != null) {
                mVerifyProcessDialog.dismiss();
            }
            if (mErrorMessage != null) {
                alertError.showDialog(VerifyActivity.this, getString(R.string.error_title), mErrorMessage);
                return;
            }
            mResultLayout.setVisibility(View.VISIBLE);
            mResultTextView.setText(result);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLeftBitmap != null && !mLeftBitmap.isRecycled()) {
            mLeftBitmap.recycle();
        }
        if (mRightBitmap != null && !mRightBitmap.isRecycled()) {
            mRightBitmap.recycle();
        }
        // 销毁Activity的时候进行资源释放
        // we should release the resource for searching when we leave the
        // activity
//        if (mDetector != null) {
//            mDetector.release();
//            mDetector = null;
//        }
//        if (mVerify != null) {
//            mVerify.release();
//            mVerify = null;
//        }

        if (alertError != null) {
            alertError.dismiss();
        }
    }

    //HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
    //以下为 从相册、相机 选择图片功能

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            alertError.showDialog(this, null, "Please choose a picture");
            return;
        }
        if (requestCode == PHOTO_REQUEST_CAREMA || requestCode == PHOTO_REQUEST_GALLERY) {
            Uri imageUri = null;
            // Android7.0开始，应用的私有目录访问权限被限制，不能通过file://uri来访问其他应用的私有目录文件，所以我们使用contentProvider
            // 来帮助我们将访问受限的file://uri转化成授权共享的content://uri
            // From Android pic.0, the application's private directory access is
            // restricted and can not be accessed via file:// uri to the private
            // directory file for other applications, so we use the
            // contentProvider to help us convert the restricted uri(file://) to
            // an authorized share uri(content://)
            if (requestCode == PHOTO_REQUEST_CAREMA) {
                if (mPhotoFilePath != null) {
                    if (VERSION.SDK_INT <= VERSION_CODES.M) {
                        imageUri = Uri.parse("file://" + mPhotoFilePath);
                    } else {
                        File file = new File(mPhotoFilePath);
                        imageUri = FileProvider.getUriForFile(this, PROVIDER_NAME, file);
                    }
                }
            }
            if (requestCode == PHOTO_REQUEST_GALLERY) {
                if (VERSION.SDK_INT <= VERSION_CODES.M) {
                    imageUri = ImageFilePath.getImageContentUri(this, data);
                } else {
                    File file = new File(getImagePath(this, data.getData()));
                    imageUri = FileProvider.getUriForFile(this, PROVIDER_NAME, file);
                }
            }
            // 如果uri不为空，进行图片裁剪，通过裁剪可以在多人脸图中选择出想要进行比对的人脸
            // if the uri is not null, go to crop picture, you can choose the
            // face you want to verify from a picture which has multi-face
            if (imageUri != null) {
                if (mCurrentState == STATE_CHOOSE_LEFT_IMAGE) {
                    if (mLeftCroppedImagePath != null) {
                        getCropImage(imageUri, mLeftCroppedImagePath, CROP_REQUEST_IMAGE);
                    }
                } else {
                    if (mRightCroppedImagePath != null) {
                        getCropImage(imageUri, mRightCroppedImagePath, CROP_REQUEST_IMAGE);
                    }
                }
            } else {
                alertError.showDialog(VerifyActivity.this, getString(R.string.error_title),
                        getString(R.string.choose_image_failed));
            }
        } else if (requestCode == CROP_REQUEST_IMAGE) {
            if (mCurrentState == STATE_CHOOSE_LEFT_IMAGE) {
                try {
                    mLeftBitmap = BitmapFactory.decodeFile(mLeftCroppedImagePath);
                    mLeftImageView.setImageBitmap(mLeftBitmap);
                } catch (Exception e) {
                    e.printStackTrace();

                    mLeftBitmap = null;
                    mLeftImageView.setImageResource(R.drawable.choose_picture);
                }
            } else {
                try {
                    mRightBitmap = BitmapFactory.decodeFile(mRightCroppedImagePath);
                    mRightImageView.setImageBitmap(mRightBitmap);
                } catch (Exception e) {
                    e.printStackTrace();

                    mRightBitmap = null;
                    mRightImageView.setImageResource(R.drawable.choose_picture);
                }
            }
        }
    }

    /**
     * 启动裁剪图片的应用，需要注意的是android pic.0开始目录访问权限的问题<br>
     * start the activity of cropping image, need pay attention to the
     * permission of the application's private directory
     *
     * @param srcUri      图片源uri<br>
     *                    the uri of source image
     * @param dstPath     裁剪后的图片路径<br>
     *                    the path of image after cropped
     * @param requestCode 请求码<br>
     *                    request code
     */
    protected void getCropImage(Uri srcUri, String dstPath, int requestCode) {
        Uri dstUri = null;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(srcUri, "image/*");
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            dstUri = Uri.parse("file://" + dstPath);
        } else {
            File dstFile = new File(dstPath);
            dstUri = FileProvider.getUriForFile(this, PROVIDER_NAME, dstFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            List<ResolveInfo> resInfoList = this.getPackageManager()
                    .queryIntentActivities(intent, 0);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, dstUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CROP_WIDTH);
        intent.putExtra("outputY", CROP_HEIGHT);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, dstUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView1:
                mCurrentState = STATE_CHOOSE_LEFT_IMAGE;
                popupChoosePictureWindows(mMainView);
                break;
            case R.id.imageView2:
                mCurrentState = STATE_CHOOSE_RIGHT_IMAGE;
                popupChoosePictureWindows(mMainView);
                break;

            default:
                break;
        }

    }

    private void initViews() {
        mMainView = (RelativeLayout) findViewById(R.id.verify_main_layout);
        mResultTextView = (TextView) findViewById(R.id.verify_result_tv);
        mResultLayout = (LinearLayout) findViewById(R.id.verify_result_layout);
        mResultLayout.setVisibility(View.INVISIBLE);
        mLeftImageView = (ImageView) findViewById(R.id.imageView1);
        mRightImageView = (ImageView) findViewById(R.id.imageView2);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        int dmWidth = dm.widthPixels;
        LayoutParams lp = (LayoutParams) mLeftImageView.getLayoutParams();
        lp.width = dmWidth / 2;
        mLeftImageView.setLayoutParams(lp);
        lp = (LayoutParams) mRightImageView.getLayoutParams();
        lp.width = dmWidth / 2;
        mRightImageView.setLayoutParams(lp);

        mVerifyBtn = (Button) findViewById(R.id.verify_btn);
        mLeftImageView.setOnClickListener(this);
        mRightImageView.setOnClickListener(this);
        mVerifyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftBitmap != null && mRightBitmap != null) {
                    mVerifyAsyncTask = (VerifyAsyncTask) new VerifyAsyncTask();
                    mVerifyAsyncTask.execute();
                } else {
                    alertError.showDialog(VerifyActivity.this, getString(R.string.error_title),
                            getString(R.string.no_picture_hint));
                }
            }
        });

        // 初始化保存剪切图片的文件
        // init the file to save cropped image
        String path = getExternalFilesDir(null).getAbsolutePath();
        mLeftCroppedImagePath = path + File.separator + "left_cropped.jpg";
        mRightCroppedImagePath = path + File.separator + "right_cropped.jpg";
        if (!createVerifyFiles(mLeftCroppedImagePath)) {
            mLeftCroppedImagePath = null;
            alertError.showDialog(this, getString(R.string.error_title),
                    getString(R.string.file_error_hint));
        }
        if (!createVerifyFiles(mRightCroppedImagePath)) {
            mRightCroppedImagePath = null;
            alertError.showDialog(this, getString(R.string.error_title),
                    getString(R.string.file_error_hint));
        }
    }

    private boolean createVerifyFiles(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public String getImagePath(Context context, Uri uri) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};

        try {
            if (uri.getScheme().equals("file")) {
                return uri.getPath();
            } else {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
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

    @Override
    protected void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

}
