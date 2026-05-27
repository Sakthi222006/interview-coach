import React from 'react';

export function SkillRadar({ radar }) {
  if (!radar) return null;

  const skills = [
    { label: 'DSA', value: radar.dsaProficiency },
    { label: 'Java', value: radar.javaProficiency },
    { label: 'SQL', value: radar.sqlProficiency },
    { label: 'React', value: radar.reactProficiency },
    { label: 'Comm.', value: radar.communicationScore },
    { label: 'Prob. Solving', value: radar.problemSolvingScore },
  ];

  const N = skills.length;
  const CX = 150;
  const CY = 150;
  const R = 110;
  const levels = [0.25, 0.5, 0.75, 1.0];

  const angle = i => (Math.PI * 2 * i) / N - Math.PI / 2;
  const point = (i, factor) => ({ x: CX + R * factor * Math.cos(angle(i)), y: CY + R * factor * Math.sin(angle(i)) });

  const dataPath = skills.map((s, i) => {
    const f = (s.value || 0) / 100;
    const p = point(i, f);
    return `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`;
  }).join(' ') + ' Z';

  return (
    <svg viewBox="0 0 300 300" style={{ width: '100%', maxWidth: '300px' }}>
      <defs>
        <linearGradient id="radarGrad" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#3b82f6" stopOpacity="0.5" />
          <stop offset="100%" stopColor="#8b5cf6" stopOpacity="0.2" />
        </linearGradient>
      </defs>

      {levels.map((lvl, li) => {
        const pts = Array.from({ length: N }, (_, i) => point(i, lvl));
        const d = pts.map((p, i) => `${i===0?'M':'L'} ${p.x} ${p.y}`).join(' ') + ' Z';
        return <path key={li} d={d} fill="none" stroke="rgba(255,255,255,0.07)" strokeWidth={1} />;
      })}

      {skills.map((_, i) => {
        const outer = point(i, 1);
        return <line key={i} x1={CX} y1={CY} x2={outer.x} y2={outer.y} stroke="rgba(255,255,255,0.07)" strokeWidth={1} />;
      })}

      <path d={dataPath} fill="url(#radarGrad)" stroke="#3b82f6" strokeWidth={2} strokeLinejoin="round" />

      {skills.map((s, i) => {
        const p = point(i, 1.22);
        return (
          <text key={i} x={p.x} y={p.y} textAnchor="middle" dominantBaseline="middle" fontSize={10} fontWeight={600} fill="rgba(255,255,255,0.55)">
            {s.label}
            <tspan x={p.x} dy={12} fontSize={9} fill="#3b82f6">{s.value || 0}</tspan>
          </text>
        );
      })}
    </svg>
  );
}
