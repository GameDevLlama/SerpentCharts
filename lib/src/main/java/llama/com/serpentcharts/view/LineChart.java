package llama.com.serpentcharts.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.Random;

import llama.com.serpentcharts.R;

/**
 * @author theWhiteLlama
 */
public class LineChart extends ChartView {

    private Paint mPaint;
    private Path mPath, mFillPath;

    private int mVerticalSteps, mHorizontalSteps;
    private int mDotVisibility = 0;
    private int mDotSize, mLineThickness;

    private int mGridColor;
    private boolean mFillArea = false;

    public LineChart(Context context) {
        super(context);
        init(null);
    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @NonNull
    @Override
    ChartAdapter.AdapterDataObserver createObserver() {
        return new ChartViewDataObserver() {
            @Override
            public void onChanged() {
                invalidate();
            }

            @Override
            public void onInvalidated() {
                invalidate();
            }
        };
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.LineChart, 0, 0);
            mVerticalSteps = a.getInteger(R.styleable.LineChart_vertical_steps, 10);
            mHorizontalSteps = a.getInteger(R.styleable.LineChart_horizontal_steps, 10);
            mDotVisibility = a.getInteger(R.styleable.LineChart_dot_visibility, 0);
            mDotSize = a.getDimensionPixelSize(R.styleable.LineChart_dot_size, 0);
            mLineThickness = a.getDimensionPixelSize(R.styleable.LineChart_line_thickness, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics));
            mGridColor = a.getColor(R.styleable.LineChart_grid_color, 0);
            mFillArea = a.getBoolean(R.styleable.LineChart_fill_area, false);
            a.recycle();
        }
        mPath = new Path();
        mFillPath = new Path();
        mPaint = new Paint();
        if (isInEditMode()) {
            initEditMode();
        }
    }

    private void initEditMode() {
        final float[][] dataSets = new float[][]{
                randomData(394852172673L, 30),
                randomData(679821983457L, 20)
        };
        final int[] colors = Editor.getColors(dataSets.length);
        mAdapter = new Adapter() {

            @Override
            public int getLineColor(int lineIndex) {
                return colors[lineIndex];
            }

            @Override
            public int getCountLines() {
                return dataSets.length;
            }

            @Override
            public int getDataCount(int lineIndex) {
                return dataSets[lineIndex].length;
            }

            @Override
            public float getDataY(int lineIndex, int dataIndex) {
                return dataSets[lineIndex][dataIndex];
            }

        };
    }

    @NonNull
    private float[] randomData(long seed, int size) {
        Random random = new Random(seed);
        float[] data = new float[size];
        for (int i = 0; i < data.length; i++)
            data[i] = (float) (Math.sin(i) * Math.sin(i * 13) * 0.1f + random.nextFloat() * 0.025f - 0.0125f) * 100f + i * 3;
        return data;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!(mAdapter instanceof Adapter)) // user must set a adapter for this view
            throw new IllegalStateException(String.format("No instance of %s provided", Adapter.class.getName()));
        float width = getWidth();
        float height = getHeight();
        float density = getResources().getDisplayMetrics().density;
        drawVerticalGridLines(canvas, width, height, density);
        drawHorizontalGridLines(canvas, width, height, density);
        Adapter adapter = (Adapter) mAdapter;
        int countLines = adapter.getCountLines();
        float minY = adapter.getMinY();
        float maxY = adapter.getMaxY();
        float deltaY = maxY - minY;
        for (int i = 0; i < countLines; i++) {
            int color = adapter.getLineColor(i);
            int dataSetSize = adapter.getDataCount(i);
            mPaint.setColor(color);
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(mLineThickness);
            mPath.reset();
            mPaint.setStyle(Paint.Style.FILL);
            for (int dataIndex = 1; dataIndex < dataSetSize; dataIndex++) {
                float relativeX1 = dataSetSize > 1 ? (float) (dataIndex - 1) / (dataSetSize - 1) : 0f;
                float relativeX2 = dataSetSize > 1 ? (float) (dataIndex) / (dataSetSize - 1) : 0f;
                float dataY1 = adapter.getDataY(i, dataIndex - 1);
                float dataY2 = adapter.getDataY(i, dataIndex);
                float relativeDataY1 = deltaY != 0 ? (dataY1 - minY) / deltaY : 0f;
                float relativeDataY2 = deltaY != 0 ? (dataY2 - minY) / deltaY : 0f;
                if (dataIndex == 1) {
                    mPath.moveTo(relativeX1 * width, height - relativeDataY1 * height);
                }
                mPath.lineTo(relativeX2 * width, height - relativeDataY2 * height);
                /*canvas.drawLine(
                        relativeX1 * width, height - relativeDataY1 * height,
                        relativeX2 * width, height - relativeDataY2 * height,
                        mPaint
                );*/
                if (mDotVisibility == 1) {
                    canvas.drawCircle(relativeX2 * width, height - relativeDataY2 * height, mDotSize / 2, mPaint);
                }
            }
            mFillPath.set(mPath);
            mFillPath.lineTo(width, height);
            mFillPath.lineTo(0f, height);
            mFillPath.close();
            if (mFillArea) {
                mPaint.setAlpha(96);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(mFillPath, mPaint);
                mPaint.setAlpha(255);
            }
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void drawVerticalGridLines(Canvas canvas, float width, float height, float density) {
        mPaint.setColor(mGridColor);
        mPaint.setStrokeWidth(1f * density);
        for (int i = 0; i < mHorizontalSteps + 1; i++) {
            float x = (float) i / mHorizontalSteps * width;
            canvas.drawLine(x, 0f, x, height, mPaint);
        }
    }

    private void drawHorizontalGridLines(Canvas canvas, float width, float height, float density) {
        mPaint.setColor(mGridColor);
        mPaint.setStrokeWidth(1f * density);
        for (int i = 0; i < mVerticalSteps + 1; i++) {
            float y = (float) i / mVerticalSteps * height;
            canvas.drawLine(0f, y, width, y, mPaint);
        }
    }

    public static abstract class Adapter extends ChartAdapter {

        /**
         * @return the color for the line with given index
         */
        @ColorInt
        public abstract int getLineColor(int lineIndex);

        /**
         * @return the amount of lines in the chart
         */
        public abstract int getCountLines();

        /**
         * @return the size of the dataSet for a given line
         */
        public abstract int getDataCount(int lineIndex);

        /**
         * @param lineIndex the line index
         * @param dataIndex the data index
         * @return the y-data value at dataIndex in the dataSet of a given line
         */
        public abstract float getDataY(int lineIndex, int dataIndex);

        /**
         * @return the minimum value for correct scaling
         */
        public float getMinY() {
            return 0;
        }

        /**
         * @return the maximum value for correct scaling
         */
        public float getMaxY() {
            return 100;
        }

    }

}
