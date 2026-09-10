package com.padimi.android.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import java.util.Locale;

public final class TtsEngine implements TextToSpeech.OnInitListener {
    private final TextToSpeech tts;
    private boolean ready;

    public TtsEngine(Context context) {
        tts = new TextToSpeech(context.getApplicationContext(), this);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override public void onStart(String utteranceId) {}
            @Override public void onDone(String utteranceId) {
                if (listener != null) listener.onDone();
            }
            @Override public void onError(String utteranceId) {
                if (listener != null) listener.onDone();
            }
        });
    }

    private Listener listener;

    public interface Listener { void onDone(); }
    public void setListener(Listener listener) { this.listener = listener; }

    @Override public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            ready = true;
            tts.setLanguage(Locale.getDefault());
        }
    }

    public void speak(String text) {
        if (ready && text != null && !text.isEmpty()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "padi");
        }
    }

    public void stop() { tts.stop(); }
    public void shutdown() { tts.shutdown(); }
}
