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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment implements View.OnClickListener {

    public ItemFragment() {
    }

    //use factory to instantiate
    public static ItemFragment newInstance(int itemid) {
        ItemFragment f = new ItemFragment();
        Bundle b = new Bundle();
        b.putInt("pkId", itemid);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_item, container, false);
        //set click event listener
        Button b = v.findViewById(R.id.btnItemUpdate);
        b.setOnClickListener(this);

        //get edt text views
        TextView txvId = v.findViewById(R.id.txvItemId);
        EditText edtxtName = v.findViewById(R.id.edtxtItemName);
        EditText edtxtPrice = v.findViewById(R.id.edtxtItemPrice);
        EditText edtxtCategory = v.findViewById(R.id.edtxtItemCategory);

        //get value from db
        DatabaseHelper _dbHelper= new DatabaseHelper(getActivity());
        //get pkid
        int pkid = this.getArguments().getInt("pkId");
        Cursor cursor = _dbHelper.displayItem(pkid);
        if (1 == cursor.getCount())
        {
            cursor.moveToFirst();
            txvId.setText(cursor.getString(cursor.getColumnIndex("itemId")));
            edtxtName.setText(cursor.getString(cursor.getColumnIndex("itemName")));
            edtxtPrice.setText(cursor.getString(cursor.getColumnIndex("price")));
            edtxtCategory.setText(cursor.getString(cursor.getColumnIndex("category")));
        }
        else  //did not find the item
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Query failed!").setMessage("Item does NOT exist")
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
        //get value from db
        DatabaseHelper _dbHelper= new DatabaseHelper(getActivity());
        //set content values
        ContentValues cv = new ContentValues();
        View v = getView();
        EditText edtxtName = v.findViewById(R.id.edtxtItemName);
        EditText edtxtPrice = v.findViewById(R.id.edtxtItemPrice);
        EditText edtxtCategory = v.findViewById(R.id.edtxtItemCategory);

        if (edtxtName.getText().toString().trim().equals("")){
            edtxtName.requestFocus();
            edtxtName.setError("Item Name is required!");
        }

        else if (edtxtPrice.getText().toString().trim().equals("")){
            edtxtPrice.requestFocus();
            edtxtPrice.setError("Item Price is required!");
        }
        else if (edtxtCategory.getText().toString().trim().equals("")){
            edtxtCategory.requestFocus();
            edtxtCategory.setError("Item Category is required!");
        }
        else{
            cv.put("itemName",edtxtName.getText().toString());
            cv.put("price",edtxtPrice.getText().toString());
            cv.put("category",edtxtCategory.getText().toString());

            int pkid = this.getArguments().getInt("pkId");
            int nofRows = _dbHelper.updateTable("Item", cv, "itemId = ?", new String[]{String.valueOf(pkid)});
            //close
            _dbHelper.close();
            if (1 == nofRows)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Update status").setMessage("Update item successful!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                result = true;
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Update status").setMessage("Update item failed!")
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
            case R.id.btnItemUpdate:
                this.onUpdateRecord();
                break;
        }
    }
}
