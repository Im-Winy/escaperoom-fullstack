import { Component, OnDestroy, OnInit } from '@angular/core'; // Importation des décorateurs de cycle de vie du composant
import { Router, RouterModule } from '@angular/router'; // Importation de Router pour la navigation entre les routes
import { Subscription } from 'rxjs'; // Importation de Subscription pour gérer les abonnements aux Observables
import { AuthenticationService } from '../../services/authentication/authentication.service'; // Importation du service d'authentification
import { NotificationService } from '../../services/notification/notification.service'; // Importation du service de notification
import { User } from '../../models/user/user.model'; // Importation du modèle utilisateur
import { NotificationType } from '../../enum/notification-type.enum'; // Importation de l'énumération pour les types de notifications
import { HttpErrorResponse } from '@angular/common/http'; // Importation des types pour gérer les erreurs HTTP
import { FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms'; // Importation de FormsModule pour utiliser les formulaires dans le composant
import { CommonModule } from '@angular/common'; // Importation de CommonModule pour les fonctionnalités communes dans les modules
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-register', // Le sélecteur pour ce composant
  imports: [FormsModule, CommonModule, RouterModule], // Déclaration des modules nécessaires pour ce composant
  templateUrl: './register.component.html', // URL du modèle HTML du composant
  styleUrl: './register.component.css' // URL des styles CSS pour ce composant
})
export class RegisterComponent implements OnInit, OnDestroy { // Le composant implémente OnInit et OnDestroy pour gérer les cycles de vie

  registerForm!: FormGroup;
  public showLoading: boolean = false; // Variable pour gérer l'affichage de l'indicateur de chargement
  private subscriptions: Subscription[] = []; // Tableau pour stocker les abonnements aux Observables

  constructor(
    private router: Router, // Injection du service Router pour la navigation
    private authenticationService: AuthenticationService, // Injection du service d'authentification
    private notificationService: NotificationService, // Injection du service de notification
    private fb: FormBuilder,
    private titleService: Title
  ) { }

  ngOnInit(): void {
    // Vérifie si l'utilisateur est déjà connecté dès que le composant est initialisé
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/home'); // Si l'utilisateur est déjà connecté, le redirige vers la gestion des utilisateurs
    }

    this.titleService.setTitle('S\'inscrire');

    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      // autres champs du formulaire ici
    });
  }

  // Méthode pour gérer la soumission du formulaire d'inscription
  public onRegister(user: User): void {
    this.showLoading = true; // Affiche l'indicateur de chargement pendant l'inscription

    this.subscriptions.push(
      this.authenticationService.register(user).subscribe({
        next: (data: User) => { // Si l'inscription réussit
          this.showLoading = false; // Cache l'indicateur de chargement
          this.sendNotification(NotificationType.SUCCESS, 
            `A new account was created for ${data.prenom}.`); // Envoie une notification de succès
        },
        error: (errorResponse: HttpErrorResponse) => { // Si une erreur survient lors de l'inscription
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message); // Envoie une notification d'erreur avec le message retourné par le serveur
          this.showLoading = false; // Cache l'indicateur de chargement
        }
      })
    );
  }

  // Méthode pour envoyer une notification à l'utilisateur
  sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      // Si un message est fourni, l'envoie avec le type de notification spécifié
      this.notificationService.notify(notificationType, message);
    } else {
      // Si aucun message spécifique n'est fourni, envoie un message générique d'erreur
      this.notificationService.notify(notificationType, "AN ERROR OCCURED. PLEASE TRY AGAIN");
    }
  }

  // Méthode appelée lors de la destruction du composant
  ngOnDestroy(): void {
    // Annule tous les abonnements pour éviter les fuites de mémoire
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}
