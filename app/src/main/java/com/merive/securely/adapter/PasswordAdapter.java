package com.merive.securely.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merive.securely.R;
import com.merive.securely.database.Password;

import java.lang.ref.WeakReference;
import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {

    public final ClickListener copyListener;
    public final ClickListener rowListener;
    private final List<Password> mPasswords;

    public PasswordAdapter(List<Password> passwords, ClickListener rowListener, ClickListener copyListener) {
        mPasswords = passwords;
        this.rowListener = rowListener;
        this.copyListener = copyListener;
    }

    @NonNull
    @Override
    public PasswordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_password, parent, false);
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

    public class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView name;
        private final ImageButton share;
        private final WeakReference<ClickListener> copyListenerRef;
        private final WeakReference<ClickListener> rowListenerRef;

        public ViewHolder(final View itemView) {
            super(itemView);

            copyListenerRef = new WeakReference<>(copyListener);
            rowListenerRef = new WeakReference<>(rowListener);

            name = itemView.findViewById(R.id.row_password_name);
            share = itemView.findViewById(R.id.row_options_button);

            itemView.setOnClickListener(this);
            share.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            try {
                if (view.getId() == share.getId())
                    copyListenerRef.get().onItemClick(mPasswords.get(getAdapterPosition()).getName());
                else
                    rowListenerRef.get().onItemClick(mPasswords.get(getAdapterPosition()).getName());
            } catch (Exception ignored) {
            }
        }
    }
}
