package com.azoft.carousellayoutmanager.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;

import java.util.Random;


public class CarouselPreviewActivity extends Activity {

    private Activity mActivity;
    private RecyclerView recyclerView;
//    private TestAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_carousel_preview);

        mActivity=this;

        recyclerView=(RecyclerView) findViewById(R.id.list_horizontal);
        initRecyclerView(recyclerView, new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false));

        initRecyclerView((RecyclerView) findViewById(R.id.list_vertical), new CarouselLayoutManager(CarouselLayoutManager.VERTICAL, true));

    }

    public void initRecyclerView(final RecyclerView recyclerView, final CarouselLayoutManager layoutManager) {

        final TestAdapter adapter= new TestAdapter();
        adapter.setContext(mActivity);

        // enable zoom effect. this line can be customized
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());

        layoutManager.setMaxVisibleItems(2);

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
                        //5.0以下的手机会不会主动刷新到界面,需要调用此方法
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }, 50);
            }
        });

        adapter.setLayoutManager(layoutManager);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(adapter.getItemCount()/2);
            }
        }, 100);

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    break;
            }
        }
    };


    private  class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {
        @SuppressWarnings("UnsecureRandomNumberGeneration")
        private final Random mRandom = new Random();
        private final int[] mColors = new int[10];
        private final int[] mPosition = new int[10];
        private int mItemsCount = 10;

        private Context context;

        CarouselLayoutManager layoutManager;

        public void setLayoutManager(CarouselLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        private TestAdapter() {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            return new TestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TestViewHolder holder, final int position) {
            holder.c_item_1.setText(String.valueOf(mPosition[position]));
            holder.c_item_2.setText(String.valueOf(mPosition[position]));
            holder.itemView.setBackgroundColor(mColors[position]);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    recyclerView.smoothScrollToPosition(position);
                    if(layoutManager.getOrientation()==CarouselLayoutManager.VERTICAL){
                        if ((int) holder.itemView.getY() > 0 && holder.itemView.getHeight() / 2 > (int) holder.itemView.getY()) {
                            Toast.makeText(context, "" + String.valueOf(mPosition[position]), Toast.LENGTH_SHORT).show();
                        }
                    }else if(layoutManager.getOrientation()==CarouselLayoutManager.HORIZONTAL){
                        if ((int) holder.itemView.getX() > 0 && holder.itemView.getWidth() / 2 > (int) holder.itemView.getX()) {
                            Toast.makeText(context, "" + String.valueOf(mPosition[position]), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mItemsCount;
        }
    }

    private static class TestViewHolder extends RecyclerView.ViewHolder {

        private  View view;
        private TextView c_item_1;
        private TextView c_item_2;

        TestViewHolder(final View view) {
            super(view);

            c_item_1 = (TextView) view.findViewById(R.id.c_item_1);
            c_item_2 = (TextView) view.findViewById(R.id.c_item_2);
        }
    }
}