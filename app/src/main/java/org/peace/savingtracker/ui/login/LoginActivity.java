package org.peace.savingtracker.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import butterknife.OnClick;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import me.ele.commons.AppLogger;
import org.json.JSONException;
import org.json.JSONObject;
import org.peace.savingtracker.R;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.home.HomeActivity;

/**
 * Created by peacepassion on 15/10/14.
 */
public class LoginActivity extends BaseActivity {

  private static final String QQ_PERMISSION = "get_user_info,get_simple_userinfo,get_info";

  private Tencent tencent;
  private String appId = "1104924318";
  private IUiListener loginListener;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("登录");
    initQQ();
  }

  private void initQQ() {
    tencent = Tencent.createInstance(appId, getApplicationContext());
    loginListener = new IUiListener() {
      @Override public void onComplete(Object o) {
        JSONObject response = (JSONObject) o;
        AppLogger.d("response: " + response);
        try {
          String token = response.getString(Constants.PARAM_ACCESS_TOKEN);
          String expire = response.getString(Constants.PARAM_EXPIRES_IN);
          String openId = response.getString(Constants.PARAM_OPEN_ID);
          if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expire) && !TextUtils.isEmpty(
              openId)) {
            tencent.setAccessToken(token, expire);
            tencent.setOpenId(openId);
            requestUserInfo();
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override public void onError(UiError uiError) {
        Snackbar.make(root, uiError.errorMessage, Snackbar.LENGTH_SHORT).show();
      }

      @Override public void onCancel() {
        Snackbar.make(root, "user canceled", Snackbar.LENGTH_SHORT).show();
      }
    };
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_login;
  }

  @OnClick({ R.id.weixin_login, R.id.qq_login }) public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.weixin_login) {
      Snackbar.make(root, "目前不支持", Snackbar.LENGTH_LONG).show();
      return;
    }
    if (id == R.id.qq_login) {
      qqLogin();
      return;
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    AppLogger.d("-->onActivityResult " + requestCode + " resultCode=" + resultCode);
    if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
      Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  private void qqLogin() {
    if (!tencent.isSessionValid()) {
      tencent.login(this, QQ_PERMISSION, loginListener);
    } else {
      tencent.logout(this);
    }
  }

  private void requestUserInfo() {
    UserInfo userInfo = new UserInfo(this, tencent.getQQToken());
    IUiListener listener = new IUiListener() {
      @Override public void onComplete(Object o) {
        JSONObject response = (JSONObject) o;
        AppLogger.d("response: " + response);
        try {
          String nickname = response.getString("nickname");
          userManager.qqUserLogin(tencent.getOpenId(), tencent.getAccessToken(), nickname);
          goHome();
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override public void onError(UiError uiError) {
        Snackbar.make(root, uiError.errorMessage, Snackbar.LENGTH_SHORT).show();
      }

      @Override public void onCancel() {
        Snackbar.make(root, "user canceled", Snackbar.LENGTH_SHORT).show();
      }
    };
    userInfo.getUserInfo(listener);
  }

  private void goHome() {
    Intent intent = new Intent(this, HomeActivity.class);
    startActivity(intent);
    finish();
  }
}
