package com.t3hh4xx0r.tweezee;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class DayPicker extends ListPreference {
	String[] values;
    public DayPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        String[] weekdays = new DateFormatSymbols().getWeekdays();
         values = new String[] {
            weekdays[Calendar.MONDAY],
            weekdays[Calendar.TUESDAY],
            weekdays[Calendar.WEDNESDAY],
            weekdays[Calendar.THURSDAY],
            weekdays[Calendar.FRIDAY],
            weekdays[Calendar.SATURDAY],
            weekdays[Calendar.SUNDAY],
        };
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            //mDaysOfWeek.set(mNewDaysOfWeek);
            //setSummary(mDaysOfWeek.toString(getContext(), true));
            //callChangeListener(mDaysOfWeek);
        } else {
            //mNewDaysOfWeek.set(mDaysOfWeek);
        }
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        boolean[] array = {false, false, false, false, false, false, false};
        
        builder.setMultiChoiceItems(
                values, array,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which,
                            boolean isChecked) {
                        //mNewDaysOfWeek.set(which, isChecked);
                    }
                });
    }
}