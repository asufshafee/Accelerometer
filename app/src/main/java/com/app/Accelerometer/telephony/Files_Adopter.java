package com.app.Accelerometer.telephony;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import com.app.Accelerometer.FileData;
        import com.app.Accelerometer.R;

        import java.util.List;

/**
 * Created by jaani on 9/7/2017.
 */


public class Files_Adopter extends RecyclerView.Adapter<Files_Adopter.MyViewHolder> {

    private List<String> dataList;
    Context context;
    Activity act;
    private Context mContext;
    private Activity mActivity;
    boolean click = true;

    private RelativeLayout mRelativeLayout;
    private Button mButton;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public final TextView tittle;

        public LinearLayout tuch;

        public MyViewHolder(View view) {
            super(view);

            tittle=(TextView)view.findViewById(R.id.tittle);
            tuch=(LinearLayout)view.findViewById(R.id.tuch);

        }

    }



    public Files_Adopter(List<String> list, Context context,Activity a) {
        this.dataList = list;
        this.context = context;
        this.act=a;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.files_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tittle.setText(dataList.get(position));
        holder.tuch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(act, FileData.class);
                intent.putExtra("filename",dataList.get(position));
                act.startActivity(intent);
            }
        });


    }



    @Override
    public int getItemCount() {
        return dataList.size();
    }







}
