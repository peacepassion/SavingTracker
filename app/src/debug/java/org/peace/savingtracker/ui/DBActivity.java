package org.peace.savingtracker.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import java.sql.Date;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.Expense;
import org.peace.savingtracker.utils.ResUtil;

/**
 * Created by peacepassion on 15/11/6.
 */
public class DBActivity extends BaseActivity {

  @Bind(R.id.db_list) RecyclerView dbRecyclerView;
  DBAdapter dbAdapter;

  private Realm realm;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    realm = Realm.getDefaultInstance();
    setupDBRecyclerView();
  }

  private void setupDBRecyclerView() {
    dbAdapter = new DBAdapter(realm.where(Expense.class).findAll());
    dbRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    dbRecyclerView.setAdapter(dbAdapter);
  }

  @Override protected int getLayoutRes() {
    return R.layout.layout_db_debug;
  }

  static class DBAdapter extends RecyclerView.Adapter<DBViewHolder> {

    RealmResults<Expense> expenses;

    public DBAdapter(RealmResults<Expense> expenses) {
      this.expenses = expenses;
    }

    @DBViewHolder.ViewType @Override public int getItemViewType(int position) {
      return position == 0 ? DBViewHolder.VIEW_TYPE_HEAD : DBViewHolder.VIEW_TYPE_CONTENT;
    }

    @Override public DBViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View item =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_db, parent, false);
      return new DBViewHolder(item, viewType);
    }

    @Override public void onBindViewHolder(DBViewHolder holder, int position) {
      int type = getItemViewType(position);
      switch (type) {
        case DBViewHolder.VIEW_TYPE_HEAD:
          onBindHeadVH(holder);
          break;
        case DBViewHolder.VIEW_TYPE_CONTENT:
          onBindContentVH(holder, getContentItem(position));
          break;
        default:
          break;
      }
    }

    private void onBindHeadVH(DBViewHolder holder) {
      holder.idTV.setText("id");
      holder.userIdTV.setText("user_id");
      holder.nameTV.setText("username");
      holder.dateTV.setText("date");
      holder.categoryTV.setText("category");
      holder.valueTV.setText("value");
      holder.setStyle(ResUtil.getColor(android.R.color.holo_red_light), true);
    }

    private void onBindContentVH(DBViewHolder holder, Expense contentItem) {
      holder.idTV.setText(String.valueOf(contentItem.getId()));
      holder.userIdTV.setText(contentItem.getUserId());
      holder.nameTV.setText(contentItem.getName());
      holder.dateTV.setText(new Date(contentItem.getDate()).toString());
      holder.categoryTV.setText(contentItem.getCategory());
      holder.valueTV.setText(String.valueOf(contentItem.getValue()));
      holder.setStyle(ResUtil.getColor(android.R.color.black), false);
    }

    private Expense getContentItem(int position) {
      return expenses.get(position - 1);
    }

    @Override public int getItemCount() {
      return expenses.size() + 1;
    }
  }

  static class DBViewHolder extends RecyclerView.ViewHolder {

    static final int VIEW_TYPE_HEAD = 0;
    static final int VIEW_TYPE_CONTENT = 1;

    int type;

    @Bind(R.id.id) TextView idTV;
    @Bind(R.id.user_id) TextView userIdTV;
    @Bind(R.id.name) TextView nameTV;
    @Bind(R.id.date) TextView dateTV;
    @Bind(R.id.category) TextView categoryTV;
    @Bind(R.id.value) TextView valueTV;

    public DBViewHolder(View itemView, int type) {
      super(itemView);
      this.type = type;
      ButterKnife.bind(this, itemView);
    }

    public void setStyle(@ColorInt int color, boolean bold) {
      for (TextView tv : getAllTVs()) {
        tv.setTextColor(color);
        tv.setTypeface(null, bold ? Typeface.BOLD : Typeface.NORMAL);
      }
    }

    private TextView[] getAllTVs() {
      return new TextView[] {idTV, userIdTV, nameTV, dateTV, categoryTV, valueTV};
    }

    @IntDef({ DBViewHolder.VIEW_TYPE_HEAD, DBViewHolder.VIEW_TYPE_CONTENT }) @interface ViewType {
    }
  }
}
