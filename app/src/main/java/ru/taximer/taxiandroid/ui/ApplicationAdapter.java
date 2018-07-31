package ru.taximer.taxiandroid.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.taximer.taxiandroid.R;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {
    private List<ApplicationInfo> mApplications;
    private PackageManager mPackageManager;
    private Context mContext;

    public ApplicationAdapter(Context context) {
        this.mContext = context;
        mPackageManager = mContext.getPackageManager();
        this.mApplications = listApplications();
    }
    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ApplicationViewHolder(parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        ApplicationInfo applicationInfo = mApplications.get(position);
        holder.appName.setText(applicationInfo.loadLabel(mPackageManager));
        holder.appIcon.setImageDrawable(applicationInfo.loadIcon(mPackageManager));
    }

    @Override
    public int getItemCount() {
        return this.mApplications.size();
    }

    static class ApplicationViewHolder extends RecyclerView.ViewHolder {

        ImageView appIcon;
        TextView appName;

        public ApplicationViewHolder(Context context) {
            this(LayoutInflater.from(context).inflate(R.layout.application_item, null));
        }

        private ApplicationViewHolder(View itemView) {
            super(itemView);
            appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            appName = (TextView) itemView.findViewById(R.id.app_name);
        }
    }

    public List<ApplicationInfo> listApplications() {
        int flags = PackageManager.GET_META_DATA;
        List<ApplicationInfo> installedApps = new ArrayList<>();
        List<ApplicationInfo> applications = mPackageManager.getInstalledApplications(flags);
        for (ApplicationInfo appInfo : applications) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                installedApps.add(appInfo);
            }
        }
        return installedApps;
    }
}
