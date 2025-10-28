package kogame.translate;

import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.view.View.TEXT_ALIGNMENT_TEXT_START;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Objects;

import kogame.translate.databinding.ActivityTranslatorBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TranslatorActivity extends AppCompatActivity {
    private ActivityTranslatorBinding binding;

    ArrayList<Language> languages = new ArrayList<>();

    TranslationApi api = null;
    Language currentLanguage;
    Drawable outputPanelDrawable;
    Drawable outputPanelErrorDrawable;

    SharedPreferences prefs;
    int inputMaxCharacterCount;
    String charCountFormat;

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_translator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // The app was launched and somehow we got here instead of the splash screen
        // (yeah this happens)
        if (Intent.ACTION_MAIN.equals(getIntent().getAction()) &&
                getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) &&
                isTaskRoot() ||
                getIntent().getSerializableExtra("languages") == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        binding = ActivityTranslatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        outputPanelDrawable = ContextCompat.getDrawable(this,
                R.drawable.output_panel);
        outputPanelErrorDrawable = ContextCompat.getDrawable(this,
                R.drawable.output_panel_error);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        inputMaxCharacterCount = getResources().getInteger(R.integer.input_max_character_count);

        charCountFormat = getResources().getText(R.string.char_count).toString();
        binding.charCount.setText(String.format(charCountFormat,
                0, inputMaxCharacterCount));
        initializeApi();
        setupListeners();
        setupTargetLanguages();
    }

    /// Setup functions

    private void initializeApi() {
        String base_url = prefs.getString("base_url",
                "https://kogame-translate.pavliktt.workers.dev");
        api = ApiClient.getApi(base_url);
    }

    private void setupListeners() {
        binding.inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // this function is called before text is edited
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.charCount.setText(String.format(charCountFormat,
                        s.length(), inputMaxCharacterCount));
                binding.translateButton.setEnabled(!s.toString().isBlank());
                set_translate_drawable(R.drawable.arrow_forward);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // this function is called after text is edited
            }
        });
        binding.translateButton.setOnClickListener(this::translate_button_clicked);
        binding.copyButton.setOnClickListener(this::copy_button_clicked);
        binding.targetLanguagePicker.setOnClickListener(this::language_picker_clicked);
        binding.feedbackButton.setOnClickListener(this::feedback_button_clicked);
        binding.settingsButton.setOnClickListener(this::settings_button_clicked);
        binding.outputText.setMovementMethod(new ScrollingMovementMethod());
    }
    @SuppressWarnings("unchecked")
    private void setupTargetLanguages() {
        binding.targetLanguagePicker.setEnabled(false);
        languages = (ArrayList<Language>) getIntent().getSerializableExtra("languages");
        if (languages == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        set_target_language(languages.get(0));
        binding.targetLanguagePicker.setEnabled(true);
    }

    protected void feedback_button_clicked(View view) {
        Toast.makeText(this, "Sorry, not yet!", Toast.LENGTH_SHORT).show();
    }

    protected void settings_button_clicked(View view) {
//        startActivity(new Intent(this, SettingsActivity.class));
    }

    protected void copy_button_clicked(View view) {
        String text = binding.outputText.getText().toString();
        if (text.isBlank()) return;

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
    }

    protected void translate_button_clicked(View view) {
        if (currentLanguage == null) {
            updateOutputPanel(true, R.string.no_target_language);
            return;
        }

        String text = binding.inputText.getText().toString().trim();
        if (text.isEmpty()) {
            updateOutputPanel(true, R.string.no_text_to_translate);
            return;
        }

        disableUi();
        updateOutputPanel(false, R.string.translating);

        TranslateRequest req = new TranslateRequest(text,
                currentLanguage.getLanguageCode());
        api.translate(req).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TranslateResponse> call,
                                   @NonNull Response<TranslateResponse> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        String output = response.body().getTranslation();
                        updateOutputPanel((output == null), (output != null) ?
                                output :
                                getResources().getString(R.string.no_server_response));
                    } else {
                        updateOutputPanel(true, response.isSuccessful() ?
                                getResources().getString(R.string.no_server_response) :
                                "HTTP " + response.code());
                    }

                    binding.inputText.setEnabled(true);
                    binding.translateButton.setEnabled(true);
                    set_translate_drawable(R.drawable.refresh);
                });
            }

            @Override
            public void onFailure(@NonNull Call<TranslateResponse> call, @NonNull Throwable t) {
                String finalMessage = Objects.requireNonNullElse(
                        t.getLocalizedMessage(),
                        getString(R.string.no_server_response)
                );

                runOnUiThread(() -> {
                    updateOutputPanel(true, finalMessage);
                    resetUi();
                });
            }
        });
    }

    private void disableUi() {
        binding.inputText.setEnabled(false);
        binding.translateButton.setEnabled(false);
    }

    private void resetUi() {
        binding.inputText.setEnabled(true);
        binding.translateButton.setEnabled(true);
        set_translate_drawable(R.drawable.refresh);
    }

    protected void language_picker_clicked(View view) {
        TargetLanguagePickerDialogFragment fragment = new TargetLanguagePickerDialogFragment();
        fragment.mainActivity = this;
        fragment.show(getSupportFragmentManager(), "TargetLanguagePickerDialogFragment");
    }

    public void set_target_language(@NonNull Language language) {
        currentLanguage = language;
        binding.targetFlag.setText(language.getLanguageFlag());
        binding.targetName.setText(language.getLanguageName());
    }

    protected void set_translate_drawable(int drawable_id) {
        Drawable drawable = ContextCompat.getDrawable(this, drawable_id);
        binding.translateButtonIcon.setImageDrawable(drawable);
    }

    protected void updateOutputPanel(boolean error, String content) {
        binding.outputPanel.setBackground(error ? outputPanelErrorDrawable : outputPanelDrawable);
        TextView ot = binding.outputText;
        ot.setGravity(error ? Gravity.CENTER : (Gravity.TOP | Gravity.START));
        ot.setTextAlignment(error ? TEXT_ALIGNMENT_CENTER : TEXT_ALIGNMENT_TEXT_START);
        ot.setText(content);
        ot.setTextColor(error ? 0xFFFFD6D6 : 0xFFF9FFD6);
    }

    protected void updateOutputPanel(boolean error, int content_int) {
        updateOutputPanel(error, getString(content_int));
    }

    public ArrayList<Language> get_languages() {
        return languages;
    }
}