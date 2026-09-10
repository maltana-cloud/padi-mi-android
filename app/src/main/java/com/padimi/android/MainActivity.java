package com.padimi.android;

import android.Manifest;
import android.app.Activity;
import android.app.role.RoleManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import java.util.List;
import com.padimi.android.actions.AppLauncher;
import com.padimi.android.actions.SystemActions;
import com.padimi.android.accessibility.PadiAccessibilityService;
import com.padimi.android.brain.Command;
import com.padimi.android.brain.CommandInterpreter;
import com.padimi.android.brain.ConversationEngine;
import com.padimi.android.memory.MemoryStore;
import com.padimi.android.speech.SpeechEngine;
import com.padimi.android.voice.TtsEngine;

public final class MainActivity extends Activity {
    private static final int MIC_REQUEST = 10;
    private static final int ASSISTANT_ROLE_REQUEST = 20;
    private SpeechEngine speech;
    private TtsEngine tts;
    private CommandInterpreter brain;
    private ConversationEngine conversation;
    private MemoryStore memory;
    private TextView status;
    private boolean conversationMode;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        buildUi();
        brain = new CommandInterpreter();
        memory = new MemoryStore(this);
        conversation = new ConversationEngine(memory);
        tts = new TtsEngine(this);
        tts.setListener(() -> {
            if (conversationMode && !isFinishing()) {
                status.postDelayed(() -> {
                    if (conversationMode && !isFinishing()) speech.start();
                }, 250);
            }
        });
        speech = new SpeechEngine(this, new SpeechEngine.Listener() {
            public void onReady() { status.setText("Listening…"); }
            public void onResult(String text) { status.setText("Heard: " + text); handle(text); }
            public void onError(int code) {
                status.setText(speechError(code));
                if (conversationMode && code != android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                    status.postDelayed(() -> {
                        if (conversationMode && !isFinishing()) speech.start();
                    }, 700);
                }
            }
        });
    }

    private void buildUi() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(32, 32, 32, 32);
        TextView title = new TextView(this);
        title.setText("PADI MI");
        title.setTextSize(34);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        status = new TextView(this);
        status.setText("Ready. Tap the microphone and speak.");
        status.setTextSize(18);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 30, 0, 30);
        Button listen = new Button(this);
        listen.setText("🎙  Talk to PADI MI");
        listen.setOnClickListener(v -> requestMicAndListen());
        Button accessibility = new Button(this);
        accessibility.setText("Enable phone control");
        accessibility.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        Button assistant = new Button(this);
        assistant.setText("Set PADI MI as phone assistant");
        assistant.setOnClickListener(v -> requestAssistantRole());
        box.addView(title);
        box.addView(status);
        box.addView(listen);
        box.addView(accessibility);
        box.addView(assistant);
        setContentView(box);
    }

    private void requestAssistantRole() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            status.setText("Android assistant role needs Android 10 or newer.");
            return;
        }
        RoleManager roles = (RoleManager) getSystemService(RoleManager.class);
        if (roles == null || !roles.isRoleAvailable(RoleManager.ROLE_ASSISTANT)) {
            status.setText("This phone does not expose the Android assistant role.");
            return;
        }
        if (roles.isRoleHeld(RoleManager.ROLE_ASSISTANT)) {
            status.setText("PADI MI is already your phone assistant.");
            return;
        }
        try {
            startActivityForResult(roles.createRequestRoleIntent(RoleManager.ROLE_ASSISTANT), ASSISTANT_ROLE_REQUEST);
        } catch (RuntimeException e) {
            status.setText("Android could not open the assistant selection screen.");
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ASSISTANT_ROLE_REQUEST) {
            if (resultCode == RESULT_OK) status.setText("PADI MI is now your phone assistant.");
            else status.setText("Assistant selection was not changed.");
        }
    }

    private void requestMicAndListen() {
        conversationMode = true;
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, MIC_REQUEST);
        } else speech.start();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MIC_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                conversationMode = true;
                speech.start();
            } else {
                conversationMode = false;
                status.setText("Microphone permission is required for voice control.");
            }
        }
    }

    private void handle(String raw) {
        List<Command> commands = brain.parseAll(raw);
        if (commands.size() == 1 && commands.get(0).type == Command.Type.UNKNOWN) {
            String reply = conversation.reply(raw);
            rememberConversation(raw, reply);
            speak(reply);
            return;
        }
        if (commands.isEmpty()) {
            String reply = conversation.reply(raw);
            rememberConversation(raw, reply);
            speak(reply);
            return;
        }
        boolean any = false;
        StringBuilder replies = new StringBuilder();
        for (Command c : commands) {
            String reply = execute(c);
            if (c.type != Command.Type.UNKNOWN) any = true;
            if (!reply.isEmpty()) {
                if (replies.length() > 0) replies.append(' ');
                replies.append(reply);
            }
        }
        if (!any) {
            String reply = conversation.reply(raw);
            rememberConversation(raw, reply);
            speak(reply);
            return;
        }
        String finalReply = replies.toString();
        rememberConversation(raw, finalReply);
        speak(finalReply);
    }

    private void rememberConversation(String user, String assistant) { memory.addConversationTurn(user, assistant); }

    private String execute(Command c) {
        PadiAccessibilityService service = PadiAccessibilityService.getInstance();
        switch (c.type) {
            case HOME:
                if (service != null) { SystemActions.home(service); return "Going home."; }
                return "Please enable PADI MI phone control first.";
            case BACK:
                if (service != null) { SystemActions.back(service); return "Going back."; }
                return "Please enable PADI MI phone control first.";
            case VOLUME_UP: SystemActions.volume(this, true); return "Volume up.";
            case VOLUME_DOWN: SystemActions.volume(this, false); return "Volume down.";
            case OPEN_APP:
                boolean opened = AppLauncher.open(this, c.argument);
                return opened ? "Opening " + c.argument + "." : "I couldn't find an app called " + c.argument + ".";
            case REMEMBER:
                memory.remember(c.argument); return "I'll remember that.";
            case WHAT_DID_I_REMEMBER:
                String m = memory.allMemories();
                return m.isEmpty() ? "I don't have any saved memories yet." : "I remember: " + m.replace('\n', ';');
            case TAP:
                if (service != null && service.tapText(c.argument)) return "Done.";
                return service == null ? "Please enable PADI MI phone control first." : "I couldn't find that button.";
            case TYPE_TEXT:
                if (service != null && service.typeText(c.argument)) return "Typed it.";
                return service == null ? "Please enable PADI MI phone control first." : "I couldn't find an active text field.";
            case SCROLL_UP:
                if (service != null && service.scroll(false)) return "Scrolling up.";
                return service == null ? "Please enable PADI MI phone control first." : "I couldn't scroll this screen.";
            case SCROLL_DOWN:
                if (service != null && service.scroll(true)) return "Scrolling down.";
                return service == null ? "Please enable PADI MI phone control first." : "I couldn't scroll this screen.";
            case SWIPE:
                return executeSwipe(service, c.argument);
            case READ_SCREEN:
                if (service == null) return "Please enable PADI MI phone control first.";
                String visible = service.readVisibleText();
                if (visible.isEmpty()) return "I can't read any visible text on this screen.";
                if (visible.length() > 1800) visible = visible.substring(0, 1800);
                return visible;
            case SEARCH_WEB:
                if (c.argument.trim().isEmpty()) return "What should I search for?";
                Intent search = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(c.argument.trim())));
                search.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(search);
                return "Searching for " + c.argument + ".";
            case STOP:
                conversationMode = false;
                speech.stop(); tts.stop(); return "Okay.";
            default: return "";
        }
    }

    private String executeSwipe(PadiAccessibilityService service, String direction) {
        if (service == null) return "Please enable PADI MI phone control first.";
        android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
        float cx = dm.widthPixels / 2f, cy = dm.heightPixels / 2f;
        String d = direction == null ? "" : direction.toLowerCase();
        if (d.contains("left") && service.swipe(dm.widthPixels * .82f, cy, dm.widthPixels * .18f, cy, 400)) return "Swiped left.";
        if (d.contains("right") && service.swipe(dm.widthPixels * .18f, cy, dm.widthPixels * .82f, cy, 400)) return "Swiped right.";
        if (d.contains("up") && service.swipe(cx, dm.heightPixels * .75f, cx, dm.heightPixels * .25f, 400)) return "Swiped up.";
        if (d.contains("down") && service.swipe(cx, dm.heightPixels * .25f, cx, dm.heightPixels * .75f, 400)) return "Swiped down.";
        return "I couldn't perform that swipe.";
    }

    private String speechError(int code) {
        switch (code) {
            case android.speech.SpeechRecognizer.ERROR_AUDIO: return "I can't access the microphone. Check microphone permission.";
            case android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: return "Microphone permission is required.";
            case android.speech.SpeechRecognizer.ERROR_NETWORK:
            case android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT: return "Speech recognition needs a working network connection on this phone.";
            case android.speech.SpeechRecognizer.ERROR_NO_MATCH: return "I didn't catch the words. Please try again.";
            case android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY: return "The speech recognizer is busy. Try again in a moment.";
            default: return "I couldn't hear that. Try again.";
        }
    }

    private void speak(String text) { status.setText(text); tts.speak(text); }

    @Override protected void onDestroy() {
        conversationMode = false;
        if (speech != null) speech.destroy();
        if (tts != null) tts.shutdown();
        super.onDestroy();
    }
}
