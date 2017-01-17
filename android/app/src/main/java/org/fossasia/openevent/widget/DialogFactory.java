package org.fossasia.openevent.widget;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import org.fossasia.openevent.R;

public final class DialogFactory {

    public static AlertDialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.mipmap.ic_launcher)
                .setNeutralButton(context.getString(android.R.string.ok).toUpperCase(), null);
        return alertDialog.create();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(titleResource))
                .setMessage(context.getString(messageResource))
                .setNeutralButton(context.getString(android.R.string.ok).toUpperCase(), listener);
        return alertDialog.create();
    }

    public static Dialog createDownloadDialog(Context context,
                                                  @StringRes int titleResource,
                                                  @StringRes int messageResource,
                                                  DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(titleResource))
                .setMessage(context.getString(messageResource))
                .setIcon(R.drawable.ic_file_download_black_24dp)
                .setNegativeButton(context.getString(R.string.no), listener)
                .setPositiveButton(context.getString(R.string.yes), listener);
        return alertDialog.create();
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setNeutralButton(context.getString(android.R.string.ok).toUpperCase(), null);
        return alertDialog.create();
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
