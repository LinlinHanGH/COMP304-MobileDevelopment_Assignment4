package com.centennialcollege.james.onlineshopping;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;


public class OrderCursorAdapter extends CursorAdapter {

    //private fields
    private LayoutInflater cursorInflater;

    //constructor
    OrderCursorAdapter(Context context, Cursor c, int flag) {
        super(context, c, flag);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.list_order, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView itemImage = view.findViewById(R.id.order_item_image);
        TextView txvOrderIdCustomerName = view.findViewById(R.id.txv_orderId_cname);
        TextView txvOrderItemAmount = view.findViewById(R.id.txv_order_itemamount);
        TextView txvOrderStatus = view.findViewById(R.id.txv_order_status);
        TextView txvOrderDeliveryDate = view.findViewById(R.id.txv_order_deliveryDate);

        String orderID = cursor.getString(cursor.getColumnIndexOrThrow("_id")) + " - " + cursor.getString(cursor.getColumnIndexOrThrow("firstName"));
        txvOrderIdCustomerName.setText(orderID);
        String itemAmount = cursor.getString(cursor.getColumnIndexOrThrow("itemName")) + " : " + cursor.getString(cursor.getColumnIndexOrThrow("amount"));
        txvOrderItemAmount.setText(itemAmount);
        String orderStatus = cursor.getString(cursor.getColumnIndexOrThrow("status"));
        txvOrderStatus.setText(orderStatus);
        int test=cursor.getColumnIndexOrThrow("deliveryDate");
        String orderDeliveryDate=cursor.getString(test);
        txvOrderDeliveryDate.setText(orderDeliveryDate);

        //load image
        byte[] blob = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(blob);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        itemImage.setImageBitmap(bitmap);
    }
}
