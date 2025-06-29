export class User {
    public idUser: number;
    public id: string;
    public username: string;
    public password: string;
    public nom: string;
    public prenom: string;
    public email: string;
    public telephone: string;
    public adresse: string;
    public ville: string;
    public pays: string;
    public lastLoginDate: Date;
    public lastLoginDateDisplay: Date;
    public joinDate: Date;
    public role: string[]; //ROLE_USER(read, edit), ROLE_ADMIN(...,delete)
    public authorities: string[];  //[]=tableau de string, Authorities = permissions(read,edit,delete)
    public active: boolean;  //Pour activer les rôles
    public notLocked: boolean;

    constructor() {
        this.idUser = 0,
            this.id = '',
            this.username = '',
            this.password = '',
            this.nom = '',
            this.prenom = '',
            this.email = '',
            this.telephone = '',
            this.adresse = '',
            this.ville = '',
            this.pays = '',
            this.lastLoginDate = new Date(),
            this.lastLoginDateDisplay = new Date(),
            this.joinDate = new Date(),
            this.role = [],
            this.authorities = [],
            this.active = false,
            this.notLocked = false;
    }

}
