package llama.com.serpentcharts.sample;

import android.graphics.Color;
import android.os.Handler;

import llama.com.serpentcharts.view.PieChart;

/**
 * @author theWhiteLlama
 */
public class SamplePieChartAdapter extends PieChart.Adapter {

    private Handler mHandler = new Handler();

    private int[] mColors = new int[]{
            Color.parseColor("#26a69a"),
            Color.parseColor("#00796b"),
            Color.parseColor("#689f38"),
            Color.parseColor("#9e9d24"),
            Color.parseColor("#558b2f"),
    };
    private float[] mValues = new float[0];

    @Override
    public int getCount() {
        return mValues.length;
    }

    @Override
    public int getColor(int index) {
        return mColors[index];
    }

    @Override
    public float getValue(int index) {
        return mValues[index];
    }

    private void randomize() {
        mValues = new float[(int) (1 + Math.random() * 5)];
        for (int i = 0; i < mValues.length; i++) {
            mValues[i] = (float) Math.max(1f, Math.random() * 100);
        }
        notifyDataSetChanged();
        mHandler.postDelayed(this::randomize, 5000);
    }

    void startRandomize() {
        randomize();
    }

    void stopRandomize() {
        mHandler.removeCallbacksAndMessages(null);
    }

}
