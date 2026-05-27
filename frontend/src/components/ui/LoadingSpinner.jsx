// frontend/src/components/ui/LoadingSpinner.jsx

// Full-page loading spinner — used while checking auth status
export default function LoadingSpinner({ message = 'Loading...' }) {
  return (
    <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center gap-4">
      <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
      <p className="text-slate-400 text-sm">{message}</p>
    </div>
  );
}