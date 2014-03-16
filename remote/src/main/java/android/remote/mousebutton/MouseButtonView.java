package android.remote.mousebutton;

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
public class MouseButtonView extends View implements Observer {
    private Paint mButtonPaint;
    private int mButtonColor = Color.RED;
    private int mButtonCornerSize;
    private RectF mButtonRectF;
    private boolean mCalculatedMeasurements = false;

    private MouseButtonModel mMouseButtonModel = null;

    public MouseButtonView(Context context) {
        super(context);
        init(null, 0);
    }

    public MouseButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MouseButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final Context context = getContext();
        if (context != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MouseButtonView,
                    defStyle, 0);

            if (a != null) {
                mButtonColor = a.getColor(R.styleable.MouseButtonView_buttonColor, mButtonColor);
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
            // 120% of button width
            int buttonHeight = (int) ((double) contentWidth * 1.2);
            // Make sure its not higher than the content
            buttonHeight = Math.min(contentHeight, buttonHeight);

            // 25% of button width
            mButtonCornerSize = (int) ((double) contentWidth * 0.25);

            mButtonRectF = new RectF(0, 0, contentWidth, buttonHeight);
            mButtonRectF.offsetTo(paddingLeft, paddingTop);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        updateMeasurements();

        if (mMouseButtonModel != null) {
            if (mMouseButtonModel.isButtonDown()) {
                canvas.drawRoundRect(mButtonRectF, mButtonCornerSize, mButtonCornerSize,
                        mButtonPaint);
            }
        }
    }

    public void setMouseButtonModel(MouseButtonModel mouseButtonModel) {
        mMouseButtonModel = mouseButtonModel;
        // Start observing
        mMouseButtonModel.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        invalidate();
    }
}
