package com.montequality.smarthouse.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.AsyncTask;

import com.montequality.smarthouse.HomeActivity;
import com.montequality.smarthouse.LoginActivity;
import com.montequality.smarthouse.R;
import com.montequality.smarthouse.util.HTTPHelper;

public class AuthenticateTask extends AsyncTask<Void, Boolean, Boolean> {

    public LoginActivity activity;

    private String username = "";
    private String pass = "";

    public AuthenticateTask(LoginActivity activity, String username, String password) {
        this.activity = activity;
        this.username = username;
        this.pass = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", pass));

        
        HTTPHelper httpHelper = new HTTPHelper("hostURL", "authenticateMethod", nameValuePairs, activity);
        
        if(!httpHelper.executeHelper().equalsIgnoreCase("-1")){
            activity.getSharedPrefs().getEditor().putString("userID", httpHelper.executeHelper());
            return true;
        }else{
            return false;
        }

    }

    @Override
    protected void onPostExecute(final Boolean success) {

        activity.showProgress(false);
        activity.mAuthTask = null;

        if (success) {

            if (activity.saveCheck.isChecked()) {
                activity.getSharedPrefs().getEditor()
                        .putString("username", activity.mUsernameView.getText().toString());
                activity.getSharedPrefs().getEditor()
                        .putString("password", activity.mPasswordView.getText().toString());
                activity.getSharedPrefs().getEditor().commit();
            }

            Intent intent = new Intent(activity, HomeActivity.class);
            activity.startActivity(intent);
        } else {
            activity.mUsernameView.setError(activity.getString(R.string.error_username_or_password));
            activity.mUsernameView.requestFocus();
        }
    }

    @Override
    protected void onCancelled() {
        activity.mAuthTask = null;
        activity.showProgress(false);
    }

}
