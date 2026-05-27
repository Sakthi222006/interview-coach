// frontend/src/components/ui/Button.jsx
import { cn } from '../../utils/cn';

/**
 * Button — single component replacing LoadingButton + any ad-hoc buttons.
 *
 * @param {string}  variant   - 'primary'|'secondary'|'ghost'|'danger'
 * @param {string}  size      - 'xs'|'sm'|'md'|'lg'|'icon-sm'|'icon-md'
 * @param {boolean} loading   - shows spinner, disables interaction
 * @param {boolean} fullWidth - stretches to container width
 * @param {string}  className - additional/override classes via cn()
 *
 * USAGE:
 *   <Button>Click me</Button>
 *   <Button variant="secondary" size="sm">Cancel</Button>
 *   <Button variant="primary" loading={isSubmitting} fullWidth>Sign In</Button>
 *   <Button variant="danger" size="icon-md" aria-label="Delete"><TrashIcon /></Button>
 *   <Button variant="ghost" onClick={handleLogout}>Sign Out</Button>
 */
export function Button({
  children,
  variant   = 'primary',
  size      = 'md',
  loading   = false,
  fullWidth = false,
  disabled  = false,
  type      = 'button',
  className,
  onClick,
  // Allow passing aria-label and other a11y props
  ...props
}) {
  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled || loading}
      className={cn(
        'btn',                    // base from @layer components
        `btn-${variant}`,        // color variant
        `btn-${size}`,           // size variant
        fullWidth && 'btn-full', // full width
        className                // consumer overrides — twMerge handles conflicts
      )}
      {...props}
    >
      {loading ? (
        <>
          <span
            className="block w-3.5 h-3.5 border-2 border-current border-t-transparent rounded-full animate-spin"
            aria-hidden="true"
          />
          <span>Please wait...</span>
        </>
      ) : (
        children
      )}
    </button>
  );
}