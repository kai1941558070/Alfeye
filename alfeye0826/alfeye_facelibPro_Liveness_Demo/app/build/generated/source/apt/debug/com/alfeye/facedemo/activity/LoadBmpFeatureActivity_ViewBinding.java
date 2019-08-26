// Generated code from Butter Knife. Do not modify!
package com.alfeye.facedemo.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.alfeye.facedemo.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LoadBmpFeatureActivity_ViewBinding implements Unbinder {
  private LoadBmpFeatureActivity target;

  private View view2131165223;

  @UiThread
  public LoadBmpFeatureActivity_ViewBinding(LoadBmpFeatureActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public LoadBmpFeatureActivity_ViewBinding(final LoadBmpFeatureActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.btn_load_bmpfeature, "field 'btnLoadBmpfeature' and method 'onViewClicked'");
    target.btnLoadBmpfeature = Utils.castView(view, R.id.btn_load_bmpfeature, "field 'btnLoadBmpfeature'", Button.class);
    view2131165223 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
    target.tvLoadCount = Utils.findRequiredViewAsType(source, R.id.tv_load_count, "field 'tvLoadCount'", TextView.class);
    target.btnBack = Utils.findRequiredViewAsType(source, R.id.btn_back, "field 'btnBack'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    LoadBmpFeatureActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btnLoadBmpfeature = null;
    target.tvLoadCount = null;
    target.btnBack = null;

    view2131165223.setOnClickListener(null);
    view2131165223 = null;
  }
}
