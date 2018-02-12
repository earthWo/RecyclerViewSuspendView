package win.whitelife.recyclerviewsuspendview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import win.whitelife.library.RecyclerViewSuspendView;

public class MainActivity extends AppCompatActivity {

    RecyclerViewSuspendView mRecyclerViewSuspendView;



    private class ItemHolder extends RecyclerView.ViewHolder{

        public ItemHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerViewSuspendView=findViewById(R.id.sv);
        mRecyclerViewSuspendView.setSuspendViewAdapter(new RecyclerViewSuspendView.SuspendViewAdapter() {
            @Override
            public int getItemCount() {
                return 100;
            }

            @Override
            public RecyclerView.ViewHolder onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                return null;
            }

            @Override
            public void onSuspendViewHolder(View suspendView, int position) {
                ((TextView)suspendView.findViewById(R.id.suspend)).setText(position+"");
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(MainActivity.this).inflate(R.layout.item_view,parent,false);
                return new ItemHolder(v);
            }

            @Override
            public View getSuspendView() {
                return LayoutInflater.from(MainActivity.this).inflate(R.layout.suspend_view,null,false);
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int itemSuspendViewVisible(int position) {
                return position<3||position==14?View.VISIBLE:View.GONE;
            }
        });
    }
}
