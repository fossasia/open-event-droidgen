package org.fossasia.openevent.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * User: opticod(Anupam Das)
 * Date: 27/1/16.
 */
public class ViewHolder {

    /**
     * to handle click listener
     */
    public interface SetOnClickListener {
        void onItemClick(int position, View itemView);
    }

    /**
     * generic ViewHolder
     */
    public static class Viewholder extends RecyclerView.ViewHolder {
        SetOnClickListener listener;
        private TextView txtView1;
        private TextView txtView2;
        private TextView txtView3;
        private TextView txtView4;
        private TextView txtView5;
        private TextView txtView6;
        private TextView txtView7;
        private TextView txtView8;
        private ImageView imgView1;
        private View view1;
        private View view2;

        public Viewholder(final View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition(), itemView);
                    }
                }
            });
        }

        public TextView getTxtView1() {
            return txtView1;
        }

        public void setTxtView1(TextView txtView1) {
            this.txtView1 = txtView1;
        }

        public SetOnClickListener getListener() {
            return listener;
        }

        public void setListener(SetOnClickListener listener) {
            this.listener = listener;
        }

        public TextView getTxtView2() {
            return txtView2;
        }

        public TextView getTxtView4() {
            return txtView4;
        }

        public void setTxtView2(TextView txtView2) {
            this.txtView2 = txtView2;
        }

        public ImageView getImgView1() {
            return imgView1;
        }

        public void setImgView1(ImageView imgView1) {
            this.imgView1 = imgView1;
        }

        public View getView2() {
            return view2;
        }

        public void setView2(View view2) {
            this.view2 = view2;
        }

        public View getView1() {

            return view1;
        }

        public void setView1(View view1) {
            this.view1 = view1;
        }

        public void setItemClickListener(SetOnClickListener itemClickListener) {
            this.listener = itemClickListener;
        }

        public TextView getTxtView3() {
            return txtView3;
        }

        public void setTxtView3(TextView txtView3) {
            this.txtView3 = txtView3;
        }

        public void setTxtView4(TextView txtView4) {
            this.txtView4 = txtView4;
        }

        public TextView getTxtView5() {
            return txtView5;
        }

        public void setTxtView5(TextView txtView5) {
            this.txtView5 = txtView5;
        }

        public TextView getTxtView6() {
            return txtView6;
        }

        public void setTxtView6(TextView txtView6) {
            this.txtView6 = txtView6;
        }

        public TextView getTxtView7() {
            return txtView7;
        }

        public void setTxtView7(TextView txtView7) {
            this.txtView7 = txtView7;
        }

        public TextView getTxtView8() {
            return txtView8;
        }

        public void setTxtView8(TextView txtView8) {
            this.txtView8 = txtView8;
        }
    }
}

