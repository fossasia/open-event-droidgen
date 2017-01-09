package org.fossasia.openevent.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import org.fossasia.openevent.R;

/**
 * Created by rohanagarwal94 on 8/1/17.
 */
public class ShareAppUtils {
    private ShareAppUtils() {
    }
    public static void shareApp(Context context, boolean shareViaWhatsApp) {
        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            if (shareViaWhatsApp) {
                shareIntent.setPackage("com.whatsapp");
            }
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    String.format(context.getString(R.string.whatsapp_promo_msg_template),
                            PrefSettings.getInstance().getShareUrl()));
            context.startActivity(shareIntent);
        }
        catch (Exception e) {
            Log.e("Exception", String.valueOf(e));
            String msg = shareViaWhatsApp ? context.getString(R.string.error_msg_whatsapp_share)
                    : context.getString(R.string.error_msg_retry);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void inviteViaFacebook(final Activity activity, final CallbackManager sCallbackManager) {
        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(activity.getString(R.string.app_link_url))
                    .setPreviewImageUrl(activity.getString(R.string.share_fb_preview_url))
                    .build();
            AppInviteDialog appInviteDialog = new AppInviteDialog(activity);
            appInviteDialog.registerCallback(sCallbackManager, new FacebookCallback<AppInviteDialog.Result>() {
                @Override
                public void onSuccess(AppInviteDialog.Result result) {
                    Log.i("result", result.toString());
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException e) {
                    Log.i("on error", e.getLocalizedMessage());
                }
            });
            AppInviteDialog.show(activity, content);
        }
    }
}
