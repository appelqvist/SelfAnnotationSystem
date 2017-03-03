package exjobb.selfannotationsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import exjobb.selfannotationsystem.Database.DBActivityHelper;
import exjobb.selfannotationsystem.Database.DBLabelHelper;


public class LifeLogActivity extends android.app.Activity {
    private static final int AUTHENTICATION_REQUEST = 1;
    private static final String PREFS_NAME = "PrefsFile";
    private static final String PREFS_KEY_ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String PREFS_KEY_REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String PROFILE_URL = "https://platform.lifelog.sonymobile.com/v1/users/me";
    private static final String ACTIVITIES_URL = "https://platform.lifelog.sonymobile.com/v1/users/me";

    private ActivitysFeedAdapter adapter;
    private LabelAdapter labelAdapter;
    private ListView feedListView;
    private ListView labelListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    private boolean refresh = false;
    private PopupWindow pw;

    private DBActivityHelper dbActivityHelper = new DBActivityHelper(this, null, null, 1);
    private DBLabelHelper dbLabelHelper = new DBLabelHelper(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = getSharedPreferences(PREFS_NAME, 0).getString(PREFS_KEY_ACCESS_TOKEN, "");

        if (token.equals("")) {
            setContentView(R.layout.button);
            final Button login = (Button) findViewById(R.id.button);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login(v);
                }
            });
        } else {
            viewLayout();
        }
    }

    private void viewLayout() {
        getPhysicalActivites();
        setContentView(R.layout.label_view);
        adapter = new ActivitysFeedAdapter(this, R.layout.row_view, getActivites(), dbLabelHelper.getAllLabels());
        feedListView = (ListView) findViewById(R.id.feed_list_view);
        feedListView.setAdapter(adapter);

        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                ActivityWrapper activity = (ActivityWrapper) parent.getItemAtPosition(position);
                int Aid = activity.getActivityID();
                List<ActivityWrapper> a = getActivites();
                for(int i = 0; i < a.size(); i++){
                    if(a.get(i).getActivityID() == Aid){ //Inte allt för snyggt..
                        activity = a.get(i);
                    }
                }
                inflatePopup(activity.getLabelID(), activity.getActivityID());
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                handler.post(refreshing);
            }
        });
    }

    private void inflatePopup(final int labelID, final int activityID) {
        View popupView = getLayoutInflater().inflate(R.layout.label_options_view, null);
        pw = new PopupWindow(popupView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pw.setAnimationStyle(android.R.style.Animation_Dialog);
        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        labelAdapter = new LabelAdapter(this, R.layout.radio_row, dbLabelHelper.getAllLabels(), labelID, activityID);
        labelListView = (ListView) popupView.findViewById(R.id.popup_listview);

        Button btn = (Button)popupView.findViewById(R.id.button2);
        final EditText editText = (EditText)popupView.findViewById(R.id.editText);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length() > 0) {
                    String str = editText.getText().toString();
                    String f = ""+str.charAt(0);
                    f = f.toUpperCase();
                    str = f+str.substring(1);
                    addAndSetLabel(str, activityID);
                }
            }
        });

        labelListView.setAdapter(labelAdapter);
    }

    private final Runnable refreshing = new Runnable() {
        public void run() {
            try {
                if (isRefreshing()) {
                    handler.postDelayed(this, 1000);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                    getPhysicalActivites();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private boolean isRefreshing() {
        return refresh;
    }

    public void login(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setClass(this, WebViewActivity.class);
        startActivityForResult(intent, AUTHENTICATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTHENTICATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                String code = data.getStringExtra("CODE");
                getAndStoreAccessToken(code);
            }
        }
    }

    private void getAndStoreAccessToken(String code) {
        Resources resources = getResources();
        final String CLIENT_ID = Credentials.CLIENT_ID;
        final String CLIENT_SECRET = Credentials.SECRET;

        Ion.with(this)
                .load("https://platform.lifelog.sonymobile.com/oauth/2/token")
                .setBodyParameter("client_id", CLIENT_ID)
                .setBodyParameter("client_secret", CLIENT_SECRET)
                .setBodyParameter("grant_type", "authorization_code")
                .setBodyParameter("code", code)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e != null) {
                        } else {
                            if (result.getHeaders().code() == 200) {
                                String accessToken = result.getResult().get("access_token").getAsString();
                                String refreshToken = result.getResult().get("refresh_token").getAsString();
                                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
                                editor.putString(PREFS_KEY_ACCESS_TOKEN, accessToken);
                                editor.putString(PREFS_KEY_REFRESH_TOKEN, refreshToken);
                                editor.commit();
                                viewLayout();
                            } else {
                                if (result.getHeaders().code() == 401) {
                                    refreshToken();
                                }
                            }
                        }
                    }

                });

    }

    public void refreshToken() {
        Resources resources = getResources();
        final String CLIENT_ID = Credentials.CLIENT_ID;
        final String CLIENT_SECRET = Credentials.SECRET;
        final String REFRESH_TOKEN = getSharedPreferences(PREFS_NAME, 0)
                .getString(PREFS_KEY_REFRESH_TOKEN, "");

        if (REFRESH_TOKEN.equals("")) {
            return;
        }

        Ion.with(this)
                .load("https://platform.lifelog.sonymobile.com/oauth/2/refresh_token")
                .setBodyParameter("client_id", CLIENT_ID)
                .setBodyParameter("client_secret", CLIENT_SECRET)
                .setBodyParameter("grant_type", "refresh_token")
                .setBodyParameter("refresh_token", REFRESH_TOKEN)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e != null) {
                            Log.e("refreshToken()", e.toString());
                        } else {
                            if (result.getHeaders().code() == 200) {
                                String accessToken = result.getResult().get("access_token").getAsString();
                                String refreshToken = result.getResult().get("refresh_token").getAsString();
                                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
                                editor.putString(PREFS_KEY_ACCESS_TOKEN, accessToken);
                                editor.putString(PREFS_KEY_REFRESH_TOKEN, refreshToken);
                                editor.commit();
                            } else {
                                Log.e("refreshToken()", "CODE != 200");
                            }
                        }
                    }
                });

    }


    private JsonObject getHTTPResponseSync(String url) {
        String accessToken = getSharedPreferences(PREFS_NAME, 0).getString(PREFS_KEY_ACCESS_TOKEN, "");
        if (!accessToken.equals("")) {
            JsonObject obj = null;
            while (obj == null) {
                try {
                    Response<JsonObject> res;
                    res = Ion.with(this)
                            .load(url)
                            .setHeader("Authorization", "Bearer " + accessToken)
                            .asJsonObject()
                            .withResponse()
                            .get();
                    if (res.getHeaders().code() == 200) {
                        obj = res.getResult();
                    } else {
                        if (res.getHeaders().code() == 401) {
                            refreshToken();
                            continue;
                        }
                        continue;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            return obj;
        }
        return null;

    }


    public void getPhysicalActivites() {
        ActivityWrapper activityActivityWrapper; // sätt till vilken aktivitet
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        JsonObject obj = getHTTPResponseSync(ACTIVITIES_URL + "/activities?start_time=" + formattedDate + "T00:00:00.000Z&end_time=" + formattedDate + "T23:59:59.000Z&type=physical");
        if (obj != null) {
            if (adapter != null) {
                adapter.clear();
            }
            JsonArray jsonPhysical = obj.getAsJsonArray("result");
            for (JsonElement i : jsonPhysical) {
                JsonObject newObject = (JsonObject) i;
                String type = newObject.get("subtype").getAsString();
                if (!type.equals("other")) {
                    String date = newObject.get("startTime").getAsString();
                    String theDate = date.substring(0, 10);
                    String theTime = date.substring(11, 19);
                    String outstring = theDate + "-" + theTime;

                    String temp = "";
                    boolean indb = false;
                    for (ActivityWrapper wrapper : dbActivityHelper.printDB()) {
                        temp = wrapper.getDate() + "-" + wrapper.getTime();
                        if (temp.equals(outstring)) {
                            indb = true;
                            break;
                        }
                    }

                    if (indb) {
                        continue;
                    }

                    int steps = 0;
                    float distance = 0;
                    if (!type.equals("bicycle")) {
                        JsonArray jsonStepsArray = newObject.getAsJsonObject("details").getAsJsonArray("steps");
                        for (JsonElement o : jsonStepsArray) {
                            steps += o.getAsInt();
                        }
                        JsonArray jsonDistanceArray = newObject.getAsJsonObject("details").getAsJsonArray("distance");
                        for (JsonElement dist : jsonDistanceArray) {
                            distance += dist.getAsFloat();
                        }
                    }
                    activityActivityWrapper = new ActivityWrapper(theDate, theTime, steps, (int) distance, type, 1);
                    dbActivityHelper.addActivity(activityActivityWrapper);
                }
            }
        }
    }

    public List<ActivityWrapper> getActivites() {
        System.out.println(dbActivityHelper.getTableAsString());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return Arrays.asList(dbActivityHelper.getActivitesByDate(formattedDate));
    }

    public void addAndSetLabel(String text ,int activityID){
        int id = dbLabelHelper.addNewLabel(text);
        setLabel(activityID, id);
        pw.dismiss();
    }

    public void setLabel(int activityID, int labelID) {
        dbActivityHelper.setLabelToActivity(activityID, labelID);
        List<LabelWrapper> l = dbLabelHelper.getAllLabels();
        LabelWrapper[] lw = new LabelWrapper[l.size()+1];
        for(int i = 0; i < l.size(); i++){
            lw[i+1] = l.get(i);
        }
        adapter.updateData(getActivites(),lw);
        adapter.notifyDataSetInvalidated();
        adapter.notifyDataSetChanged();
    }
}
