# ScreenShotView
Android实现屏幕截图的空间，可以对图片进行涂鸦、画圈，可将修改图片保存到本地

对涂鸦可以进行撤销操作

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
            compile 'com.github.luweibin3118:ScreenShotView:v1.0.1'
        }

2. 需要截图的View中添加，创建ScreenShotView的时候只需要将想要被截图的Activity传入即可，进入截图功能就会针对当前Activity进行截图

        ScreenShotView screenShotView = new ScreenShotView(MainActivity.this);

     或者

        <com.lwb.screenshot.ScreenShotView
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

3. 提供了一些界面定制的方法：

        /**
         * 设置可以选择的颜色值数组， eg：0xffff0000;
         *
         * @param colors
         */
        public void setColors(int[] colors) {
            this.colors = colors;
        }
    
        /**
         * 设置工具栏背景
         *
         * @param toolBarBackgroundColor
         */
        public void setToolBarBackgroundColor(int toolBarBackgroundColor) {
            this.toolBarBackgroundColor = toolBarBackgroundColor;
        }
    
        /**
         * 设置工具栏字体大小
         *
         * @param toolBarTextSize
         */
        public void setToolBarTextSize(int toolBarTextSize) {
            this.toolBarTextSize = toolBarTextSize;
        }
    
        /**
         * 设置工具栏字体颜色
         *
         * @param toolBarTextColor
         */
        public void setToolBarTextColor(int toolBarTextColor) {
            this.toolBarTextColor = toolBarTextColor;
        }
    
        /**
         * 设置是否将截图放大
         *
         * @param scaleShot
         */
        public void setScaleShot(boolean scaleShot) {
            this.scaleShot = scaleShot;
        }

4. 效果图如下：

   双击屏幕截图：

    ![image](https://github.com/luweibin3118/ScreenShotView/blob/master/app/Screenshot_20180110-003634.png)

   截图添加涂鸦或者画圆：

    ![image](https://github.com/luweibin3118/ScreenShotView/blob/master/app/Screenshot_20180110-003708.png)

    ![image](https://github.com/luweibin3118/ScreenShotView/blob/master/app/Screenshot_20180110-003806.png)
