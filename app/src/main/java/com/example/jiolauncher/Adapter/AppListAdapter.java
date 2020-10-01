package com.example.jiolauncher.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.jiolauncher.InterfaceMethods;
import com.example.jiolauncher.Models.AppInfo;
import com.example.jiolauncher.R;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> implements Filterable {

    private static Context context;
    private List<AppInfo> appsList,appsListsFull;
    private InterfaceMethods.AppListClickListener mListener;

    public AppListAdapter(Context c, List<AppInfo> theAppList, InterfaceMethods.AppListClickListener theListener) {

        //This is where we build our list of app details, using the app
        //object we created to store the label, package name and icon
        appsList=theAppList;
        appsListsFull=new ArrayList<>(theAppList);
        context = c;
        //setUpApps();
        mListener=theListener;

    }

//    public static void setUpApps(){
//
//        PackageManager pManager = context.getPackageManager();
//        appsList = new ArrayList<AppInfo>();
//
//        Intent i = new Intent(Intent.ACTION_MAIN, null);
//        i.addCategory(Intent.CATEGORY_LAUNCHER);
//
//        List<ResolveInfo> allApps = pManager.queryIntentActivities(i, 0);
//        for (ResolveInfo ri : allApps) {
//            AppInfo app = new AppInfo();
//            app.label = ri.loadLabel(pManager);
//            app.packageName = ri.activityInfo.packageName;
//
//            Log.i(" Log package ",app.packageName.toString());
//            app.icon = ri.activityInfo.loadIcon(pManager);
//            appsList.add(app);
//
//        }
//
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //This is what adds the code we've written in here to our target view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.row_app_info, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        AppInfo appInfo=appsList.get(position);

        final String appPackage = appInfo.getPackageName().toString();

        holder.textView.setText(Html.fromHtml("<B>"+appInfo.getLabel().toString()+"</B>"));
        holder.img.setImageDrawable(appInfo.getIcon());
        holder.tvActivityName.setText(Html.fromHtml("<B>Activity: </B>"+appInfo.getMainActivity()));
        holder.tvVersionCode.setText(Html.fromHtml("<B>Version Code: </B>"+appInfo.getVersionCode()));
        holder.tvVersionName.setText(Html.fromHtml("<B>Version Name: </B>"+appInfo.getVersionName()));
        holder.rlParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickCallback(appPackage);
            }
        });
        holder.rlParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.onLongClickCallBack(appPackage);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView,tvActivityName,tvVersionName,tvVersionCode;
        public ImageView img;
        public RelativeLayout rlParent;

        public ViewHolder(View itemView) {
            super(itemView);
            //Finds the views from our row.xml
            textView =  itemView.findViewById(R.id.tv_app_name);
            img = itemView.findViewById(R.id.app_icon);
            rlParent=itemView.findViewById(R.id.rlParent);
            tvActivityName=itemView.findViewById(R.id.tvActivityName);
            tvVersionName=itemView.findViewById(R.id.tvVersionName);
            tvVersionCode=itemView.findViewById(R.id.tvVersionCode);

        }
    }

    //This method will filter the list
    //here we are passing the filtered data
    //and assigning it to the list with notifydatasetchanged method
//    public void filterList(ArrayList<String> filterdNames) {
//        this.names = filterdNames;
//        notifyDataSetChanged();
//    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AppInfo> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(appsListsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (AppInfo item : appsListsFull) {
                    if (item.getLabel().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            appsList.clear();
            appsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
