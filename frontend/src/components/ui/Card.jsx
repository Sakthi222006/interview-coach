// frontend/src/components/ui/Card.jsx
import { cn } from '../../utils/cn';

/**
 * Card — the most reused container in the app.
 *
 * @param {boolean}  interactive  - adds hover effects (for clickable cards)
 * @param {string}   accent       - top-border color: 'blue'|'green'|'purple'|'amber'|'red'
 * @param {string}   padding      - controls inner padding: 'none'|'sm'|'md'|'lg'
 * @param {string}   className    - additional classes merged in
 *
 * USAGE:
 *   <Card>content</Card>
 *   <Card padding="lg" accent="blue">stat card</Card>
 *   <Card interactive onClick={fn}>clickable card</Card>
 *   <Card className="col-span-2">wider card</Card>
 */
export function Card({
  children,
  className,
  interactive = false,
  accent,              // 'blue' | 'green' | 'purple' | 'amber' | 'red'
  padding = 'md',      // 'none' | 'sm' | 'md' | 'lg'
  onClick,
  as: Tag = 'div',     // render as any HTML element — div by default
}) {
  const paddings = {
    none: '',
    sm:   'p-4',
    md:   'p-5',
    lg:   'p-6',
    xl:   'p-8',
  };

  const accentClasses = {
    blue:   'before:bg-gradient-to-r before:from-accent-blue before:to-transparent',
    green:  'before:bg-gradient-to-r before:from-accent-green before:to-transparent',
    purple: 'before:bg-gradient-to-r before:from-accent-purple before:to-transparent',
    amber:  'before:bg-gradient-to-r before:from-accent-amber before:to-transparent',
    red:    'before:bg-gradient-to-r before:from-accent-red before:to-transparent',
  };

  return (
    <Tag
      onClick={onClick}
      className={cn(
        // Base card style (from @layer components)
        interactive ? 'card-interactive' : 'card',

        // Inner padding
        paddings[padding],

        // Accent top border — uses ::before pseudo-element
        accent && [
          'relative overflow-hidden',
          'before:absolute before:top-0 before:inset-x-0 before:h-0.5',
          accentClasses[accent],
        ],

        // If onClick is provided but interactive not explicitly set,
        // still show pointer cursor
        onClick && !interactive && 'cursor-pointer',

        className
      )}
    >
      {children}
    </Tag>
  );
}