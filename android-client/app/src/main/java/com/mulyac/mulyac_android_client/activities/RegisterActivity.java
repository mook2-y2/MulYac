package com.mulyac.mulyac_android_client.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mulyac.mulyac_android_client.R;
import com.mulyac.mulyac_android_client.utils.StorageController;
import com.mulyac.mulyac_android_client.utils.Utils;


public class RegisterActivity extends Activity {

    EditText mUserIdEditText;
    EditText mRegionEditText;
    private StorageController mStorageController;
    private String mUserId;
    private String mRegionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mStorageController = new StorageController(getApplicationContext(), Utils.INFO_STORAGE);
        mUserIdEditText = (EditText) findViewById(R.id.userIdEditText);
        mUserIdEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true;
                }


                return false;
            }
        });
    }


    public void onClickRegister(View view){
        mUserId = mUserIdEditText.getText().toString();
        mStorageController.set(Utils.USERID_STRING, mUserId);
        Intent intent = new Intent(RegisterActivity.this, ViewActivity.class);
        startActivity(intent);
    }

}
