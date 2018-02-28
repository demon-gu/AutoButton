package com.autobutton.autobuttondemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @创建者 Demon
 * @创建时间 2017/6/19 15:49
 * @描述 自定义开关
 */

public class AutoButton extends View implements View.OnTouchListener {
    //背景图片
    private Bitmap bgBitmap;
    private Bitmap bgSelectedBitmap;
    private Bitmap bgUnSelectedBitmap;
    //按钮图片
    private Bitmap btnBitmap;
    private Paint  paint;
    private int leftDis = 0;
    //标记最大滑动
    private int slidingMax;
    //标记按钮开关状态
    private boolean mCurrent;
    //标记是否按压开关按钮
    private boolean isPressed = false;
    //标记是否点击事件
    private boolean isClickable;
    //标记是否移动
    private boolean isMove;
    //"开"事件监听器
    private SoftFloorListener softFloorListener;
    //"关"事件监听器
    private HydropowerListener hydropowerListener;
    //标记开关文本的宽度
    float width1, width2;
    //记录文本中心点 cx1:绘制文本1的x坐标  cx2:绘制文本2的x坐标
    //cy记录绘制文本的高度
    float cx1, cy, cx2;
    //定义"开"文本
    String textOn;
    //定义"关"文本
    String textOff;
    //定义文本大小
    float textSize;

    private Drawable mCurrent1;
    private float mNewBgWidth;
    private float mNewBgHeight;

    public AutoButton(Context context) {
        this(context, null);
    }

    public AutoButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs);
        initView();
    }
    private void initData(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoButton);
        Drawable bg_UnSelected_Drawable = a.getDrawable(R.styleable.AutoButton_bg_unselected_bitmap);
        Drawable bg_Selected_Drawable = a.getDrawable(R.styleable.AutoButton_bg_selected_bitmap);
        mCurrent1 = a.getDrawable(R.styleable.AutoButton_btn_bitmap);
        textOn = a.getString(R.styleable.AutoButton_textOn);
        textOff = a.getString(R.styleable.AutoButton_textOff);
        textSize = a.getDimension(R.styleable.AutoButton_textSize_ab, 35);

        bgUnSelectedBitmap = ((BitmapDrawable) bg_UnSelected_Drawable).getBitmap();
        bgSelectedBitmap = ((BitmapDrawable) bg_Selected_Drawable).getBitmap();
        btnBitmap = ((BitmapDrawable) mCurrent1).getBitmap();
        mNewBgWidth = a.getDimension(R.styleable.AutoButton_bg_width, 50);
        mNewBgHeight = a.getDimension(R.styleable.AutoButton_bg_height, 30);
        mCurrent = a.getBoolean(R.styleable.AutoButton_isSelected, false);
        float newBgnWidth = a.getDimension(R.styleable.AutoButton_bgn_width, 30);
        float newBgnHeight = a.getDimension(R.styleable.AutoButton_bgn_height, 30);
        bgUnSelectedBitmap = zoomImg(bgUnSelectedBitmap, mNewBgWidth, mNewBgHeight);
        bgSelectedBitmap = zoomImg(bgSelectedBitmap, mNewBgWidth, mNewBgHeight);
        btnBitmap = zoomImg(btnBitmap, newBgnWidth, newBgnHeight);

        bgBitmap = mCurrent ? bgSelectedBitmap : bgUnSelectedBitmap;
        a.recycle();
    }

    private void initView() {
        paint = new Paint();
        slidingMax = bgBitmap.getWidth() - btnBitmap.getWidth();
        paint.setTextSize(textSize);
        width1 = paint.measureText(textOn);
        cx1 = btnBitmap.getWidth() / 2 - width1 / 2;

        //测量绘制文本高度
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        cy = btnBitmap.getHeight() - (btnBitmap.getHeight() - fontHeight) / 2 - fontMetrics.bottom;
        width2 = paint.measureText(textOff);
        cx2 = (bgBitmap.getWidth() * 2 - btnBitmap.getWidth()) / 2 - width2 / 2;
        paint.setAntiAlias(true);
        setOnTouchListener(this);

        leftDis = mCurrent ? slidingMax : 0;
    }

    private Bitmap zoomImg(Bitmap bm, float newWidth ,float newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(bgBitmap.getWidth(), bgBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bgBitmap, 0, 0, paint);
        canvas.drawBitmap(btnBitmap, leftDis, 0, paint);
        if (!isPressed) {
            if (mCurrent) {
                paint.setColor(getResources().getColor(R.color.colorDark));
                canvas.drawText(textOff, cx2, cy, paint);
                paint.setColor(Color.WHITE);
                canvas.drawText(textOn, cx1, cy, paint);
            } else {
                paint.setColor(getResources().getColor(R.color.colorDark));
                canvas.drawText(textOn, cx1, cy, paint);
                paint.setColor(Color.WHITE);
                canvas.drawText(textOff, cx2, cy, paint);
            }
        } else {
            paint.setColor(Color.WHITE);
            canvas.drawText(textOn, cx1, cy, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(textOff, cx2, cy, paint);
        }
    }

    //刷新视图
    private void flushView() {
        mCurrent = !mCurrent;
        if (mCurrent) {
            //开
            bgBitmap = bgSelectedBitmap;
            leftDis = slidingMax;
            if (hydropowerListener != null) {
                hydropowerListener.hydropower();
            }
        } else {
            //关
            bgBitmap = bgUnSelectedBitmap;
            leftDis = 0;
            if (softFloorListener != null) {
                softFloorListener.softFloor();
            }
        }
    }

    //startX 标记按下的X坐标,  lastX标记移动后的X坐标 ,disX移动的距离
    float startX, lastX, disX;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPressed = true;
                isClickable = true;
                startX = event.getX();
                isMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                isPressed = true;
                lastX = event.getX();
                disX = lastX - startX;
                if (Math.abs(disX) < 5) break;
                isMove = true;
                isClickable = false;
                moveBtn();
                startX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                isPressed = false;
                if (isClickable) {
                    flushView();
                }
                if (isMove) {
                    if (leftDis > slidingMax / 2) {
                        mCurrent = false;
                    } else {
                        mCurrent = true;
                    }
                    flushView();
                }
                break;
        }
        invalidate();
        return true;
    }

    //移动后判断位置
    private void moveBtn() {
        leftDis += disX;
        if (leftDis > slidingMax) {
            bgBitmap = bgSelectedBitmap;
            leftDis = slidingMax;
        } else if (leftDis < 0) {
            bgBitmap = bgUnSelectedBitmap;
            leftDis = 0;
        }
        invalidate();
    }


    //设置左边按钮点击事件监听器
    public void setSoftFloorListener(SoftFloorListener softFloorListener) {
        this.softFloorListener = softFloorListener;
    }

    //设置右边按钮点击事件监听器
    public void setHydropowerListener(HydropowerListener hydropowerListener) {
        this.hydropowerListener = hydropowerListener;
    }

    //开点击事件
    public interface SoftFloorListener {
        void softFloor();
    }

    //关点击事件
    public interface HydropowerListener {
        void hydropower();
    }
}
