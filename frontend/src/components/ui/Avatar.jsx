// frontend/src/components/ui/Avatar.jsx
import { cn } from '../../utils/cn';

/**
 * Avatar — user avatar with fallback to initials
 *
 * USAGE:
 *   <Avatar name="Sakthivel" />
 *   <Avatar name="John Doe" size="lg" />
 *   <Avatar name="S" className="ring-2 ring-brand-500" />
 */
export function Avatar({ name = '', size = 'md', className }) {
  // Get initials: "John Doe" → "JD", "Sakthivel" → "S"
  const initials = name
    .split(' ')
    .map(word => word.charAt(0).toUpperCase())
    .slice(0, 2)
    .join('');

  const sizes = {
    xs:  'w-6 h-6 text-[10px]',
    sm:  'w-7 h-7 text-xs',
    md:  'w-8 h-8 text-xs',
    lg:  'w-10 h-10 text-sm',
    xl:  'w-12 h-12 text-base',
  };

  return (
    <div
      className={cn(
        // Gradient background — same system as navbar
        'rounded-full flex items-center justify-center',
        'bg-gradient-to-br from-brand-500 to-violet-600',
        'font-semibold text-white flex-shrink-0',
        'select-none',
        sizes[size],
        className
      )}
      aria-label={`Avatar for ${name}`}
    >
      {initials || '?'}
    </div>
  );
}