package com.haoche51.bee;

import android.content.Context;
import com.haoche51.bee.helper.DatabaseHelper;

public class GlobalData {
    public static DatabaseHelper mDbHelper = null;
    public static Context mContext;

    public static void init(Context context) {
        mContext = context;
        mDbHelper = new DatabaseHelper(context);
    }
}
