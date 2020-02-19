# AndroidUtils

该项目主要是自己写的和收集的一些android工具包。

1.StringUtils 一些String的处理，包含是否为空，是否小数，编码，解码等

2.FileUtils 一些File的操作，包括写入，读取，追加，删除，拷贝等

3.HTMLUtils 来源于oschina的官方app

4.IOUtils 一些IO流相关的操作，包括写入，读取，关闭等

5.LogUtils 主要参考XUtils中的LogUtils，并做出一点修改

6.SPUtils 用于操作SharedPreferences。来源于oschina的官方app，并做出一点修改

7.StatusBarUtils 状态栏操作类，包含透明，填色，等。来源于网络

8.TypefaceUtils 字体操作类，主要用于TextView的字体修改。

9.IntentUtils 主要用于android之间的activity跳转。

10.ViewUtils 参考XUtils的ViewUtils，并作出一定修改完善。

11.CacheUtils 主要是用于获取一些缓存目录。

12.BitmapUtils 主要是对Picasso的封装。及一些Bitmap的操作。

13.ZipUtils 来源于KJFrameForAndroid

14.UiUtils 一些dp转px，获取屏幕宽高，toast等。

15.CanvasUtils 一些兼容低版本的绘制方法。

16.DDUtils 一些操作数据库的简单封装。



> 1.项目根目录 build.gradle

> 1.1           allprojects {

> 1.2               repositories {

> 1.3                   maven { url 'https://jitpack.io' }

> 1.4               }

> 1.5           }


> 2.项目 module 下的 build.gradle

> 2.1   implementation 'com.github.lyx32:AndroidUtils:x.x.x' [![](https://jitpack.io/v/lyx32/AndroidUtils.svg)](https://jitpack.io/#lyx32/AndroidUtils)
