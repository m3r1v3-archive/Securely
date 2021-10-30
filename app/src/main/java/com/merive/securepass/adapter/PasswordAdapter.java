package com.merive.securepass.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merive.securepass.R;
import com.merive.securepass.database.Password;

import java.lang.ref.WeakReference;
import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {

    public final ClickListener copyListener;
    public final ClickListener rowListener;
    private final List<Password> mPasswords;

    /**
     * PasswordAdapter Constructor
     *
     * @param passwords    List of Password items.
     * @param rowListener  Row click listener.
     * @param copyListener Copy of row click listener.
     */
    public PasswordAdapter(List<Password> passwords, ClickListener rowListener, ClickListener copyListener) {
        mPasswords = passwords;
        this.rowListener = rowListener;
        this.copyListener = copyListener;
    }

    /**
     * This method is executing when ViewHolder is creating.
     *
     * @param parent   View group.
     * @param viewType View type.
     * @return View holder.
     */
    @NonNull
    @Override
    public PasswordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.password_row, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This method is setting name values to rows.
     *
     * @param holder   ViewHolder.
     * @param position Row position.
     */
    @Override
    public void onBindViewHolder(PasswordAdapter.ViewHolder holder, int position) {
        holder.name.setText(mPasswords.get(position).getName());
    }

    /**
     * This method is getting count of Password Items.
     *
     * @return Item count.
     */
    @Override
    public int getItemCount() {
        return mPasswords.size();
    }

    public class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView name;
        private final ImageButton copy;
        private final WeakReference<ClickListener> copyListenerRef;
        private final WeakReference<ClickListener> rowListenerRef;

        /**
         * ViewHolder Constructor
         *
         * @param itemView RecyclerView item.
         */
        public ViewHolder(final View itemView) {
            super(itemView);

            copyListenerRef = new WeakReference<>(copyListener);
            rowListenerRef = new WeakReference<>(rowListener);

            name = itemView.findViewById(R.id.password_name);
            copy = itemView.findViewById(R.id.copy_password_button);


            itemView.setOnClickListener(this);
            copy.setOnClickListener(this);
        }

        /**
         * This method is executing after clicking on row or copy button.
         * If was clicked row, will open PasswordFragment in edit mode.
         * Else will be coping password value by name.
         *
         * @param view View value.
         */
        @Override
        public void onClick(View view) {
            try {
                if (view.getId() == copy.getId())
                    copyListenerRef.get().onItemClick(mPasswords.get(getAdapterPosition()).getName());
                else
                    rowListenerRef.get().onItemClick(mPasswords.get(getAdapterPosition()).getName());
            } catch (Exception ignored) {
            }
        }
    }
}
