package org.peace.savingtracker.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.peace.savingtracker.R;

/**
 * Created by peacepassion on 15/11/6.
 */
public class UIUtil {

  public static void attachDebugView(Activity activity) {
    LayoutInflater inflater = LayoutInflater.from(activity);
    ViewGroup contentParent =
        (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
    View content = contentParent.getChildAt(0);
    contentParent.removeView(content);
    View root = inflater.inflate(R.layout.layout_home_debug_container, contentParent, true);
    ViewGroup homeContainer = (ViewGroup) root.findViewById(R.id.home_container);
    homeContainer.addView(content);
  }
}
