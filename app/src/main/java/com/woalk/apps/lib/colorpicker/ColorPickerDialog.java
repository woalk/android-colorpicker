/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.woalk.apps.lib.colorpicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A dialog which takes in as input an array of colors and creates a palette allowing the user to
 * select a specific color swatch, which invokes a listener.
 */
public class ColorPickerDialog extends DialogFragment implements ColorPickerSwatch.OnColorSelectedListener {

    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;

    protected AlertDialog mAlertDialog;

    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_TITLE = "title";
    protected static final String KEY_COLORS = "colors";
    protected static final String KEY_SELECTED_COLOR = "selected_color";
    protected static final String KEY_COLUMNS = "columns";
    protected static final String KEY_SIZE = "size";
    protected static final String KEY_CUSTOM_COLOR = "allow_custom";

    protected int mTitleResId = R.string.color_picker_default_title;
    protected String mTitle = null;
    protected int[] mColors = null;
    protected int mSelectedColor;
    protected int mColumns;
    protected int mSize;
    protected boolean mAllowCustomColor = false;

    private ColorPickerPalette mPalette;
    private ProgressBar mProgress;

    protected ColorPickerSwatch.OnColorSelectedListener mListener;

    /**
     * New instance of {@link ColorPickerDialog}.
     * Do not use the constructor outside of the library.
     * Use one of {@link #newInstance} instead.
     */
    public ColorPickerDialog() {
        // Empty constructor required for dialog fragments.
    }

    /**
     * Obtain a new instance of {@link ColorPickerDialog} to use it.
     * @param titleResId The resource id of the dialog title to show.
     * @param colors A color array, containing all colors that should be selectable in this dialog.
     * @param selectedColor The currently selected color (or the default color).
     * @param columns The number of columns of {@code ColorPickerSwatches} to use in the dialog.
     * @param size The dialog size. This should be one of {@code SIZE_LARGE}, {@code SIZE_SMALL}.
     * @return The {@link ColorPickerDialog} instance requested.
     */
    public static ColorPickerDialog newInstance(int titleResId, int[] colors, int selectedColor,
            int columns, int size) {
        ColorPickerDialog ret = new ColorPickerDialog();
        ret.initialize(titleResId, colors, selectedColor, columns, size);
        return ret;
    }

    public void initialize(int titleResId, int[] colors, int selectedColor, int columns, int size) {
        setArguments(titleResId, columns, size);
        setColors(colors, selectedColor);
    }

    public void setArguments(int titleResId, int columns, int size) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TITLE_ID, titleResId);
        bundle.putInt(KEY_COLUMNS, columns);
        bundle.putInt(KEY_SIZE, size);
        setArguments(bundle);
    }

    /**
     * Obtain a new instance of {@link ColorPickerDialog} to use it.
     * @param title The string to use as dialog title.
     * @param colors A color array, containing all colors that should be selectable in this dialog.
     * @param selectedColor The currently selected color (or the default color).
     * @param columns The number of columns of {@code ColorPickerSwatches} to use in the dialog.
     * @param size The dialog size. This should be one of {@code SIZE_LARGE}, {@code SIZE_SMALL}.
     * @return The {@link ColorPickerDialog} instance requested.
     */
    public static ColorPickerDialog newInstance(String title, int[] colors, int selectedColor,
                                                int columns, int size) {
        ColorPickerDialog ret = new ColorPickerDialog();
        ret.initialize(title, colors, selectedColor, columns, size);
        return ret;
    }

    public void initialize(String title, int[] colors, int selectedColor, int columns, int size) {
        setArguments(title, columns, size);
        setColors(colors, selectedColor);
    }

    public void setArguments(String title, int columns, int size) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE_ID, title);
        bundle.putInt(KEY_COLUMNS, columns);
        bundle.putInt(KEY_SIZE, size);
        setArguments(bundle);
    }

    /**
     * Obtain a new instance of {@link ColorPickerDialog} to use it.
     * @param title The resource id of the dialog title to show.
     * @param colors A color array, containing all colors that should be selectable in this dialog.
     * @param selectedColor The currently selected color (or the default color).
     * @param columns The number of columns of {@code ColorPickerSwatches} to use in the dialog.
     * @param size The dialog size. This should be one of {@code SIZE_LARGE}, {@code SIZE_SMALL}.
     * @param allowCustomColor Specify {@code true} to show an {@link EditText} to enter a color
     *                         hex code manually. The default is {@code false}.
     * @return The {@link ColorPickerDialog} instance requested.
     */
    public static ColorPickerDialog newInstance(String title, int[] colors, int selectedColor,
                                                int columns, int size, boolean allowCustomColor) {
        ColorPickerDialog ret = new ColorPickerDialog();
        ret.initialize(title, colors, selectedColor, columns, size, allowCustomColor);
        return ret;
    }

    public void initialize(String title, int[] colors, int selectedColor, int columns, int size,
                           boolean allowCustomColor) {
        setArguments(title, columns, size, allowCustomColor);
        setColors(colors, selectedColor);
    }

    public void setArguments(String title, int columns, int size, boolean allowCustomColor) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE_ID, title);
        bundle.putInt(KEY_COLUMNS, columns);
        bundle.putInt(KEY_SIZE, size);
        bundle.putBoolean(KEY_CUSTOM_COLOR, allowCustomColor);
        setArguments(bundle);
    }

    /**
     * Set the {@link OnColorSelectedListener}, called when the user selects a color in the dialog.
     * @param listener The listener to set.
     */
    public void setOnColorSelectedListener(ColorPickerSwatch.OnColorSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTitleResId = getArguments().getInt(KEY_TITLE, R.string.color_picker_default_title);
            mTitle = getArguments().getString(KEY_TITLE);
            mColumns = getArguments().getInt(KEY_COLUMNS);
            mSize = getArguments().getInt(KEY_SIZE);
            mAllowCustomColor = getArguments().getBoolean(KEY_CUSTOM_COLOR);
        }

        if (savedInstanceState != null) {
            mColors = savedInstanceState.getIntArray(KEY_COLORS);
            mSelectedColor = (Integer) savedInstanceState.getSerializable(KEY_SELECTED_COLOR);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.color_picker_dialog, null);
        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
        mPalette = (ColorPickerPalette) view.findViewById(R.id.color_picker);
        mPalette.init(mSize, mColumns, this);

        if (mColors != null) {
            showPaletteView();
        }

        mAlertDialog = new AlertDialog.Builder(activity)
                .setTitle(mTitle == null ? getText(mTitleResId) : mTitle)
                .setView(view)
                .create();

        EditText customColorField = (EditText) view.findViewById(android.R.id.edit);

        if (mAllowCustomColor) {
            customColorField.setVisibility(View.VISIBLE);
        }

        customColorField.setOnEditorActionListener(new TextView
                .OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 10 && event.getAction() == EditorInfo.IME_ACTION_DONE) {
                    if (!v.getText().toString().startsWith("#") && v.getText().toString()
                            .matches(".*\\d.*")) {
                        v.setText("#" + v.getText());
                    }
                    try {
                        onColorSelected(Color.parseColor(v.getText().toString()));
                    } catch (Throwable e) {
                        v.setTextColor(Color.RED);
                    }
                }
                return false;
            }
        });

        return mAlertDialog;
    }

    @Override
    public void onColorSelected(int color) {
        if (mListener != null) {
            mListener.onColorSelected(color);
        }

        if (getTargetFragment() instanceof ColorPickerSwatch.OnColorSelectedListener) {
            final ColorPickerSwatch.OnColorSelectedListener listener =
                    (ColorPickerSwatch.OnColorSelectedListener) getTargetFragment();
            listener.onColorSelected(color);
        }

        if (color != mSelectedColor) {
            mSelectedColor = color;
            // Redraw palette to show checkmark on newly selected color before dismissing.
            mPalette.drawPalette(mColors, mSelectedColor);
        }

        dismiss();
    }

    /**
     * Show the palette view and hide the {@link ProgressBar}.
     * Should be called when a loading operation completed.
     */
    public void showPaletteView() {
        if (mProgress != null && mPalette != null) {
            mProgress.setVisibility(View.GONE);
            refreshPalette();
            mPalette.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Show a spinning {@link ProgressBar} to indicate a loading condition.
     * Hide the {@code ProgressBar} again with {@link #showPaletteView()}.
     */
    public void showProgressBarView() {
        if (mProgress != null && mPalette != null) {
            mProgress.setVisibility(View.VISIBLE);
            mPalette.setVisibility(View.GONE);
        }
    }

    /**
     * Change the colors displayed in the dialog.
     * @param colors A color array, containing all colors that should be selectable in this dialog.
     * @param selectedColor The currently selected color (or the default color).
     */
    public void setColors(int[] colors, int selectedColor) {
        if (mColors != colors || mSelectedColor != selectedColor) {
            mColors = colors;
            mSelectedColor = selectedColor;
            refreshPalette();
        }
    }

    /**
     * Change the colors displayed in the dialog.
     * @param colors A color array, containing all colors that should be selectable in this dialog.
     */
    public void setColors(int[] colors) {
        if (mColors != colors) {
            mColors = colors;
            refreshPalette();
        }
    }

    /**
     * Change the currently selected color.
     * @param color The color int to set.
     */
    public void setSelectedColor(int color) {
        if (mSelectedColor != color) {
            mSelectedColor = color;
            refreshPalette();
        }
    }

    private void refreshPalette() {
        if (mPalette != null && mColors != null) {
            mPalette.drawPalette(mColors, mSelectedColor);
        }
    }

    /**
     * Get the colors displayed in this dialog.
     * @return A color array, containing all colors that are selectable in this dialog.
     */
    public int[] getColors() {
        return mColors;
    }

    /**
     * Get the currently selected color.
     * @return A color int representing the selected color.
     */
    public int getSelectedColor() {
        return mSelectedColor;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(KEY_COLORS, mColors);
        outState.putSerializable(KEY_SELECTED_COLOR, mSelectedColor);
    }
}
