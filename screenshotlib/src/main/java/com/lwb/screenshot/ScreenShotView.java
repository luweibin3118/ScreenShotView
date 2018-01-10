package com.lwb.screenshot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/9.
 */
public class ScreenShotView extends View {

    private final int STATUS_SHOT = 1;

    private final int STATUS_DOODLE = 2;

    private final int STATUS_DRAW_CIRCLE = 3;

    private int defaultShotWidth;

    private Rect shotRect, LTRect, RTRect, LBRect, RBRect, currentTouchRect, tempShotRect;

    private int l, t, r, b, width, height;

    private Paint mPaint;

    private int shotMinWidth, halfMinWidth;

    private Path mPath;

    private GestureDetector shotGestureDetector, editGestureDetector;

    private int downX, downY;

    private int lastL, lastT, lastR, lastB;

    private int unSelectedBackgroundColor = 0x88000000;

    private int currentStatus = STATUS_SHOT;

    private long lastClickTime = 0L;

    private Bitmap shotBitmap;

    private Rect srcRect, dstRect, fullRect;

    private int toolBarHeight;

    private PopupWindow selectDoodlePop, toolPop;

    private int[] colors, sizes;

    private int doodleDownX, doodleDownY, doodleTempX, doodleTempY;

    private Path doodlePath;

    private List<DrawItem> doodlePathList;

    private int doodleColor = 0xffff0000, doodleSize = 5;

    private SelectDoodleTextView doodle, drawCircle;

    private RectF drawCircleRectF;

    private boolean onDoodleDrawing = false;

    private boolean scaleShot = true;

    private int toolBarBackgroundColor = 0xffaaaaaa, toolBarTextSize = 14,
            toolBarTextColor = 0xff1874CD, selectViewBackgroundColor = 0xffCCCCCC;

    private GestureDetector.OnGestureListener shotGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            downX = (int) e.getX();
            downY = (int) e.getY();
            if (LTRect.contains(downX, downY)) {
                currentTouchRect = LTRect;
            } else if (RTRect.contains(downX, downY)) {
                currentTouchRect = RTRect;
            } else if (LBRect.contains(downX, downY)) {
                currentTouchRect = LBRect;
            } else if (RBRect.contains(downX, downY)) {
                currentTouchRect = RBRect;
            } else if (tempShotRect.contains(downX, downY)) {
                currentTouchRect = tempShotRect;
            }
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (System.currentTimeMillis() - lastClickTime < 300) {
                onScreenShot();
                lastClickTime = 0L;
            } else {
                lastClickTime = System.currentTimeMillis();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (currentTouchRect != null) {
                int moveX = (int) (e2.getX() - downX);
                int moveY = (int) (e2.getY() - downY);
                if (currentTouchRect == LTRect) {
                    l = lastL + moveX;
                    t = lastT + moveY;
                    if (l > lastR - shotMinWidth) {
                        l = lastR - shotMinWidth;
                    } else if (l < 0) {
                        l = 0;
                    }
                    if (t > lastB - shotMinWidth) {
                        t = lastB - shotMinWidth;
                    } else if (t < 0) {
                        t = 0;
                    }
                    b = lastB;
                    r = lastR;
                } else if (currentTouchRect == RTRect) {
                    r = lastR + moveX;
                    t = lastT + moveY;
                    if (r < lastL + shotMinWidth) {
                        r = lastL + shotMinWidth;
                    } else if (r > width) {
                        r = width;
                    }
                    if (t > lastB - shotMinWidth) {
                        t = lastB - shotMinWidth;
                    } else if (t < 0) {
                        t = 0;
                    }
                    l = lastL;
                    b = lastB;
                } else if (currentTouchRect == LBRect) {
                    l = lastL + moveX;
                    b = lastB + moveY;
                    if (l > lastR - shotMinWidth) {
                        l = lastR - shotMinWidth;
                    } else if (l < 0) {
                        l = 0;
                    }
                    if (b < t + shotMinWidth) {
                        b = t + shotMinWidth;
                    } else if (b > height) {
                        b = height;
                    }
                    r = lastR;
                    t = lastT;
                } else if (currentTouchRect == RBRect) {
                    r = lastR + moveX;
                    b = lastB + moveY;
                    if (r < lastL + shotMinWidth) {
                        r = lastL + shotMinWidth;
                    } else if (r > width) {
                        r = width;
                    }
                    if (b < t + shotMinWidth) {
                        b = t + shotMinWidth;
                    } else if (b > height) {
                        b = height;
                    }
                    l = lastL;
                    t = lastT;
                } else if (currentTouchRect == tempShotRect) {
                    l = lastL + moveX;
                    t = lastT + moveY;
                    r = lastR + moveX;
                    b = lastB + moveY;
                    if (l > width - (lastR - lastL)) {
                        l = width - (lastR - lastL);
                    } else if (l < 0) {
                        l = 0;
                    }
                    if (t > height - (lastB - lastT)) {
                        t = height - (lastB - lastT);
                    } else if (t < 0) {
                        t = 0;
                    }
                    if (r < lastR - lastL) {
                        r = lastR - lastL;
                    } else if (r > width) {
                        r = width;
                    }
                    if (b < lastB - lastT) {
                        b = lastB - lastT;
                    } else if (b > height) {
                        b = height;
                    }
                }
                setPosition();
                invalidate();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    };

    private GestureDetector.OnGestureListener editGestureListener = new GestureDetector.OnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            onDoodleDrawing = true;
            doodleTempX = doodleDownX = (int) e.getX();
            doodleTempY = doodleDownY = (int) e.getY();
            if (currentStatus == STATUS_DOODLE) {
                doodlePath = new Path();
                doodlePath.moveTo(doodleDownX, doodleDownY);
                if (doodle != null) {
                    doodleColor = doodle.getDoodleColor();
                    doodleSize = doodle.getDoodleSize();
                }
            } else if (currentStatus == STATUS_DRAW_CIRCLE) {
                if (drawCircle != null) {
                    doodleColor = drawCircle.getDoodleColor();
                    doodleSize = drawCircle.getDoodleSize();
                }
            }
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int x = (int) e2.getX();
            int y = (int) e2.getY();
            if (dstRect.contains(x, y)) {
                if (currentStatus == STATUS_DOODLE) {
                    doodlePath.quadTo(doodleTempX, doodleTempY, x, y);
                    doodleTempX = x;
                    doodleTempY = y;
                    invalidate();
                } else if (currentStatus == STATUS_DRAW_CIRCLE) {
                    doodleTempX = x;
                    doodleTempY = y;
                    invalidate();
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    };

    public ScreenShotView(Activity context) {
        super(context);
        ViewGroup content = (ViewGroup) context.findViewById(android.R.id.content);
        content.addView(this, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        init();
    }

    public ScreenShotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        shotRect = new Rect();
        tempShotRect = new Rect();
        fullRect = new Rect();
        srcRect = new Rect();
        dstRect = new Rect();
        LTRect = new Rect();
        RTRect = new Rect();
        LBRect = new Rect();
        RBRect = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
        drawCircleRectF = new RectF();
        shotGestureDetector = new GestureDetector(getContext(), shotGestureListener);
        shotGestureDetector.setIsLongpressEnabled(false);

        editGestureDetector = new GestureDetector(getContext(), editGestureListener);
        editGestureDetector.setIsLongpressEnabled(false);
        colors = new int[20];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = 0xffff0000 - 0x00125932 * i;
        }
        sizes = new int[]{10, 12, 15, 18, 20};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        defaultShotWidth = Math.min(height, width) * 2 / 3;
        shotMinWidth = Math.min(height, width) / 6;
        halfMinWidth = shotMinWidth / 2;
        lastL = l = (width - defaultShotWidth) / 2;
        lastT = t = (height - defaultShotWidth) / 2;
        lastR = r = l + defaultShotWidth;
        lastB = b = t + defaultShotWidth;
        toolBarHeight = height / 10;
        fullRect.set(0, 0, width, height);
        setPosition();
    }

    private void setPosition() {
        shotRect.set(l, t, r, b);
        tempShotRect.set(l, t, r, b);
        LTRect.set(l - halfMinWidth, t - halfMinWidth, l + halfMinWidth, t + halfMinWidth);
        RTRect.set(r - halfMinWidth, t - halfMinWidth, r + halfMinWidth, t + halfMinWidth);
        LBRect.set(l - halfMinWidth, b - halfMinWidth, l + halfMinWidth, b + halfMinWidth);
        RBRect.set(r - halfMinWidth, b - halfMinWidth, r + halfMinWidth, b + halfMinWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentStatus == STATUS_SHOT) {
            drawShotRect(canvas);
            drawShotLine(canvas);
            drawTipText(canvas);
        } else if (currentStatus == STATUS_DOODLE || currentStatus == STATUS_DRAW_CIRCLE) {
            drawEditRect(canvas);
            drawDoodle(canvas);
        }
    }

    private void drawDoodle(Canvas canvas) {
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        if (doodlePathList != null) {
            for (DrawItem item : doodlePathList) {
                mPaint.setColor(item.color);
                if (item.itemType == STATUS_DOODLE) {
                    mPaint.setStrokeWidth(item.size);
                    canvas.drawPath(item.path, mPaint);
                } else if (item.itemType == STATUS_DRAW_CIRCLE) {
                    mPaint.setStrokeWidth(5);
                    canvas.drawOval(item.circleRectF, mPaint);
                }
            }
        }
        mPaint.reset();
        mPaint.setStrokeWidth(doodleSize);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(doodleColor);

        if (onDoodleDrawing) {
            if (currentStatus == STATUS_DOODLE) {
                if (doodlePath != null) {
                    canvas.drawPath(doodlePath, mPaint);
                }
            } else if (currentStatus == STATUS_DRAW_CIRCLE) {
                drawCircleRectF.set(doodleDownX, doodleDownY, doodleTempX, doodleTempY);
                mPaint.setStrokeWidth(5);
                canvas.drawOval(drawCircleRectF, mPaint);
            }
        }
    }

    private void drawEditRect(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(0xFFFFFFFF);
        canvas.drawRect(fullRect, mPaint);
        canvas.drawBitmap(shotBitmap, srcRect, dstRect, mPaint);

        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xff999999);
        mPaint.setStrokeWidth(2);
        canvas.drawRect(dstRect, mPaint);
    }


    private void drawShotRect(Canvas canvas) {
        mPaint.reset();
        int sc;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            sc = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        } else {
            sc = canvas.saveLayer(0, 0, width, height, mPaint);
        }

        mPaint.setColor(unSelectedBackgroundColor);
        canvas.drawRect(fullRect, mPaint);

        mPaint.reset();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(10);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawRect(shotRect, mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(sc);

        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xFF3A5FCD);
        mPaint.setStrokeWidth(5);
        mPath.reset();
        mPath.moveTo(l + (r - l) / 2, t + (b - t) / 2);
        mPath.lineTo(l + (r - l) / 2, t + (b - t) / 2 + halfMinWidth / 2);
        mPath.lineTo(l + (r - l) / 2, t + (b - t) / 2 - halfMinWidth / 2);
        mPath.lineTo(l + (r - l) / 2, t + (b - t) / 2);
        mPath.lineTo(l + (r - l) / 2 - halfMinWidth / 2, t + (b - t) / 2);
        mPath.lineTo(l + (r - l) / 2 + halfMinWidth / 2, t + (b - t) / 2);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawShotLine(Canvas canvas) {
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xFF3A5FCD);
        mPaint.setStrokeWidth(10);

        mPath.reset();
        mPath.moveTo(l, t + halfMinWidth);
        mPath.lineTo(l, t);
        mPath.lineTo(l + halfMinWidth, t);
        canvas.drawPath(mPath, mPaint);

        mPath.reset();
        mPath.moveTo(r, t + halfMinWidth);
        mPath.lineTo(r, t);
        mPath.lineTo(r - halfMinWidth, t);
        canvas.drawPath(mPath, mPaint);

        mPath.reset();
        mPath.moveTo(l, b - halfMinWidth);
        mPath.lineTo(l, b);
        mPath.lineTo(l + halfMinWidth, b);
        canvas.drawPath(mPath, mPaint);

        mPath.reset();
        mPath.moveTo(r, b - halfMinWidth);
        mPath.lineTo(r, b);
        mPath.lineTo(r - halfMinWidth, b);
        canvas.drawPath(mPath, mPaint);
    }


    private void drawTipText(Canvas canvas) {
        mPaint.reset();
        mPaint.setTextSize(50);
        mPaint.setColor(0xff888888);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("双击截屏", width / 2, (float) (height * 0.95), mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (currentStatus == STATUS_DOODLE || currentStatus == STATUS_DRAW_CIRCLE) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (currentStatus == STATUS_DOODLE) {
                        if (doodlePath != null) {
                            if (doodlePathList == null) {
                                doodlePathList = new ArrayList<>();
                            }
                            DrawItem item = new DrawItem();
                            item.itemType = currentStatus;
                            item.path = doodlePath;
                            item.color = doodleColor;
                            item.size = doodleSize;
                            doodlePathList.add(item);
                            doodlePath = null;
                        }
                    } else if (currentStatus == STATUS_DRAW_CIRCLE) {
                        if (doodlePathList == null) {
                            doodlePathList = new ArrayList<>();
                        }
                        DrawItem item = new DrawItem();
                        item.itemType = currentStatus;
                        item.circleRectF = new RectF(doodleDownX, doodleDownY, doodleTempX, doodleTempY);
                        item.color = doodleColor;
                        item.size = doodleSize;
                        doodlePathList.add(item);
                    }
                    onDoodleDrawing = false;
                    break;
                default:
                    break;
            }
            return editGestureDetector.onTouchEvent(event);
        } else if (currentStatus == STATUS_SHOT) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    currentTouchRect = null;
                    lastL = l;
                    lastR = r;
                    lastB = b;
                    lastT = t;
                    break;
                default:
                    break;
            }
            return shotGestureDetector.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void onScreenShot() {
        if (this.getParent() != null) {
            ViewGroup parent = (ViewGroup) this.getParent();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View tagView = parent.getChildAt(i);
                if (tagView != this && tagView.getVisibility() == VISIBLE) {
                    shotBitmap = snapShot(tagView, l, t, r - l, b - t);
                    int bw = shotBitmap.getWidth();
                    int bh = shotBitmap.getHeight();
                    srcRect.set(0, 0, bw, bh);

                    int dstL = (width - bw) / 2;
                    int dstT = 0;
                    int dstR = (width - bw) / 2 + bw;
                    int dstB = bh;

                    if (scaleShot) {
                        float dstAlpha = (float) bw / (float) bh;
                        float srcAlpha = (float) width / (float) (height - toolBarHeight);
                        if (dstAlpha > srcAlpha) {
                            float sc = ((float) (width)) / ((float) (dstR - dstL));
                            int th = (int) ((dstB - dstT) * sc);
                            dstL = 0;
                            dstT = (height - toolBarHeight - th) / 2;
                            dstR = width;
                            dstB = height - toolBarHeight - dstT;
                        } else {
                            float sc = ((float) (height - toolBarHeight)) / ((float) (dstB - dstT));
                            int tw = (int) ((dstR - dstL) * sc);
                            dstL = (width - tw) / 2;
                            dstR = width - dstL;
                            dstT = 0;
                            dstB = height - toolBarHeight;
                        }
                        dstRect.set(dstL, dstT, dstR, dstB);
                    } else {
                        dstRect.set(dstL, dstT, dstR, dstB);
                        dstRect.top = (height - toolBarHeight - bh) / 2;
                        if (dstRect.top < 0) {
                            dstRect.top = 0;
                        }
                        dstRect.bottom = dstRect.top + bh;
                        if (dstRect.bottom > height - toolBarHeight) {
                            dstRect.bottom = height - toolBarHeight;
                        }
                    }
                    currentStatus = STATUS_DOODLE;
                    showToolBar();
                    invalidate();
                }
            }
        }
    }

    private void showToolBar() {
        if (toolPop == null) {
            toolPop = new PopupWindow();
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setBackgroundColor(toolBarBackgroundColor);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            layoutParams.gravity = Gravity.CENTER;

            TextView reSelect = new TextView(getContext());
            reSelect.setText("重选");
            reSelect.setTextSize(toolBarTextSize);
            reSelect.setTextColor(toolBarTextColor);
            reSelect.setGravity(Gravity.CENTER);
            linearLayout.addView(reSelect, layoutParams);
            reSelect.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentStatus = STATUS_SHOT;
                    doodlePathList = null;
                    invalidate();
                    if (toolPop != null) {
                        toolPop.dismiss();
                    }
                    if (selectDoodlePop != null) {
                        selectDoodlePop.dismiss();
                    }
                }
            });

            TextView reset = new TextView(getContext());
            reset.setText("撤销");
            reset.setTextSize(toolBarTextSize);
            reset.setTextColor(toolBarTextColor);
            reset.setGravity(Gravity.CENTER);
            linearLayout.addView(reset, layoutParams);
            reset.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (doodlePathList != null && doodlePathList.size() > 0) {
                        doodlePathList.remove(doodlePathList.size() - 1);
                        invalidate();
                    }
                }
            });

            doodle = new SelectDoodleTextView(getContext(), STATUS_DOODLE);
            doodle.setText("涂鸦");
            doodle.setTextSize(toolBarTextSize);
            doodle.setTextColor(toolBarTextColor);
            doodle.setGravity(Gravity.CENTER);
            doodle.setShowHeader(true);
            linearLayout.addView(doodle, layoutParams);
            doodle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    doodle.setShowHeader(true);
                    drawCircle.setShowHeader(false);
                    currentStatus = STATUS_DOODLE;
                    showSelectDoodleView();
                }
            });

            drawCircle = new SelectDoodleTextView(getContext(), STATUS_DRAW_CIRCLE);
            drawCircle.setText("画圈");
            drawCircle.setTextSize(toolBarTextSize);
            drawCircle.setTextColor(toolBarTextColor);
            drawCircle.setGravity(Gravity.CENTER);
            drawCircle.setShowHeader(false);
            linearLayout.addView(drawCircle, layoutParams);
            drawCircle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawCircle.setShowHeader(true);
                    doodle.setShowHeader(false);
                    if (doodle != null) {
                        drawCircle.setDoodleColor(doodle.getDoodleColor());
                    }
                    currentStatus = STATUS_DRAW_CIRCLE;
                    showSelectDoodleView();
                }
            });

            TextView save = new TextView(getContext());
            save.setText("保存");
            save.setTextSize(toolBarTextSize);
            save.setTextColor(toolBarTextColor);
            save.setGravity(Gravity.CENTER);
            linearLayout.addView(save, layoutParams);
            save.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        saveShot();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            toolPop.setContentView(linearLayout);
            toolPop.setWidth(width);
            toolPop.setHeight(toolBarHeight);
        }
        toolPop.showAtLocation(this, Gravity.BOTTOM, 0, 0);
    }

    private void showSelectDoodleView() {
        if (selectDoodlePop == null) {
            selectDoodlePop = new PopupWindow();
            DoodlePaintSelectView doodlePaintSelectView = new DoodlePaintSelectView(getContext(), colors, sizes, width);
            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.setBackgroundColor(0x88000000);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.BOTTOM;
            frameLayout.addView(doodlePaintSelectView, layoutParams);
            frameLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectDoodlePop.dismiss();
                }
            });
            selectDoodlePop.setContentView(frameLayout);
            selectDoodlePop.setWidth(width);
            selectDoodlePop.setHeight(height - toolBarHeight);
        }
        selectDoodlePop.showAtLocation(this, Gravity.BOTTOM, 0, toolBarHeight);
    }

    private void onColorChange(int color, int size) {
        if (doodle != null) {
            doodle.setDoodleColor(color);
            doodle.setDoodleSize(size);
        }
        if (drawCircle != null) {
            drawCircle.setDoodleColor(color);
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != VISIBLE) {
            if (toolPop != null) {
                toolPop.dismiss();
            }
            if (selectDoodlePop != null) {
                selectDoodlePop.dismiss();
            }
            if (doodlePathList != null) {
                doodlePathList.clear();
            }
        } else {
            currentStatus = STATUS_SHOT;
        }
    }

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

    private Bitmap snapShot(View tagView, int x, int y, int width, int height) {
        View view = tagView;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap cache = view.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cache, x, y, width, height);
        view.destroyDrawingCache();
        return bitmap;
    }

    private void saveShot() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String bitName = "Screenshot_" + format.format(System.currentTimeMillis()) + ".jpg";
        Bitmap bitmap = snapShot(this, dstRect.left, dstRect.top,
                dstRect.right - dstRect.left, dstRect.bottom - dstRect.top);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdCardDir = Environment.getExternalStorageDirectory() + "/Pictures/Screenshots/";
            File dirFile = new File(sdCardDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File file = new File(sdCardDir, bitName);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this.getContext(), "保存失败，请确认应用是否已开启存储权限！", Toast.LENGTH_LONG).show();
                return;
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Toast.makeText(this.getContext(), "已保存至：" + Environment.getExternalStorageDirectory()
                    + "/Pictures/Screenshots/" + bitName, Toast.LENGTH_SHORT).show();
        }
    }

    private class SelectDoodleTextView extends TextView {

        private int doodleSize = 20;

        private int doodleColor = 0xffff0000;

        private Paint paint;

        private int type;

        private boolean showHeader = false;

        public SelectDoodleTextView(Context context, int type) {
            super(context);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            if (colors != null) {
                doodleColor = colors[0];
            }
            if (sizes != null) {
                doodleSize = sizes[0];
            }
            this.type = type;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!showHeader) {
                return;
            }
            paint.setColor(doodleColor);
            if (type == STATUS_DOODLE) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(getWidth() / 2, getHeight() / 5, doodleSize, paint);
            } else if (type == STATUS_DRAW_CIRCLE) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);
                canvas.drawCircle(getWidth() / 2, getHeight() / 5, getHeight() / 10, paint);
            }
        }

        public void setDoodleSize(int doodleSize) {
            this.doodleSize = doodleSize;
            invalidate();
        }

        public void setDoodleColor(int doodleColor) {
            this.doodleColor = doodleColor;
            invalidate();
        }

        public int getDoodleSize() {
            return doodleSize;
        }

        public int getDoodleColor() {
            return doodleColor;
        }

        public void setShowHeader(boolean showHeader) {
            this.showHeader = showHeader;
            invalidate();
        }
    }

    private class DoodlePaintSelectView extends View implements GestureDetector.OnGestureListener {

        private int[] colors;

        private int[] sizes;

        private Paint paint;

        private int colorItemPadding;

        private int height, width, colorItemSize, paintW, paintH;

        private int currentColor;

        private int currentSize;

        private GestureDetector mGestureDetector;

        private int sizeTop;

        public DoodlePaintSelectView(Context context, int[] colors, int[] sizes, int width) {
            super(context);
            this.colors = colors;
            this.sizes = sizes;
            this.width = width;
            currentColor = colors[0];
            currentSize = sizes[0];
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mGestureDetector = new GestureDetector(getContext(), this);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            paintH = colorItemSize = width / 10;
            paintW = width / 5;
            colorItemPadding = colorItemSize / 4;
            height = (colors.length / 11 + 1) * colorItemSize + (sizes.length / 6 + 1) * paintH;
            setMeasuredDimension(width, height);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.reset();
            paint.setColor(selectViewBackgroundColor);
            canvas.drawRect(0, 0, width, height, paint);

            int col = -1;
            int num = 0;
            for (int i = 0; i < colors.length; i++) {
                if (i % 10 == 0) {
                    col++;
                    num = 0;
                }
                paint.setColor(colors[i]);
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(colorItemSize / 20);
                canvas.drawRect(colorItemSize * num + colorItemPadding, colorItemSize * col + colorItemPadding,
                        colorItemSize * (num + 1) - colorItemPadding, colorItemSize * (col + 1) - colorItemPadding, paint);
                if (currentColor == colors[i]) {
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(colorItemSize * num + colorItemPadding / 2, colorItemSize * col + colorItemPadding / 2,
                            colorItemSize * (num + 1) - colorItemPadding / 2, colorItemSize * (col + 1) - colorItemPadding / 2, paint);
                }
                num++;
            }

            num = 0;
            sizeTop = colorItemSize * (col + 1);
            for (int i = 0; i < sizes.length; i++) {
                if (i % 5 == 0) {
                    col++;
                    num = 0;
                }
                paint.setColor(currentColor);
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(colorItemSize / 20);
                canvas.drawCircle(paintW * num + (paintW / 2), colorItemSize * col + paintH / 2, sizes[i], paint);
                if (currentSize == sizes[i]) {
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(paintW * num + (paintW / 2), colorItemSize * col + paintH / 2, paintH / 3, paint);
                }
                num++;
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return mGestureDetector.onTouchEvent(event);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            int number = x / colorItemSize + (y / colorItemSize) * 10;
            if (number < colors.length) {
                currentColor = colors[number];
                invalidate();
                onColorChange(currentColor, currentSize);
            }
            if (y > sizeTop) {
                number = x / paintW + (y - sizeTop) / paintW * 5;
                if (number < sizes.length) {
                    currentSize = sizes[number];
                    invalidate();
                    onColorChange(currentColor, currentSize);
                }
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    private class DrawItem {
        int itemType;
        int color;
        int size;
        Path path;
        RectF circleRectF;
    }
}