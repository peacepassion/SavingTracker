package org.peace.savingtracker.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import butterknife.Bind;
import java.util.ArrayList;
import java.util.List;
import org.peace.savingtracker.BuildConfig;
import org.peace.savingtracker.R;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peacepassion.layout.DataHolder;
import org.peacepassion.layout.SlidingTabLayout;

/**
 * Created by peacepassion on 15/10/14.
 */
public class HomeActivity extends BaseActivity {

  @Bind(R.id.view_pager) ViewPager viewPager;
  @Bind(R.id.sliding_tab_layout) SlidingTabLayout slidingTabLayout;

  private BaseHomeFragment[] fragments;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getString(R.string.app_name));
    attachDebugDrawer();

    initViewPager();
  }

  private void initViewPager() {
    fragments = new BaseHomeFragment[] {
        HomeExpenseFragment.newInstance(), //
        HomeReportFragment.newInstance(), //
        HomeUserCenterFragment.newInstance()
    };

    FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override public Fragment getItem(int position) {
        return fragments[position];
      }

      @Override public int getCount() {
        return fragments.length;
      }
    };

    viewPager.setAdapter(adapter);
    viewPager.setOffscreenPageLimit(fragments.length);

    String[] titles = new String[] {
        getString(R.string.add), //
        getString(R.string.report), //
        getString(R.string.account)
    };

    List<DataHolder> dataHolders = new ArrayList<>();
    for (int i = 0; i < fragments.length; ++i) {
      dataHolders.add(new DataHolder(getResources().getDrawable(R.drawable.unselected),
          getResources().getDrawable(R.drawable.selected), //
          titles[i], //
          getResources().getColor(android.R.color.holo_blue_light)));
    }

    if (!TextUtils.isEmpty(fragments[0].getTitle())) {
      setTitle(fragments[0].getTitle());
    }
    viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override public void onPageSelected(int position) {
        String title = fragments[position].getTitle();
        if (TextUtils.isEmpty(title)) {
          setTitle(getString(R.string.app_name));
        } else {
          setTitle(title);
        }
      }
    });

    slidingTabLayout.setUpViewPager(viewPager, dataHolders);
  }

  private void attachDebugDrawer() {
    if (BuildConfig.DEBUG) {
      try {
        Class.forName("org.peace.savingtracker.ui.UIUtil")
            .getMethod("attachDebugView", Activity.class)
            .invoke(null, this);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_home;
  }

  @Override protected boolean allowActionUp() {
    return false;
  }

  @Override protected boolean needLogin() {
    return true;
  }
}
