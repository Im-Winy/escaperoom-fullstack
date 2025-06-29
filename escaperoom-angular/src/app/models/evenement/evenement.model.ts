export class Evenement {
    public idEvenement: number;
    public nom: string;
    public description: string;
    public image: string;
    public duree: number;
    public nbeJoueurMax: number;
    public prix: number;
    public difficulte: string;

    constructor() {
        this.idEvenement = 0;
        this.nom = '';
        this.description = '';
        this.image = '';
        this.nom = '';
        this.duree = 0;
        this.nbeJoueurMax = 0;
        this.prix = 0;
        this.difficulte = '';
    }
}
