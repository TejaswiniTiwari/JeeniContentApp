package com.jeeni.content.classic.volly;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ProgressBar;

import com.jeeni.content.classic.R;


/**
 * Represents an custom dialog.
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */

public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(Context context, String Message,
                                boolean isCancelable) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_progress_dialog);
        setCancelable(isCancelable);
        ProgressBar pBar = (ProgressBar) findViewById(R.id.dialogProgressBar);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

}
