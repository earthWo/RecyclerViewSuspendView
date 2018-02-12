package win.whitelife.library;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * @author wuzefeng
 * @date 2018/2/11
 */
public class RecyclerViewSuspendView extends FrameLayout {


    private RecyclerView mRecyclerView;



    public RecyclerViewSuspendView(@NonNull Context context) {
        this(context,null);
    }

    public RecyclerViewSuspendView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecyclerViewSuspendView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRecyclerView=new RecyclerView(context,attrs,defStyleAttr);
        init();
    }


    private void init(){
        addView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager= (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstCount=linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                int firsrPosition=linearLayoutManager.findFirstVisibleItemPosition();
                int lastCount=linearLayoutManager.findLastVisibleItemPosition();
                if(firstCount==0){
                    suspendView.setY(0);
                    showCount=0;
                    if(mSuspendViewAdapter!=null){
                        mSuspendViewAdapter.onSuspendViewHolder(suspendView,showCount);
                    }
                }else{
                    for(int i=0;i<lastCount;i++){
                        View itemView=linearLayoutManager.getChildAt(i);
                        if(itemView!=null&&((ViewGroup)itemView).getChildCount()>0){
                            View suView=((ViewGroup)itemView).getChildAt(0);
                            if(suView!=null&&suView.getVisibility()==View.VISIBLE){
                                int top=itemView.getTop();
                                if(top<0){
                                    suspendView.setY(0);
                                    if(firsrPosition!=showCount){
                                        showCount=firsrPosition;
                                        if(mSuspendViewAdapter!=null){
                                            mSuspendViewAdapter.onSuspendViewHolder(suspendView,showCount);
                                        }
                                    }
                                }else if(top<suspendView.getHeight()){
                                    suspendView.setY(top-suspendView.getHeight());
                                    if(firsrPosition!=showCount){
                                        showCount=firsrPosition;
                                        if(mSuspendViewAdapter!=null){
                                            mSuspendViewAdapter.onSuspendViewHolder(suspendView,showCount);
                                        }
                                    }
                                    return;
                                }else{
                                    suspendView.setY(0);
                                    showCount=firsrPosition;
                                }
                            }
                        }
                    }
                    suspendView.setY(0);
                }
            }
        });
    }


    private class SuspendHolder extends RecyclerView.ViewHolder{

        RecyclerView.ViewHolder mViewHolder;

        View suspendView;

        public SuspendHolder(View itemView, RecyclerView.ViewHolder holder,View suspendView) {
            super(itemView);
            mViewHolder=holder;
            this.suspendView=suspendView;
        }
    }


    private SuspendViewAdapter mSuspendViewAdapter;


    private View suspendView;

    private int showCount;


    public void setSuspendViewAdapter(SuspendViewAdapter adapter){
        mSuspendViewAdapter=adapter;
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

        if(mSuspendViewAdapter!=null){
            suspendView=mSuspendViewAdapter.getSuspendView();
            this.addView(suspendView,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mSuspendViewAdapter.onSuspendViewHolder(suspendView,0);
            showCount=0;
        }

    }

    private RecyclerView.Adapter<SuspendHolder> mAdapter=new RecyclerView.Adapter<SuspendHolder>() {
        @Override
        public SuspendHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if(mSuspendViewAdapter!=null){
                LinearLayout linearLayout= (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_item,parent,false);
                RecyclerView.ViewHolder viewHolder=mSuspendViewAdapter.onCreateViewHolder(parent,viewType);
                View suspendView=mSuspendViewAdapter.getSuspendView();
                linearLayout.addView(suspendView);
                linearLayout.addView(viewHolder.itemView);
                SuspendHolder suspendHolder=new SuspendHolder(linearLayout,viewHolder,suspendView);
                return suspendHolder;
            }

            return null;
        }

        @Override
        public void onBindViewHolder(SuspendHolder holder, int position) {
            if(mSuspendViewAdapter!=null){
                int visible=mSuspendViewAdapter.itemSuspendViewVisible(position);
                holder.suspendView.setVisibility(visible);
                if(visible==View.VISIBLE){
                    mSuspendViewAdapter.onSuspendViewHolder(holder.suspendView,position);
                }
                mSuspendViewAdapter.onBindViewHolder(holder.mViewHolder,position);
            }
        }

        @Override
        public int getItemCount() {
            if(mSuspendViewAdapter!=null){
                return mSuspendViewAdapter.getItemCount();
            }
            return 0;
        }


        @Override
        public int getItemViewType(int position) {
            if(mSuspendViewAdapter!=null){
                return mSuspendViewAdapter.getItemViewType(position);
            }
            return super.getItemViewType(position);
        }
    };

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public View getSuspendView() {
        return suspendView;
    }

    public  interface SuspendViewAdapter {

        /**
         * 获取item数量
         * @return
         */
        int getItemCount();

        /**
         * 实现item显示
         * @param holder
         * @param position
         * @return
         */
        RecyclerView.ViewHolder onBindViewHolder(RecyclerView.ViewHolder holder, int position);

        /**
         * 实现标签显示
         * @param suspendView
         * @param position
         */
        void onSuspendViewHolder(View suspendView, int position);

        /**
         * 获取创建的item
         * @param parent
         * @param viewType
         * @return
         */
        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        /**
         * 获取标签VIEW
         * @return
         */
        View getSuspendView();

        /**
         * 获取item类型
         * @param position
         * @return
         */
        int getItemViewType(int position);

        /**
         * 判断标签是否显示
         * @param position
         * @return
         */
        int itemSuspendViewVisible(int position);

    }




}
