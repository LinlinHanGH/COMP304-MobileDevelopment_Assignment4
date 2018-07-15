package com.centennialcollege.james.onlineshopping;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustOrderFragment extends Fragment implements View.OnClickListener {
    private DatabaseHelper _dbHelper= null;
    private int _orderId;
    private int _itemId;
    private int _customerId;
    private String _selectedItemName;
    private String _deliveryDate="TBD";
    private String _status;
    public static CustOrderFragment f;
    private String spinnerDefaultText="<Please select item>";
    private TextView _txvId ;
    private TextView _txvTitleId;
    private EditText _edtxtAmount;
    private TextView _txvDeliverDate;
    private TextView _txvStatus;
    private TextView _txvTitleStatus;
    private LinearLayout _llEditBtn;
    private LinearLayout _llAddBtn;
    private LinearLayout _llDeliveryDate;


    public CustOrderFragment() {
        // Required empty public constructor
    }

    //use factory to instantiate
    public static CustOrderFragment newInstance(int orderId, int customerId,String editType) {
        f = new CustOrderFragment();
        Bundle b = new Bundle();
        b.putInt("pkId", orderId);
        b.putInt("customerId", customerId);
        b.putString("editType",editType);
        f.setArguments(b);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cust_order, container, false);
        //set click event listener
        Button btnOrderUpdate = v.findViewById(R.id.btnOrderUpdate);
        btnOrderUpdate.setOnClickListener(this);
        Button btnPlaceOrder = v.findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(this);
        Button btnOrderCancel = v.findViewById(R.id.btnOrderCancel);
        btnOrderCancel.setOnClickListener(this);



        //get edt text views
        _txvId = v.findViewById(R.id.txvOrderId);
        _txvTitleId = v.findViewById(R.id.txvTitleOrderId);
        _edtxtAmount = v.findViewById(R.id.edtxtOrderAmount);
        _txvDeliverDate=v.findViewById(R.id.txvOrderDeliveryDate);
        _txvStatus=v.findViewById(R.id.txvOrderStatus);
        _txvTitleStatus=v.findViewById(R.id.txvTitleOrderStatus);
        _llEditBtn=v.findViewById(R.id.llEditBtn);
        _llAddBtn=v.findViewById(R.id.llAddBtn);
        _llDeliveryDate=v.findViewById(R.id.llDeliveryDate);

        //get value from db
        _dbHelper= new DatabaseHelper(getActivity());

        //get pkid customerId and editType
        int pkid = this.getArguments().getInt("pkId");
        _customerId=this.getArguments().getInt("customerId");
        String editType=this.getArguments().getString("editType");

        //get itemNames
        List<String> itemNames=_dbHelper.getItemName();

        // set Place Order btn GONE when edit order
        if (editType.contains("order_customer")){
            _llAddBtn.setVisibility(View.GONE);
        }

        // set Edit and Cancel btn GONE when place order
        else if (editType.contains("placeOrder_customer")){
            _llEditBtn.setVisibility(View.GONE);
            _txvId.setVisibility(View.GONE);
            _txvTitleId.setVisibility(View.GONE);
            _txvStatus.setVisibility(View.GONE);
            _llDeliveryDate.setVisibility(View.GONE);
            _txvTitleStatus.setVisibility(View.GONE);
            itemNames.add(0,spinnerDefaultText);
        }

        //set spinner for itemNames
        Spinner spinItemName = v.findViewById(R.id.spinItemName);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,itemNames);
        spinItemName.setAdapter(adapter);

        //get values from db if edit order
        if (editType.contains("order_customer")){

            Cursor cursor = _dbHelper.displayOrder(pkid);
            if (1 == cursor.getCount())
            {
                cursor.moveToFirst();
                _txvId.setText(cursor.getString(cursor.getColumnIndex("orderId")));
                spinItemName.setSelection(cursor.getInt(cursor.getColumnIndex("itemId")) - 1);
                _edtxtAmount.setText(cursor.getString(cursor.getColumnIndex("amount")));
                _txvDeliverDate.setText(cursor.getString(cursor.getColumnIndex("deliveryDate")));
                //get status
                _status=cursor.getString(cursor.getColumnIndex("status"));
                _txvStatus.setText(_status);

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

        }

        _dbHelper.close();
        return v;
    }

    public boolean onInsertRecord(){
        boolean result=false;
        View v = getView();
        Spinner spinItemName = v.findViewById(R.id.spinItemName);

        //get selected itemName from spinner
        _selectedItemName=spinItemName.getSelectedItem().toString();

        if(spinnerDefaultText==_selectedItemName){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Place order status").setMessage("Failed: Please select the item!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            result = false;
        }
        else if (_edtxtAmount.getText().toString().trim().equals("")){
            _edtxtAmount.requestFocus();
            _edtxtAmount.setError("Please enter the amount!");
        }

        else{

            //get value from db
            _dbHelper= new DatabaseHelper(getActivity());
            //set content values
            ContentValues cv = new ContentValues();

            EditText edtxtAmount = v.findViewById(R.id.edtxtOrderAmount);

            //get selected itemId from itemName
            _itemId=_dbHelper.getItemId(_selectedItemName);

            //get max orderId from order table then plus one as new orderId
            _orderId=_dbHelper.getMaxId("orderId","'Order'")+1;

            cv.put("orderId",_orderId);
            cv.put("amount",edtxtAmount.getText().toString());
            cv.put("deliveryDate",_deliveryDate);
            cv.put("status","In-Process");
            cv.put("customerId",_customerId);
            cv.put("itemId",String.valueOf(_itemId));

            long nofRows=_dbHelper.insertRecord("`Order`",cv);

            //close
            _dbHelper.close();
            if (_orderId == nofRows)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Place order status").setMessage("Place order successful!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                result = true;
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Place order status").setMessage("Place order failed!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                result = false;
            }
        }


        return result;
    }

    public boolean onUpdateRecord()
    {
        boolean result=false;
        if (_status.contains("Delivered"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Update status").setMessage("You cannot update order when it is delivered!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            result = false;

            //remove fragment if failed
            removeFragmentHandler();
        }
        else{
            if (_edtxtAmount.getText().toString().trim().equals("")){
                _edtxtAmount.requestFocus();
                _edtxtAmount.setError("Please enter the amount!");
            }
            else {
                //get value from db
                _dbHelper= new DatabaseHelper(getActivity());
                //set content values
                ContentValues cv = new ContentValues();
                View v = getView();

                EditText edtxtAmount = v.findViewById(R.id.edtxtOrderAmount);
                Spinner spinItemName = v.findViewById(R.id.spinItemName);

                //get selected itemName from spinner
                _selectedItemName=spinItemName.getSelectedItem().toString();
                //get selected itemId from itemName
                _itemId=_dbHelper.getItemId(_selectedItemName);

                cv.put("itemId",String.valueOf(_itemId));
                cv.put("amount",edtxtAmount.getText().toString());

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


        }
        return result;
    }

    public boolean onDeleteRecord()
    {
        boolean result;
        if (_status.contains("Delivered"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Cancel status").setMessage("You cannot cancel order when it is delivered!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            result = false;
        }
        else{
            //get value from db
            _dbHelper= new DatabaseHelper(getActivity());

            int pkid = this.getArguments().getInt("pkId");
            int nofRows = _dbHelper.deleteRecord("`Order`", "orderId = ?", new String[]{String.valueOf(pkid)});
            //close
            _dbHelper.close();
            if (1 == nofRows)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Cancel status").setMessage("Cancel order successful!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                result = true;

                //remove fragment after cancelling order
                removeFragmentHandler();

            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Cancel status").setMessage("Cancel order failed!")
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
            case R.id.btnPlaceOrder:
                this.onInsertRecord();
                break;
            case R.id.btnOrderCancel:
                this.onDeleteRecord();
        }
    }

    //remove fragment order
    public void removeFragmentHandler(){
        if (f!=null){
            getFragmentManager().beginTransaction().remove(f).commit();
        }

    }
}

