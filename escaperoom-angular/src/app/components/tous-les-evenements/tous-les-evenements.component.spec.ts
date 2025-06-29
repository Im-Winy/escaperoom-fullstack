import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TousLesEvenementsComponent } from './tous-les-evenements.component';

describe('TousLesEvenementsComponent', () => {
  let component: TousLesEvenementsComponent;
  let fixture: ComponentFixture<TousLesEvenementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TousLesEvenementsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TousLesEvenementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
