import { Evenement } from "../evenement/evenement.model";
import { Paiement } from "../paiement/paiement";
import { TimeSlot } from "../time-slot/time-slot.model";

export interface Reservation {
  idReservation: number;
  numeroReservation: string;
  dateReservation: string;
  montant: number;
  montantHT: number;
  montantTVA: number;
  evenement: Evenement;
  timeSlot: TimeSlot;
  paiement: Paiement;
}