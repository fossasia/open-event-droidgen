package org.fossasia.openevent.data.facebook;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rohanagarwal94 on 11/6/17.
 */
public class CommentItem implements Parcelable {

    @JsonProperty("created_time")
    private String createdTime;
    private Commenter from;
    private String message;
    private String id;

    public CommentItem() {}

    protected CommentItem(Parcel in) {
        createdTime = in.readString();
        message = in.readString();
        id = in.readString();
    }

    public static final Creator<CommentItem> CREATOR = new Creator<CommentItem>() {
        @Override
        public CommentItem createFromParcel(Parcel in) {
            return new CommentItem(in);
        }

        @Override
        public CommentItem[] newArray(int size) {
            return new CommentItem[size];
        }
    };

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public Commenter getFrom() {
        return from;
    }

    public void setFrom(Commenter from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(createdTime);
        dest.writeString(message);
        dest.writeString(id);
    }
}