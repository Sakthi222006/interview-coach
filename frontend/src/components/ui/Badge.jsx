// frontend/src/components/ui/Badge.jsx
import { cn } from '../../utils/cn';

/**
 * Badge — small status/label pill
 *
 * USAGE:
 *   <Badge color="green">Active</Badge>
 *   <Badge color="amber">In Progress</Badge>
 *   <Badge color="blue" className="ml-2">New</Badge>
 */
export function Badge({ children, color = 'gray', className }) {
  return (
    <span className={cn(`badge badge-${color}`, className)}>
      {children}
    </span>
  );
}