import { Injectable } from '@angular/core'; // Importation du décorateur Injectable pour que le service puisse être injecté dans d'autres classes
import { AppSettings } from '../../settings/app.settings'; // Importation des paramètres de configuration de l'application (URL de base, etc.)
import { JwtHelperService } from '@auth0/angular-jwt'; // Importation du service pour manipuler les JWT (JSON Web Tokens)
import { HttpClient, HttpResponse } from '@angular/common/http'; // Importation des classes HttpClient et HttpResponse pour effectuer des requêtes HTTP
import { User } from '../../models/user/user.model'; // Importation du modèle de l'utilisateur
import { Observable, tap } from 'rxjs'; // Importation d'Observable pour gérer les appels asynchrones
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root' // Le service sera fourni à la racine de l'application, accessible globalement
})
export class AuthenticationService {

  public host = AppSettings.APP_URL; // URL de base pour l'API (définie dans app.settings.ts)
  declare private token: string; // Déclaration d'une variable privée pour stocker le token JWT
  declare private loggedInUsername: string; // Déclaration d'une variable privée pour stocker le nom d'utilisateur de la personne connectée
  private jwtHelper = new JwtHelperService(); // Instance du service JwtHelper pour décoder et vérifier les tokens

  constructor(
    private http: HttpClient, // Injection du HttpClient pour effectuer des requêtes HTTP
    private router: Router
  ) { }

  // Méthode pour se connecter
  public login(user: User): Observable<HttpResponse<User>> {
    // Envoie une requête POST pour l'inscription de l'utilisateur
    return this.http.post<User>(`${this.host}/api/auth/login`, user, { observe: 'response' }).pipe(
      tap(() => {
        window.location.reload(); // Recharge la page après la connexion réussie
      })
    );
  }

  // Méthode pour s'enregistrer
  public register(user: User): Observable<User> {
    // Envoie une requête POST pour l'inscription de l'utilisateur
    return this.http.post<User>(`${this.host}/api/auth/register`, user);
  }

  // Méthode pour se déconnecter
  public logOut(): void {
    this.token = ''; // Réinitialise le token
    this.loggedInUsername = ''; // Réinitialise le nom d'utilisateur
    localStorage.removeItem('user'); // Supprime l'utilisateur du stockage local
    localStorage.removeItem('token'); // Supprime le token du stockage local
    localStorage.removeItem('users'); // Supprime les autres données liées aux utilisateurs du stockage local
    this.router.navigate(['/welcome']).then(() => {
      window.location.reload(); // Recharge la page après la redirection
    });
  }

  // Méthode pour enregistrer un token dans le stockage local
  public saveToken(token: string): void {
    this.token = token; // Assigne le token à la variable locale
    localStorage.setItem('token', token); // Enregistre le token dans le stockage local
  }

  // Méthode pour enregistrer un utilisateur dans le stockage local
  public addUserToLocalCache(user: User): void {
    localStorage.setItem('user', JSON.stringify(user)); // Enregistre l'utilisateur en format JSON dans le stockage local
  }

  // Méthode pour récupérer l'utilisateur depuis le stockage local
  public getUserFromLocalCache(): User {
    return JSON.parse(localStorage.getItem('user')!); // Récupère l'utilisateur depuis le stockage local et le parse en objet
  }

  // Méthode pour charger le token depuis le stockage local
  public loadToken(): void {
    this.token = localStorage.getItem('token') || '{}'; // Si le token est trouvé, il est chargé, sinon une valeur vide '{}' est attribuée
  }

  // Méthode pour obtenir le token
  public getToken(): string {
    return this.token; // Retourne le token actuel
  }

  // Méthode pour vérifier si l'utilisateur est connecté
  public isUserLoggedIn(): boolean {
    this.loadToken(); // Charge le token depuis le stockage local
    
    if (this.token != null && this.token !== '') { // Vérifie que le token n'est pas null ou vide
      if (this.jwtHelper.decodeToken(this.token).sub != null || '') { // Vérifie que la sous-partie du token 'sub' (subject) n'est pas null
        if (!this.jwtHelper.isTokenExpired(this.token)) { // Vérifie que le token n'a pas expiré
          this.loggedInUsername = this.jwtHelper.decodeToken(this.token).sub; // Récupère le nom d'utilisateur du token
          return true; // Si tout est valide, l'utilisateur est connecté
        }
      }
    } else {
      this.logOut(); // Si aucune condition n'est remplie, on déconnecte l'utilisateur
      return false; // L'utilisateur n'est pas connecté
    }
    return false; // Retourne false si aucune condition n'est remplie
  }
}
