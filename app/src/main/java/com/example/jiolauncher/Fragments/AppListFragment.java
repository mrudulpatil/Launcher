package com.example.jiolauncher.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import com.example.jiolauncher.Adapter.AppListAdapter;
import com.example.jiolauncher.InterfaceMethods;
import com.example.jiolauncher.Models.AppInfo;
import com.example.jiolauncher.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_FIRST_USER;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@linkAppListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppListFragment extends Fragment {
    int UNINSTALL_REQUEST_CODE = 1;
    View rootView;
    RecyclerView recyclerView;
    AppListAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private List<AppInfo> appsList;
    private ProgressBar mProgressBar;

    public AppListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.app_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

         super.onCreateOptionsMenu(menu, inflater);;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_app_list, container, false);
        recyclerView = rootView.findViewById(R.id.rvAppList);
        mProgressBar=rootView.findViewById(R.id.progressBar);
        appsList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        new LoadAppsAsyncTask().execute();
        return rootView;
    }


    public class LoadAppsAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... Params) {

            PackageManager pm = getActivity().getPackageManager();

            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> allApps = pm.queryIntentActivities(i, 0);
            for(ResolveInfo ri:allApps) {
                AppInfo app = new AppInfo();
                app.setLabel(ri.loadLabel(pm).toString());
                app.setPackageName(ri.activityInfo.packageName);
                app.setIcon(ri.activityInfo.loadIcon(pm));
                app.setMainActivity(ri.activityInfo.name);
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(ri.activityInfo.packageName, 0);
                    app.setVersionCode(String.valueOf(packageInfo.versionCode));
                    app.setVersionName(packageInfo.versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                appsList.add(app);
            }

            Collections.sort(appsList, new Comparator<AppInfo>(){
                public int compare(AppInfo obj1, AppInfo obj2) {
                    // ## Ascending order
                    return obj1.getLabel().compareToIgnoreCase(obj2.getLabel());
                }
            });

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new AppListAdapter(getContext(), appsList, new InterfaceMethods.AppListClickListener() {
                @Override
                public void onClickCallback(String thePackageName) {

                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(thePackageName);
                    startActivity(launchIntent);
                }

                @Override
                public void onLongClickCallBack(final String thePackageName) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Do you wants to Uninstall this app?");
                    builder.setPositiveButton("Uninstall", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                            intent.setData(Uri.parse("package:" + thePackageName));
                            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                            startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            });
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            recyclerView.setAdapter(adapter);
            if (resultCode == RESULT_OK) {
                Log.d("TAG", "onActivityResult: user accepted the (un)install");
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("TAG", "onActivityResult: user canceled the (un)install");
            } else if (resultCode == RESULT_FIRST_USER) {
                Log.d("TAG", "onActivityResult: failed to (un)install");
            }
        }
    }
}