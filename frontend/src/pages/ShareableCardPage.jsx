import React from 'react';
import { useLocation } from 'react-router-dom';
import { Card, Avatar } from '../components/ui';

export default function ShareableCardPage() {
  const { state } = useLocation();
  const data = state?.shareData;

  if (!data) return <div className="p-8">No share data found.</div>;

  return (
    <div className="p-8 flex items-center justify-center">
      <Card padding="lg" className="w-full max-w-2xl text-center">
        <div className="flex items-center gap-4 justify-center mb-4">
          <Avatar name={data.userName || 'User'} size={64} />
          <div>
            <h2 className="text-lg font-bold">{data.userName}</h2>
            <p className="text-xs" style={{ color: 'var(--color-content-muted)' }}>{data.title || 'Interview Snapshot'}</p>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-4">
          <div>
            <p className="text-xs text-muted">Overall Score</p>
            <p className="text-3xl font-extrabold" style={{ color: '#3b82f6' }}>{data.overallScore || 0}</p>
          </div>
          <div>
            <p className="text-xs text-muted">Top Strength</p>
            <p className="font-semibold">{data.topStrength || '—'}</p>
          </div>
        </div>

        <div className="space-y-3">
          <div>
            <p className="text-xs text-muted">Key Recommendation</p>
            <p className="text-sm">{data.recommendation || 'Keep practicing in weak areas.'}</p>
          </div>
          <div>
            <p className="text-xs text-muted">Snapshot</p>
            <p className="text-sm">{data.snapshot || ''}</p>
          </div>
        </div>
      </Card>
    </div>
  );
}
