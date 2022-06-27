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

    public final ClickListener rowListener;
    public final ClickListener shareListener;
    private final List<Password> passwordsList;

    /**
     * Initialize the dataset of the Adapter
     *
     * @param passwordsList List of password names
     * @param rowListener   Row click listener
     * @param shareListener Share click listener
     */
    public PasswordAdapter(List<Password> passwordsList, ClickListener rowListener, ClickListener shareListener) {
        this.passwordsList = passwordsList;
        this.rowListener = rowListener;
        this.shareListener = shareListener;
    }

    /**
     * Create new views (invoked by the layout manager)
     *
     * @param parent   ViewGroup parent element
     * @param viewType Type of view
     * @return New ViewHolder
     */
    @NonNull
    @Override
    public PasswordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_password, parent, false));
    }


    /**
     * Sets password names to nameText for rows by position
     *
     * @param viewHolder ViewHolder object
     * @param position   Position of list item
     */
    @Override
    public void onBindViewHolder(PasswordAdapter.ViewHolder viewHolder, int position) {
        viewHolder.nameText.setText(passwordsList.get(position).getName());
    }

    /**
     * Return size of passwordList
     */
    @Override
    public int getItemCount() {
        return passwordsList.size();
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView nameText;
        private final ImageButton shareButton;
        private final WeakReference<ClickListener> rowListenerReference;
        private final WeakReference<ClickListener> shareListenerReference;

        public ViewHolder(final View itemView) {
            super(itemView);

            rowListenerReference = new WeakReference<>(rowListener);
            shareListenerReference = new WeakReference<>(shareListener);

            nameText = itemView.findViewById(R.id.row_password_name);
            shareButton = itemView.findViewById(R.id.row_options_button);

            itemView.setOnClickListener(this);
            shareButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            try {
                if (view.getId() == shareButton.getId())
                    shareListenerReference.get().onItemClick(passwordsList.get(getAdapterPosition()).getName());
                else
                    rowListenerReference.get().onItemClick(passwordsList.get(getAdapterPosition()).getName());
            } catch (Exception ignored) {
            }
        }
    }
}
