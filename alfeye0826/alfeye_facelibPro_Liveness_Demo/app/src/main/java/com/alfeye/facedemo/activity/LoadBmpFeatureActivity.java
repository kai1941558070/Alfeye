package com.alfeye.facedemo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alfeye.facedemo.R;
import com.facelib.LicenseST.DialogUtil;
import com.facelib.entity.UserFeature;
import com.facelib.faceRecog.Face1NRecogManager;
import com.facelib.faceRecog.OnLoadFeatureListener;
import com.facelib.featureStorage.GetBmpFeatureManager;
import com.facelib.featureStorage.OnGetBmpFeatureListener;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadBmpFeatureActivity extends AppCompatActivity {
    // 图片库 路径
    public static String PATH_BMP_FEATURE = Environment.getExternalStorageDirectory() + File.separator + "A1";

    @BindView(R.id.btn_load_bmpfeature)
    Button btnLoadBmpfeature;
    @BindView(R.id.tv_load_count)
    TextView tvLoadCount;
    @BindView(R.id.btn_back)
    Button btnBack;

    private AlertDialog errorDialog;

    private GetBmpFeatureManager getBmpFeatureManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_bmp_feature);
        ButterKnife.bind(this);

        requestAllPermissionsIfNeed();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //加载图像库 人脸特征
        getBmpFeatureManager = new GetBmpFeatureManager(new OnGetBmpFeatureListener() {
            @Override
            public void onHookInsertDB(UserFeature userFeature) {
                userFeature.setCardNum("1234");
            }

            @Override
            public void onGetBmpFeatureLoading(String s, String s1, int i) {

                tvLoadCount.setText("加载图片: " + s1 + "  加载个数：" + i);
            }

            @Override
            public void onGetBmpFeatureLoaded(String lable, List<String> list) {

                //list 获取特征失败的图片
                tvLoadCount.append("\n获取特征失败：" + list.size() + "\n");
                for (String fileName : list) {
                    tvLoadCount.append(fileName);
                }

                //加载 人脸库信息， 每次更新人脸库 只需要调用一次
                Face1NRecogManager.ins().updateFeatureInfos(lable, new OnLoadFeatureListener() {
                    @Override
                    public void onLoaded(int i) {
                        Log.d("", "加载特征 count: " + i);
                    }
                });

                Toast.makeText(LoadBmpFeatureActivity.this, "加载图片库完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGetBmpFeatureError(String s, int i, String s1) {
                Toast.makeText(LoadBmpFeatureActivity.this, s1, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getBmpFeatureManager.destroy();

        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.dismiss();
        }
    }

    @OnClick(R.id.btn_load_bmpfeature)
    public void onViewClicked() {
        Toast.makeText(this, "开始获取人脸图片特征", Toast.LENGTH_SHORT).show();

        getBmpFeatureManager.getAllBmpFaceFeature(PATH_BMP_FEATURE);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void requestAllPermissionsIfNeed() {
        // 申请权限
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                errorDialog = DialogUtil.createAlertDialog(this, getResources().getString(R.string.error_title), "请打开存储权限", null);
                errorDialog.show();

            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

}
