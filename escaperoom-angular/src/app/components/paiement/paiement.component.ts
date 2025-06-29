import { Component, OnInit, ViewChild } from '@angular/core';
import { PaiementService, PaymentRequest } from '../../services/paiement/paiement.service';
import { Paiement } from '../../models/paiement/paiement';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Reservation } from '../../models/reservation/reservation.model';
import { ReservationService } from '../../services/reservation/reservation.service';
import { Title } from '@angular/platform-browser';
import { AuthenticationService } from '../../services/authentication/authentication.service';
import { User } from '../../models/user/user.model';

@Component({
  selector: 'app-paiement',
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './paiement.component.html',
  styleUrls: ['./paiement.component.css']
})
export class PaiementComponent implements OnInit {

  reservationId!: number;
  paymentRequest: PaymentRequest = {
    montant: 0,
    numeroCarteBancaire: '',
    dateExppiration: '',
    cvv: ''
  };

  paiement?: Paiement;
  reservations: Reservation[] = [];
  groupedReservations: Reservation[][] = [];
  userId!: number;
  user!: User;

  constructor(
    private paiementService: PaiementService,
    private reservationService: ReservationService,
    private authenticationService: AuthenticationService,
    private titleService: Title
  ) { }

  ngOnInit(): void {
    this.titleService.setTitle('Paiement');
    this.getUser();
    this.loadReservations();
  }

  selectedReservation(reservation: Reservation): void {
    this.reservationId = reservation.idReservation;
    this.paymentRequest.montant = reservation.montant;
  }

  loadReservations(): void {
    this.reservationService.getReservationsByUserId(this.userId).subscribe({
      next: (data) => {
        this.reservations = data;
        this.groupReservations();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des réservations :', err);
      }
    });
  }

  getUser(): void {
    this.user = this.authenticationService.getUserFromLocalCache();
    if (this.user) {
      this.userId = this.user.idUser;
      this.loadReservations();
    } else {
      console.warn('Aucun utilisateur trouvé dans le cache local');
    }
  }

  groupReservations(): void {
    this.groupedReservations = [];
    for (let i = 0; i < this.reservations.length; i += 2) {
      this.groupedReservations.push(this.reservations.slice(i, i + 2));
    }
  }

  onSubmit(): void {
    this.paiementService.processPayment(this.reservationId, this.paymentRequest).subscribe({
      next: (data) => {
        this.paiement = data;
        alert('✅ Paiement effectué avec succès.');
      },
      error: (err) => {
        console.error(err);
        alert('❌ Échec du paiement : ' + (err.error?.message || 'Erreur inconnue'));
      }
    });
  }
}
