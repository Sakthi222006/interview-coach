// frontend/src/hooks/useFormValidation.js

import { useState } from 'react';

// This hook manages form fields, validation, and errors
// Usage:
//   const { values, errors, handleChange, validate, reset } = useFormValidation(initialValues, rules)

export function useFormValidation(initialValues, validationRules) {
  const [values, setValues]   = useState(initialValues);
  const [errors, setErrors]   = useState({});
  const [touched, setTouched] = useState({}); // tracks which fields user has interacted with

  // Called every time user types in a field
  const handleChange = (e) => {
    const { name, value } = e.target;
    setValues(prev => ({ ...prev, [name]: value }));

    // Clear the error for this field as user types
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  // Called when user leaves a field (onBlur)
  const handleBlur = (e) => {
    const { name } = e.target;
    setTouched(prev => ({ ...prev, [name]: true }));
    // Validate just this field
    validateField(name, values[name]);
  };

  // Validate a single field
  const validateField = (name, value) => {
    if (!validationRules[name]) return true;

    const rule  = validationRules[name];
    let   error = '';

    if (rule.required && !value?.trim()) {
      error = rule.requiredMessage || `${name} is required`;
    } else if (rule.minLength && value?.length < rule.minLength) {
      error = rule.minLengthMessage || `Minimum ${rule.minLength} characters`;
    } else if (rule.pattern && !rule.pattern.test(value)) {
      error = rule.patternMessage || `Invalid ${name} format`;
    } else if (rule.custom) {
      error = rule.custom(value, values) || '';
    }

    setErrors(prev => ({ ...prev, [name]: error }));
    return !error; // returns true if valid
  };

  // Validate ALL fields at once (called on form submit)
  const validate = () => {
    let allValid = true;
    const newErrors = {};

    Object.keys(validationRules).forEach(name => {
      const rule  = validationRules[name];
      const value = values[name];
      let   error = '';

      if (rule.required && !value?.trim()) {
        error = rule.requiredMessage || `This field is required`;
      } else if (rule.minLength && value?.length < rule.minLength) {
        error = rule.minLengthMessage || `Minimum ${rule.minLength} characters`;
      } else if (rule.pattern && !rule.pattern.test(value)) {
        error = rule.patternMessage || `Invalid format`;
      } else if (rule.custom) {
        error = rule.custom(value, values) || '';
      }

      if (error) allValid = false;
      newErrors[name] = error;
    });

    setErrors(newErrors);
    return allValid;
  };

  // Reset form to initial state
  const reset = () => {
    setValues(initialValues);
    setErrors({});
    setTouched({});
  };

  return { values, errors, touched, handleChange, handleBlur, validate, reset };
}