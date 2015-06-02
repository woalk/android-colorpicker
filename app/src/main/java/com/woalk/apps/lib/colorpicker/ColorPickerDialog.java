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
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.woalk.apps.lib.colorpicker.ColorPickerSwatch.OnColorSelectedListener;

import java.util.HashMap;
import java.util.Locale;

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
        bundle.putString(KEY_TITLE, title);
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

        View customColorContainer = view.findViewById(android.R.id.custom);
        final EditText customColorField = (EditText) view.findViewById(android.R.id.edit);
        Button customColorOKButton = (Button) view.findViewById(android.R.id.button1);

        if (mAllowCustomColor) {
            customColorContainer.setVisibility(View.VISIBLE);
        }

        customColorOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = customColorField.getText().toString();
                if (text.equals("")) {
                    dismiss();
                    return;
                } else if (!text.startsWith("#") && text.matches("^[0-9A-Fa-f]+$")) {
                    text = "#" + customColorField.getText();
                    customColorField.setText(text);
                }
                try {
                    onColorSelected(parseColor(text));
                } catch (Throwable e) {
                    customColorField.setTextColor(Color.RED);
                }
            }
        });
        customColorField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                customColorField.setTextColor(getResources().getColor(android.R.color
                        .primary_text_light));
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

    /**
     * <i>A modification of {@link Color#parseColor(String)}.</i>
     * <br/><br/>
     * Parse the color string into a usable color int.
     * <br/><br/>
     * <b>Supported formats are:</b>
     * <ul>
     * <li>{@code #RGB}</li>
     * <li>{@code #ARGB}</li>
     * <li>{@code #RRGGBB}</li>
     * <li>{@code #AARRGGBB}</li>
     * <li>One of the color names
     * 'red', 'blue', 'green', 'black', 'white', 'gray', 'cyan', 'magenta',
     * 'yellow', 'lightgray', 'darkgray', 'grey', 'lightgrey', 'darkgrey',
     * 'aqua', 'fuchsia', 'lime', 'maroon', 'navy', 'olive', 'purple',
     * 'silver', 'teal'</li>
     *
     * @param colorString The color string in one of the above formats.
     * @return The requested color int {@code (0xAARRGGBB)}.
     * @throws IllegalArgumentException When the color string provided is invalid.
     */
    public static int parseColor(String colorString) throws IllegalArgumentException {
        if (colorString.charAt(0) == '#') {
            String colorStr = colorString.substring(1);
            String s_a; String s_r; String s_g; String s_b;
            int a; int r; int g; int b;
            switch (colorStr.length()) {
                case 3:
                    s_r = colorStr.substring(0, 1); // each digit is one color (#RGB)
                    s_g = colorStr.substring(1, 2);
                    s_b = colorStr.substring(2, 3);
                    a = 0xff; // fixed alpha full 255 (opaque)
                    r = Integer.parseInt(s_r + s_r, 16); // each digit represents the number twice
                    g = Integer.parseInt(s_g + s_g, 16); // (#123 == #112233)
                    b = Integer.parseInt(s_b + s_b, 16);
                    break;
                case 4:
                    s_a = colorStr.substring(0, 1); // each digit is one color or alpha (#ARGB)
                    s_r = colorStr.substring(1, 2);
                    s_g = colorStr.substring(2, 3);
                    s_b = colorStr.substring(3, 4);
                    a = Integer.parseInt(s_a + s_a, 16); // each digit represents the number twice
                    r = Integer.parseInt(s_r + s_r, 16); // (#1234 == #11223344)
                    g = Integer.parseInt(s_g + s_g, 16);
                    b = Integer.parseInt(s_b + s_b, 16);
                    break;
                case 6:
                    s_r = colorStr.substring(0, 2); // full color notation without alpha (#RRGGBB)
                    s_g = colorStr.substring(2, 4);
                    s_b = colorStr.substring(4, 6);
                    a = 0xff; // fixed alpha full 255 (opaque)
                    r = Integer.parseInt(s_r, 16);
                    g = Integer.parseInt(s_g, 16);
                    b = Integer.parseInt(s_b, 16);
                    break;
                case 8:
                    s_a = colorStr.substring(0, 2); // full color notation with alpha (#AARRGGBB)
                    s_r = colorStr.substring(2, 4);
                    s_g = colorStr.substring(4, 6);
                    s_b = colorStr.substring(6, 8);
                    a = Integer.parseInt(s_a, 16);
                    r = Integer.parseInt(s_r, 16);
                    g = Integer.parseInt(s_g, 16);
                    b = Integer.parseInt(s_b, 16);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown color");
            }
            return Color.argb(a, r, g, b); // Return color int of a, r, g, b (last parse by system)
        } else {
            Integer color = sColorNameMap.get(colorString.toLowerCase(Locale.ROOT));
            if (color != null) {
                return color;
            }
        }
        throw new IllegalArgumentException("Unknown color");
    }

    /** A color name list for possible parse-by-name in {@link #parseColor(String)}. */
    private static final HashMap<String, Integer> sColorNameMap;
    static {
        sColorNameMap = new HashMap<String, Integer>();
        sColorNameMap.put("black", Color.BLACK);
        sColorNameMap.put("darkgray", Color.DKGRAY);
        sColorNameMap.put("gray", Color.GRAY);
        sColorNameMap.put("lightgray", Color.LTGRAY);
        sColorNameMap.put("white", Color.WHITE);
        sColorNameMap.put("red", Color.RED);
        sColorNameMap.put("green", Color.GREEN);
        sColorNameMap.put("blue", Color.BLUE);
        sColorNameMap.put("yellow", Color.YELLOW);
        sColorNameMap.put("cyan", Color.CYAN);
        sColorNameMap.put("magenta", Color.MAGENTA);
        sColorNameMap.put("aqua", 0xFF00FFFF);
        sColorNameMap.put("fuchsia", 0xFFFF00FF);
        sColorNameMap.put("darkgrey", Color.DKGRAY);
        sColorNameMap.put("grey", Color.GRAY);
        sColorNameMap.put("lightgrey", Color.LTGRAY);
        sColorNameMap.put("lime", 0xFF00FF00);
        sColorNameMap.put("maroon", 0xFF800000);
        sColorNameMap.put("navy", 0xFF000080);
        sColorNameMap.put("olive", 0xFF808000);
        sColorNameMap.put("purple", 0xFF800080);
        sColorNameMap.put("silver", 0xFFC0C0C0);
        sColorNameMap.put("teal", 0xFF008080);

    }
}
