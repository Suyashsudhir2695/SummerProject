package com.example.change.foodorder.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.change.foodorder.Model.User;
import com.example.change.foodorder.Remote.APIService;
import com.example.change.foodorder.Remote.RetrofitClient;

import retrofit2.Retrofit;

public class Common {
    public static User currentUser;
    public static final String DELETE = "Delete";
    public static final String USER = "User";
    public static final String PWD = "Password";
    private static final String BASE_URL = "https://fcm.googleapis.com/";
    public static String PHONE = "userPhone";

    public static APIService getApiService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static String codeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "Preparing";
        else
            return "Shipped";

    }

    public static boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if (infos != null){
                for (int i=0;i<infos.length;i++){
                    if (infos[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;

    }
}
