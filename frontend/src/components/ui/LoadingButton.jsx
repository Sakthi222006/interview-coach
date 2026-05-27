// frontend/src/components/ui/LoadingButton.jsx

// A button that shows a spinner when loading
// Usage: <LoadingButton loading={isLoading} onClick={handleSubmit}>Login</LoadingButton>

export default function LoadingButton({
  children,       // button text
  loading = false,// is it in loading state?
  disabled = false,
  type = 'button',
  onClick,
  className = '',
  variant = 'primary', // 'primary' | 'secondary' | 'danger'
}) {
  const baseStyles = 'w-full py-3 px-4 rounded-lg font-semibold text-sm transition-all duration-200 flex items-center justify-center gap-2 disabled:cursor-not-allowed';

  const variants = {
    primary:   'bg-blue-600 hover:bg-blue-700 disabled:bg-blue-800/50 text-white',
    secondary: 'bg-slate-700 hover:bg-slate-600 disabled:bg-slate-800 text-white',
    danger:    'bg-red-600 hover:bg-red-700 disabled:bg-red-800/50 text-white',
  };

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled || loading}
      className={`${baseStyles} ${variants[variant]} ${className}`}
    >
      {loading ? (
        <>
          {/* Spinning circle animation */}
          <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
          <span>Please wait...</span>
        </>
      ) : (
        children
      )}
    </button>
  );
}