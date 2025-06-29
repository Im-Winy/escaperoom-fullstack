import { Component, OnDestroy, OnInit } from '@angular/core';
import { User } from '../../models/user/user.model';
import { AuthenticationService } from '../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-footer',
  imports:[CommonModule, RouterLink],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit, OnDestroy {

  user: User | null = null;  // Permet d'éviter une erreur si l'utilisateur n'est pas trouvé

  constructor(private authenticationService: AuthenticationService) { }

  ngOnInit(): void {
    this.getMyUser();
  }

  getMyUser(): void {
    // Récupérer l’utilisateur depuis le cache
    this.user = this.authenticationService.getUserFromLocalCache();
  }

  ngOnDestroy(): void {
    // Ajouter ici des opérations de nettoyage si nécessaire
  }
}
