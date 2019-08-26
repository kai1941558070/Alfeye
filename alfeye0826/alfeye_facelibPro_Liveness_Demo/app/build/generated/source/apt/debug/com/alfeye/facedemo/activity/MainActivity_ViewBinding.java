// Generated code from Butter Knife. Do not modify!
package com.alfeye.facedemo.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.alfeye.facedemo.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  private View view2131165227;

  private View view2131165221;

  private View view2131165223;

  private View view2131165222;

  private View view2131165226;

  private View view2131165225;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(final MainActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.btn_verify_activity, "field 'btnVerifyActivity' and method 'onViewClicked'");
    target.btnVerifyActivity = Utils.castView(view, R.id.btn_verify_activity, "field 'btnVerifyActivity'", Button.class);
    view2131165227 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_face11_recog, "field 'btnFace11Recog' and method 'onViewClicked'");
    target.btnFace11Recog = Utils.castView(view, R.id.btn_face11_recog, "field 'btnFace11Recog'", Button.class);
    view2131165221 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_load_bmpfeature, "field 'btnLoadBmpfeature' and method 'onViewClicked'");
    target.btnLoadBmpfeature = Utils.castView(view, R.id.btn_load_bmpfeature, "field 'btnLoadBmpfeature'", Button.class);
    view2131165223 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_face1n_recog, "field 'btnFace1nRecog' and method 'onViewClicked'");
    target.btnFace1nRecog = Utils.castView(view, R.id.btn_face1n_recog, "field 'btnFace1nRecog'", Button.class);
    view2131165222 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_track_face, "field 'btnTrackFace' and method 'onViewClicked'");
    target.btnTrackFace = Utils.castView(view, R.id.btn_track_face, "field 'btnTrackFace'", Button.class);
    view2131165226 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_takePhoto, "field 'btnTakePhoto' and method 'onViewClicked'");
    target.btnTakePhoto = Utils.castView(view, R.id.btn_takePhoto, "field 'btnTakePhoto'", Button.class);
    view2131165225 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.ivShowImage = Utils.findRequiredViewAsType(source, R.id.iv_showImage, "field 'ivShowImage'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btnVerifyActivity = null;
    target.btnFace11Recog = null;
    target.btnLoadBmpfeature = null;
    target.btnFace1nRecog = null;
    target.btnTrackFace = null;
    target.btnTakePhoto = null;
    target.ivShowImage = null;

    view2131165227.setOnClickListener(null);
    view2131165227 = null;
    view2131165221.setOnClickListener(null);
    view2131165221 = null;
    view2131165223.setOnClickListener(null);
    view2131165223 = null;
    view2131165222.setOnClickListener(null);
    view2131165222 = null;
    view2131165226.setOnClickListener(null);
    view2131165226 = null;
    view2131165225.setOnClickListener(null);
    view2131165225 = null;
  }
}
