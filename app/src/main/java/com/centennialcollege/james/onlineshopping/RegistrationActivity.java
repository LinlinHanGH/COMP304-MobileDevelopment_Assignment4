package com.centennialcollege.james.onlineshopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegistrationActivity extends AppCompatActivity {
    private DatabaseHelper _dbHelper= null;
    private int _fgmtId;
    private int _customerId;
    private int _employeeId;
    private String _registrationType;
    private String _editType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //get user name
        SharedPreferences myP = getSharedPreferences("Type", MODE_PRIVATE);
        _registrationType = myP.getString("LoginType", "");

        initTitle();
        generateFragment();

    }//end of OnCreate

    private void initTitle() {
        TextView textView = findViewById(R.id.txvRegiTitle);
        String title = null;

        switch (_registrationType)
        {
            case "Rep":
                title = "Registration -- Representative";
                break;
            case "customer":
                title = "Registration -- Customer";
                break;
        }
        textView.setText(title);
    }

    private void generateFragment() {

        //get value from db
        _dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        LinearLayout fragContainer = findViewById(R.id.llFragmentContainer);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        _fgmtId = View.generateViewId();
        ll.setId(_fgmtId);

        android.support.v4.app.Fragment fragment = null;
        switch (_registrationType)
        {
            case "customer":
                _editType="add_userInfo_customer";
                _customerId=_dbHelper.getMaxId("customerId","Customer")+1;
                fragment = UserInfoFragment.newInstance(_customerId,_editType);
                break;
            case "Rep":
                _editType="add_userInfo_Rep";
                _employeeId=_dbHelper.getMaxId("employeeId","OrderRep")+1;
                fragment = UserInfoFragment.newInstance(_employeeId,_editType);
                break;
        }
        android.support.v4.app.FragmentManager manager =  getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(ll.getId(), fragment);
        transaction.commit();
        fragContainer.addView(ll);
    }
}
