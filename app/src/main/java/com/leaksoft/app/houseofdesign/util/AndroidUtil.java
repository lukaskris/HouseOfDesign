package com.leaksoft.app.houseofdesign.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Lukaskris on 25/08/2017.
 */

public class AndroidUtil {
    public static Snackbar showMessage(View container, String message) {
        if (container == null) return null;

        Snackbar snackbar = Snackbar.make(container, message, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // do nothing
                    }
                });
        snackbar.show();

        return snackbar;
    }

    public static ProgressDialog showProgressDialog(Context context, String title, String message) {
        return ProgressDialog.show(context, title, message, true, false);
    }

}
