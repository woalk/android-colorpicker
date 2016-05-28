#Android Stock Color Picker Library [![Build Status](https://travis-ci.org/woalk/android-colorpicker.svg?branch=woalk%2Fmaster)](https://travis-ci.org/woalk/android-colorpicker)
## This is an original color picker written by Google/Android.
Cloned from original source
at https://android.googlesource.com/platform/frameworks/opt/colorpicker/
from the branch `android-5.1.1_r2`, which does not exist anymore.

A little bit adjusted for better development
(imported into Android Studio, updated to newest build tools, etc).

###How does it look like?
![scr1](http://ext.woalk.de/img/github/android-colorpicker-scr1.png)
The color picker as it is per default, unmodified,
with the test colors being all static colors in `android.graphics.Color.*`

###Usage
####Include library: Android Studio / Gradle
Include this project (either manually or via `git submodule`) into your Android app project path.
Let's say, you put it under `<PROJECT_ROOT>/libs/android-colorpicker`.

Add to your `settings.gradle`:
```gradle
...
include ':android-colorpicker'
project(':android-colorpicker').projectDir = new File('libs/android-colorpicker/app')
```

Add to your **module's** `build.gradle` (i.e. in most cases `<PROJECT_ROOT>/app/build.gradle`):
```gradle
...
dependencies {
    ...
    compile project(':android-colorpicker')
    ...
}
...
```

The package name of the library is `com.woalk.apps.lib.colorpicker`
*(may be different across different branches of this repo)*.

####Use `ColorPickerDialog`
The simplest way to use the color picker:
```java
mSelectedColor = Color.BLACK;

ColorPickerDialog dialog = ColorPickerDialog.newInstance(
       R.string.some_title_string, 
       new int[] { Color.BLACK, Color.RED, Color.GREEN, ... },
       mSelectedColor,
       5, // Number of columns
       ColorPickerDialog.SIZE_SMALL);

dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener(){

       @Override
       public void onColorSelected(int color) {
           mSelectedColor = color;
       }

});

dialog.show(getFragmentManager(), "some_tag");
```

The `OnColorSelectedListener` will be called when the user clicks on a color.
`color` will contain the selected color value (`0xAARRGGBB`).

You can add an `EditText` to enter a custom color hex code by adding `true` as the last value to the contructor.
Clicking 'Done' on the keyboard will trigger the `OnColorSelectedListener` as usual.

####Use `ColorPreference`
The library comes with a pre-written `Preference` class to use with a `PreferenceScreen` (i.e. `PreferenceActivity` or `PreferenceFragment`).

![scr2](http://ext.woalk.de/img/github/android-colorpicker-scr2.png)

Simply add it like this to your `preferences.xml`:
```xml
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android"
                  xmlns:app="http://schemas.android.com/apk/res-auto">

    <com.woalk.apps.lib.colorpicker.ColorPreference
        android:key="pref_example"
        android:title="@string/example_title"
        android:summary="@string/example_summary"
        app:picker_dialogTitle="@string/example_dialog_title"
        app:picker_colors="@array/colors"
        app:picker_columns="3"
        app:picker_allowCustomColor="false"
        android:defaultValue="@color/bk" />

</PreferenceScreen>
```

You can name the namespace defined in the second line (`xmlns:app...`) as you like.
You just have to name it everywhere in the file the same.

You should reference an `integer-array` for `app:picker_colors`,
containing either full integer colors (`0xAARRGGBB`) or `color` references, like this:
```xml
<resources>
    <integer-array name="colors">
        <item>@color/bk</item>
        <item>@color/r</item>
        <item>@color/g</item>
        <item>@color/b</item>
        <item>@color/y</item>
        <item>@color/m</item>
        <item>@color/c</item>
        <item>@color/w</item>
    </integer-array>

    <color name="bk">#000</color>
    <color name="r">#f00</color>
    <color name="g">#0f0</color>
    <color name="b">#00f</color>
    <color name="y">#ff0</color>
    <color name="m">#f0f</color>
    <color name="c">#0ff</color>
    <color name="w">#fff</color>
</resources>
```

Change `app:picker_allowCustomColors` to `true` to enable the `EditText` for custom colors.

####Use other small things in this library
#####`parseColor(String)
There is the static method `ColorPickerDialog.parseColorString(colorString)`.
It is a modified version of [`Color.parseColor(colorString)`](http://developer.android.com/reference/android/graphics/Color.html#parseColor(java.lang.String)), it is capable of parsing more color string types (`#RGB`, `#ARGB`).


####There are string in this project that are not translated yet.
Feel free to translate them in your language (or any other language you know well enough).
It would be very nice if you would pull-request these additions to this project, so I can complete the translations.
*Thank you!*



*There will be extensions of the existing features in the future.*

```
(C) 2013  The Android Open Source Project
    Licensed under Apache License v2.0.
    You can obtain a copy of the license at http://www.apache.org/licenses/LICENSE-2.0

(C) 2015  Woalk Software (http://woalk.com)
    Also, licensed under Apache License v2.0.
    You can obtain a copy of the license at http://www.apache.org/licenses/LICENSE-2.0

DISCLAIMER:
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```
