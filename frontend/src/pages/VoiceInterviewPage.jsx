import { useEffect, useRef, useState } from 'react';
import { Card, Button } from '../components/ui';
import { MicrophoneRecorder } from '../components/features/voice/MicrophoneRecorder';
import { LiveTranscript } from '../components/features/voice/LiveTranscript';
import { ConfidenceMeter } from '../components/features/voice/ConfidenceMeter';
import { SpeakingSpeedCard } from '../components/features/voice/SpeakingSpeedCard';
import { FillerWordDetector } from '../components/features/voice/FillerWordDetector';
import { LiveCoachPanel } from '../components/features/voice/LiveCoachPanel';
import { ConversationHistory } from '../components/features/voice/ConversationHistory';
import { useVoiceInterview } from '../hooks/useVoiceInterview';

const fillerWords = ['um', 'uh', 'like', 'so', 'actually', 'basically', 'you know', 'right', 'well'];

export default function VoiceInterviewPage() {
  const recognitionRef = useRef(null);
  const [isSupported, setIsSupported] = useState(false);
  const [recordingState, setRecordingState] = useState('stopped');
  const [transcript, setTranscript] = useState('');
  const [interimTranscript, setInterimTranscript] = useState('');
  const [elapsedSeconds, setElapsedSeconds] = useState(0);
  const [timer, setTimer] = useState(null);
  const [topic, setTopic] = useState('Technical interview');
  const [targetRole, setTargetRole] = useState('Software Engineer');
  const [difficulty, setDifficulty] = useState('MEDIUM');
  const [questionText, setQuestionText] = useState('Tell me about a recent technical challenge you solved.');
  const [lastTranscript, setLastTranscript] = useState('');
  const { session, answers, loading, error, startSession, submitTranscript, stopSession } = useVoiceInterview();

  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    setIsSupported(Boolean(SpeechRecognition));
  }, []);

  useEffect(() => {
    return () => {
      if (recognitionRef.current) {
        recognitionRef.current.onresult = null;
        recognitionRef.current.onend = null;
        recognitionRef.current.onerror = null;
        try { recognitionRef.current.stop(); } catch (e) { }
      }
      if (timer) {
        clearInterval(timer);
      }
    };
  }, [timer]);

  const createRecognition = () => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) return null;

    const recognition = new SpeechRecognition();
    recognition.interimResults = true;
    recognition.continuous = true;
    recognition.lang = 'en-US';

    recognition.onresult = (event) => {
      let finalText = transcript;
      let interimText = '';

      for (let i = event.resultIndex; i < event.results.length; i++) {
        const result = event.results[i];
        if (result.isFinal) {
          finalText += result[0].transcript + ' ';
        } else {
          interimText += result[0].transcript;
        }
      }

      setTranscript(finalText.trim());
      setInterimTranscript(interimText.trim());
    };

    recognition.onerror = (event) => {
      console.error('Speech recognition error', event.error);
      if (event.error === 'no-speech' || event.error === 'audio-capture') {
        setRecordingState('stopped');
      }
    };

    recognition.onend = () => {
      if (recordingState === 'recording') {
        try {
          recognition.start();
        } catch (e) {
          console.warn('Unable to restart recognition', e);
        }
      }
    };

    return recognition;
  };

  const startRecording = () => {
    if (!session) {
      return;
    }
    const recognition = createRecognition();
    if (!recognition) {
      return;
    }

    recognitionRef.current = recognition;
    recognition.start();
    setRecordingState('recording');
    setTranscript('');
    setInterimTranscript('');
    setLastTranscript('');

    const interval = setInterval(() => {
      setElapsedSeconds((prev) => prev + 1);
    }, 1000);
    setTimer(interval);
  };

  const pauseRecording = () => {
    if (recognitionRef.current) {
      recognitionRef.current.stop();
    }
    setRecordingState('paused');
    if (timer) {
      clearInterval(timer);
      setTimer(null);
    }
  };

  const resumeRecording = () => {
    if (!session) {
      return;
    }
    const recognition = createRecognition();
    if (!recognition) {
      return;
    }

    recognitionRef.current = recognition;
    recognition.start();
    setRecordingState('recording');
    const interval = setInterval(() => {
      setElapsedSeconds((prev) => prev + 1);
    }, 1000);
    setTimer(interval);
  };

  const stopRecording = async () => {
    if (recognitionRef.current) {
      recognitionRef.current.stop();
    }
    if (timer) {
      clearInterval(timer);
      setTimer(null);
    }
    setRecordingState('stopped');
    setInterimTranscript('');
    setLastTranscript(transcript);

    if (session && transcript.trim()) {
      await submitTranscript(session.sessionId, {
        questionText,
        transcript,
        durationSeconds: elapsedSeconds,
      });
    }
    setElapsedSeconds(0);
  };

  const handleCreateSession = async () => {
    const result = await startSession({ topic, targetRole, difficulty });
    if (result) {
      setTranscript('');
      setInterimTranscript('');
      setElapsedSeconds(0);
      setRecordingState('stopped');
    }
  };

  const recentAnswer = answers.length ? answers[answers.length - 1] : null;

  return (
    <div className="min-h-screen bg-slate-950 px-6 py-8">
      <div className="max-w-7xl mx-auto space-y-6">
        <Card className="p-6 bg-slate-900 border border-slate-700">
          <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
            <div>
              <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Voice Mock Interview</p>
              <h1 className="text-3xl font-semibold text-white mt-2">Real-Time Voice Interview</h1>
              <p className="text-slate-500 mt-2">Speak your answers, receive live coaching feedback, and simulate a recruiter conversation.</p>
            </div>
            <div className="flex flex-wrap gap-3">
              <Button onClick={handleCreateSession} disabled={loading}>Start Session</Button>
              {session && <Button variant="secondary" onClick={() => stopSession(session.sessionId)} disabled={loading}>Finish Session</Button>}
            </div>
          </div>
        </Card>

        {error && (
          <Card className="p-4 bg-rose-950 border border-rose-700 text-rose-100">
            <p>{error}</p>
          </Card>
        )}

        <div className="grid gap-4 xl:grid-cols-[0.9fr_0.7fr]">
          <div className="space-y-4">
            <Card className="p-6 bg-slate-900 border border-slate-700">
              <div className="grid gap-4 lg:grid-cols-[1fr_1fr]">
                <div>
                  <label className="block text-sm text-slate-300 mb-2">Interview Topic</label>
                  <input
                    value={topic}
                    onChange={(event) => setTopic(event.target.value)}
                    className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white"
                  />
                </div>
                <div>
                  <label className="block text-sm text-slate-300 mb-2">Target Role</label>
                  <input
                    value={targetRole}
                    onChange={(event) => setTargetRole(event.target.value)}
                    className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white"
                  />
                </div>
              </div>
              <div className="grid gap-4 lg:grid-cols-[1fr_1fr] mt-4">
                <div>
                  <label className="block text-sm text-slate-300 mb-2">Difficulty</label>
                  <select
                    value={difficulty}
                    onChange={(event) => setDifficulty(event.target.value)}
                    className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white"
                  >
                    <option>EASY</option>
                    <option>MEDIUM</option>
                    <option>HARD</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm text-slate-300 mb-2">Current Question</label>
                  <input
                    value={questionText}
                    onChange={(event) => setQuestionText(event.target.value)}
                    className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white"
                  />
                </div>
              </div>
            </Card>

            <MicrophoneRecorder
              isSupported={isSupported}
              recordingState={recordingState}
              onStart={startRecording}
              onPause={pauseRecording}
              onResume={resumeRecording}
              onStop={stopRecording}
            />

            <LiveTranscript transcript={transcript || lastTranscript} interimTranscript={interimTranscript} />

            <div className="grid gap-4 lg:grid-cols-3">
              <ConfidenceMeter score={recentAnswer?.confidenceScore || 0} />
              <SpeakingSpeedCard wpm={recentAnswer?.speakingRateWpm || 0} />
              <FillerWordDetector count={recentAnswer?.fillerWordCount || 0} words={fillerWords} />
            </div>
          </div>

          <div className="space-y-4">
            <LiveCoachPanel
              feedback={recentAnswer?.interviewerFeedback}
              improvements={recentAnswer?.improvements || []}
              nextQuestion={recentAnswer?.nextQuestion}
            />
            <ConversationHistory history={answers.map((answer) => ({ transcript: answer.transcript, nextQuestion: answer.nextQuestion }))} />
          </div>
        </div>
      </div>
    </div>
  );
}
