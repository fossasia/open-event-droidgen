package org.fossasia.openevent.config.strategies;

import android.content.Context;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.config.ConfigStrategy;

/**
 * Configures Date Time library and timezone information of the project
 */
public class TimeConfigStrategy implements ConfigStrategy {

    @Override
    public boolean configure(Context context) {
        AndroidThreeTen.init(context);
        DateConverter.setShowLocalTime(SharedPreferencesUtil.getBoolean(context.getResources()
                .getString(R.string.timezone_mode_key), false));
        return false;
    }

}
