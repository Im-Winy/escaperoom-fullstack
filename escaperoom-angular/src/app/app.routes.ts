import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { AboutComponent } from './components/about/about.component';
import { ContactComponent } from './components/contact/contact.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { EvenementComponent } from './components/evenement/evenement.component';
import { TousLesEvenementsComponent } from './components/tous-les-evenements/tous-les-evenements.component';
import { AuthenticationGuard } from './guard/authentication.guard';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { UserComponent } from './components/user/user.component';
import { AdminComponent } from './components/admin/admin.component';
import { roleGuard } from './guard/role.guard';
import { PaiementComponent } from './components/paiement/paiement.component';

export const routes: Routes = [
    { path: '', redirectTo: 'welcome', pathMatch: 'full' }, // Route pour la page "Accueil"
    { path: 'welcome', component: WelcomeComponent },  // Route pour la page "Bienvenue"
    { path: 'home', component: HomeComponent, canActivate: [AuthenticationGuard] },  // Route pour la page "Accueil" protégée
    { path: 'about', component: AboutComponent, canActivate: [AuthenticationGuard] },  // Route pour la page "À propos"
    { path: 'contact', component: ContactComponent, canActivate: [AuthenticationGuard] },  // Route pour la page "Contact"
    { path: 'login', component: LoginComponent },  // Route pour la page "Connexion"
    { path: 'register', component: RegisterComponent },  // Route pour la page "Inscription"
    { path: 'profile', component: UserComponent, canActivate: [AuthenticationGuard] },  // Route pour la page "Utilisateur"
    { path: 'admin', component: AdminComponent, canActivate: [AuthenticationGuard, roleGuard], data: { role: ['ROLE_ADMIN'] } },  // Route pour la page "Administrateur"
    { path: 'evenement/:id', component: EvenementComponent, canActivate: [AuthenticationGuard] }, // Route pour la page "Evenement"
    { path: 'tous-les-evenements', component: TousLesEvenementsComponent, canActivate: [AuthenticationGuard] },  // Route pour la page "tous les Evenements"
    { path: 'booking', component: PaiementComponent, canActivate: [AuthenticationGuard] },  // Route pour la page "tous les Evenements"
    { path: '**', component: PageNotFoundComponent } // Route pour la page "page introuvable / erreur 404"
];
