import { CanActivateChildFn, Router } from '@angular/router';
import { RoleService } from '../services/role/role.service';
import { inject } from '@angular/core';
import { NotificationService } from '../services/notification/notification.service';
import { NotificationType } from '../enum/notification-type.enum';

export const roleGuard: CanActivateChildFn = (childRoute) => {
  const roleService = inject(RoleService);
  const router = inject(Router);
  const notificationService = inject(NotificationService);

  // Récupération des rôles requis définis dans le routing
  const requiredRoles: string[] = childRoute.data['role'];

  const hasAccess = roleService.hasAnyRole(requiredRoles);

  if (!hasAccess) {
    notificationService.notify(NotificationType.ERROR, `You don't have permission to access this page`);
    router.navigate(['/welcome']); // ou redirige vers une autre page
    return false;
  }

  return true;
};
