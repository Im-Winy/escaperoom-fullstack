import { inject } from "@angular/core"; // Importation de la fonction `inject` pour accéder aux services Angular dans des fonctions
import { CanActivateChildFn, Router } from "@angular/router"; // Importation des types nécessaires pour une garde de route, et de Router pour la navigation
import { AuthenticationService } from "../services/authentication/authentication.service"; // Importation du service d'authentification pour vérifier l'état de la connexion
import { NotificationService } from "../services/notification/notification.service"; // Importation du service de notification pour afficher des messages d'alerte
import { NotificationType } from "../enum/notification-type.enum"; // Importation de l'énumération définissant les types de notifications

// Définition de la garde de route (CanActivateChild) sous forme de fonction
export const AuthenticationGuard: CanActivateChildFn = () => {

    // Vérifie si l'utilisateur est authentifié via le service AuthenticationService
    var auth: boolean = inject(AuthenticationService).isUserLoggedIn();

    // Si l'utilisateur n'est pas authentifié
    if (!auth) {
        // Redirige l'utilisateur vers la page d'accueil (route racine)
        inject(Router).navigate(['/welcome']);

        // Affiche une notification d'erreur pour informer l'utilisateur qu'il doit être connecté
        inject(NotificationService).notify(NotificationType.ERROR,
            `You need to log in to access this page`);

    }

    // Retourne true si l'utilisateur est authentifié, sinon false
    return auth;
};
