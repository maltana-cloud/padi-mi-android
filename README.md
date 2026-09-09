# PADI MI — Android

PADI MI is a personal voice-controlled Android assistant.

## Foundation
- Voice input using Android SpeechRecognizer
- Spoken responses using Android TextToSpeech
- Deterministic command interpreter
- App launching by spoken app name
- Home / Back / volume actions
- Local memory foundation
- AccessibilityService foundation for legitimate user-requested phone control

## Roadmap
1. Reliable voice + TTS foundation
2. Robust command/intent engine
3. Accessibility action layer: tap, swipe, scroll, type
4. Multi-step command execution
5. App-specific adapters and UI-state awareness
6. VoiceInteractionService / wake-phrase research and device testing
7. Local memory, preferences and context
8. Optional free/local AI reasoning layer

PADI MI must remain transparent about permissions and should only automate actions the user explicitly requests.
