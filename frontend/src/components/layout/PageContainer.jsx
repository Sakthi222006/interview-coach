// frontend/src/components/layout/PageContainer.jsx
import { cn } from '../../utils/cn';

/**
 * PageContainer — consistent max-width and padding for every page.
 *
 * WHY: Without this, each page manually sets max-w-5xl mx-auto px-6 py-8.
 * If you change the max-width later (e.g., to 7xl), you change ONE file.
 *
 * USAGE:
 *   <PageContainer>page content</PageContainer>
 *   <PageContainer size="sm">narrow form page</PageContainer>
 *   <PageContainer size="full">full-width analytics</PageContainer>
 */
export function PageContainer({ children, className, size = 'default' }) {
  const sizes = {
    sm:      'max-w-xl',
    md:      'max-w-2xl',
    default: 'max-w-5xl',
    lg:      'max-w-6xl',
    xl:      'max-w-7xl',
    full:    'max-w-none',
  };

  return (
    <div
      className={cn(
        sizes[size],
        'mx-auto px-4 sm:px-6 py-8',
        className
      )}
    >
      {children}
    </div>
  );
}