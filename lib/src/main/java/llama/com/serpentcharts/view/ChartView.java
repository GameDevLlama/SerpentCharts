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
            mAdapter.unregisterAdapterDataObserver(retrieveObserver());
        }
        mAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(retrieveObserver());
        }
    }

    @NonNull
    private ChartAdapter.AdapterDataObserver retrieveObserver() {
        if (mObserver == null) mObserver = createObserver();
        return mObserver;
    }

    @NonNull
    abstract ChartAdapter.AdapterDataObserver createObserver();

    class ChartViewDataObserver extends ChartAdapter.AdapterDataObserver {
        ChartViewDataObserver() {

        }
    }

    public static abstract class ChartAdapter {

        @NonNull
        private final AdapterDataObservable mObservable = new AdapterDataObservable();

        void unregisterAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            mObservable.unregisterObserver(observer);
        }

        void registerAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }

        public final void notifyDataSetChanged() {
            mObservable.notifyDataSetChanged();
        }

        abstract static class AdapterDataObserver extends DataSetObserver {

            void onItemRangeInserted(int positionStart, int itemCount) {

            }

            void onItemRangeChanged(int positionStart, int itemCount) {

            }

            void onItemRangeRemoved(int positionStart, int itemCount) {

            }

        }

        static class AdapterDataObservable extends Observable<AdapterDataObserver> {

            private void notifyDataRangeInserted(int positionStart, int itemCount) {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onItemRangeInserted(positionStart, itemCount);
                }
            }

            private void notifyDataRangeChanged(int positionStart, int itemCount) {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onItemRangeChanged(positionStart, itemCount);
                }
            }

            private void notifyDataRangeRemoved(int positionStart, int itemCount) {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onItemRangeRemoved(positionStart, itemCount);
                }
            }

            public void notifyDataSetChanged() {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onChanged();
                }
            }

        }

    }

}
