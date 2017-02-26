package com.penn.ppj.models;

import android.widget.EditText;

/**
 * Created by penn on 24/02/2017.
 */

public class NotifyObject {
    String type;
    Object obj;

    public NotifyObject(String type, Object obj){
        this.type = type;
        this.obj = obj;
    }

    public String getType() {
        return type;
    }

    public Object getObj() {
        return obj;
    }
}
