package fi.tamk.anpro;

/**
 * Sis�lt�� kaikkien graafisten objektien yhteiset ominaisuudet ja tiedot.
 * Hallitsee esimerkiksi animaatioiden p�ivitt�misen, k�yt�ss� olevat tekstuurit
 * (tunnukset) ja objektin sijainnin.
 */
abstract public class GfxObject
{
    /* Objektin sijainti */
    public float x = 0;
    public float y = 0;
    
    /* K�yt�ss� oleva animaatio ja sen tiedot */
    public  int   usedAnimation    = -1;
    public  int[] animationLength;
    public  int   currentFrame     = 0;
    private int   currentLoop      = 0;
    private int   animationLoops   = 0;
    
    /* Staattinen k�yt�ss� oleva tekstuuri */
    public int usedTexture = 0;
    
    /* Erityistoiminto */
    protected boolean actionActivated = false; // Kertoo, onko toiminto k�ynniss� (asetetaan arvoksi true,
                                               // kun halutaan kutsua objektin triggerEndOfAction-funktiota
                                               // k�ynniss� olevan animaation loputtua)
    protected int     actionId;                // Toiminnon tunnus
    
    /**
     * Alustaa luokan muuttujat.
     */
    public GfxObject()
    {
        animationLength = new int[4];
    }
    
    /**
     * K�ynnist�� animaation ja m��ritt�� toistokerrat.
     * 
     * @param int Animaation tunnus
     * @param int Toistokerrat
     */
    public final void startAnimation(int _animation, int _loops)
    {
        // Tallennetaan muuttujat
        usedAnimation  = _animation;
        animationLoops = _loops;
        currentFrame   = 0;
        
        // M��ritet��n ensimm�inen toistokerta
        if (_loops > 0) {
            currentLoop = 1;
        }
        else {
            currentLoop = 0;
        }
    }
    
    /**
     * Lopettaa animaation ja ottaa halutun tekstuurin k�ytt��n.
     * 
     * @param int Tekstuurin tunnus
     */
    public final void stopAnimation(int _texture)
    {
        usedAnimation = -1;
        
        usedTexture   = _texture;
    }
    
    /**
     * P�ivitt�� animaation seuraavaan kuvaruutuun. Hallitsee my�s toistokerrat
     * ja mahdollisen palautuksen vakiotekstuuriin (tunnus 0).
     */
    public final void update()
    {
        // Animaatiolle on m��ritetty toistokerrat
        if (animationLoops > 0) {
            
            // Tarkistetaan, p��ttyyk� animaation toistokerta ja toimitaan sen mukaisesti.
            if (currentFrame + 1 > animationLength[usedAnimation]) {
                currentFrame = 0;
                ++currentLoop;
                if (currentLoop > animationLoops) {
                    usedAnimation = -1;
                    usedTexture   = 0;
                    
                    if (actionActivated) {
                        actionActivated = false;
                        triggerEndOfAction();
                    }
                }
            }
            else {
                ++currentFrame;
            }
        }
        // Animaatio on p��ttym�t�n
        else {
            
            // Tarkistetaan, p��ttyyk� animaation toistokerta. Kelataan takaisin alkuun
            // tarvittaessa.
            if (currentFrame + 1 > animationLength[usedAnimation]) {
                currentFrame = 0;
            }
            else {
                ++currentFrame;
            }
        }
    }

    /**
     * K�sittelee jonkin toiminnon p��ttymisen. Kutsutaan animaation loputtua, mik�li
     * actionActivated on TRUE.
     * 
     * K�ytet��n esimerkiksi objektin tuhoutuessa. Objektille m��ritet��n animaatioksi
     * sen tuhoutumisanimaatio, tilaksi Wrapperissa m��ritet��n 2 (piirret��n, mutta
     * p�ivitet��n ainoastaan animaatio) ja asetetaan actionActivatedin arvoksi TRUE.
     * T�ll�in GameThread p�ivitt�� objektin animaation, Renderer piirt�� sen, ja kun
     * animaatio p��ttyy, kutsutaan objektin triggerEndOfAction-funktiota. T�ss�
     * funktiossa objekti k�sittelee tilansa. Tuhoutumisanimaation tapauksessa objekti
     * m��ritt�� itsens� ep�aktiiviseksi.
     * 
     * Jokainen objekti luo funktiosta oman toteutuksensa, sill� toimintoja voi olla
     * useita. Objekteilla on my�s k�yt�ss��n actionId-muuttuja, jolle voidaan asettaa
     * haluttu arvo. T�m� arvo kertoo objektille, mink� toiminnon se juuri suoritti.
     */
    abstract protected void triggerEndOfAction();
}
