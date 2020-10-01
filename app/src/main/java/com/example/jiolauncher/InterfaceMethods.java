package com.example.jiolauncher;

public class InterfaceMethods {

    public interface AppListClickListener {
        void onClickCallback(String thePackageName);
        void onLongClickCallBack(String thePackageName);
    }

}
