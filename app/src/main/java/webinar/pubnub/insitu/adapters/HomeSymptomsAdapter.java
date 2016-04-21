package webinar.pubnub.insitu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import webinar.pubnub.insitu.MainActivity;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.dialogs.MoreInfoDialog;
import webinar.pubnub.insitu.managers.SymptomManager;
import webinar.pubnub.insitu.model.Symptom;


/**
 * Created by Konstantinos Michail on 4/20/2016.
 */
public class HomeSymptomsAdapter extends BaseSwipeAdapter {

    private Context mContext;
    List<Symptom> symptoms;
    public HomeSymptomsAdapter(Context mContext,List<Symptom> symptoms) {
        this.mContext = mContext;
        this.symptoms=symptoms;
    }
    @Bind(R.id.tell_us_more)Button addMoreInfoButton;
    @OnClick(R.id.tell_us_more) void openMoreDialog(){
        MoreInfoDialog.newInstance(5).show(MainActivity.getInstance().getSupportFragmentManager(),"more");
    }
    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_home_listview, null);
        ButterKnife.bind(this,v);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView t = (TextView)convertView.findViewById(R.id.position);
        t.setText(String.valueOf(position + 1));
    }

    @Override
    public int getCount() {
        return symptoms.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
