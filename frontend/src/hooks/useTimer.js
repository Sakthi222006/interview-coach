// frontend/src/hooks/useTimer.js
import { useState, useEffect, useRef, useCallback } from 'react';

// useTimer — tracks elapsed seconds since start
// Returns: { seconds, formattedTime, isRunning, pause, resume, reset }
//
// Usage:
//   const { seconds, formattedTime } = useTimer({ autoStart: true });
//   const { seconds, pause, resume } = useTimer({ autoStart: false });

export function useTimer({ autoStart = true } = {}) {
  const [seconds,   setSeconds]   = useState(0);
  const [isRunning, setIsRunning] = useState(autoStart);
  const intervalRef = useRef(null);

  // Start the interval when isRunning becomes true
  useEffect(() => {
    if (isRunning) {
      intervalRef.current = setInterval(() => {
        setSeconds(prev => prev + 1);
      }, 1000);
    } else {
      clearInterval(intervalRef.current);
    }
    // Cleanup: clear interval when component unmounts or isRunning changes
    return () => clearInterval(intervalRef.current);
  }, [isRunning]);

  const pause  = useCallback(() => setIsRunning(false), []);
  const resume = useCallback(() => setIsRunning(true),  []);
  const reset  = useCallback(() => { setSeconds(0); setIsRunning(autoStart); }, [autoStart]);

  // Format seconds → "MM:SS" string
  // e.g. 125 → "02:05"
  const formattedTime = formatSeconds(seconds);

  return { seconds, formattedTime, isRunning, pause, resume, reset };
}

// useCountdown — counts DOWN from a given number of seconds
// Returns: { remaining, formattedTime, isExpired, pause, resume }
//
// Usage:
//   const { remaining, formattedTime, isExpired } = useCountdown(600); // 10 min
export function useCountdown(totalSeconds, onExpire) {
  const [remaining, setRemaining] = useState(totalSeconds);
  const [isRunning, setIsRunning] = useState(true);
  const intervalRef = useRef(null);

  useEffect(() => {
    if (!isRunning || remaining <= 0) {
      clearInterval(intervalRef.current);
      if (remaining <= 0 && onExpire) onExpire();
      return;
    }
    intervalRef.current = setInterval(() => {
      setRemaining(prev => {
        if (prev <= 1) {
          clearInterval(intervalRef.current);
          if (onExpire) onExpire();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
    return () => clearInterval(intervalRef.current);
  }, [isRunning]);

  const pause  = useCallback(() => setIsRunning(false), []);
  const resume = useCallback(() => setIsRunning(true),  []);

  return {
    remaining,
    formattedTime: formatSeconds(remaining),
    isExpired: remaining <= 0,
    isRunning,
    pause,
    resume,
    percentRemaining: Math.round((remaining / totalSeconds) * 100),
  };
}

// Helper: 125 → "02:05"
function formatSeconds(totalSeconds) {
  const mins = Math.floor(totalSeconds / 60);
  const secs = totalSeconds % 60;
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
}