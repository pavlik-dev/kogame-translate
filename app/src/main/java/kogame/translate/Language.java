package kogame.translate;

import android.os.Parcel;
import android.os.Parcelable;

public class Language implements Parcelable {
    private final String languageCode;
    private final String languageName;
    private final String languageFlag;

    public Language(String languageCode, String languageName, String languageFlag) {
        this.languageCode = languageCode;
        this.languageName = languageName;
        this.languageFlag = languageFlag;
    }

    protected Language(Parcel in) {
        languageCode = in.readString();
        languageName = in.readString();
        languageFlag = in.readString();
    }

    public static final Creator<Language> CREATOR = new Creator<>() {
        @Override
        public Language createFromParcel(Parcel in) {
            return new Language(in);
        }

        @Override
        public Language[] newArray(int size) {
            return new Language[size];
        }
    };

    public String getLanguageCode() {
        return languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getLanguageFlag() {
        return languageFlag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(languageCode);
        dest.writeString(languageName);
        dest.writeString(languageFlag);
    }
}
