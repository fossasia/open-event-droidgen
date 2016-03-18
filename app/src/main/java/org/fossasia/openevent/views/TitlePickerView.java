package org.fossasia.openevent.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.fossasia.openevent.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duncanleo on 25/2/16.
 */
public class TitlePickerView extends RelativeLayout {
    private ImageView leftButton, rightButton;
    private TextView titleTextView;
    private List<String> titles;
    private int curIndex = 0;
    private TitlePickerViewListener listener;

    public TitlePickerView(Context context) {
        super(context);
        init(context);
    }

    public TitlePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitlePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TitlePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.title_picker, this);
        leftButton = (ImageView) findViewById(R.id.title_picker_left_button);
        rightButton = (ImageView) findViewById(R.id.title_picker_right_button);
        titleTextView = (TextView) findViewById(R.id.title_picker_title);

        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getAlpha() != 1f) {
                    return;
                }
                handleChange(curIndex - 1);
            }
        });

        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getAlpha() != 1f) {
                    return;
                }
                handleChange(curIndex + 1);
            }
        });

        setBackgroundColor(getResources().getColor(R.color.title_picker_bg));
    }

    public void setupWithViewPager(final ViewPager viewPager) {
        titles = new ArrayList<>();
        for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
            titles.add(viewPager.getAdapter().getPageTitle(i).toString());
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                handleChange(position, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        listener = new TitlePickerViewListener() {
            @Override
            public void onSelected(int index) {
                viewPager.setCurrentItem(index);
            }
        };
        handleChange(0);
    }

    /**
     * Handle a change in the selection and update the listener if any.
     * @param change amount of change. Can be -1, 0 or +1.
     */
    private void handleChange(int change) {
        handleChange(change, true);
    }

    /**
     * Handle a change in the selection
     * @param index new index
     * @param updateListener whether to update the listener. for ViewPager use.
     */
    private void handleChange(int index, boolean updateListener) {
        if (titles == null) {
            return;
        }
        curIndex = index;

        //Reset alphas
        leftButton.setAlpha(1f);
        rightButton.setAlpha(1f);

        //Check index to disable buttons
        if (curIndex == 0) {
            //First item, disable left
            leftButton.setAlpha(0.5f);
        } else if (curIndex == titles.size() - 1) {
            //Last item, disable right
            rightButton.setAlpha(0.5f);
        }
        //Set text
        titleTextView.setText(titles.get(curIndex));

        //Call listener if any
        if (listener != null && updateListener) {
            listener.onSelected(curIndex);
        }
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
        handleChange(0);
    }

    //Getters & Setters

    public void setListener(TitlePickerViewListener listener) {
        this.listener = listener;
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public interface TitlePickerViewListener {
        /**
         * Triggers when an the picker is changed and an item is selected
         * @param index index of the item
         */
        void onSelected(int index);
    }
}
