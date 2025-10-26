package kogame.translate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import java.util.List;
import java.util.Map;

public interface TranslationApi {
    @GET("languages")
    Call<Map<String, LanguageInfo>> getLanguages();

    @POST("translate")
    Call<TranslateResponse> translate(@Body TranslateRequest request);
}
