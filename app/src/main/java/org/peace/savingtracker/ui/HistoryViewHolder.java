package org.peace.savingtracker.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.joanzapata.iconify.widget.IconTextView;
import org.peace.savingtracker.R;

/**
 * Created by peacepassion on 15/11/10.
 */
public class HistoryViewHolder extends RecyclerView.ViewHolder {

  @Bind(R.id.username) TextView userName;
  @Bind(R.id.date) TextView date;
  @Bind(R.id.category) TextView category;
  @Bind(R.id.amount) TextView amount;
  @Bind(R.id.edit) IconTextView edit;
  @Bind(R.id.delete) IconTextView delete;

  public HistoryViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }
}
