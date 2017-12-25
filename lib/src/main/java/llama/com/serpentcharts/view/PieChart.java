package llama.com.serpentcharts.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

import llama.com.serpentcharts.R;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;

/**
 * @author theWhiteLlama
 */
public class PieChart extends ChartView {

    private Paint mPaint;
    private Path mPath;
    private RectF mOval, mInnerOval;

    private DisplayMetrics mMetrics;

    private boolean mRing = false;
    private int mRingThickness = 0;

    @NonNull
    private List<Float> mValues = new ArrayList<>();
    private List<Float> mInterpolatedValues = new ArrayList<>();

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

    @NonNull
    @Override
    ChartAdapter.AdapterDataObserver createObserver() {
        return new ChartViewDataObserver() {
            @Override
            public void onChanged() {
                mValues.clear();
                PieChart.Adapter adapter = (Adapter) mAdapter;
                for (int i = 0; i < adapter.getCount(); i++) mValues.add(adapter.getValue(i));
                invalidate();
            }

            @Override
            public void onInvalidated() {
                mValues.clear();
                PieChart.Adapter adapter = (Adapter) mAdapter;
                for (int i = 0; i < adapter.getCount(); i++) mValues.add(adapter.getValue(i));
                invalidate();
            }

            @Override
            void onItemRangeInserted(int positionStart, int itemCount) {
                PieChart.Adapter adapter = (Adapter) mAdapter;
                for (int i = 0; i < itemCount; i++) {
                    mValues.add(positionStart + i, adapter.getValue(positionStart + i));
                }
            }

            @Override
            void onItemRangeChanged(int positionStart, int itemCount) {
                PieChart.Adapter adapter = (Adapter) mAdapter;
                for (int i = 0; i < itemCount; i++) {
                    mValues.set(positionStart + i, adapter.getValue(positionStart + i));
                }
            }

            @Override
            void onItemRangeRemoved(int positionStart, int itemCount) {
            }

        };
    }

    private void init(@Nullable AttributeSet attrs, int defStyle) {
        mMetrics = getResources().getDisplayMetrics();
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
            final int[] colors = Editor.getColors(values.length);
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
            mAdapter.notifyDataSetChanged(); // TODO find fix for hacky xml layout solution
        }
        mPaint = new Paint();
        mPath = new Path();
        mOval = new RectF();
        mInnerOval = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width, height, diameter;

        if (widthMode == EXACTLY && heightMode == EXACTLY) {
            diameter = Math.min(widthSize, heightSize);
            width = diameter;
            height = diameter;
        } else if (widthMode == EXACTLY) {
            if (heightMeasureSpec == AT_MOST) {
                diameter = Math.min(widthSize, heightSize);
                width = diameter;
                height = diameter;
            } else {
                width = widthSize;
                height = widthSize;
            }
        } else if (heightMode == EXACTLY) {
            if (widthMeasureSpec == AT_MOST) {
                diameter = Math.min(widthSize, heightSize);
                width = diameter;
                height = diameter;
            } else {
                width = heightSize;
                height = heightSize;
            }
        } else {
            width = 100;
            height = 100;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (!(mAdapter instanceof Adapter)) // user must set a adapter for this view
            throw new IllegalStateException(String.format("No instance of %s provided", PieChart.Adapter.class.getName()));
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

        Adapter adapter = (Adapter) mAdapter;
        int count = mValues.size(); // adapter.getCount();
        if (count > 1) {
            float sum = 0;
            for (int i = 0; i < count; i++) {
                sum += Math.max(0f, mValues.get(i)/*adapter.getValue(i)*/);
            }
            float current = 0;
            for (int i = 0; i < count; i++) {
                float value = mValues.get(i) /*adapter.getValue(i)*/;
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
                mPaint.setColor(adapter.getColor(i));
                canvas.drawPath(mPath, mPaint);
            }
        } else if (count == 1) {
            mPath.reset();
            mPaint.setColor(adapter.getColor(0));
            mPath.addOval(mOval, Path.Direction.CCW);
            if (mRing) {
                mPath.addOval(mInnerOval, Path.Direction.CW);
            }
            canvas.drawPath(mPath, mPaint);
        }

    }

    public void setRing(boolean ring, float thicknessDp) {
        if (mRing == ring) return;
        mRing = ring;
        mRingThickness = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, thicknessDp, mMetrics);
        invalidate();
    }

    public abstract static class Adapter extends ChartAdapter {

        public abstract int getCount();

        @ColorInt
        public abstract int getColor(int index);

        public abstract float getValue(int index);

    }

}