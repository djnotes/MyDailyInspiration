package com.mehdi.memo;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by john on 7/13/17.
 */

public class DayTimePreference extends  Preference {


    public DayTimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        super.onClick();
        Intent intent=new Intent(getContext(),DayTimeActivity.class);
        getContext().startActivity(intent);

    }


}
