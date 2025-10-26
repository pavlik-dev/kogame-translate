package kogame.translate;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://kogame-translate.pavliktt.workers.dev/"; //  trailing slash!
    private static Retrofit retrofit;
    public static TranslationApi getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(TranslationApi.class);
    }
}
