import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../services/authentication/authentication.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  // Injection du service d’authentification pour accéder au token
  constructor(private authService: AuthenticationService) { }

  // Méthode appelée automatiquement pour intercepter chaque requête HTTP.
  // Elle peut modifier la requête avant son envoi au serveur.
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Récupération du token JWT (par exemple depuis le localStorage via le service d'authentification)
    const token = this.authService.getToken();

    // Vérifie si l'URL correspond à une route d'authentification (login/register), pour laquelle on ne doit pas ajouter de token
    if (req.url.includes('/login') || req.url.includes('/register')) {
      // Si la requête est pour le login, on la laisse passer sans la modifier
      return next.handle(req);
    }

    // Si un token existe, on clone la requête et on y ajoute un header Authorization
    if (token) {
      const cloned = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}` // Ajout du token dans l’en-tête Authorization
        }
      });

      // Envoie la requête clonée (modifiée) au serveur
      return next.handle(cloned);
    } else {
      // Si aucun token n'est présent, on transmet la requête telle quelle
      return next.handle(req);
    }
  }
}
