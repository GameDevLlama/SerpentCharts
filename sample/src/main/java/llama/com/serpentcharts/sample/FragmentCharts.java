package llama.com.serpentcharts.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.math.MathUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import llama.com.serpentcharts.view.PieChart;

/**
 * @author theWhiteLlama
 */
public class FragmentCharts extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    @NonNull
    public static FragmentCharts create(int sectionNumber) {
        FragmentCharts fragment = new FragmentCharts();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return new FragmentCharts();
    }

    private SamplePieChartAdapter mPieChartAdapter;

    public FragmentCharts() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPieChartAdapter = new SamplePieChartAdapter();
        mPieChartAdapter.startRandomize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        PieChart pieChart = rootView.findViewById(R.id.pie_chart);
        pieChart.setAdapter(mPieChartAdapter);
        pieChart.setRing(true, 24);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPieChartAdapter.stopRandomize();
    }

}
