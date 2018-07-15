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

public class RepActivity extends AppCompatActivity {

    //private fields
    private DatabaseHelper _dbHelper= null;
    private int _repId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep);

        //init rep user
        initRepUser();

        //init rep options
        initRepOptions();
    }

    private void initRepUser() {
        //get user name
        SharedPreferences myP = getSharedPreferences("Type", MODE_PRIVATE);
        String repUsrName = myP.getString("UserName", "");

        //query rep info from rep table through username
        _dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        String repFlName =  "Hello Representative: " + _dbHelper.getLoginUserProfile(repUsrName, false);
        TextView textView = findViewById(R.id.txvRepUser);
        textView.setText(repFlName);

        // get customerId
        _repId=_dbHelper.getUserId("employeeId","OrderRep",repUsrName);
        _dbHelper.close();
    }



    private void initRepOptions() {
        //get options from string array
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.rep_options));
        ListView listview = findViewById(R.id.lvRepOptions);
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
                                intent = new Intent(RepActivity.this, RepViewAllOrdersActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(RepActivity.this, EditDbActivity.class);
                                extras.putString("editType", "item_rep");
                                intent.putExtras(extras);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(RepActivity.this, EditDbActivity.class);
                                extras.putString("editType", "order_rep");
                                intent.putExtras(extras);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(RepActivity.this, EditDbActivity.class);
                                extras.putInt("employeeId",_repId);
                                extras.putString("editType", "edit_userInfo_rep");
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
