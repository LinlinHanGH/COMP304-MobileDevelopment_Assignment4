package com.centennialcollege.james.onlineshopping;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment implements View.OnClickListener {

    public static OrderFragment f;
    EditText edtxtOrderItemId;
    EditText edtxtAmount;
    EditText edtxtDeliverDate;

    public OrderFragment() {
        // Required empty public constructor
    }

    //use factory to instantiate
    public static OrderFragment newInstance(int orderId) {
        f = new OrderFragment();
        Bundle b = new Bundle();
        b.putInt("pkId", orderId);
        f.setArguments(b);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_order, container, false);
        //set click event listener
        Button b = v.findViewById(R.id.btnOrderUpdate);
        b.setOnClickListener(this);

        //get edt text views
        TextView txvId = v.findViewById(R.id.txvOrderId);
        edtxtOrderItemId = v.findViewById(R.id.edtxtOrderItemId);
        edtxtAmount = v.findViewById(R.id.edtxtOrderAmount);
        edtxtDeliverDate = v.findViewById(R.id.edtxtOrderDeliveryDate);

        Spinner spinStatus = v.findViewById(R.id.spinOrderStatus);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.order_status, android.R.layout.simple_spinner_item);
        spinStatus.setAdapter(adapter);

        //get value from db
        DatabaseHelper _dbHelper= new DatabaseHelper(getActivity());
        //get pkid
        int pkid = this.getArguments().getInt("pkId");
        Cursor cursor = _dbHelper.displayOrder(pkid);
        if (1 == cursor.getCount())
        {
            cursor.moveToFirst();
            txvId.setText(cursor.getString(cursor.getColumnIndex("orderId")));
            edtxtOrderItemId.setText(cursor.getString(cursor.getColumnIndex("itemId")));
            edtxtAmount.setText(cursor.getString(cursor.getColumnIndex("amount")));
            edtxtDeliverDate.setText(cursor.getString(cursor.getColumnIndex("deliveryDate")));
            if (cursor.getString(cursor.getColumnIndex("status")).contains("In-Process"))
            {
                spinStatus.setSelection(0);
            }
            else
            {
                spinStatus.setSelection(1);
            }
        }
        else  //did not find the item
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Query failed!").setMessage("Order does NOT exist")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
        cursor.close();
        _dbHelper.close();
        return v;
    }

    public boolean onUpdateRecord()
    {
        boolean result=false;

        boolean validationResult=validateInput();
        if (true==validationResult)
        {
            //get value from db
            DatabaseHelper _dbHelper= new DatabaseHelper(getActivity());
            //set content values
            ContentValues cv = new ContentValues();
            View v = getView();
            EditText edtxtItemId = v.findViewById(R.id.edtxtOrderItemId);
            EditText edtxtAmount = v.findViewById(R.id.edtxtOrderAmount);
            EditText edtxtDeliverDate = v.findViewById(R.id.edtxtOrderDeliveryDate);
            Spinner spinStatus = v.findViewById(R.id.spinOrderStatus);

            String status=spinStatus.getSelectedItem().toString();
            String deliveryDate=edtxtDeliverDate.getText().toString();


            if (status.contains("Delivered")&&deliveryDate.contains("TBD"))
            {
                //set deliveryDate as current date when Rep change status to delivered
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd-yyyy");
                Calendar currentCal = Calendar.getInstance();
                deliveryDate=dateFormat.format(currentCal.getTime());
            }


            cv.put("itemId",edtxtItemId.getText().toString());
            cv.put("amount",edtxtAmount.getText().toString());
            cv.put("deliveryDate",deliveryDate);
            cv.put("status",spinStatus.getSelectedItem().toString());

            int pkid = this.getArguments().getInt("pkId");
            int nofRows = _dbHelper.updateTable("`Order`", cv, "orderId = ?", new String[]{String.valueOf(pkid)});
            //close
            _dbHelper.close();
            if (1 == nofRows)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Update status").setMessage("Update order successful!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                result = true;

                //delete fragment after updating record
                removeFragmentHandler();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Update status").setMessage("Update order failed!")
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
        switch (v.getId()) {
            case R.id.btnOrderUpdate:
                this.onUpdateRecord();
                break;
        }
    }

    //remove fragment order
    public void removeFragmentHandler(){
        if (f!=null){
            getFragmentManager().beginTransaction().remove(f).commit();
        }

    }

    private boolean validateInput(){
        boolean result=false;
        if (edtxtOrderItemId.getText().toString().trim().equals(""))
        {
            edtxtOrderItemId.requestFocus();
            edtxtOrderItemId.setError( "Item ID is required!" );
        }
        else if (edtxtAmount.getText().toString().trim().equals(""))
        {
            edtxtAmount.requestFocus();
            edtxtAmount.setError("Amount is required!");
        }
        else if (edtxtDeliverDate.getText().toString().trim().equals(""))
        {
            edtxtDeliverDate.requestFocus();
            edtxtDeliverDate.setError("Delivery Date is required!");
        }
        else{
            result=true;
        }


        return result;
    }

}
