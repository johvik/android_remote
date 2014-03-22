package android.remote.mouse;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.remote.R;
import android.util.AttributeSet;
import android.view.View;

import java.util.Observable;
import java.util.Observer;

/**
 * A view to draw the state of a mouse model. It can show buttons and indicates when they are
 * clicked.
 */
public class MouseView extends View implements Observer {
    private Paint mButtonPaint;
    private int mButtonColor = Color.RED;
    private int mButtonCornerSize;
    private RectF mLeftButtonRectF;
    private RectF mRightButtonRectF;
    private float mMiddle;
    private boolean mCalculatedMeasurements = false;

    private MouseModel mMouseModel = null;

    public MouseView(Context context) {
        super(context);
        init(null, 0);
    }

    public MouseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MouseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final Context context = getContext();
        if (context != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MouseView,
                    defStyle, 0);

            if (a != null) {
                mButtonColor = a.getColor(R.styleable.MouseView_buttonColor, mButtonColor);
                a.recycle();
            }
            // Change the alpha of the color
            mButtonColor &= 0x00FFFFFF; // 0% alpha

            mButtonPaint = new Paint();
            mButtonPaint.setAntiAlias(true);
            mButtonPaint.setStyle(Paint.Style.FILL);
            // 50% alpha
            mButtonPaint.setColor((128 << 24) | mButtonColor);
        }
    }

    private void updateMeasurements() {
        // Calculate once
        if (!mCalculatedMeasurements) {
            mCalculatedMeasurements = true;
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int paddingRight = getPaddingRight();
            int paddingBottom = getPaddingBottom();

            int contentWidth = getWidth() - paddingLeft - paddingRight;
            int contentHeight = getHeight() - paddingTop - paddingBottom;
            // Middle point of the view
            mMiddle = (float) paddingLeft + (float) contentWidth / 2.0f;

            // 50% of width and 5% for the middle
            int buttonWidth = (int) ((double) contentWidth * 0.5 * 0.95);
            int middleSpace = contentWidth - buttonWidth * 2;
            // 120% of button width
            int buttonHeight = (int) ((double) buttonWidth * 1.2);
            // Make sure its not higher than the content
            buttonHeight = Math.min(contentHeight, buttonHeight);

            // 25% of button width
            mButtonCornerSize = (int) ((double) buttonWidth * 0.25);

            mLeftButtonRectF = new RectF(0, 0, buttonWidth, buttonHeight);
            mLeftButtonRectF.offsetTo(paddingLeft, paddingTop);

            mRightButtonRectF = new RectF(0, 0, buttonWidth, buttonHeight);
            mRightButtonRectF.offsetTo(mLeftButtonRectF.right + middleSpace, paddingTop);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        updateMeasurements();

        if (mMouseModel != null) {
            if (mMouseModel.isLeftButtonDown()) {
                canvas.drawRoundRect(mLeftButtonRectF, mButtonCornerSize, mButtonCornerSize,
                        mButtonPaint);
            }
            if (mMouseModel.isRightButtonDown()) {
                canvas.drawRoundRect(mRightButtonRectF, mButtonCornerSize, mButtonCornerSize,
                        mButtonPaint);
            }
        }
    }

    public void setMouseModel(MouseModel mouseModel) {
        if (mMouseModel != mouseModel) {
            mMouseModel = mouseModel;
            // Start observing
            mMouseModel.addObserver(this);
            invalidate();
        }
    }

    public boolean isLeftSide(float x) {
        return x < mMiddle;
    }

    @Override
    public void update(Observable observable, Object o) {
        invalidate();
    }
}
