import { Injectable } from '@angular/core';
import { AppSettings } from '../../settings/app.settings';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PaymentRequest {
  montant: number;
  numeroCarteBancaire: string;
  dateExppiration: string;
  cvv: string;
}

export interface Paiement {
  id: number;
  montant: number;
  datePaiement: string;
  statut: 'APPROUVE' | 'REFUSE' | 'EN_ATTENTE';
}

@Injectable({
  providedIn: 'root'
})
export class PaiementService {

  // Stocke l'URL de base de l'API pour éviter la duplication
  private host = AppSettings.APP_URL;

  // Injection du service HttpClient pour faire des appels HTTP
  constructor(private http: HttpClient) { }

  public processPayment(reservationId: number, paymentRequest: PaymentRequest): Observable<Paiement> {
    const params = new HttpParams().set('reservationId', reservationId.toString());
    return this.http.post<Paiement>(`${this.host}/api/auth/payment`, paymentRequest, { params });
  }

  public getAllPayments(): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(this.host);
  }

  public getPaymentsByStatus(statut: string): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(`${this.host}/status/${statut}`);
  }
}

