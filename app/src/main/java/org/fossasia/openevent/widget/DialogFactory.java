package org.fossasia.openevent.widget;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import org.fossasia.openevent.R;

public final class DialogFactory {

    public static AlertDialog alertDialog;

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null)
                .create();
        return alertDialog;
    }

    public static void dismissDialog(){
        if(alertDialog!=null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }
    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static Dialog createSimpleActionDialog(Context context,
                                                  @StringRes int titleResource,
                                                  @StringRes int messageResource,
                                                  DialogInterface.OnClickListener listener) {
        alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(titleResource))
                .setMessage(context.getString(messageResource))
                .setNeutralButton(R.string.dialog_action_ok, listener)
                .create();
        return alertDialog;
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null)
                .create();
        return alertDialog;
    }

    public static Dialog createGenericErrorDialog(Context context, @StringRes int messageResource) {
        return createGenericErrorDialog(context, context.getString(messageResource));
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static ProgressDialog createProgressDialog(Context context,
                                                      @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }
}
