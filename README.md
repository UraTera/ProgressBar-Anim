## An example of creating a circular animated ProgressBar.

![ProgressBarAnim3](https://github.com/user-attachments/assets/08eb2eb1-815f-4856-8e00-200150357c10)


Open source. To use the ready-made library, add the dependency:
```
dependencies {
    implementation("io.github.uratera:progress_anim:1.1.3")
}
```
### Attributes
Attributes	    |Description
----------------|--------------
animation	    |Animation state (default - true)
animDuration	|Animation duration (default 1500ms)
itemBlurStyle	|Styles of blur (default – normal)
itemBlurWidth	|Blur width
itemColor	    |Items color
itemColorEnd	|Arc end color (for "gradient" style)
itemCount	    |Number items
itemIcon	    |Items icon
itemHeight	    |Items height
itemWidth	    |Items width
itemStyle	    |Styles of items (default – dash)


**Styles of item:**
- arrow
- circle
- dash
- gradient
- random 

**Styles of blur:**
- normal
- solid

Note: Blur is only available for the "gradient" style.

**Usage:**
```
<com.tera.progress.ProgressBarAnim
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```
Method:
```
setAnimation
```
false – pause, true – resume.

