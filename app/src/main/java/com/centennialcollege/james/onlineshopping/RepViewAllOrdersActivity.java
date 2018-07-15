package com.centennialcollege.james.onlineshopping;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

public class RepViewAllOrdersActivity extends AppCompatActivity {

    //private fields
    private DatabaseHelper _dbHelper= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_view_all_orders);

        displayAllOrders();
    }

    private void displayAllOrders()
    {
        //prepare db
        _dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        //display all orders
        ListView listView = findViewById(R.id.lsvOrders);
        Cursor allOrderCursor = _dbHelper.displayAllOrders();
        OrderCursorAdapter orderCursorAdapter = new OrderCursorAdapter(this, allOrderCursor, 0);
        listView.setAdapter(orderCursorAdapter);
        //close db
        _dbHelper.close();
    }

}
