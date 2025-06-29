import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthenticationService } from '../../services/authentication/authentication.service';
import { UserService } from '../../services/user/user.service';
import { User } from '../../models/user/user.model';
import { Title } from '@angular/platform-browser';
import { Reservation } from '../../models/reservation/reservation.model';
import { ReservationService } from '../../services/reservation/reservation.service';
import { Paiement, PaiementService } from '../../services/paiement/paiement.service';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  user!: User; // Données de l'utilisateur actuel
  paiement!: Paiement; // Paiement (non utilisé ici pour l'instant)
  formUpdate!: FormGroup; // Formulaire de mise à jour de profil
  errorMessage = ''; // Message d'erreur à afficher
  successMessage = ''; // Message de succès à afficher

  reservations: Reservation[] = []; // Réservations de l'utilisateur
  groupedReservations: Reservation[][] = []; // Réservations groupées par 2 pour affichage
  userId!: number; // Identifiant de l'utilisateur

  constructor(
    private authenticationService: AuthenticationService, // Service d'authentification
    private fb: FormBuilder, // Pour créer des formulaires réactifs
    private userService: UserService, // Service utilisateur
    private reservationService: ReservationService, // Service des réservations
    private titleService: Title // Pour changer dynamiquement le titre de la page
  ) { }

  ngOnInit(): void {
    // Définir le titre de la page
    this.titleService.setTitle('Profil');

    // Initialisation du formulaire avec des validateurs
    this.formUpdate = this.fb.group({
      prenom: ['', Validators.required],
      nom: ['', Validators.required],
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.minLength(6)]
    });

    // Charger les informations de l'utilisateur
    this.getUser();
  }

  // Récupère l'utilisateur depuis le cache local
  getUser(): void {
    this.user = this.authenticationService.getUserFromLocalCache();
    console.log('Utilisateur récupéré du cache:', this.user);

    if (this.user) {
      this.userId = this.user.idUser;
      this.patchForm(); // Remplit le formulaire avec les données de l'utilisateur
      this.loadReservations(); // Charge les réservations de l'utilisateur
    } else {
      console.warn('Aucun utilisateur trouvé dans le cache local');
    }
  }

  // Charge les réservations de l'utilisateur à partir du service
  loadReservations(): void {
    console.log('Chargement des réservations pour userId:', this.userId);
    this.reservationService.getReservationsByUserId(this.userId).subscribe({
      next: (data) => {
        console.log('Données des réservations :', data);
        this.reservations = data;
        this.groupReservations(); // Regroupe les réservations pour l'affichage
      },
      error: (err) => {
        console.error('Erreur lors du chargement des réservations :', err);
      }
    });
  }

  // Regroupe les réservations par 2 pour les afficher par lignes de 2 éléments
  groupReservations(): void {
    this.groupedReservations = [];
    for (let i = 0; i < this.reservations.length; i += 2) {
      this.groupedReservations.push(this.reservations.slice(i, i + 2));
    }
  }

  // Pré-remplit le formulaire avec les données actuelles de l'utilisateur
  patchForm(): void {
    this.formUpdate.patchValue({
      prenom: this.user.prenom,
      nom: this.user.nom,
      username: this.user.username,
      email: this.user.email
    });
  }

  // Envoie les données mises à jour au backend via le UserService
  updateUser(): void {
    // Vérifie que le formulaire est valide et que l'utilisateur est défini
    if (this.formUpdate.invalid || !this.user.idUser) {
      console.warn("Formulaire invalide ou utilisateur non sélectionné.");
      return;
    }

    // Création de l'objet FormData pour l'envoi au backend
    const formData = new FormData();
    formData.append('prenom', this.formUpdate.value.prenom);
    formData.append('nom', this.formUpdate.value.nom);
    formData.append('username', this.formUpdate.value.username);
    formData.append('email', this.formUpdate.value.email);
    // ✅ Si un mot de passe a été saisi, on l’envoie
    const passwordValue = this.formUpdate.value.password;
    if (passwordValue && passwordValue.trim() !== '') {
      formData.append('password', passwordValue);
    }

    // Appel du service pour mettre à jour le profil
    this.userService.updateMyProfile(formData, this.user.idUser).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser; // Met à jour l'utilisateur local
        this.successMessage = 'Utilisateur mis à jour avec succès.';
        this.errorMessage = '';
        this.formUpdate.reset(); // Réinitialise le formulaire
        this.patchForm(); // Recharge les nouvelles données dans le formulaire
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors de la mise à jour de l\'utilisateur.';
        this.successMessage = '';
        console.error(error);
      }
    });
  }
}
