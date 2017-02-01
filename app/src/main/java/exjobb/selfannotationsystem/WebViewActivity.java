package exjobb.selfannotationsystem;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Ändrad av oss
 */

public class WebViewActivity extends Activity {
    private static final String CALLBACK_URL = "https://localhost";
    private static final String SCOPES = "lifelog.profile.read lifelog.activities.read lifelog.locations.read";

    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources resources = getResources();
        //String CLIENT_ID = resources.getString(R.string.client_id);
        String CLIENT_ID = Credentials.CLIENT_ID;

        setContentView(R.layout.activity_main);

        String url = "https://platform.lifelog.sonymobile.com/oauth/2/authorize" +
                "?client_id=" + CLIENT_ID +
                "&scope=" + SCOPES;

        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(CALLBACK_URL)) {
                    String code = "";
                    try {
                        List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");
                        for (NameValuePair param : params){
                            if (param.getName().equals("code")) {
                                code = param.getValue();
                            }
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent().putExtra("CODE", code);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    return true;
                } else {
                    return false;
                }
            }
        });

        mWebView.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
