# Kogame Translate
Use the power of LLMs to translate text of any size right on your phone!

**Kogame Translate** makes it possible to translate texts into one of 11 languages ​​using open language models, at a speed comparable to other popular machine translation services.

## Supported Languages
As of now, Kogame Translate supports the following languages:
* German
* Spanish
* English (both US and UK)
* French
* Italian
* Japanese
* Polish
* Russian
* Ukrainian
* Chinese (Simplified)

More languages are being added regularly.

## Building Kogame Translate

To build Kogame Translate, follow these steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/pavlik-dev/kogame-translate.git
   ```

2. Open the project in **Android Studio**.

3. Make sure to have Android SDK version 36 or higher installed

4. Click "Run" to build and launch the app.

**Note:** Kogame Translate requires Android 7.0 (API level 24) or higher. I'm working on supporting lower versions (Android 4.4 or 5.0).

## Getting started
There's nothing to put here. You can use the app right after the installation, without having to create an account first.

## Custom API
As of now, the app doesn't support changing API endpoints. But what you can do is modify the source code directly.

Modify `BASE_URL` string in [ApiClient.java](app/src/main/java/kogame/translate/ApiClient.java) and you're good to go.

To make your own API compatible with Kogame Translate, make sure to have these two endpoints:
  - GET `/languages` - Available target languages
    ```json
    {
      "de": {
        "name": "German",
        "flag": "🇩🇪"
      },
      "es": {
        "name": "Spanish",
        "flag": "🇪🇸"
      },
      "en-UK": {
        "name": "English (UK)",
        "flag": "🇬🇧"
      },
      "en-US": {
        "name": "English (US)",
        "flag": "🇺🇸"
      },
      ...
    }
    ```
  - POST `/translate` - Translate text
    Request:
    ```json
    {
      "text": "Hello, World!",
      "language": "de"
    }
    ```
    Response:
    ```json
    {
      "translation": "Hallo, Welt!"
    }
    ```

## Todo:
  - Add the ability to share text with the application to make translating text in other languages ​​much easier
  - Make a proper settings page and not built-in Android one
    - And make it actually work
  - Add more languages
