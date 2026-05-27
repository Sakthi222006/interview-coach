import { useNavigate } from 'react-router-dom';
import { Button } from '../../ui';

export function WeakTopicBanner({ weakTopics = [], strongTopics = [] }) {
  const navigate = useNavigate();
  if ((!weakTopics || weakTopics.length === 0) && (!strongTopics || strongTopics.length === 0)) return null;

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
      {weakTopics.slice(0,2).map(topic => (
        <div key={topic} className="p-4 rounded-xl flex items-start justify-between gap-3" style={{ background: 'rgba(239,68,68,0.07)', border: '1px solid rgba(239,68,68,0.15)' }}>
          <div>
            <p className="text-sm font-semibold" style={{ color: '#f87171' }}>⚠ Weak Area: {topic}</p>
            <p className="text-xs mt-0.5" style={{ color: 'var(--color-content-muted)' }}>Rolling average below 60%. Focus here next.</p>
          </div>
          <Button variant="danger" size="xs" onClick={() => navigate('/interview/setup', { state: { preselectedTopic: topic } })}>Practice</Button>
        </div>
      ))}

      {strongTopics.slice(0,2).map(topic => (
        <div key={topic} className="p-4 rounded-xl" style={{ background: 'rgba(16,185,129,0.07)', border: '1px solid rgba(16,185,129,0.15)' }}>
          <p className="text-sm font-semibold" style={{ color: '#34d399' }}>✓ Strong Area: {topic}</p>
          <p className="text-xs mt-0.5" style={{ color: 'var(--color-content-muted)' }}>Averaging 75%+. Try Hard difficulty to stretch further.</p>
        </div>
      ))}
    </div>
  );
}
