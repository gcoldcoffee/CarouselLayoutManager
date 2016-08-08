package com.azoft.carousellayoutmanager.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.azoft.carousellayoutmanager.sample.databinding.ItemViewBinding;
import java.util.Random;


public class CarouselPreviewActivity extends AppCompatActivity {

    private Activity mActivity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_carousel_preview);

        mActivity=this;

        final TestAdapter adapter = new TestAdapter();
        adapter.setContext(mActivity);

        initRecyclerView((RecyclerView) findViewById(R.id.list_horizontal), new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false), adapter);

        initRecyclerView((RecyclerView)findViewById(R.id.list_vertical), new CarouselLayoutManager(CarouselLayoutManager.VERTICAL, true), adapter);

        findViewById(R.id.fab_scroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int itemToRemove = adapter.mItemsCount;
                if (10 != itemToRemove) {
                    adapter.mItemsCount++;
                    adapter.notifyItemInserted(itemToRemove);
                }
            }
        });

        findViewById(R.id.fab_change_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int itemToRemove = adapter.mItemsCount - 1;
                if (0 <= itemToRemove) {
                    adapter.mItemsCount--;
                    adapter.notifyItemRemoved(itemToRemove);
                }
            }
        });
    }

    private void initRecyclerView(final RecyclerView recyclerView, final CarouselLayoutManager layoutManager, final TestAdapter adapter) {
        // enable zoom effect. this line can be customized
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());

        recyclerView.setLayoutManager(layoutManager);
        // we expect only fixed sized item for now
        recyclerView.setHasFixedSize(true);
        // sample adapter with random data
        recyclerView.setAdapter(adapter);
        // enable center post scrolling
        recyclerView.addOnScrollListener(new CenterScrollListener());
        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {

            @Override
            public void onCenterItemChanged(final int adapterPosition) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //final int value = adapter.mPosition[adapterPosition];
                        //adapter.mPosition[adapterPosition] = (value % 10) + (value / 10 + 1) * 10;
                       // adapter.notifyItemChanged(adapterPosition);

                        //5.0以下的手机会不会主动刷新到界面,需要调用此方法
                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                },50);
            }
        });
    }

    private static final class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {

        @SuppressWarnings("UnsecureRandomNumberGeneration")
        private final Random mRandom = new Random();
        private final int[] mColors = new int[10];
        private final int[] mPosition = new int[10];
        private int mItemsCount = 10;

        private Context context;

        private TestAdapter() {
//            mColors = new int[10];
//            mPosition = new int[10];
            for (int i = 0; 10 > i; ++i) {
                //noinspection MagicNumber
                mColors[i] = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
                mPosition[i] = i;
            }
        }

        public void setContext(Context context) {
            this.context = context;
        }
        @Override
        public TestViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            return new TestViewHolder(ItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(final TestViewHolder holder, final int position) {
            holder.mItemViewBinding.cItem1.setText(String.valueOf(mPosition[position]));
            holder.mItemViewBinding.cItem2.setText(String.valueOf(mPosition[position]));
            holder.itemView.setBackgroundColor(mColors[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,""+String.valueOf(mPosition[position]),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItemsCount;
        }
    }

    private static class TestViewHolder extends RecyclerView.ViewHolder {

        private final ItemViewBinding mItemViewBinding;

        TestViewHolder(final ItemViewBinding itemViewBinding) {
            super(itemViewBinding.getRoot());

            mItemViewBinding = itemViewBinding;
        }
    }
}