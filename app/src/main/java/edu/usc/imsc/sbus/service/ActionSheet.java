package edu.usc.imsc.sbus.service;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.usc.imsc.sbus.R;

/**
 * Created by Mengjia on 16/1/2.
 */
public class ActionSheet {
    public interface OnActionSheetSelected {
        void onClick(int whichButton);
    }

    private ActionSheet(){};

    public static Dialog showSheet(Context context, final OnActionSheetSelected actionSheetSelected, DialogInterface.OnCancelListener cancelListener){
        final Dialog dlg = new Dialog(context,R.style.ActionSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.actionsheet, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);

        TextView mContent = (TextView)layout.findViewById(R.id.content);
        TextView mCancel = (TextView)layout.findViewById(R.id.cancel);

        mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSheetSelected.onClick(0);
                dlg.dismiss();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSheetSelected.onClick(1);
                dlg.dismiss();
            }
        });

        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(false);
        if(cancelListener!=null)
            dlg.setOnCancelListener(cancelListener);
        dlg.setContentView(layout);
        dlg.show();

        return dlg;

    }
}
