package kogame.translate;
public class TranslateRequest {
    private final String text;
    private final String language;

    public TranslateRequest(String text, String target) {
        this.text = text;
        this.language = target;
    }

    // Getters (optional if you only send)
    public String getText() { return text; }
    public String getlanguage() { return language; }
}
