import { Injectable } from '@angular/core';
import { AuthenticationService } from '../authentication/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  constructor(private authService: AuthenticationService) { }

  /**
   * Retourne les rôles de l'utilisateur courant
   */
  public getUserRoles(): string[] {
    const user = this.authService.getUserFromLocalCache(); // à adapter selon ta logique
    return user?.role;
  }

  /**
   * Vérifie si l'utilisateur a au moins un des rôles demandés
   */
  public hasAnyRole(expectedRoles: string[]): boolean {
    const userRoles = this.getUserRoles();
    return expectedRoles.some(role => userRoles.includes(role));
  }

  /**
   * Vérifie si l'utilisateur a un rôle exact
   */
  public hasRole(role: string): boolean {
    return this.getUserRoles().includes(role);
  }
}
