// Importation des modules nécessaires
import { Injectable } from '@angular/core'; // Permet d'injecter ce service dans l'application Angular
import { AppSettings } from '../../settings/app.settings'; // Contient l'URL de base de l'API
import { HttpClient, HttpErrorResponse } from '@angular/common/http'; // Service Angular pour faire des requêtes HTTP
import { User } from '../../models/user/user.model'; // Modèle de données pour un utilisateur
import { Observable } from 'rxjs'; // Permet de gérer les requêtes HTTP de manière asynchrone avec des Observables
import { CustomHttpResponse } from '../../models/custom-http-response'; // Modèle pour les réponses personnalisées du serveur

// Déclare ce service comme injectable et disponible dans toute l'application
@Injectable({
  providedIn: 'root'
})
export class UserService {
  // Stocke l'URL de base de l'API pour éviter la duplication
  private host = AppSettings.APP_URL;

  // Injection du service HttpClient pour faire des appels HTTP
  constructor(private http: HttpClient) { }

  // Récupère la liste des utilisateurs depuis l'API
  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.host}/api/admin/list`);
  }

  // Ajoute un nouvel utilisateur en envoyant un formulaire de données
  public addUser(formData: FormData): Observable<User> {
    return this.http.post<User>(`${this.host}/api/admin/add`, formData);
  }

  // Met à jour un utilisateur existant en envoyant un formulaire de données
  public updateUser(formData: FormData, userId: number): Observable<User> {
    return this.http.put<User>(`${this.host}/api/admin/update/${userId}`, formData);
  }

  // Met à jour un utilisateur existant en envoyant un formulaire de données
  public updateMyProfile(formData: FormData, userId: number): Observable<User> {
    return this.http.put<User>(`${this.host}/api/user/user/${userId}`, formData);
  }

  // Supprime un utilisateur via son identifiant
  public deleteUser(userId: number): Observable<CustomHttpResponse> {
    return this.http.delete<CustomHttpResponse>(`${this.host}/api/admin/delete/${userId}`);
  }

  // Sauvegarde une liste d'utilisateurs dans le cache local (localStorage)
  public addUsersToLocalCache(users: User[]): void {
    localStorage.setItem('users', JSON.stringify(users));
  }

  // Récupère la liste des utilisateurs stockés en cache local
  public getUsersFromLocalCache(): User[] {
    const users = localStorage.getItem('users');
    return users ? JSON.parse(users) : []; // Vérifie si l'élément existe avant de le parser
  }
}
