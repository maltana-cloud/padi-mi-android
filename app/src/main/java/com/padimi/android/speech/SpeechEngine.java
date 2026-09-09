package com.padimi.android.speech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import java.util.ArrayList;
import java.util.Locale;

public final class SpeechEngine {
    public interface Listener {
        void onReady();
        void onResult(String text);
        void onError(int code);
    }

    private final SpeechRecognizer recognizer;
    private final Listener listener;
    private boolean listening;

    public SpeechEngine(Context context, Listener listener) {
        this.listener = listener;
        recognizer = SpeechRecognizer.createSpeechRecognizer(context.getApplicationContext());
        recognizer.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle p) { listening = true; listener.onReady(); }
            public void onBeginningOfSpeech() {}
            public void onRmsChanged(float r) {}
            public void onBufferReceived(byte[] b) {}
            public void onEndOfSpeech() { listening = false; }
            public void onError(int e) { listening = false; listener.onError(e); }
            public void onResults(Bundle b) {
                listening = false;
                ArrayList<String> r = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (r != null && !r.isEmpty() && !r.get(0).trim().isEmpty()) listener.onResult(r.get(0).trim());
                else listener.onError(SpeechRecognizer.ERROR_NO_MATCH);
            }
            public void onPartialResults(Bundle b) {}
            public void onEvent(int t, Bundle p) {}
        });
    }

    public boolean isAvailable() { return SpeechRecognizer.isRecognitionAvailable(null); }

    public void start() {
        if (listening) recognizer.cancel();
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening for PADI MI");
        recognizer.startListening(i);
    }

    public void stop() {
        if (listening) recognizer.stopListening();
    }

    public void cancel() {
        listening = false;
        recognizer.cancel();
    }

    public void destroy() {
        listening = false;
        recognizer.destroy();
    }
}
