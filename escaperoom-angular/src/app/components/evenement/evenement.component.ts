// evenement.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EvenementService } from '../../services/evenement/evenement.service';
import { ReservationService } from '../../services/reservation/reservation.service';
import { Title } from '@angular/platform-browser';
import { TimeSlot } from '../../models/time-slot/time-slot.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoleService } from '../../services/role/role.service';
import { AuthenticationService } from '../../services/authentication/authentication.service';
import { Evenement } from '../../models/evenement/evenement.model';

@Component({
  selector: 'app-evenement',
  imports: [CommonModule, FormsModule],
  templateUrl: './evenement.component.html',
  styleUrls: ['./evenement.component.css']
})
export class EvenementComponent implements OnInit {

  evenement!: Evenement;
  evenementId!: number;
  selectedDate: string = '';
  timeSlots: TimeSlot[] = [];  // Correctement défini comme un tableau de TimeSlot[]
  isLoading = true;
  hasRole = false!

  constructor(
    private route: ActivatedRoute,
    private roleService: RoleService,
    private evenementService: EvenementService,
    private reservationService: ReservationService,
    private titleService: Title,
    private authService: AuthenticationService
  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (isNaN(id)) {
      console.error("ID invalide");
      return;
    }

    this.evenementId = id;
    this.loadEventDetails(id);
    this.hasRole = this.roleService.hasRole('ROLE_ADMIN');
  }

  reserver(slot: TimeSlot): void {
    const user = this.authService.getUserFromLocalCache();

    if (!user || !user.id) {
      console.error('Utilisateur non connecté ou id manquant');
      alert('Vous devez être connecté pour effectuer une réservation.');
      return;  // Empêche de procéder à la réservation si l'utilisateur n'est pas valide
    }

    this.reservationService.reserve(slot.idTimeSlot, this.evenement.idEvenement).subscribe(
      (response) => {
        console.log('Réservation réussie', response);
        this.loadAvailableTimeSlots();
      },
      (error) => {
        console.error('Erreur lors de la réservation', error);
      }
    );
  }

  annulerReservation(_t58: TimeSlot) {
    throw new Error('Method not implemented.');
  }

  loadEventDetails(id: number): void {
    this.evenementService.getEvenement(id).subscribe({
      next: (evenement) => {
        this.evenement = evenement;
        this.titleService.setTitle(evenement.nom);
        this.isLoading = false;
      },
      error: (err) => {
        console.error("Erreur lors de la récupération de l'événement", err);
        this.isLoading = false;
      }
    });
  }

  onDateChange(event: any): void {
    this.selectedDate = event.target.value;
    if (this.selectedDate) {
      this.loadAvailableTimeSlots();
    } else {
      this.timeSlots = []; // On vide les créneaux si la date est vide
    }
  }

  generateSlots(): void {
    if (!this.selectedDate) {
      console.error('Aucune date sélectionnée.');
      return;
    }

    const dateObj = new Date(this.selectedDate); // Conversion du string vers Date
    this.reservationService.generateSlotsForDay(dateObj).subscribe({
      next: (slots: TimeSlot[]) => {
        this.timeSlots = slots;
        console.log('Créneaux générés:', slots);
      },
      error: (err) => {
        console.error('Erreur lors de la génération des créneaux', err);
      }
    });
  }

  loadAvailableTimeSlots(): void {
    if (this.selectedDate) {
      this.reservationService.getAvailableTimeSlotsForEvent(this.evenementId, this.selectedDate)
        .subscribe({
          next: (slots: TimeSlot[]) => {
            this.timeSlots = slots;
            console.log("Créneaux horaires : ", this.timeSlots);
          },
          error: (err) => {
            console.error('Erreur de chargement des créneaux :', err);
          }
        });
    }
  }

}
