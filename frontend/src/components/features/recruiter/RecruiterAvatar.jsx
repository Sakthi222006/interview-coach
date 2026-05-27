// frontend/src/components/features/recruiter/RecruiterAvatar.jsx
import { cn } from '../../../utils/cn';

/**
 * RecruiterAvatar — displays recruiter profile info with avatar
 */
export function RecruiterAvatar({ 
  recruiterName, 
  recruiterType, 
  personality,
  className 
}) {
  const getInitials = (name) => {
    return name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase();
  };

  const getRecruiterColor = (type) => {
    switch (type) {
      case 'HR_RECRUITER': return 'bg-blue-500';
      case 'SENIOR_DEVELOPER': return 'bg-purple-500';
      case 'TECH_LEAD': return 'bg-indigo-500';
      case 'ENGINEERING_MANAGER': return 'bg-green-500';
      case 'SYSTEM_DESIGN_INTERVIEWER': return 'bg-orange-500';
      default: return 'bg-gray-500';
    }
  };

  const getRecruiterTitle = (type) => {
    return type
      .split('_')
      .join(' ')
      .toLowerCase()
      .replace(/\b\w/g, l => l.toUpperCase());
  };

  return (
    <div className={cn('flex items-center gap-4', className)}>
      <div className={cn(
        'w-16 h-16 rounded-full flex items-center justify-center text-white font-bold text-lg',
        getRecruiterColor(recruiterType)
      )}>
        {getInitials(recruiterName)}
      </div>
      <div className="flex-1">
        <h3 className="text-lg font-semibold text-content">{recruiterName}</h3>
        <p className="text-sm text-content-muted">{getRecruiterTitle(recruiterType)}</p>
        {personality && (
          <p className="text-xs text-content-muted mt-1">{personality}</p>
        )}
      </div>
    </div>
  );
}
