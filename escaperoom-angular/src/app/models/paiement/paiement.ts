export interface Paiement {
  id: number;
  montant: number;
  datePaiement: string;
  statut: 'APPROUVE' | 'REFUSE' | 'EN_ATTENTE';
}