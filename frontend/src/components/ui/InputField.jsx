// frontend/src/components/ui/InputField.jsx

// Reusable input field with label and error message
// Usage: <InputField label="Email" type="email" name="email" value={...} onChange={...} error="Required" />

export default function InputField({
  label,
  type = 'text',
  name,
  value,
  onChange,
  placeholder,
  error,       // validation error message
  required = false,
  autoComplete,
}) {
  return (
    <div className="flex flex-col gap-1.5">
      {/* Label */}
      <label htmlFor={name} className="text-sm font-medium text-slate-300">
        {label}
        {required && <span className="text-red-400 ml-1">*</span>}
      </label>

      {/* Input */}
      <input
        id={name}
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required={required}
        autoComplete={autoComplete}
        className={`
          w-full bg-slate-800 border rounded-lg px-4 py-3
          text-white placeholder-slate-500 text-sm
          focus:outline-none focus:ring-2 transition-colors
          ${error
            ? 'border-red-500/50 focus:border-red-500 focus:ring-red-500/20'
            : 'border-slate-600 focus:border-blue-500 focus:ring-blue-500/20'
          }
        `}
      />

      {/* Inline error message below the field */}
      {error && (
        <p className="text-red-400 text-xs flex items-center gap-1">
          <span>⚠</span> {error}
        </p>
      )}
    </div>
  );
}