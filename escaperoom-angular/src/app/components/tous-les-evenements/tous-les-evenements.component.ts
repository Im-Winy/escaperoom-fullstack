import { Component, OnInit } from '@angular/core';
import { Evenement } from '../../models/evenement/evenement.model';
import { EvenementService } from '../../services//evenement/evenement.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-tous-les-evenements',
  imports: [CommonModule, RouterModule],
  templateUrl: './tous-les-evenements.component.html',
  styleUrl: './tous-les-evenements.component.css'
})
export class TousLesEvenementsComponent implements OnInit {

  evenements: Evenement[] = [];

  constructor(
    private evenementService: EvenementService,
    private titleService: Title
  ) { }

  ngOnInit(): void {
    this.titleService.setTitle('Tous les évènements');
    this.evenementService.getEvenements().subscribe(evenements => {
      this.evenements = evenements;
    });
  }

}
