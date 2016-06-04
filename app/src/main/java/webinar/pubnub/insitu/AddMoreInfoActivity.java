package webinar.pubnub.insitu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import webinar.pubnub.insitu.adapters.MyExpandableListItemAdapter;

public class AddMoreInfoActivity extends AppCompatActivity {
    @Bind(R.id.activity_mylist_listview)ListView listView;
    private static AddMoreInfoActivity addMoreInfoActivity;
    private static final int INITIAL_DELAY_MILLIS = 500;
    private MyExpandableListItemAdapter mExpandableListItemAdapter;

    public static AddMoreInfoActivity getInstance(){
        return addMoreInfoActivity;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMoreInfoActivity=this;
        setContentView(R.layout.activity_add_more_info);
        ButterKnife.bind(this);
        mExpandableListItemAdapter = new MyExpandableListItemAdapter(this);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(mExpandableListItemAdapter);
        alphaInAnimationAdapter.setAbsListView(listView);

        assert alphaInAnimationAdapter.getViewAnimator() != null;
        alphaInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

        listView.setAdapter(alphaInAnimationAdapter);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}
