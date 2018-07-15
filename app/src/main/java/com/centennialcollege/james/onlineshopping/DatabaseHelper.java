package com.centennialcollege.james.onlineshopping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_NONE;


public class DatabaseHelper extends SQLiteOpenHelper {

    //private fields
    private final static String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "onlineshopping.db";
    private static final int DATABASE_VERSION = 1;

    private static String pathToSaveDBFile;
    private SQLiteDatabase _db = null;
    private static String _tables[]; //table names
    private static String _tableCreatorString[]; //SQL statements to create tables

    //this one is no longer needed
    public DatabaseHelper(Context context, String filePath) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        pathToSaveDBFile = new StringBuffer(filePath).append("/").append(DATABASE_NAME).toString();

        //replace with script solution
        _db = this.getWritableDatabase();
    }

    //overloading constructor to quickly open db
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        //replace with script solution
        _db = this.getWritableDatabase();
    }


    //initialize database table names and DDL statements
    public static void dbInitialize(String[] tables, String tableCreatorString[])
    {
        _tables = tables;
        _tableCreatorString = tableCreatorString;
    }


    // Create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Drop existing tables
        for (int i = 0;i < _tables.length;i++)
            db.execSQL("DROP TABLE IF EXISTS " + _tables[i]);
        //create them
        for (int i = 0;i < _tableCreatorString.length; i++)
            db.execSQL(_tableCreatorString[i]);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //user db specific
    //verify customer or rep login
    public boolean verifyLogin(String username, String userpwd, boolean isCustomer) {
        String query;
        String[] arguments;
        if (isCustomer)
        {
            query = "SELECT customerId FROM Customer WHERE userName = ? AND userPwd = ?";
            arguments = new String[]{username, userpwd};
        }
        else
        {
            query = "SELECT employeeId FROM OrderRep WHERE userName = ? AND userPwd = ?";
            arguments = new String[]{username, userpwd};
        }
        Cursor cursor = _db.rawQuery(query, arguments);
        boolean result;
        if (1 == cursor.getCount())
        {
            result = true;
        }
        else
        {
            result = false;
        }
        cursor.close();
        return result;
    }

    //query log in customer/rep profile
    //currently just return first name and last name
    public String getLoginUserProfile(String username, boolean isCustomer)
    {
        String flname = null;
        String query;
        String[] arguments;
        if (isCustomer)
        {
            query = "SELECT firstName, lastName FROM Customer WHERE userName = ? ";
            arguments = new String[]{username};
        }
        else
        {
            query = "SELECT firstName, lastName FROM OrderRep WHERE userName = ? ";
            arguments = new String[]{username};
        }
        Cursor cursor = _db.rawQuery(query, arguments);
        if (1 == cursor.getCount()) {
            cursor.moveToFirst();
            flname = cursor.getString(cursor.getColumnIndex("firstName")) + " " +  cursor.getString(cursor.getColumnIndex("lastName"));
        }
        else
        {
            flname = null;
        }
        cursor.close();
        return flname;
    }

    //query max Id from table
    public int getMaxId (String columnName, String tableName)
    {
        int maxId= 0;
        String query;
        String[] arguments;

        query = "SELECT MAX("+columnName+") FROM "+ tableName ;

        Cursor cursor = _db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do
            {
                maxId = cursor.getInt(0);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return maxId;
    }

    //query customerId from Customer table
    public int getUserId(String userIdDes, String tableName,String userName)
    {
        int userId= 0;
        String query;
        String[] arguments;

        query = "SELECT "+userIdDes+" FROM "+tableName+" WHERE username = ? ";
        arguments = new String[]{userName};

        Cursor cursor = _db.rawQuery(query, arguments);
        if (1 == cursor.getCount()) {
            cursor.moveToFirst();
            userId=cursor.getInt(cursor.getColumnIndex(userIdDes));
        }
        else
        {
            userId= 0;
        }
        cursor.close();
        return userId;
    }

    //query itemName from Item table for customer to select
    public List<String> getItemName()
    {
        String query;
        String[] arguments;
        List<String> itemNames=new ArrayList<String>();

        query = "SELECT itemName FROM Item";
        arguments = new String[]{};

        Cursor cursor = _db.rawQuery(query, arguments);
        while (cursor.moveToNext()){
            String iName = cursor.getString(cursor.getColumnIndex("itemName"));
            itemNames.add(iName);
        }
        cursor.close();
        return itemNames;
    }

    //query itemId from Item table
    public int getItemId(String itemName)
    {
        int itemId;
        String query;
        String[] arguments;

        query = "SELECT itemId FROM Item WHERE itemName = ? ";
        arguments = new String[]{itemName};

        Cursor cursor = _db.rawQuery(query, arguments);
        if (1 == cursor.getCount()) {
            cursor.moveToFirst();
            itemId=cursor.getInt(cursor.getColumnIndex("itemId"));
        }
        else
        {
            itemId= 0;
        }
        cursor.close();
        return itemId;
    }

    //query order details by joinging order, customer, item three tables
    public Cursor displayAllOrders()
    {
        String query = "SELECT o.orderId AS _id, o.amount, o.status,o.deliveryDate, c.firstName, i.itemName, i.image " +
                "FROM Customer c " +
                "INNER JOIN `Order` o ON o.customerId = c.customerId " +
                "INNER JOIN Item i ON i.itemId = o.itemId";
        return _db.rawQuery(query, null);
    }

    //query order details for one specific customer
    public Cursor displayMyOrders(int customerId)
    {
        String query = "SELECT o.orderId AS _id, o.amount, o.status,o.deliveryDate, c.firstName, i.itemName, i.image " +
                "FROM Customer c " +
                "INNER JOIN `Order` o ON o.customerId = c.customerId " +
                "INNER JOIN Item i ON i.itemId = o.itemId WHERE o.customerId="+customerId;
        return _db.rawQuery(query, null);
    }

    //query specific one item record
    public Cursor displayItem(int itemid)
    {
        String query = "SELECT itemId, itemName, price, category " +
                "FROM Item " +
                "WHERE itemId = ?";

        String[] arguments = new String[]{Integer.toString(itemid)};
        return _db.rawQuery(query, arguments);
    }

    public Cursor displayOrder(int orderid)
    {
        String query = "SELECT orderId, itemId, amount, deliveryDate, status " +
                "FROM `Order` " +
                "WHERE orderId = ?";

        String[] arguments = new String[]{Integer.toString(orderid)};
        return _db.rawQuery(query, arguments);
    }

    //display order search result for specific customer (the customer cannot search order which is not placed by him)
    public Cursor displayOrderForCustomer(int orderid, int customerId)
    {
        String query = "SELECT orderId, itemId, amount, deliveryDate, status " +
                "FROM `Order` " +
                "WHERE orderId = ? AND customerId=?";

        String[] arguments = new String[]{Integer.toString(orderid),Integer.toString(customerId) };
        return _db.rawQuery(query, arguments);
    }


    public Cursor displayCustomerInfo(int customerId){
        String query = "SELECT customerId, userName, userPwd, firstName, lastName, address, postalCode " +
                "FROM Customer " +
                "WHERE customerId=?";

        String[] arguments = new String[]{Integer.toString(customerId) };
        return _db.rawQuery(query, arguments);
    }

    public Cursor displayRepInfo(int employeeId){
        String query = "SELECT employeeId, userName, userPwd, firstName, lastName " +
                "FROM OrderRep " +
                "WHERE employeeId=?";

        String[] arguments = new String[]{Integer.toString(employeeId) };
        return _db.rawQuery(query, arguments);
    }

    //insert record
    public long insertRecord(String table, ContentValues values)
    {
        return _db.insert(table,null,values);
    }

    //update table
    public int updateTable(String table, ContentValues values, String whereClause, String[] whereArgs)
    {
        return _db.update(table, values, whereClause, whereArgs);
    }

    //delete record
    public int deleteRecord(String table, String whereClause, String[] whereArgs)
    {
        return _db.delete(table, whereClause, whereArgs);
    }
}
