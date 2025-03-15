## An example of creating a circular animated ProgressBar.

![ProgressBarAnim3](https://github.com/user-attachments/assets/08eb2eb1-815f-4856-8e00-200150357c10)


Open source. To use the ready-made library, add the dependency:
```
dependencies {
    implementation("io.github.uratera:progress_anim:1.0.1")
}
```
### Attributes
|Attributes   |Description |
|-------------|------------|
|animDuration |Animation duration (default 1500ms)
|itemColor    |Items color
|itemColorEnd |Arc end color (for "gradient" style)
|itemCount    |Number items
|itemIcon     |Items icon
|itemHeight   |Items height
|itemWidth    |Items width
|itemStyle    |Items style
| |arrow
| |circle
| |dash
| |gradient

Method:
```
setAnimation
```
false – pause, true – resume.

