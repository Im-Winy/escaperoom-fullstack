import { Component, OnInit } from '@angular/core';
import { Route, Router, RouterLink } from '@angular/router';
import { AuthenticationService } from '../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { RoleService } from '../../services/role/role.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {

  isLoggedIn = false!
  hasRole = false!

  constructor(
    private authenticationService: AuthenticationService, // Injection du service d'authentification
    private roleService: RoleService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.isLoggedIn = this.authenticationService.isUserLoggedIn();
    this.hasRole = this.roleService.hasRole('ROLE_ADMIN');
  }

  logout() {
    localStorage.removeItem('user'); // Supprime l'utilisateur du stockage local
    localStorage.removeItem('token'); // Supprime le token du stockage local
    localStorage.removeItem('users'); // Supprime les autres données liées aux utilisateurs du stockage local
    this.router.navigate(['/welcome']).then(() => {
      window.location.reload(); // Recharge la page après la redirection
    });
  }

}
