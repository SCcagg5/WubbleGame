import java.io.Serializable;

class Data implements Serializable {
    public personnage[] persos = new personnage[5];
    public block[] blockbase = new block[25];
    public personnage[] enemies = new personnage[5];
    public anime[] anime = new anime[5];
    public personnage perso = new personnage(0);
    public int number = -1;
    public Data() {}
}
