package com.centennialcollege.james.onlineshopping;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends Fragment implements View.OnClickListener{
    private DatabaseHelper _dbHelper= null;
    private int _userId;
    private TextView _txvUserId;
    private EditText _edtxtUserName;
    private EditText _edtxtPassword;
    private EditText _edtxtConfPassword;
    private EditText _edtxtFirstName;
    private EditText _edtxtLastName;
    private EditText _edtxtAddress;
    private EditText _edtxtPostalCode;
    private String _editType;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    //use factory to instantiate
    public static UserInfoFragment newInstance( int userId,String editType) {
        UserInfoFragment f = new UserInfoFragment();
        Bundle b = new Bundle();
        b.putInt("userId", userId);
        b.putString("editType",editType);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_info, container, false);
        //set click event listener
        Button btnEditUserInfo = v.findViewById(R.id.btnEditUserInfo);
        btnEditUserInfo.setOnClickListener(this);

        _txvUserId=v.findViewById(R.id.txvUserId);
        _edtxtUserName = v.findViewById(R.id.edtxtUserName);
        _edtxtPassword = v.findViewById(R.id.edtxtPassword);
        _edtxtConfPassword=v.findViewById(R.id.edtxtConfPassword);
        _edtxtFirstName = v.findViewById(R.id.edtxtFirstName);
        _edtxtLastName = v.findViewById(R.id.edtxtLastName);
        _edtxtAddress = v.findViewById(R.id.edtxtAddress);
        _edtxtPostalCode = v.findViewById(R.id.edtxtPostalCode);
        LinearLayout llCustAddandPost=v.findViewById(R.id.llCustAddandPost);

        //get value from db
        _dbHelper= new DatabaseHelper(getActivity());

        //get customerId and editType
        _userId=this.getArguments().getInt("userId");
        _editType=this.getArguments().getString("editType");

        //get values from db if edit order
        switch (_editType)
        {
            case "add_userInfo_customer":
                _txvUserId.setText(Integer.toString(_userId));
                btnEditUserInfo.setText("Add New Customer");
                break;
            case"add_userInfo_Rep":
                llCustAddandPost.setVisibility(View.GONE);
                _txvUserId.setText(Integer.toString(_userId));
                btnEditUserInfo.setText("Add New Rep");
                break;
            case "edit_userInfo_customer":
                btnEditUserInfo.setText("Edit Customer Info");
                Cursor cursor = _dbHelper.displayCustomerInfo(_userId);
                if(1 == cursor.getCount()){
                    cursor.moveToFirst();
                    _txvUserId.setText(cursor.getString(cursor.getColumnIndex("customerId")));
                    _edtxtUserName.setText(cursor.getString(cursor.getColumnIndex("userName")));
                    _edtxtPassword.setText(cursor.getString(cursor.getColumnIndex("userPwd")));
                    _edtxtConfPassword.setText(cursor.getString(cursor.getColumnIndex("userPwd")));
                    _edtxtFirstName.setText(cursor.getString(cursor.getColumnIndex("firstName")));
                    _edtxtLastName.setText(cursor.getString(cursor.getColumnIndex("lastName")));
                    _edtxtAddress.setText(cursor.getString(cursor.getColumnIndex("address")));
                    _edtxtPostalCode.setText(cursor.getString(cursor.getColumnIndex("postalCode")));
                }

                else  //did not find the item
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Query failed!").setMessage("Customer does NOT exist")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
                cursor.close();
                break;
            case "edit_userInfo_rep":
                llCustAddandPost.setVisibility(View.GONE);
                btnEditUserInfo.setText("Edit Rep Info");
                cursor = _dbHelper.displayRepInfo(_userId);
                if(1 == cursor.getCount()){
                    cursor.moveToFirst();
                    _txvUserId.setText(cursor.getString(cursor.getColumnIndex("employeeId")));
                    _edtxtUserName.setText(cursor.getString(cursor.getColumnIndex("userName")));
                    _edtxtPassword.setText(cursor.getString(cursor.getColumnIndex("userPwd")));
                    _edtxtConfPassword.setText(cursor.getString(cursor.getColumnIndex("userPwd")));
                    _edtxtFirstName.setText(cursor.getString(cursor.getColumnIndex("firstName")));
                    _edtxtLastName.setText(cursor.getString(cursor.getColumnIndex("lastName")));

                }

                else  //did not find the item
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Query failed!").setMessage("Representative does NOT exist")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
                cursor.close();
                break;
                default:
                    break;

        }


        _dbHelper.close();
        return v;
        }// end of OnCreate

    public boolean onUpdateRecord()
    {
        boolean result=false;

        boolean validationResult=validateInput();
        if (true==validationResult){
            //get value from db
            _dbHelper= new DatabaseHelper(getActivity());
            //set content values
            ContentValues cv = new ContentValues();
            View v = getView();
            int nofRows=0;
            if (_editType.contains("edit_userInfo_customer")){
                cv.put("userName",_edtxtUserName.getText().toString());
                cv.put("userPwd",_edtxtPassword.getText().toString());
                cv.put("firstName",_edtxtFirstName.getText().toString());
                cv.put("lastName",_edtxtLastName.getText().toString());
                cv.put("address",_edtxtAddress.getText().toString());
                cv.put("postalCode",_edtxtPostalCode.getText().toString());

                int pkid = this.getArguments().getInt("userId");
                nofRows = _dbHelper.updateTable("Customer", cv, "customerId = ?", new String[]{String.valueOf(pkid)});
            }

            else if (_editType.contains("edit_userInfo_rep")){
                cv.put("userName",_edtxtUserName.getText().toString());
                cv.put("userPwd",_edtxtPassword.getText().toString());
                cv.put("firstName",_edtxtFirstName.getText().toString());
                cv.put("lastName",_edtxtLastName.getText().toString());
                int pkid = this.getArguments().getInt("userId");
                nofRows = _dbHelper.updateTable("OrderRep", cv, "employeeId = ?", new String[]{String.valueOf(pkid)});
            }

            //close
            _dbHelper.close();
            if (1 == nofRows)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Update status").setMessage("Update user information successful!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                result = true;
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Update status").setMessage("Update user information failed!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                result = false;
            }
        }

        return result;
    }

    public boolean onInsertRecord(){
        boolean result=false;

        boolean validationResult=validateInput();
        if (true==validationResult){
            //get value from db
            _dbHelper= new DatabaseHelper(getActivity());
            //set content values
            ContentValues cv = new ContentValues();
            View v = getView();
            long nofRows=0;
            if (_editType.contains("add_userInfo_customer")){
                cv.put("customerId",_userId);
                cv.put("userName",_edtxtUserName.getText().toString());
                cv.put("userPwd",_edtxtPassword.getText().toString());
                cv.put("firstName",_edtxtFirstName.getText().toString());
                cv.put("lastName",_edtxtLastName.getText().toString());
                cv.put("address",_edtxtAddress.getText().toString());
                cv.put("postalCode",_edtxtPostalCode.getText().toString());

                nofRows=_dbHelper.insertRecord("Customer",cv);
            }
            else if (_editType.contains("add_userInfo_Rep"))
            {
                cv.put("employeeId",_userId);
                cv.put("userName",_edtxtUserName.getText().toString());
                cv.put("userPwd",_edtxtPassword.getText().toString());
                cv.put("firstName",_edtxtFirstName.getText().toString());
                cv.put("lastName",_edtxtLastName.getText().toString());
                nofRows=_dbHelper.insertRecord("OrderRep",cv);
            }


            //close
            _dbHelper.close();
            if (_userId == nofRows)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


                builder.setTitle("Registration status").setMessage("Registrate successful! Go to next step!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getActivity(), CustomerActivity.class);
                                if (_editType.contains("add_userInfo_Rep")){
                                    intent = new Intent(getActivity(), RepActivity.class);
                                }
                                //share pref
                                SharedPreferences myP =  getActivity().getSharedPreferences("Type", MODE_PRIVATE);
                                SharedPreferences.Editor prefE = myP.edit();
                                prefE.putString("UserName", _edtxtUserName.getText().toString());
                                prefE.apply();
                                startActivity(intent);
                            }
                        }).show();
                result = true;
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Registration status").setMessage("Registrate failed!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                result = false;
            }

        }


        return result;
    }

    @Override
    public void onClick(View v) {
        switch (_editType) {
            case "edit_userInfo_customer":
                this.onUpdateRecord();
                break;
            case "edit_userInfo_rep":
                this.onUpdateRecord();
                break;
            case "add_userInfo_customer":
                this.onInsertRecord();
                break;
            case "add_userInfo_Rep":
                this.onInsertRecord();
                break;

        }
    }

    private boolean validateInput(){
        boolean result=false;

        String pwdRegex = "^(?=.*?[A-Za-z])(?=.*?[0-9]).{6,}$";
        Pattern pwdPattern = Pattern.compile(pwdRegex);
        String pwd=_edtxtPassword.getText().toString().trim();
        Matcher pwdMatcher = pwdPattern.matcher(pwd);

        if (_edtxtUserName.getText().toString().trim().equals(""))
        {
            _edtxtUserName.requestFocus();
            _edtxtUserName.setError( "User name is required!" );
        }
        else if (pwd.equals(""))
        {
            _edtxtPassword.requestFocus();
            _edtxtPassword.setError("Password is required!");
        }
        else if (false==pwdMatcher.matches()){
            _edtxtPassword.requestFocus();
            _edtxtPassword.setError("Password must contain at least one English letter, one digit, minimum six in length");
        }
        else if (_edtxtConfPassword.getText().toString().trim().equals(""))
        {
            _edtxtConfPassword.requestFocus();
            _edtxtConfPassword.setError("Please confirm your password!");
        }
        else if (!_edtxtConfPassword.getText().toString().trim().matches(_edtxtPassword.getText().toString().trim())){
            _edtxtConfPassword.requestFocus();
            _edtxtConfPassword.setError("Password does not match!");
        }
        else if (_edtxtFirstName.getText().toString().trim().equals("")){
            _edtxtFirstName.requestFocus();
            _edtxtFirstName.setError("First Name is required!");
        }
        else if (_edtxtLastName.getText().toString().trim().equals("")){
            _edtxtLastName.requestFocus();
            _edtxtLastName.setError("Last Name is required!");
        }
        else if (_editType.contains("add_userInfo_customer")||_editType.contains("edit_userInfo_customer")){
            String postalRegex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z]?[0-9][A-Z][0-9]$";
            Pattern postalPattern = Pattern.compile(postalRegex);
            String postalCode=_edtxtPostalCode.getText().toString().trim();
            Matcher postalMatcher = postalPattern.matcher(postalCode);

            if (_edtxtAddress.getText().toString().trim().equals("")){
                _edtxtAddress.requestFocus();
                _edtxtAddress.setError("Address is required!");
            }
            else if (postalCode.equals("")){
                _edtxtPostalCode.requestFocus();
                _edtxtPostalCode.setError("Postal code is required!");
            }
            else if (false==postalMatcher.matches()){
                _edtxtPostalCode.requestFocus();
                _edtxtPostalCode.setError("Invalid postal code!");
            }
            else{
                result=true;
            }
        }

        else {
            result=true;
        }

        return result;
    }

    }

