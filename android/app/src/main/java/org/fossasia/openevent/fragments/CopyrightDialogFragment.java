package org.fossasia.openevent.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.extras.Copyright;
import org.w3c.dom.Text;


public class CopyrightDialogFragment extends DialogFragment{

    private Copyright copyright;

    public static CopyrightDialogFragment newInstance(Copyright copyright){
        CopyrightDialogFragment frag = new CopyrightDialogFragment();
        frag.setCopyrightVariable(copyright);
        return frag;


    }
    private void setCopyrightVariable(Copyright copyright){

        this.copyright=copyright;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.copyright_dialog, null))
                // Add action buttons
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        dialog.cancel();
                    }
                });
        View rootView = inflater.inflate(R.layout.copyright_dialog,null);
        builder.setView(rootView);
        TextView licence = (TextView) rootView.findViewById(R.id.license);
        licence.setText(copyright.getLicence()+" "+String.valueOf(copyright.getYear()));
        TextView holder = (TextView) rootView.findViewById(R.id.holder_textview);
        holder.setText(copyright.getHolder());
        TextView licenceurl = (TextView) rootView.findViewById(R.id.licence_url);

        String linkedurl=String.format("<a href=\"%s\">"+copyright.getLicenceUrl()+"</a> ", copyright.getLicenceUrl());
        licenceurl.setText(Html.fromHtml(linkedurl));
        licenceurl.setMovementMethod(LinkMovementMethod.getInstance());
        //TextView year= (TextView) rootView.findViewById(R.id.year);
        //year.setText("Year: "+String.valueOf(copyright.getYear()));
        return builder.create();
    }

}
