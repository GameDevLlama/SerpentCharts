package llama.com.serpentcharts.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import llama.com.serpentcharts.R;

/**
 * @author christianringshofer
 */
public class PieChart extends View {

    private Paint mPaint;
    private Path mPath;
    private RectF mOval, mInnerOval;

    private boolean mRing = false;
    private int mRingThickness = 0;

    @Nullable
    private Adapter mAdapter;

    @NonNull
    private float[] mValues = new float[0], mInterpolatedValues = new float[0];

    public PieChart(Context context) {
        super(context);
        init(null, 0);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(@Nullable AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PieChart, defStyle, 0);
            mRing = a.getBoolean(R.styleable.PieChart_ring, false);
            mRingThickness = a.getDimensionPixelSize(R.styleable.PieChart_ring_thickness, 0);
            a.recycle();
        }
        if (isInEditMode()) {
            final float[] values = new float[]{
                    10, 17, 44, 94, 124
            };
            final int[] colors = new int[]{
                    Color.rgb(255, 255, 0),
                    Color.rgb(255, 100, 77),
                    Color.rgb(180, 40, 230),
                    Color.rgb(55, 255, 170),
                    Color.rgb(255, 57, 120),
            };
            setAdapter(new Adapter() {
                @Override
                public int getCount() {
                    return values.length;
                }

                @Override
                public int getColor(int index) {
                    return colors[index];
                }

                @Override
                public float getValue(int index) {
                    return values[index];
                }
            });
        }
        mPaint = new Paint();
        mPath = new Path();
        mOval = new RectF();
        mInnerOval = new RectF();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        float width = getWidth();
        float height = getHeight();
        float radius = Math.min(width, height) * 0.5f;
        float innerRadius = Math.max(0f, radius - mRingThickness);
        mOval.set(
                width * 0.5f - radius,
                height * 0.5f - radius,
                width * 0.5f + radius,
                height * 0.5f + radius
        );
        mInnerOval.set(
                width * 0.5f - innerRadius,
                height * 0.5f - innerRadius,
                width * 0.5f + innerRadius,
                height * 0.5f + innerRadius
        );

        mPaint.setAntiAlias(true);
        mPath.reset();
        mPath.moveTo((width + radius) * 0.5f, height * 0.5f);

        if (mAdapter != null) {
            int count = mAdapter.getCount();
            float sum = 0;
            for (int i = 0; i < count; i++) {
                sum += Math.max(0f, mAdapter.getValue(i));
            }
            float current = 0;
            for (int i = 0; i < count; i++) {
                float value = mAdapter.getValue(i);
                float relativeValue = value / sum;
                current += Math.max(0f, relativeValue);
                mPath.reset();
                float radians = (float) ((current - relativeValue) * 2f * Math.PI);
                float cos = (float) Math.cos(radians);
                float sin = (float) Math.sin(radians);
                if (mRing) {
                    mPath.moveTo(
                            width * 0.5f + cos * innerRadius,
                            height * 0.5f + sin * innerRadius
                    );
                } else {
                    mPath.moveTo(width * 0.5f, height * 0.5f);
                }
                mPath.lineTo(
                        width * 0.5f + cos * radius,
                        height * 0.5f + sin * radius
                );
                mPath.arcTo(mOval, (current - relativeValue) * 360f, relativeValue * 360f);
                if (mRing) {
                    radians = (float) (current * 2f * Math.PI);
                    cos = (float) Math.cos(radians);
                    sin = (float) Math.sin(radians);
                    mPath.lineTo(
                            width * 0.5f + cos * innerRadius,
                            height * 0.5f + sin * innerRadius
                    );
                    mPath.arcTo(mInnerOval, current * 360f, -relativeValue * 360f);
                } else {
                    mPath.lineTo(width * 0.5f, height * 0.5f);
                }
                mPath.close();
                mPaint.setColor(mAdapter.getColor(i));
                canvas.drawPath(mPath, mPaint);
            }
        }

    }

    public void setAdapter(@NonNull Adapter adapter) {
        mAdapter = adapter;
    }

    public abstract static class Adapter {

        public abstract int getCount();

        @ColorInt
        public abstract int getColor(int index);

        public abstract float getValue(int index);

    }

}