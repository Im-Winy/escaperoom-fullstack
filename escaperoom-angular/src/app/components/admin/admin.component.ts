import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { User } from '../../models/user/user.model';
import { UserService } from '../../services/user/user.service';
import { NotificationType } from '../../enum/notification-type.enum';
import { HttpErrorResponse } from '@angular/common/http';
import { NotificationService } from '../../services/notification/notification.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Evenement } from '../../models/evenement/evenement.model';
import { EvenementService } from '../../services/evenement/evenement.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-admin',
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit, OnDestroy {

  // -------------------------
  // Déclaration des variables
  // -------------------------
  public users: User[] = []; //Tableau des utilisateurs
  public evenements: Evenement[] = []; //Tableau des évènements
  public refreshing: boolean = false; //Refresh
  private subscriptions: Subscription[] = [];
  declare public selectedUser: User | null;
  declare public selectedEvenement: Evenement | null;
  difficultes: string[] = ['NORMAL', 'INTERMEDIAIRE', 'DIFFICILE'] //Liste des niveaux de difficultés disponibles
  roles: string[] = ['ROLE_USER', 'ROLE_HR', 'ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']; //Liste des rôles disponibles
  userForm!: FormGroup;
  updateEvenementForm!: FormGroup;
  addEvenementForm!: FormGroup;
  successMessage: string = '';
  errorMessage: string = '';

  // ------------------
  // Appel des services
  // ------------------
  constructor(
    private userService: UserService,
    private notificationService: NotificationService,
    private fb: FormBuilder,
    private evenementService: EvenementService,
    private titleService: Title
  ) { }

  // ------------------------
  // Méthode d'initialisation
  // ------------------------
  ngOnInit(): void {
    this.titleService.setTitle('Tableau de bord');
    this.getUsers(true); //Charge les utilisateurs dès le démarrage du composant
    this.userForm = this.fb.group({ //Charge le formulaire de modification d'utilisateur
      prenom: ['', Validators.required],
      nom: ['', Validators.required],
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      role: ['', Validators.required]
    });
    this.getEvenements(); //Charge les évènements dès le démarrage du composant
    this.updateEvenementForm = this.fb.group({ //Charge le formulaire de modification d'évènement
      nom: ['', Validators.required],
      image: ['', Validators.required],
      description: ['', Validators.required],
      nbeJoueurMax: ['', [Validators.required]],
      duree: ['', [Validators.required]],
      difficulte: ['', Validators.required],
      prix: ['', Validators.required]
    });
    this.addEvenementForm = this.fb.group({ //Charge le formulaire d'ajout d'évènement
      nom: ['', Validators.required],
      image: ['', Validators.required],
      description: ['', Validators.required],
      nbeJoueurMax: ['', [Validators.required]],
      duree: ['', [Validators.required]],
      difficulte: ['', Validators.required],
      prix: ['', Validators.required]
    });
  }

  // -----------------------------
  // Récupération des utilisateurs
  // -----------------------------
  public getUsers(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.userService.getUsers().subscribe({
        next:
          (response: User[]) => {
            this.userService.addUsersToLocalCache(response);
            this.users = response;
            this.refreshing = false;
            if (showNotification) {
              this.sendNotification(NotificationType.SUCCESS, `${response.length} user(s) loaded successfully.`);
            }
          },
        error:
          (errorResponse: HttpErrorResponse) => {
            this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
            this.refreshing = false;
          }
      }));
  }

  // -------------------------
  // Envoie d'une notification
  // -------------------------
  private sendNotification(notificationType: NotificationType, message: string) {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'Une erreur est survenue. Veuillez réessayer.');
    }
  }

  // ----------------------------
  // Mise à jour d'un utilisateur
  // ----------------------------
  updateUser(): void {
    if (this.userForm.invalid || !this.selectedUser?.id) {
      console.warn("Formulaire invalide ou utilisateur non sélectionné.");
      return;
    }
    const formData = new FormData();
    formData.append('prenom', this.userForm.value.prenom);
    formData.append('nom', this.userForm.value.nom);
    formData.append('username', this.userForm.value.username);
    formData.append('email', this.userForm.value.email);
    formData.append('role', this.userForm.value.role);
    console.log('Données envoyées :', Array.from(formData.entries()));
    this.userService.updateUser(formData, this.selectedUser.idUser).subscribe({
      next: (updatedUser) => {
        console.log('Utilisateur mis à jour avec succès');
        this.selectedUser = updatedUser;
        this.userForm.reset();
      },
      error: (error) => {
        console.error('Erreur lors de la mise à jour de l\'utilisateur :', error);
      }
    });
  }

  // --------------------------
  // Sélection d'un utilisateur
  // --------------------------
  public onSelectUser(selectedUser: User): void {
    this.selectedUser = selectedUser;
    this.userForm?.patchValue(selectedUser);
  }

  // ----------------------------
  // Suppression d'un utilisateur
  // ----------------------------
  deleteUser(userId: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) {
      this.userService.deleteUser(userId).subscribe(
        (response) => {
          console.log('Utilisateur supprimé avec succès', response);
          this.users = this.users.filter(user => user.idUser !== userId);
        },
        (error) => {
          console.error('Erreur lors de la suppression de l\'utilisateur', error);
        }
      );
    }
  }

  /* .................................................................................................................................. */

  imagePreview: string | ArrayBuffer | null = null;
  selectedFile: File | null = null;

  // Variable pour stocker le fichier sélectionné
  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];

    if (file) {
      this.selectedFile = file; // Stocke le fichier dans une variable
      this.addEvenementForm.patchValue({ image: file.name }); // Stocke le nom du fichier

      // Générer un aperçu de l'image
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result; // Met à jour l'aperçu
      };
      reader.readAsDataURL(file);
    }
  }

  // --------------------
  // Ajout d'un évènement
  // --------------------
  addEvenements(): void {
    this.refreshing = true;
    const formData = new FormData();

    // Ajouter les autres champs
    formData.append('nom', this.addEvenementForm.value.nom);
    formData.append('description', this.addEvenementForm.value.description);
    formData.append('duree', this.addEvenementForm.value.duree);
    formData.append('nbeJoueurMax', this.addEvenementForm.value.nbeJoueurMax);
    formData.append('prix', this.addEvenementForm.value.prix);
    formData.append('difficulte', this.addEvenementForm.value.difficulte);

    // Vérifier si une image est sélectionnée et l'ajouter
    if (this.selectedFile) {
      formData.append('image', this.selectedFile, this.selectedFile.name);
    } else {
      console.warn("Aucune image sélectionnée !");
    }

    console.log('Données envoyées :', Array.from(formData.entries()));

    this.subscriptions.push(
      this.evenementService.addEvenement(formData).subscribe({
        next: (data: Evenement) => {
          console.log('Évènement ajouté avec succès');
          this.sendNotification(NotificationType.SUCCESS, `Un nouvel évènement a été créé : ${data.nom}`);
          this.addEvenementForm.reset();
          this.selectedFile = null; // Réinitialiser le fichier sélectionné
          this.imagePreview = null; // Réinitialiser l'aperçu
        },
        error: (error) => {
          console.error('Erreur lors de l\'ajout de l\'évènement :', error);
        }
      })
    );
  }

  // --------------------------
  // Mise à jour d'un évènement
  // --------------------------
  updateEvenement(): void {
    if (this.updateEvenementForm.invalid || !this.selectedEvenement?.idEvenement) {
      console.warn("Formulaire invalide ou évènement non sélectionné.");
      return;
    }
    const formData = new FormData();
    formData.append('nom', this.updateEvenementForm.value.nom);
    formData.append('description', this.updateEvenementForm.value.description);
    formData.append('image', this.updateEvenementForm.value.image);
    formData.append("duree", this.updateEvenementForm.value.duree);
    formData.append("nbeJoueurMax", this.updateEvenementForm.value.nbeJoueurMax);
    formData.append("prix", this.updateEvenementForm.value.prix);
    formData.append('difficulte', this.updateEvenementForm.value.difficulte);
    console.log('Données envoyées :', Array.from(formData.entries()));
    this.evenementService.updateEvenement(formData, this.selectedEvenement.idEvenement).subscribe({
      next: (updatedEvenement) => {
        console.log('Évènement mis à jour avec succès');
        this.selectedEvenement = updatedEvenement;
        this.updateEvenementForm.reset();
      },
      error: (error) => {
        console.error('Erreur lors de la mise à jour de l\'évènement :', error);
      }
    });
  }

  // -----------------------------------
  // Récupération de tout les évènements
  // -----------------------------------
  getEvenements() {
    this.refreshing = true;
    this.subscriptions.push(
      this.evenementService.getEvenements().subscribe(evenements => {
        this.evenements = evenements;
        this.refreshing = false;
      }));
  }

  // ------------------------
  // Sélection d'un évènement
  // ------------------------
  public onSelectEvenement(selectedEvenement: Evenement): void {
    this.selectedEvenement = selectedEvenement;
    this.updateEvenementForm?.patchValue(selectedEvenement);
  }

  // --------------------------
  // Suppression d'un évènement
  // --------------------------
  deleteEvenement(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet évènement ?')) {
      this.evenementService.deleteEvenement(id).subscribe(
        (response) => {
          console.log('Utilisateur supprimé avec succès', response);
          this.evenements = this.evenements.filter(evenement => evenement.idEvenement !== id);
        },
        (error) => {
          console.error('Erreur lors de la suppression de l\'évènement', error);
        }
      );
    }
  }

  // Méthode appelée lors de la destruction du composant
  ngOnDestroy(): void {
    // Annule tous les abonnements pour éviter les fuites de mémoire
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}