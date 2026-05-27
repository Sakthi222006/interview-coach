import { Card } from '../../ui';

export function SpeakingSpeedCard({ wpm = 0 }) {
  const paceLabel = wpm === 0 ? 'No speech yet' : wpm < 120 ? 'Slow' : wpm <= 160 ? 'Good' : 'Fast';

  return (
    <Card className="bg-slate-900 border border-slate-700 p-5">
      <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Speaking Speed</p>
      <p className="text-4xl font-semibold text-white mt-4">{Math.round(wpm)} WPM</p>
      <p className="text-slate-400 mt-2">{paceLabel} pace for interview speech.</p>
    </Card>
  );
}

