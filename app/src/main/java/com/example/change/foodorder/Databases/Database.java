package com.example.change.foodorder.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.change.foodorder.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "foodOrderDB";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCarts() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ID","ProductName", "ProductId", "Quantity", "Price", "Discount"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);

        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();

        if (c.moveToFirst()) {

            do {
                result.add(new Order(c.getInt(c.getColumnIndex("ID")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")))


                );
            }
            while (c.moveToNext());
        }
        return result;

    }

    public void addToCart(Order order) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductId,ProductName,Quantity,Price,Discount) VALUES('%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount()
        );

        db.execSQL(query);
    }

    public void emptyCart() {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);

    }

    //Favorites

    public void addFavorites(String s){

        SQLiteDatabase db = getReadableDatabase();
        String query = "INSERT INTO Favorites(FoodId) VALUES('"+s + "');";
        db.execSQL(query);

    }

    public void removeFavorites(String s){

        SQLiteDatabase db = getReadableDatabase();
        String query = "DELETE FROM Favorites WHERE FoodId='" + s + "';'";
        db.execSQL(query);

    }

    public boolean isFavorites(String s){

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM Favorites WHERE FoodId='" + s + "';'";
        Cursor cursor = db.rawQuery(query,null);

        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();

        return true;

    }


    public int getCartCount() {
        int count  = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*)  FROM OrderDetail ";
        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst() ){
            do {
                count = cursor.getInt(0);
            }while (cursor.moveToNext());

        }
        return count;




    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "UPDATE OrderDetail SET Quantity='" + order.getQuantity()+ "' WHERE ID=" + order.getID()+";";
        db.execSQL(query);

    }
}
