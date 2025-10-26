package kogame.translate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LanguagePickerAdapter extends RecyclerView.Adapter<LanguagePickerAdapter.MyViewHolder> {

    private final List<Language> itemList;
    private final TargetLanguagePickerDialogFragment activity;

    public LanguagePickerAdapter(List<Language> itemList, TargetLanguagePickerDialogFragment activity) {
        this.itemList = itemList;
        this.activity = activity;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView languageFlag;
        TextView languageName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            languageFlag = itemView.findViewById(R.id.languageFlag);
            languageName = itemView.findViewById(R.id.languageName);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.language_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Language item = itemList.get(position);
        holder.languageFlag.setText(item.getLanguageFlag());
        holder.languageName.setText(item.getLanguageName());

        // Handle click
        holder.itemView.setOnClickListener(v ->
            activity.recyclerSelected(item)
        );
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
