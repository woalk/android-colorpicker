#Android Stock Color Picker Library [![Build Status](https://travis-ci.org/woalk/android-colorpicker.svg?branch=master)](https://travis-ci.org/woalk/android-colorpicker)
## This is an original color picker written by Google/Android.
Cloned from original source
at https://android.googlesource.com/platform/frameworks/opt/colorpicker/+/android-5.1.1\_r2/

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

####Use library components
The package name of the library is `com.android.colorpicker`
*(may be different across different branches of this repo)*.

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
