package com.centennialcollege.james.onlineshopping;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class EditDbActivity extends AppCompatActivity {

    //private fields
    private DatabaseHelper _dbHelper= null;
    private String _editType;
    private int _pkId;
    private int _fgmtId;
    private int _customerId;
    private int _empolyeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_db);

        //get intent value
        getIntentValues();

        //initTextViewTitle
        initTitle();

        //set query invisible when place order and visible when edit order
        setQueryInput();
    }

    private void initTitle() {
        TextView textView = findViewById(R.id.txvEditTitle);
        String title = null;

        switch (_editType)
        {
            case "item_rep":
                title = "Edit Item -- Representative";
                break;
            case "order_rep":
                title = "Edit Order -- Representative";
                break;
            case "placeOrder_customer":
                title = "Place Order -- Customer";
                break;
            case "order_customer":
                title = "Edit Order -- Customer";
                break;
            case "edit_userInfo_customer":
                title = "Edit User Information -- Customer";
                break;
            case "edit_userInfo_rep":
                title = "Edit User Information -- Representative";
                break;
        }
        textView.setText(title);
    }

    //set query input GONE and generate fragment when place order
    private void setQueryInput(){
        LinearLayout llQuery=findViewById(R.id.llQuery);

        if (_editType.contains("placeOrder_customer")||_editType.contains("edit_userInfo_customer")||_editType.contains("edit_userInfo_rep"))
        {
            llQuery.setVisibility(View.GONE);
            generateFragment();
        }
        else
        {
            llQuery.setVisibility(View.VISIBLE);
        }

    }

    private void getIntentValues() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        _editType = bundle.getString("editType");
        _customerId=bundle.getInt("customerId");
        _empolyeeId=bundle.getInt("employeeId");

    }

    public void getPrimaryKey(View view) {
        EditText editText = findViewById(R.id.edtPK);
        _pkId = Integer.parseInt(editText.getText().toString());

        if (_editType.contains("order_customer")){
            //get value from db
            _dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
            Cursor cursor = _dbHelper.displayOrderForCustomer(_pkId,_customerId);
            if (0==cursor.getCount()){
                AlertDialog.Builder builder = new AlertDialog.Builder(EditDbActivity.this);
                builder.setTitle("Query failed!").setMessage("Order does NOT exist, please check again!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
            else {
                //generate fragment
                generateFragment();
            }
            cursor.close();
            _dbHelper.close();
        }
        else{
            //generate fragment
            generateFragment();
        }


    }

    private void generateFragment() {

        LinearLayout fragContainer = findViewById(R.id.llFragmentContainer);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        _fgmtId = View.generateViewId();
        ll.setId(_fgmtId);

        android.support.v4.app.Fragment fragment = null;
        switch (_editType)
        {
            case "item_rep":
                fragment = ItemFragment.newInstance(_pkId);
                break;
            case "order_rep":
                fragment = OrderFragment.newInstance(_pkId);
                break;
            case "placeOrder_customer":
                fragment = CustOrderFragment.newInstance(_pkId,_customerId,_editType);
                break;
            case "order_customer":
                fragment = CustOrderFragment.newInstance(_pkId,_customerId,_editType);
                break;
            case "edit_userInfo_customer":
                fragment = UserInfoFragment.newInstance(_customerId,_editType);
                break;
            case "edit_userInfo_rep":
                fragment = UserInfoFragment.newInstance(_empolyeeId,_editType);
                break;
        }
        android.support.v4.app.FragmentManager manager =  getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(ll.getId(), fragment);
        transaction.commit();
        fragContainer.addView(ll);
    }
}
