package com.woalk.apps.lib.colorpicker;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * A preference showing a {@link ColorPickerDialog} to allow the user to select a color to save as
 * {@link Preference}.
 */
public class ColorPreference extends Preference implements ColorPickerSwatch
                                                                   .OnColorSelectedListener {

    private static final int DEFAULT_VALUE = Color.BLACK;

    private String mTitle;
    private int mCurrentValue;
    private int[] mColors;
    private int mColumns;
    private boolean mAllowCustomColor;

    private View mColorView;

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .ColorPreference, 0, 0);
        try {
            int id = a.getResourceId(R.styleable.ColorPreference_picker_colors, 0);
            if (id != 0) {
                mColors = getContext().getResources().getIntArray(id);
            }
            id = a.getResourceId(R.styleable.ColorPreference_picker_dialogTitle, 0);
            if (id != 0) {
                mTitle = getContext().getResources().getString(id);
            } else { // use string
                mTitle = a.getString(R.styleable.ColorPreference_picker_dialogTitle);
                if (mTitle == null) {
                    mTitle = getContext().getResources().getString(R.string
                            .color_picker_default_title);
                }
            }
            mColumns = a.getInt(R.styleable.ColorPreference_picker_columns, 2);
            mAllowCustomColor = a.getBoolean(R.styleable.ColorPreference_picker_allowCustomColor, false);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View s = super.onCreateView(parent);
        mColorView = new View(getContext());
        int size = (int) dpToPx(48);
        mColorView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mColorView.setBackground(new ShapeDrawable(new OvalShape()));
            ((ShapeDrawable) mColorView.getBackground()).getPaint().setColor(mCurrentValue);
        } else {
            mColorView.setBackgroundColor(mCurrentValue);
        }
        ViewGroup w = (ViewGroup) s.findViewById(android.R.id.widget_frame);
        w.setVisibility(View.VISIBLE);
        w.addView(mColorView);
        return s;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mCurrentValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    protected void onClick() {
        int[] colors = mColors.length != 0 ? mColors : new int[]{Color.BLACK, Color.WHITE, Color
                .RED, Color.GREEN, Color.BLUE};
        ColorPickerDialog d = ColorPickerDialog.newInstance(mTitle, colors, mCurrentValue, mColumns,
                ColorPickerDialog.SIZE_SMALL, mAllowCustomColor);
        d.setOnColorSelectedListener(this);
        d.show(((Activity) getContext()).getFragmentManager(), null);
    }

    @Override
    public void onColorSelected(int color) {
        persistInt(color);
        mCurrentValue = color;

        // Update shown color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((ShapeDrawable) mColorView.getBackground()).getPaint().setColor(mCurrentValue);
            mColorView.invalidate();
        } else {
            mColorView.setBackgroundColor(mCurrentValue);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent,
            // use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current
        // setting value
        myState.current = mCurrentValue;
        myState.colors = mColors;
        myState.columns = mColumns;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Update own values
        mCurrentValue = myState.current;
        mColors = myState.colors;
        mColumns = myState.columns;

        // Update shown color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mColorView.setBackground(new ShapeDrawable(new OvalShape()));
            ((ShapeDrawable) mColorView.getBackground()).getPaint().setColor(mCurrentValue);
        } else {
            mColorView.setBackgroundColor(mCurrentValue);
        }

        // Set this Preference's widget to reflect the restored state
        //mNumberPicker.setValue(myState.value);
    }

    private static class SavedState extends BaseSavedState {
        // Member that holds the preference's values
        int current;
        int[] colors;
        int columns;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's values
            current = source.readInt();
            source.readIntArray(colors);
            columns = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's values
            dest.writeInt(current);
            dest.writeIntArray(colors);
            dest.writeInt(columns);
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    /**
     * Convert a dp size to pixel.
     * Useful for specifying view sizes in code.
     * @param dp The size in density-independent pixels.
     * @return {@code px} - The size in generic pixels (density-dependent).
     */
    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }
}
