package org.fossasia.openevent.fragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.CommentsListAdapter;
import org.fossasia.openevent.data.facebook.CommentItem;
import org.fossasia.openevent.utils.ConstantStrings;

import java.util.List;

/**
 * Created by rohanagarwal94 on 30/7/16.
 */
public class CommentsDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View root = inflater.inflate(R.layout.list_comment, container, false);
        getDialog().getWindow().getAttributes().alpha = 0.8f;
        List<CommentItem> commentItems = this.getArguments().getParcelableArrayList(ConstantStrings.FACEBOOK_COMMENTS);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.comment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CommentsListAdapter commentsAdapter = new CommentsListAdapter(commentItems);
        recyclerView.setAdapter(commentsAdapter);
        return root;
    }
}
