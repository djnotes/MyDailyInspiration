package com.mehdi.memo;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Created by john on 7/15/17.
 */

public class DayTimeItemPreference extends Preference {
    //This preference will deal with individual day/time items.
    //We can have one or more items in our DayTimeActivity
    //and for each time item we can select week days when inspirations repeat

    public DayTimeItemPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
