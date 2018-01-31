package org.fossasia.openevent.core.feed.facebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.core.feed.facebook.api.CommentItem;

import java.util.List;

public class CommentsDialogFragment extends DialogFragment {

    // TODO: Move implementation from MainActivity to respective fragment

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View root = inflater.inflate(R.layout.list_comment, container, false);
        List<CommentItem> commentItems = this.getArguments().getParcelableArrayList(ConstantStrings.FACEBOOK_COMMENTS);
        RecyclerView recyclerView = root.findViewById(R.id.comment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CommentsListAdapter commentsAdapter = new CommentsListAdapter(commentItems);
        recyclerView.setAdapter(commentsAdapter);
        return root;
    }
}
