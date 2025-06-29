import { Component, OnDestroy, OnInit } from '@angular/core'; // Importation des décorateurs de cycle de vie du composant
import { Router, RouterModule } from '@angular/router'; // Importation de Router pour la navigation entre les routes
import { Subscription } from 'rxjs'; // Importation de Subscription pour gérer les abonnements aux Observables
import { AuthenticationService } from '../../services/authentication/authentication.service'; // Importation du service d'authentification
import { NotificationService } from '../../services/notification/notification.service'; // Importation du service de notification
import { User } from '../../models/user/user.model'; // Importation du modèle utilisateur
import { HttpErrorResponse, HttpResponse } from '@angular/common/http'; // Importation des types pour gérer les réponses HTTP
import { HeaderType } from '../../enum/header-type.enum'; // Importation de l'énumération pour le type d'en-tête du token
import { NotificationType } from '../../enum/notification-type.enum'; // Importation de l'énumération pour les types de notifications
import { FormsModule } from '@angular/forms'; // Importation de FormsModule pour utiliser les formulaires dans le composant
import { CommonModule } from '@angular/common'; // Importation de CommonModule pour les fonctionnalités communes dans les modules
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-login', // Le sélecteur pour ce composant
  imports: [FormsModule, CommonModule, RouterModule], // Déclaration des modules nécessaires pour ce composant
  templateUrl: './login.component.html', // URL du modèle HTML du composant
  styleUrl: './login.component.css' // URL des styles CSS pour ce composant
})
export class LoginComponent implements OnInit, OnDestroy { // Le composant implémente OnInit et OnDestroy pour gérer les cycles de vie

  public showLoading !: boolean; // Variable qui gère l'affichage du chargement lors de la soumission du formulaire
  private subscriptions: Subscription[] = []; // Tableau pour stocker les abonnements aux Observables et pouvoir les désabonner


  constructor(
    private router: Router, // Injection du service Router pour la navigation
    private authenticationService: AuthenticationService, // Injection du service d'authentification
    private notificationService: NotificationService, // Injection du service de notification
    private titleService: Title
  ) { }

  ngOnInit(): void {
    // Vérifie si l'utilisateur est déjà connecté dès que le composant est initialisé
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/home'); // Si connecté, redirige vers la page d'accueil
    } else {
      this.router.navigateByUrl('/login'); // Si non connecté, assure que l'utilisateur reste sur la page de connexion
    }

    this.titleService.setTitle('Se connecter');
  }

  // Méthode pour gérer la soumission du formulaire de connexion
  public onLogin(user: User): void {
    this.showLoading = true; // Affiche un indicateur de chargement

    this.subscriptions.push(
      this.authenticationService.login(user).subscribe({
        next: (response: HttpResponse<User>) => { // Si la connexion réussit
          const token = response.headers.get(HeaderType.JWT_TOKEN); // Récupère le token depuis l'en-tête de la réponse
          console.log(response); // Affiche la réponse dans la console (pour débogage)

          // Enregistre le token et l'utilisateur dans les caches locaux
          this.authenticationService.saveToken(token!);
          this.authenticationService.addUserToLocalCache(response.body!);

          // Redirige l'utilisateur vers la page de gestion des utilisateurs après une connexion réussie
          this.router.navigateByUrl('/home');

          this.showLoading = false; // Cache l'indicateur de chargement
        },
        error: (errorResponse: HttpErrorResponse) => { // Si une erreur survient lors de la connexion
          this.sendErrorNotification(NotificationType.ERROR, errorResponse.error.message); // Envoie une notification d'erreur
          this.showLoading = false; // Cache l'indicateur de chargement
        }
      })
    );
  }

  // Méthode pour envoyer une notification d'erreur
  sendErrorNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      // Si un message d'erreur spécifique est fourni, on l'envoie
      this.notificationService.notify(notificationType, message);
    } else {
      // Si aucun message spécifique n'est fourni, envoie un message générique d'erreur
      this.notificationService.notify(notificationType, "AN ERROR OCCURED. PLEASE TRY AGAIN");
    }
  }

  // Méthode appelée lors de la destruction du composant
  ngOnDestroy(): void {
    // Annule tous les abonnements pour éviter les fuites de mémoire
    this.subscriptions.forEach(
      sub => sub.unsubscribe()
    );
  }
}
