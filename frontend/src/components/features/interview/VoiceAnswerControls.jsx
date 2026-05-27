import { Button } from '../../ui';
import { useSpeechAnswer } from '../../../hooks/useSpeechAnswer';

export function VoiceAnswerControls({ onTranscript, placeholderText = 'Speak your answer and it will appear here.' }) {
  const {
    isSupported,
    status,
    transcript,
    interimTranscript,
    startRecording,
    stopRecording,
    reset,
  } = useSpeechAnswer({ onTranscript });

  if (!isSupported) {
    return (
      <div className="rounded-3xl bg-slate-950 border border-slate-800 p-4 text-sm text-slate-300">
        Voice answer is not supported in this browser. Use manual typing to complete your response.
      </div>
    );
  }

  return (
    <div className="rounded-3xl bg-slate-950 border border-slate-800 p-4 space-y-4 animate-fade-in">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-slate-400 text-xs uppercase tracking-[0.28em]">Voice Answer</p>
          <h3 className="text-base font-semibold text-white">Speak your response</h3>
        </div>
        <div className="flex flex-wrap gap-2">
          <Button
            onClick={startRecording}
            disabled={status === 'recording'}
            size="sm"
          >
            Start Recording
          </Button>
          <Button
            onClick={stopRecording}
            disabled={status !== 'recording'}
            variant="secondary"
            size="sm"
          >
            Stop Recording
          </Button>
          <Button
            onClick={reset}
            disabled={status === 'recording'}
            variant="ghost"
            size="sm"
          >
            Reset
          </Button>
        </div>
      </div>

      <div className="rounded-2xl bg-slate-900 border border-slate-800 p-4 min-h-[96px]">
        <p className="text-sm text-slate-500 mb-2">Transcription</p>
        <p className="text-sm leading-relaxed text-slate-200">
          {transcript || interimTranscript || placeholderText}
        </p>
      </div>

      <div className="flex items-center justify-between text-sm text-slate-400">
        <span>Status: {status === 'recording' ? 'Listening…' : status === 'stopped' ? 'Stopped' : 'Ready'}</span>
        <span>{transcript ? `${transcript.split(/\s+/).filter(Boolean).length} words` : '0 words'}</span>
      </div>
    </div>
  );
}
