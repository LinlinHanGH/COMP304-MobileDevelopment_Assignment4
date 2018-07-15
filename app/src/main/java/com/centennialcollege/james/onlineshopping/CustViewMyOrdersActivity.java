package com.centennialcollege.james.onlineshopping;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class CustViewMyOrdersActivity extends AppCompatActivity {
    //private fields
    private DatabaseHelper _dbHelper= null;
    private int _customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_view_my_orders);

        displayMyOrders();
    }

    private void displayMyOrders()
    {
        //get customerId
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        _customerId = bundle.getInt("customerId");
        //prepare db
        _dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        //display all orders
        ListView listView = findViewById(R.id.lsvMyOrders);
        Cursor allOrderCursor = _dbHelper.displayMyOrders(_customerId);
        OrderCursorAdapter orderCursorAdapter = new OrderCursorAdapter(this, allOrderCursor, 0);
        listView.setAdapter(orderCursorAdapter);
        //close db
        _dbHelper.close();
    }
}
