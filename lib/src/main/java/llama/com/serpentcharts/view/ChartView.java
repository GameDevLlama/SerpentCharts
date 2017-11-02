package llama.com.serpentcharts.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.database.Observable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author theWhiteLlama
 */
public abstract class ChartView extends View {

    ChartAdapter.AdapterDataObserver mObserver;
    ChartAdapter mAdapter;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mObserver = new ChartAdapter.AdapterDataObserver() {

            @Override
            public void onChanged() {
                requestLayout();
            }

            @Override
            public void onInvalidated() {
                requestLayout();
            }
        };
    }

    public void setAdapter(@NonNull ChartAdapter adapter) {
        setAdapterInternal(adapter);
        requestLayout();
    }

    /**
     * Replaces the current adapter with the new one and triggers listeners.
     *
     * @param adapter The new adapter
     */
    private void setAdapterInternal(ChartAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mObserver);
        }
        mAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
    }

    public static abstract class ChartAdapter {

        AdapterDataObservable mObservable = new AdapterDataObservable();

        void unregisterAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            mObservable.unregisterObserver(observer);
        }

        void registerAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }

        public final void notifyDataSetChanged() {
            // mObservable.notifyChanged();
        }

        public  static class AdapterDataObserver extends DataSetObserver {

            public  void onItemRangeChanged(int positionStart, int itemCount){

            }

        }

        private static class AdapterDataObservable extends Observable<AdapterDataObserver> {

            private void notifyDataRangeInserted(int positionStart, int itemCount) {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onItemRangeChanged(positionStart, itemCount);
                }
            }

        }

    }

}
