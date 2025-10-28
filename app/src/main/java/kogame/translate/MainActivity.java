package kogame.translate;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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

        Button retryButton = findViewById(R.id.retry_connection_button);
        retryButton.setOnClickListener((view) -> fetchLanguages());
    }

    private void fetchLanguages() {
        ApiClient.getApi().getLanguages().enqueue(new Callback<>() {
        TextView textView = findViewById(R.id.textView);
        textView.setText(R.string.welcome_message);
        findViewById(R.id.retry_connection_button).setVisibility(GONE);
        findViewById(R.id.progressBar).setVisibility(VISIBLE);
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
                Log.println(Log.ERROR, "KogameTranslate", t.getClass().getName());
                connectionFailed();
            }
        });
    }

    private void connectionFailed() {
        runOnUiThread(() -> {
            findViewById(R.id.retry_connection_button).setVisibility(VISIBLE);
            findViewById(R.id.progressBar).setVisibility(GONE);

            TextView textView = findViewById(R.id.textView);
            textView.setText(R.string.server_connect_failed);
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