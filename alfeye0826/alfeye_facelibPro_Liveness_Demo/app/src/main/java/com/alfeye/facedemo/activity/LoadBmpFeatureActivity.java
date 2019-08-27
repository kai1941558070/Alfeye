package com.alfeye.facedemo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alfeye.facedemo.R;
import com.alfeye.facedemo.RecyclerViewOnClickListener;
import com.alfeye.facedemo.adapter.AllPhotoRecyclerViewAdapter;
import com.facelib.LicenseST.DialogUtil;
import com.facelib.entity.UserFeature;
import com.facelib.faceRecog.Face1NRecogManager;
import com.facelib.faceRecog.OnLoadFeatureListener;
import com.facelib.featureStorage.GetBmpFeatureManager;
import com.facelib.featureStorage.OnGetBmpFeatureListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadBmpFeatureActivity extends AppCompatActivity {
    // 图片库 路径
    public static String PATH_BMP_FEATURE = Environment.getExternalStorageDirectory() + File.separator + "A1";
    public static List<String> imageList = new ArrayList<>();

    private AllPhotoRecyclerViewAdapter adapter;

    @BindView(R.id.btn_load_bmpfeature)
    Button btnLoadBmpfeature;
    @BindView(R.id.tv_load_count)
    TextView tvLoadCount;
    @BindView(R.id.btn_back)
    Button btnBack;
    //初始化控件
    @BindView(R.id.mRecyclerView_allPhoto)
    RecyclerView mRecyclerViewAllPhoto;

    private AlertDialog errorDialog;

    private GetBmpFeatureManager getBmpFeatureManager;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1:
                    initAdapter(imageList);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_bmp_feature);
        ButterKnife.bind(this);
        imageList.clear();
        requestAllPermissionsIfNeed();

        GridLayoutManager manager = new GridLayoutManager(LoadBmpFeatureActivity.this, 4, OrientationHelper.VERTICAL, false);
        mRecyclerViewAllPhoto.setLayoutManager(manager);
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
                String imagePath = PATH_BMP_FEATURE + "/" + s1;
                imageList.add(imagePath);
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
                handler.sendEmptyMessage(1);
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
        imageList.clear();
        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.dismiss();
        }
    }

    @OnClick(R.id.btn_load_bmpfeature)
    public void onViewClicked() {
        Toast.makeText(this, "开始获取人脸图片特征", Toast.LENGTH_SHORT).show();
        imageList.clear();
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

    private void initAdapter(List<String> images) {
        adapter = new AllPhotoRecyclerViewAdapter(LoadBmpFeatureActivity.this, images);
        mRecyclerViewAllPhoto.setAdapter(adapter);
        adapter.setListener(new RecyclerViewOnClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(LoadBmpFeatureActivity.this, "点击了:" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(int position) {
                deleteItemData(position);
            }
        });
    }

    private void deleteItemData(final int position) {

        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(LoadBmpFeatureActivity.this)
                .setTitle("确定要删除这张图片吗？")
                .setNeutralButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File file=new File(imageList.get(position));
                        if (file.isFile()&&file.exists()){
                            boolean delete = file.delete();
                            getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA + "=?",new String[]{imageList.get(position)});
                            if (delete) {
                                Toast.makeText(LoadBmpFeatureActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                imageList.remove(position);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(LoadBmpFeatureActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        dialog.show();
    }

}
