import { Card } from '../../ui';

export function LiveTranscript({ transcript, interimTranscript }) {
  return (
    <Card className="bg-slate-900 border border-slate-700">
      <p className="text-sm text-slate-400 uppercase tracking-[0.2em] mb-4">Live Transcript</p>
      <div className="min-h-[180px] rounded-3xl bg-slate-950 p-5 text-slate-200 text-sm leading-6 whitespace-pre-wrap">
        {transcript || 'No transcript captured yet.'}
        {interimTranscript && (
          <span className="text-slate-400"> {interimTranscript}</span>
        )}
      </div>
    </Card>
  );
}
