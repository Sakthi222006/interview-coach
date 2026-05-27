// frontend/src/components/ui/Alert.jsx

// A reusable component for showing error or success messages
// Usage: <Alert type="error" message="Invalid password" />
//        <Alert type="success" message="Account created!" />

export default function Alert({ type = 'error', message, onClose }) {
  if (!message) return null; // If no message, render nothing

  // Style configuration based on type
  const styles = {
    error: {
      container: 'bg-red-500/10 border border-red-500/30 text-red-400',
      icon: '✕',
    },
    success: {
      container: 'bg-green-500/10 border border-green-500/30 text-green-400',
      icon: '✓',
    },
    warning: {
      container: 'bg-yellow-500/10 border border-yellow-500/30 text-yellow-400',
      icon: '⚠',
    },
    info: {
      container: 'bg-blue-500/10 border border-blue-500/30 text-blue-400',
      icon: 'ℹ',
    },
  };

  const style = styles[type] || styles.error;

  return (
    <div className={`rounded-lg p-3 flex items-start gap-3 ${style.container}`}>
      {/* Icon */}
      <span className="text-sm font-bold mt-0.5 flex-shrink-0">{style.icon}</span>

      {/* Message */}
      <p className="text-sm flex-1">{message}</p>

      {/* Optional close button */}
      {onClose && (
        <button
          onClick={onClose}
          className="text-sm opacity-60 hover:opacity-100 flex-shrink-0"
        >
          ✕
        </button>
      )}
    </div>
  );
}