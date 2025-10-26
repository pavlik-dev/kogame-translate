package kogame.translate;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fetchLanguages();
    }

    private void fetchLanguages() {
        ApiClient.getApi().getLanguages().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, LanguageInfo>> call,
                                   @NonNull Response<Map<String, LanguageInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Language> languages = new ArrayList<>();
                    for (Map.Entry<String, LanguageInfo> entry : response.body().entrySet()) {
                        languages.add(new Language(entry.getKey(), entry.getValue().getName(), entry.getValue().getFlag()));
                    }
                    StartTranslatorActivity(languages);
                } else {
                    connectionFailed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, LanguageInfo>> call,
                                  @NonNull Throwable t) {
                connectionFailed();
            }
        });
    }

    private void connectionFailed() {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Failed to connect to the server!")
                    .setMessage("Please check your internet connection and try again.")
                    .setCancelable(false)
                    .setPositiveButton("Close",
                            (dialogInterface, which) -> finishAffinity())
                    .create();

            dialog.setOnShowListener(d -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(this, R.color.text_color));
                Window window = dialog.getWindow();
                if (window != null)
                    window.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,
                            R.color.ic_launcher_background)));
            });

            dialog.show();
        });
    }

    private void StartTranslatorActivity(ArrayList<Language> languages) {
        runOnUiThread(() -> {
            Intent intent = new Intent(MainActivity.this, TranslatorActivity.class);
            intent.putExtra("languages", languages);
            startActivity(intent);
            finish();
        });
    }
}