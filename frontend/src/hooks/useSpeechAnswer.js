import { useEffect, useRef, useState } from 'react';

export function useSpeechAnswer({ onTranscript } = {}) {
  const [isSupported, setIsSupported] = useState(false);
  const [status, setStatus] = useState('stopped');
  const [transcript, setTranscript] = useState('');
  const [interimTranscript, setInterimTranscript] = useState('');
  const statusRef = useRef('stopped');
  const transcriptRef = useRef('');
  const recognitionRef = useRef(null);
  const onTranscriptRef = useRef(onTranscript);

  useEffect(() => {
    onTranscriptRef.current = onTranscript;
  }, [onTranscript]);

  useEffect(() => {
    const SpeechRecognition = window?.SpeechRecognition || window?.webkitSpeechRecognition;
    setIsSupported(Boolean(SpeechRecognition));

    return () => {
      if (recognitionRef.current) {
        try { recognitionRef.current.stop(); } catch (e) { }
      }
    };
  }, []);

  const createRecognition = () => {
    const SpeechRecognition = window?.SpeechRecognition || window?.webkitSpeechRecognition;
    if (!SpeechRecognition) return null;

    const recognition = new SpeechRecognition();
    recognition.interimResults = true;
    recognition.continuous = true;
    recognition.lang = 'en-US';

    recognition.onresult = (event) => {
      let finalText = transcriptRef.current;
      let interimText = '';

      for (let i = event.resultIndex; i < event.results.length; i += 1) {
        const result = event.results[i];
        if (result.isFinal) {
          finalText += `${result[0].transcript} `;
        } else {
          interimText += result[0].transcript;
        }
      }

      transcriptRef.current = finalText.trim();
      setTranscript(finalText.trim());
      setInterimTranscript(interimText.trim());
    };

    recognition.onerror = () => {
      setStatus('stopped');
      try { recognition.stop(); } catch (error) { }
    };

    recognition.onend = () => {
      if (statusRef.current === 'recording') {
        try { recognition.start(); } catch (error) { }
      }
    };

    return recognition;
  };

  const startRecording = () => {
    if (!isSupported || status === 'recording') return;
    const recognition = createRecognition();
    if (!recognition) return;

    recognitionRef.current = recognition;
    recognition.start();
    statusRef.current = 'recording';
    setStatus('recording');
  };

  const stopRecording = () => {
    if (!recognitionRef.current) return;
    try { recognitionRef.current.stop(); } catch (error) { }
    statusRef.current = 'stopped';
    setStatus('stopped');
    setInterimTranscript('');
    if (onTranscriptRef.current) {
      onTranscriptRef.current(transcriptRef.current.trim());
    }
    return transcriptRef.current.trim();
  };

  const reset = () => {
    setTranscript('');
    transcriptRef.current = '';
    setInterimTranscript('');
    statusRef.current = 'stopped';
    setStatus('stopped');
    if (recognitionRef.current) {
      try { recognitionRef.current.stop(); } catch (error) { }
    }
  };

  return {
    isSupported,
    status,
    transcript,
    interimTranscript,
    startRecording,
    stopRecording,
    reset,
  };
}
