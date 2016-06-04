package webinar.pubnub.insitu;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.Bind;
import butterknife.ButterKnife;
import webinar.pubnub.insitu.fragments.ExplorationFragment;
import webinar.pubnub.insitu.fragments.HomeFragment;
import webinar.pubnub.insitu.fragments.LineChartFragment;

public class InsightActivity extends AppCompatActivity implements HomeFragment.OnHomeInteractionListener, ExplorationFragment.OnExplorationInteractionListener, LineChartFragment.OnLineChartInteractionListener  {
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.viewpagertab)
    SmartTabLayout viewPagerTab;
    FragmentPagerItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insight);
        ButterKnife.bind(this);

        setupTabs();

    }

    private void setupTabs() {
        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Pie Chart", HomeFragment.class)
                .add("Bubble Chart", ExplorationFragment.class)
                .add("Line Chart", LineChartFragment.class)
                .create());

        viewPager.setAdapter(adapter);

        viewPagerTab.setViewPager(viewPager);
    }

    @Override
    public void OnExplorationInteraction(Uri uri) {

    }

    @Override
    public void OnHomeInteraction(Uri uri) {

    }

    @Override
    public void OnLineChartInteraction(Uri uri) {

    }
}
