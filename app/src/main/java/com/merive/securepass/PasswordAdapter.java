package com.merive.securepass;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merive.securepass.database.Password;

import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {

    private final List<Password> mPasswords;

    public PasswordAdapter(List<Password> passwords) {
        mPasswords = passwords;
    }

    @NonNull
    @Override
    public PasswordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.password_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PasswordAdapter.ViewHolder holder, int position) {
        holder.name.setText(mPasswords.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mPasswords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.password_name);
        }
    }
}
