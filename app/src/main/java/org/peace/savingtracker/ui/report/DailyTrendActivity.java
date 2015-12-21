package org.peace.savingtracker.ui.report;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;
import autodagger.AutoInjector;
import butterknife.Bind;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.Expense;
import org.peace.savingtracker.model.ExpenseAPI;
import org.peace.savingtracker.model.ExpenseHelper;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/12/20.
 */
@AutoInjector(MyApp.class) public class DailyTrendActivity extends BaseActivity {

  private static final int DATA_WINDOW_SIZE = 7;
  private static final int ONE_DAY_MILL = 24 * 60 * 60 * 1000;

  @Bind(R.id.bar_chart) BarChart barChart;

  @Inject ExpenseAPI expenseAPI;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent.inject(this);

    setupBarChart();
  }

  private void setupBarChart() {
    barChart.setNoDataText(getString(R.string.no_data_title));
    barChart.setNoDataTextDescription(getString(R.string.no_data_des));

    expenseAPI.getCurrentAccountBookExpenses()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindToLifecycle())
        // sort by date
        .map(expenses -> {
          if (expenses.size() > 0) {
            Collections.sort(expenses, (lhs, rhs) -> (int) (lhs.getDate() - rhs.getDate()));
          }
          return expenses;
        })
        // split into each single Observable
        .flatMap(expenses -> Observable.from(expenses))
        // group by same day
        .groupBy(expense -> ExpenseHelper.getYearMonthDay(expense))
        // convert GroupedObservable -> Observable<List>
        .flatMap(groupedObservable -> groupedObservable.toList())
        // convert to each Pair
        .map(expenses -> {
          float v = 0;
          for (Expense expense : expenses) {
            v += expense.getValue();
          }
          return new Pair<>(expenses.get(0).getDate(), v);
        })
        .toList()
        // fill the data to one week data
        .map(pairs -> {
          if (pairs.size() == 0) {
            return pairs;
          }

          if (pairs.size() >= DATA_WINDOW_SIZE) {
            return pairs;
          }

          long firstDayMill = pairs.get(0).first;

          // fill data
          int count = DATA_WINDOW_SIZE - pairs.size();
          for (int i = 0; i < count; ++i) {
            pairs.add(0, new Pair<>(firstDayMill - (i + 1) * ONE_DAY_MILL, 0f));
          }

          return pairs;
        })
        .flatMap(pairs -> Observable.from(pairs))
        .map(longFloatPair -> new Pair<>(ExpenseHelper.getMonthDay(longFloatPair.first),
            longFloatPair.second))
        .takeLast(DATA_WINDOW_SIZE)
        .subscribe(new Subscriber<Pair<String, Float>>() {
          ProgressDialog dlg = new ProgressDialog(DailyTrendActivity.this);
          List<Pair<String, Float>> dataSet = new ArrayList<>(31);

          @Override public void onStart() {
            dlg.show();
            barChart.setVisibility(View.GONE);
          }

          @Override public void onCompleted() {
            dlg.dismiss();

            if (dataSet.size() == 0) {
              barChart.setVisibility(View.VISIBLE);
              return;
            }

            ArrayList xData = new ArrayList<String>(dataSet.size());
            ArrayList yData = new ArrayList<BarEntry>(dataSet.size());
            for (int i = 0; i < dataSet.size(); i++) {
              xData.add(dataSet.get(i).first);

              yData.add(new BarEntry(dataSet.get(i).second, i));
            }

            BarData barData = new BarData(xData, new BarDataSet(yData, ""));
            barChart.setData(barData);

            barChart.setPinchZoom(false);
            barChart.setDoubleTapToZoomEnabled(false);

            barChart.setVisibility(View.VISIBLE);
          }

          @Override public void onError(Throwable e) {
            popHint(e.getMessage());
            dlg.dismiss();
          }

          @Override public void onNext(Pair<String, Float> data) {
            dataSet.add(data);
          }
        });
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_daily_trend;
  }

  @Override protected boolean needLogin() {
    return true;
  }
}
