// frontend/src/utils/cn.js

import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

/**
 * cn() — class name utility for conditional + conflict-free Tailwind classes
 *
 * WHAT IT SOLVES:
 *   Without it: className={`btn-primary ${isFullWidth ? 'w-full' : ''} ${className}`}
 *   With it:    className={cn('btn-primary', isFullWidth && 'w-full', className)}
 *
 *   Also solves conflicts:
 *   twMerge('px-4', 'px-8')  → 'px-8'    (last one wins, no duplicate)
 *   twMerge('p-4', 'px-8')   → 'p-4 px-8' (different properties, both kept)
 *
 * USAGE EXAMPLES:
 *   cn('card', 'p-6')
 *   cn('btn btn-primary btn-md', isFullWidth && 'btn-full', className)
 *   cn('input-base', hasError && 'input-error', props.className)
 *   cn({ 'opacity-50': disabled, 'ring-2': focused })
 */
export function cn(...inputs) {
  return twMerge(clsx(inputs));
}