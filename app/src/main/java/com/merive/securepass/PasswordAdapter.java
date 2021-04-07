package com.merive.securepass;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merive.securepass.database.Password;

import java.lang.ref.WeakReference;
import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {

    public final ClickListener listener;
    private final List<Password> mPasswords;

    public PasswordAdapter(List<Password> passwords, ClickListener listener) {
        mPasswords = passwords;
        this.listener = listener;
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

    public class ViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView name;
        private final ImageButton copy;
        private final WeakReference<ClickListener> listenerRef;

        public ViewHolder(final View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            name = itemView.findViewById(R.id.password_name);
            copy = itemView.findViewById(R.id.copy_password);


            itemView.setOnClickListener(this);
            copy.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == copy.getId())
                listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }
}
