package com.zulfikar.aaiplayer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyHolder> {

    private final ArrayList<String> folderName;
    private final FragmentActivity mContext;

    private View titleBar, bottomNavBar;

    public static String folderTitle;

    public FolderAdapter(ArrayList<String> folderName, FragmentActivity mContext) {
        this.folderName = folderName;
        this.mContext = mContext;
    }

    public FolderAdapter(ArrayList<String> folderName, FragmentActivity mContext, View titleBar, View bottomNavBar) {
        this.folderName = folderName;
        this.mContext = mContext;
        this.titleBar = titleBar;
        this.bottomNavBar = bottomNavBar;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.folder_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.folder.setText(folderName.get(position));
        holder.itemView.setOnClickListener(v -> openFolder(position));
    }

    private void openFolder(int position) {
        folderTitle = folderName.get(position);
        FolderVideoFragment folderVideoFragment = new FolderVideoFragment();
        if (mContext instanceof MainActivity) mContext.getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, folderVideoFragment).commit();
        else if (mContext instanceof HomeActivity) mContext.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.from_right, R.anim.to_left).replace(R.id.mainFragmentAH, folderVideoFragment.setBars(titleBar, bottomNavBar).setInitialPaddingTop(240, 200)).commit();
    }

    @Override
    public int getItemCount() {
        return folderName.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        TextView folder;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            folder = itemView.findViewById((R.id.folderName));
        }
    }
}
