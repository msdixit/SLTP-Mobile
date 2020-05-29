package com.sparc.frjvcapp;

import android.Manifest;
import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sparc.frjvcapp.config.AllApi;
import com.sparc.frjvcapp.pojo.M_fb;
import com.sparc.frjvcapp.pojo.M_range;

import org.joda.time.DateTimeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity {
    public static final String userlogin = "userlogin";
    public static final String m_pillar_reg = "m_pillar_reg";
    private static final String CREATE_m_pillar_reg_Table = "CREATE TABLE IF NOT EXISTS " + m_pillar_reg + "( " +
            "id INTEGER PRIMARY KEY, " +
            "d_id Text, " +
            "r_id Text, " +
            "fb_id Text, " +
            "p_sl_no Text, " +
            "p_lat TEXT, " +
            "p_long TEXT, " +
            "p_type TEXT," +
            "p_cond TEXT," +
            "p_rmk TEXT," +
            "p_pic TEXT," +
            "p_sts Text," +
            "patch_no TEXT," +
            "ring_no TEXT," +
            "p_loc_type TEXT," +
            "p_no INTEGER," +
            "p_paint_status TEXT," +
            "fb_name TEXT," +
            "uid TEXT," +
            "point_no INTEGER," +
            "img_status TEXT," +
            "delete_status TEXT," +
            "shifting_status TEXT," +
            "surv_direction TEXT," +
            "p_accuracy TEXT," +
            "survey_dt TEXT)";

    ProgressDialog progressDialog;
    ProgressDialog progress;
    DbHelper dbHelper;
    ArrayList<String> cmv_list;
    ArrayList<String> mmv_list;
    Context context;
    private ImageView bookIconImageView;
    private TextView bookITextView, skipTextView, macaddress;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView;
    LinearLayout afterAnimationView;
    private TextInputEditText txtemail, txtpassword, otpEditText;
    private Button login, getOTPButton;
    private String password;
    private DbHelper.DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    ArrayList<Integer> headlist = new ArrayList<>();
    ArrayList<Integer> subheadlist = new ArrayList<>();
    int[] head = new int[]{1, 2, 3, 12};
    int[] subhead = new int[]{4, 5, 6, 7, 10, 11};
    TelephonyManager telephonyManager;
    String _token, _serviceOTP;
    SimpleDateFormat _startDateFormat, _endDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        //Log.d("login", "service");

        dbHelper = new DbHelper(this);
        mDb = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        mDb.execSQL(CREATE_m_pillar_reg_Table);

        txtemail = findViewById(R.id.emailEditText);
        txtpassword = findViewById(R.id.passwordEditText);
        otpEditText = findViewById(R.id.otpEditText);

        macaddress = findViewById(R.id.macaddress);
        macaddress.setText("MAC ID:" + getMyMacAddress());

        login = findViewById(R.id.loginButton);
        getOTPButton = findViewById(R.id.getOTPButton);
        login.setVisibility(GONE);

        for (int id : head) {
            headlist.add(id);
        }
        for (int id : subhead) {
            subheadlist.add(id);
        }

        initViews();

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //bookITextView.setVisibility(GONE);
                loadingProgressBar.setVisibility(VISIBLE);
                // bookIconImageView.setImageResource(R.drawable);
                rootView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorBackground));
            }

            @Override
            public void onFinish() {
                bookIconImageView.setImageResource(R.drawable.black_tree);
                rootView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorSplashText));
                rootView.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.bg));
                loadingProgressBar.setVisibility(GONE);
                startAnimation();

            }
        }.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasPermissions()) {
                SharedPreferences sharedPreferences = getSharedPreferences(userlogin, 0);
                if (!sharedPreferences.getString("uemail", "0").equals("0")) {
                    Intent intent = new Intent(getApplicationContext(), MainContainerActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                requestPerms();
            }
        }

        otpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!(otpEditText.toString().trim().isEmpty()))
                    login.setVisibility(VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtemail.getText().toString().matches("")) {
                    txtemail.setError("Please Enter the User Id");
                } else if (txtpassword.getText().toString().matches("")) {
                    txtpassword.setError("Please Enter Password");
                } else {
                    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cm.getActiveNetworkInfo();
                    if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
                        get_OTP(txtemail.getText().toString(), txtpassword.getText().toString());

                    } else {
                        Toast.makeText(getApplicationContext(), "You don't have Internet Connection.", Toast.LENGTH_SHORT).show();
                    }
                    // DownloadAllKMLFile();

                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtemail.getText().toString().matches("")) {
                    txtemail.setError("Please Enter the User Id");
                } else if (txtpassword.getText().toString().matches("")) {
                    txtpassword.setError("Please Enter Password");
                } else if (otpEditText.getText().toString().matches("")) {
                    txtpassword.setError("Please Enter OTP");
                } else {
                    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cm.getActiveNetworkInfo();
                    if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
                        login_auth(txtpassword.getText().toString(), otpEditText.getText().toString(), _serviceOTP, getMyMacAddress());
                    } else {
                        Toast.makeText(getApplicationContext(), "You don't have Internet Connection.", Toast.LENGTH_SHORT).show();
                    }
                    // DownloadAllKMLFile();

                }
            }
        });
    }

    private String getMyMacAddress() {
        String macAddress = "";
        telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
            macAddress = "";
        } else {
            macAddress = android.provider.Settings.Secure.getString(this.getApplicationContext().getContentResolver(), "android_id");
        }
        return macAddress;
    }

    //Permission methods
    private boolean hasPermissions() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allowed = true;

        switch (requestCode) {
            case 1:

                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {

        } else {
            Toast.makeText(this, "Application will not work if you dined the permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        bookIconImageView = findViewById(R.id.bookIconImageView);
        //bookITextView = findViewById(R.id.bookITextView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        rootView = findViewById(R.id.rootView);
        afterAnimationView = findViewById(R.id.afterAnimationView);
    }

    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = bookIconImageView.animate();
        //viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                afterAnimationView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private boolean login_auth(String pass, String _OTP, String _sOTP, String mac_add) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = BuildConfig.LOG_IN_API + txtemail.getText().toString() + "/" + pass + "/" + _OTP + "/" + _sOTP + "/" + mac_add;
            progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
            progressDialog.setMessage("Please wait...You are logging in to GFLO");
            progressDialog.show();
            //progressDialog = ProgressDialog.show(LoginActivity.this, "", "Please wait...You are logging in to GFLO", false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
                try {

                    _startDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                    String currentDateandTime = _startDateFormat.format(new Date());

                    JSONObject obj = new JSONObject(response);
                    JSONArray arr = obj.getJSONArray("info");
                    _token = obj.getString("token");
                    //Toast.makeText(this, "hsh", Toast.LENGTH_SHORT);
                    // JSONArray arr = new JSONArray(response);
                    if (arr.length() > 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            progressDialog.dismiss();
                            JSONObject jsonobject = arr.getJSONObject(i);
                            Intent intent = new Intent(getApplicationContext(), MainContainerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            SharedPreferences sharedPreferences = getSharedPreferences(userlogin, 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.putString("uemail", txtemail.getText().toString());
                            editor.putString("upass", txtpassword.getText().toString());
                            editor.putString("uname", jsonobject.getString("chrv_name"));
                            editor.putString("upos", jsonobject.getString("chrv_designation_nm"));
                            editor.putString("logintime", currentDateandTime);

                            if (Integer.parseInt(jsonobject.getString("circle_id")) == 0) {
                                editor.putString("ucir", "");
                                editor.putString("udivid", "");
                                editor.putString("udivname", "");
                            } else {
                                editor.putString("ucir", jsonobject.getString("chrv_circle_nm"));
                                editor.putString("udivid", jsonobject.getString("div_id"));
                                editor.putString("udivname", jsonobject.getString("chrv_division_nm"));
                            }

                            editor.putString("uid", jsonobject.getString("chrv_email"));
                            editor.putString("userdivid", jsonobject.getString("desig_id"));
                            editor.putString("token", _token);
                            //editor.putString("startdate",Date.getTime());
                            editor.apply();

                            if (subheadlist.contains(Integer.parseInt(jsonobject.getString("desig_id")))) {
                                //editor.putString("userdivid", jsonobject.getString("chrv_division_nm"));
                                insertrangeData(jsonobject.getString("div_id"));
                            }
                            startActivity(intent);
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        final int httpStatusCode = error.networkResponse.statusCode;
                        progressDialog.dismiss();
                        if (httpStatusCode == 400) {
                            Toast.makeText(LoginActivity.this, "Unauthorized User", Toast.LENGTH_LONG).show();

                        } else if (httpStatusCode == 401) {
                            Toast.makeText(LoginActivity.this, "Invalid User or Password or OTP", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private void insertrangeData(final String div_id) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = BuildConfig.F_D_RANGE_DATA_API + div_id;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            M_range m_range = new M_range(object.getString("range"),
                                    object.getString("id"),
                                    div_id);
                            dbHelper.open();
                            dbHelper.insertRangeData(m_range);
                            dbHelper.close();
                        }
                        inserfbdata(div_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "This division doesn't have any range", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Server Error Try Again", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", "Bearer " + _token);
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inserfbdata(final String div_id) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = BuildConfig.F_D_FB_DATA_API + div_id;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            M_fb m_fb = new M_fb(object.getString("fb"),
                                    object.getString("fid"),
                                    object.getString("rid"),
                                    div_id,
                                    object.getString("fb_type"),
                                    object.getString("cmv_path"),
                                    object.getString("mmv_path"), "");//object.getString("point_path")

                            dbHelper.open();
                            dbHelper.inserFBData(m_fb);
                            dbHelper.close();

                        }
                        //inserfbdata(div_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "you have no points.", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Server Error Try Again", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", "Bearer " + _token);
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String get_OTP(String _userID, String _password) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = BuildConfig.OTP_IN_API + _userID + "/" + _password;//+"/"+_macaddress

            progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
            progressDialog.setMessage("Wait for sometime...");
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, response -> {
                try {
                    int _otp = Integer.parseInt(response);
                    if (_serviceOTP != "")
                        _serviceOTP = "";
                    _serviceOTP = String.valueOf(_otp);

                    if (_serviceOTP.equals("2")) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Your mobile number is not registered with us..Please contact server admin", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "OTP sent to your registered mobile number..", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        final int httpStatusCode = error.networkResponse.statusCode;
                        if (httpStatusCode == 400) {
                            Toast.makeText(LoginActivity.this, "Invalid User or Password", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
                    }
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _serviceOTP;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
