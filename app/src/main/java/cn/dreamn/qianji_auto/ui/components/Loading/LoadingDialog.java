package cn.dreamn.qianji_auto.ui.components.Loading;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.dreamn.qianji_auto.R;

public class LoadingDialog {
    LVCircularRing mLoadingView;
    Dialog mLoadingDialog;

    public LoadingDialog(Context context, String msg) {
        // 首先得到整个View
        View view = LayoutInflater.from(context).inflate(
                R.layout.components_loading_dialog_view, null);
        // 获取整个布局
        LinearLayout layout = view.findViewById(R.id.dialog_view);
        // 页面中的LoadingView
        mLoadingView = view.findViewById(R.id.lv_circularring);
        // 页面中显示文本
        TextView loadingText = view.findViewById(R.id.loading_text);
        // 显示文本
        loadingText.setText(msg);
        // 创建自定义样式的Dialog
        mLoadingDialog = new Dialog(context, R.style.loading_dialog);
        // 设置返回键无效
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    public void show() {

        mLoadingDialog.show();
        mLoadingView.startAnim();
    }

    public void close() {
        if (mLoadingDialog != null) {
            mLoadingView.stopAnim();
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }
}
