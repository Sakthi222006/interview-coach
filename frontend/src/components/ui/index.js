// frontend/src/components/ui/index.js
//
// Barrel export — import multiple UI components from one path.
//
// BEFORE barrel:
//   import { Card }    from '../components/ui/Card';
//   import { Button }  from '../components/ui/Button';
//   import { Badge }   from '../components/ui/Badge';
//   import Alert       from '../components/ui/Alert';
//
// AFTER barrel:
//   import { Card, Button, Badge, Alert } from '../components/ui';
//
// When you add a new component, add ONE line here. All consumers
// automatically get access without changing their import paths.

export { Card }                      from './Card';
export { Button }                    from './Button';
export { Badge }                     from './Badge';
export { Avatar }                    from './Avatar';
export { default as Alert }          from './Alert';
export { default as InputField }     from './InputField';
export { default as LoadingSpinner } from './LoadingSpinner';