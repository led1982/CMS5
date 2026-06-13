import { ButtonHTMLAttributes, ElementType, ReactNode } from 'react';

type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger';

type ButtonProps = {
  as?: ElementType;
  variant?: ButtonVariant;
  icon?: ReactNode;
  to?: string;
  href?: string;
} & ButtonHTMLAttributes<HTMLButtonElement>;

export function Button({
  as,
  variant = 'secondary',
  icon,
  children,
  className,
  ...props
}: ButtonProps) {
  const Component = as ?? 'button';
  return (
    <Component className={['btn', `btn-${variant}`, className].filter(Boolean).join(' ')} {...props}>
      {icon}
      <span>{children}</span>
    </Component>
  );
}
