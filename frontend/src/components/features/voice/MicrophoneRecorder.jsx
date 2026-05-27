import { Button, Card } from '../../ui';

export function MicrophoneRecorder({ isSupported, recordingState, onStart, onPause, onResume, onStop }) {
  const label = recordingState === 'recording' ? 'Recording' : recordingState === 'paused' ? 'Paused' : 'Stopped';

  return (
    <Card className="space-y-4 bg-slate-900 border border-slate-700">
      <div>
        <p className="text-sm text-slate-400 uppercase tracking-[0.2em] mb-2">Microphone Recorder</p>
        <p className="text-white text-lg font-semibold">{label}</p>
      </div>

      {isSupported ? (
        <div className="grid grid-cols-2 gap-3">
          <Button onClick={onStart} disabled={recordingState === 'recording'}>Start</Button>
          <Button onClick={onPause} disabled={recordingState !== 'recording'} variant="secondary">Pause</Button>
          <Button onClick={onResume} disabled={recordingState !== 'paused'} variant="secondary">Resume</Button>
          <Button onClick={onStop} disabled={recordingState === 'stopped'} variant="danger">Stop</Button>
        </div>
      ) : (
        <p className="text-slate-500">Your browser does not support the Web Speech API. Use the manual transcript field instead.</p>
      )}
    </Card>
  );
}
