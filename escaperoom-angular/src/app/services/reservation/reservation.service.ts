import { Injectable } from '@angular/core';
import { AppSettings } from '../../settings/app.settings';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { TimeSlot } from '../../models/time-slot/time-slot.model';
import { Reservation } from '../../models/reservation/reservation.model';
import { AuthenticationService } from '../authentication/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  private host = AppSettings.APP_URL;

  constructor(private http: HttpClient, private authService: AuthenticationService) { }

  public getAvailableTimeSlotsForEvent(eventId: number, selectedDate: string): Observable<TimeSlot[]> {
    const url = `${this.host}/api/auth/evenement/${eventId}/timeslots?selectedDate=${selectedDate}`;  // L'URL dépend de l'API, il faut l'ajuster
    return this.http.get<TimeSlot[]>(url);
  }

  public generateSlotsForDay(date: Date): Observable<TimeSlot[]> {
    const params = new HttpParams().set('date', date.toISOString().split('T')[0]);
    return this.http.post<TimeSlot[]>(`${this.host}/api/auth/generer-creneaux-journee`, null, { params });
  }

  public getReservationsByUserId(userId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.host}/api/auth/historique-reservations/user/${userId}`);
  }

  public reserve(timeSlotId: number, idEvenement: number): Observable<Reservation> {
    const user = this.authService.getUserFromLocalCache();
    if (!user || !user.id) {
      return throwError(() => new Error('Utilisateur non connecté ou id manquant.'));
    }
  
    const url = `${this.host}/api/auth/reservation/${user.idUser}/${idEvenement}`;
    const params = new HttpParams().set('timeSlotId', timeSlotId.toString());  // Assurez-vous que timeSlotId est bien un string
  
    return this.http.post<Reservation>(url, null, { params });
  }
  
  
}
