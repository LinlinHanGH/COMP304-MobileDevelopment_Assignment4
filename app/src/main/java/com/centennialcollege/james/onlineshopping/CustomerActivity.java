package com.centennialcollege.james.onlineshopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CustomerActivity extends AppCompatActivity {

    //private fields
    private DatabaseHelper _dbHelper= null;
    private int _customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        //init customer user
        initCustUser();

        //init customer options
        initCustOptions();

    }// end of onCreate

    private void initCustUser() {
        //get user name
        SharedPreferences myP = getSharedPreferences("Type", MODE_PRIVATE);
        String custUsrName = myP.getString("UserName", "");

        //query customer info from customer table through username
        _dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        String custFlName =  "Hello Customer: " + _dbHelper.getLoginUserProfile(custUsrName, true);
        TextView textView = findViewById(R.id.txvCustUser);
        textView.setText(custFlName);

        // get customerId
        _customerId=_dbHelper.getUserId("customerId","Customer",custUsrName);

        _dbHelper.close();
    }

    private void initCustOptions() {
        //get options from string array
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.cust_options));
        ListView listview = findViewById(R.id.lvCustOptions);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent;
                        Bundle extras = new Bundle();
                        switch (position)
                        {
                            case 0:
                                intent = new Intent(CustomerActivity.this, CustViewMyOrdersActivity.class);
                                extras.putInt("customerId",_customerId);
                                intent.putExtras(extras);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(CustomerActivity.this, EditDbActivity.class);
                                extras.putInt("customerId",_customerId);
                                extras.putString("editType", "placeOrder_customer");
                                intent.putExtras(extras);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(CustomerActivity.this, EditDbActivity.class);
                                extras.putInt("customerId",_customerId);
                                extras.putString("editType", "order_customer");
                                intent.putExtras(extras);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(CustomerActivity.this, EditDbActivity.class);
                                extras.putInt("customerId",_customerId);
                                extras.putString("editType", "edit_userInfo_customer");
                                intent.putExtras(extras);
                                startActivity(intent);
                                break;

                            default:
                                break;
                        }
                    }
                }
        );
    }
}
