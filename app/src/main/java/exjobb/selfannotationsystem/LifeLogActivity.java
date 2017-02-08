package exjobb.selfannotationsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;



public class LifeLogActivity extends Activity {
    private static final int AUTHENTICATION_REQUEST = 1;
    private static final String PREFS_NAME = "PrefsFile";
    private static final String PREFS_KEY_ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String PREFS_KEY_REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String PROFILE_URL = "https://platform.lifelog.sonymobile.com/v1/users/me";
    private static final String ACTIVITIES_URL = "https://platform.lifelog.sonymobile.com/v1/users/me";

    private ActivitysFeedAdapter adapter;
    private List<Label> labels = new ArrayList<>();
    private ListView feedListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = getSharedPreferences(PREFS_NAME, 0).getString(PREFS_KEY_ACCESS_TOKEN, "");

        if(token.equals("")) {
            setContentView(R.layout.button);
            final Button login = (Button)findViewById(R.id.button);
            final Button steps = (Button)findViewById(R.id.getsteps);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login(v);
                }
            });

            steps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("VIKT ", ""+getWeight());
                    Log.d("STEPS", ""+getSteps());
                }
            });
        } else {
            viewLayout();
        }
    }

    private void viewLayout() {
        Log.d("viewlayout()", "skapa");

        for(int i = 0; i < 10; i++) {       // Dummy loop
            getSteps();
            getWeight();
        }

        labels.add(new Label("steps", 2000, null)); // Dummy value
        setContentView(R.layout.label_view);
        adapter = new ActivitysFeedAdapter(this, R.layout.row_view, labels);
        feedListView = (ListView) findViewById(R.id.feed_list_view);
        feedListView.setAdapter(adapter);
        Log.d("viewlayout()", "efter adapter");
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public void login(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setClass(this, WebViewActivity.class);
        startActivityForResult(intent, AUTHENTICATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == AUTHENTICATION_REQUEST) {
            if (resultCode == RESULT_OK){
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
                            Log.d("KALAS", "BRA?!");
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

    public void refreshToken(){
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
                        }
                        else {
                            if (result.getHeaders().code() == 200) {
                                String accessToken = result.getResult().get("access_token").getAsString();
                                String refreshToken = result.getResult().get("refresh_token").getAsString();
                                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
                                editor.putString(PREFS_KEY_ACCESS_TOKEN, accessToken);
                                editor.putString(PREFS_KEY_REFRESH_TOKEN, refreshToken);
                                editor.commit();
                            }
                            else {
                                Log.e("refreshToken()", "CODE != 200");
                            }
                        }
                    }
                });

    }


    private JsonObject getHTTPResponseSync(String url){
        String accessToken = getSharedPreferences(PREFS_NAME, 0).getString(PREFS_KEY_ACCESS_TOKEN, "");
        if(!accessToken.equals("")) {
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
    private Double getBMR(){
        JsonObject obj = getHTTPResponseSync(PROFILE_URL);
        if (obj != null) {
            return obj.getAsJsonArray("result").get(0).getAsJsonObject().get("bmr").getAsDouble();
        }
        else {
            return null;
        }
    }

    private Double getBM(){
        Double bmr = getBMR();
        Calendar cal = Calendar.getInstance();
        Double hour = cal.get(Calendar.HOUR_OF_DAY) + (cal.get(Calendar.MINUTE) / 60.0);
        Log.i("HOUR", Double.toString(hour));

        return bmr * hour;
    }

    private Double getCalorieFromJson(JsonObject o){
        Double sumCalorie = 0.0;
        for(JsonElement je : o.getAsJsonArray("result")){
            Log.i("Activity ID", je.getAsJsonObject().get("id").getAsString());
            Double calorie = 0.0;
            JsonArray ja = je.getAsJsonObject().get("details").getAsJsonObject().getAsJsonArray("aee");
            if( ja == null)
                continue;
            for (JsonElement e : ja){
                calorie += e.getAsDouble();
            }
            sumCalorie += calorie;
            Log.i("Calorie", Double.toString(calorie));
        }
        return sumCalorie;
    }

    public Double getCalorie(View v) {
        String url = "https://platform.lifelog.sonymobile.com/v1/users/me/activities";// +
//                    "?start_time=2015-02-22T00:00:00.000+0900";
        JsonObject obj = getHTTPResponseSync(url);
        Double aae = getCalorieFromJson(obj);
        Double bm = getBM();
        return aae + bm;
    }

    public Double getWeight() {
        Label label = new Label("Weight");
        JsonObject obj = getHTTPResponseSync(PROFILE_URL);
        if (obj != null) {
            label.setValue((int)obj.getAsJsonArray("result").get(0).getAsJsonObject().get("weight").getAsDouble());
            label.setActivityType("weight");
            labels.add(label);
            setLabels(labels);
            return obj.getAsJsonArray("result").get(0).getAsJsonObject().get("weight").getAsDouble();
        }
        else {
            Log.e("getWeight()", "ERROR: return dummy value (70.0 kg)");
            return 70.0;
        }
    }

    public int getSteps(){
        Label labelsteps = new Label("Steps");
        JsonObject obj = getHTTPResponseSync(ACTIVITIES_URL+ "/activities?start_time=2017-02-06T00:00:00.000Z&end_time=2017-02-08T20:00:00.000Z&type=physical:walk");
        if(obj != null){

            JsonObject x = (JsonObject)obj.getAsJsonArray("result").get(0);
            x = x.getAsJsonObject("details");

            JsonArray y = x.getAsJsonArray("steps");
            int sum = 0;
            for(JsonElement o : y){
                sum += o.getAsInt();
            }
            labelsteps.setValue(sum);
            labelsteps.setActivityType("steps");
            labels.add(labelsteps);
            setLabels(labels);
            return sum;
        }else{
            Log.d("NULL", "NULL");
            return 0;
        }

    }
}
