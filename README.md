# ScreenShotView
Android实现屏幕截图的空间，可以对图片进行涂鸦、画圈

1. 项目添加依赖：

    project/build.gradle中添加：
    
        allprojects {
            repositories {
                ...
                maven { url 'https://jitpack.io' }
            }
        }

    project/app/build.gradle中添加：
        
        dependencies {
            compile 'com.github.luweibin3118:ScreenShotView:v1.0.0'
        }

2. 需要截图的View中添加

        <com.lwb.screenshot.ScreenShotView
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

3. 效果图如下：

   双击屏幕截图：

    ![image](https://github.com/luweibin3118/ScreenShotView/blob/master/app/Screenshot_20180110-003634.png)

   截图添加涂鸦或者画圆：

    ![image](https://github.com/luweibin3118/ScreenShotView/blob/master/app/Screenshot_20180110-003708.png)

    ![image](https://github.com/luweibin3118/ScreenShotView/blob/master/app/Screenshot_20180110-003806.png)
