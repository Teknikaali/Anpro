package fi.tamk.anpro;

import android.util.Log;

/**
 * Sis�lt�� kaikkien peliobjektien (pelaaja, viholliset, liittolaiset, ammukset yms.)
 * yhteiset tiedot ja toiminnallisuudet.
 * 
 * @extends GfxObject
 */
abstract public class GameObject extends GfxObject
{
    /* Objektin tiedot (kaikille) */
    public int speed;
    
    /* Objektin tiedot (pelaajalle, vihollisille ja liittolaisille) */
    public int armor;
    public int currentArmor;
    public int health;
    public int currentHealth;
    	
    /* Vakioita */
    // K��ntymissuunnat
    public static final int TO_THE_LEFT  = 1;
    public static final int TO_THE_RIGHT = 2;
    
    // T�rm�ystyypit
    public static final int COLLISION_WITH_PROJECTILE  = 10;
    public static final int COLLISION_WITH_PLAYER      = 11;
    public static final int COLLISION_WITH_ENEMY       = 12;
    public static final int COLLISION_WITH_OBSTACLE    = 13;
    public static final int COLLISION_WITH_COLLECTABLE = 14;
    
    /* T�rm�ystunnistus */
    public int collisionRadius = 0;
    
    /* Lineaarinen liike */
    protected int movementSpeed;            // Kuinka monta yksikk�� objekti liikkuu kerrallaan. Arvot v�lill� 0-5
    protected int movementDelay;            // Arvot v�lill� 5-100(ms), mit� suurempi sit� hitaampi kiihtyvyys
    protected int movementAcceleration = 0; // Liikkeen kiihtyminen ja hidastuminen
    
    /* K��ntyminen (liikkumissuunta) */
    protected int turningSpeed;            // Montako astetta k��nnyt��n per p�ivitys
    protected int turningDelay;            // Arvot v�lill� 5-100(ms), mit� suurempi sit� hitaampi k��ntyminen
    protected int turningAcceleration = 0; // K��ntymisen kiihtyvyys
    public    int turningDirection    = 0; // 0 ei k��nny, 1 vasen, 2 oikea
    
    /* K��ntyminen (katsomissuunta) */
    protected int facingTurningSpeed;
    protected int facingTurningDelay;
    protected int facingTurningAcceleration = 0;
    public    int facingTurningDirection    = 0;
    
    /* P�ivitysajat */
    private long turningTime  = 0;
    private long movementTime = 0;
    
    /**
     * Alustaa luokan muuttujat.
     */
    public GameObject(int _speed)
    {
        super();
        
        speed = _speed;
        
        setMovementSpeed(1.0f);
        setMovementDelay(1.0f);
            
        setTurningSpeed(1.0f);
        setTurningDelay(1.0f);
    }
    
    /**
     * K�sittelee r�j�hdyksien vaikutukset objektiin.
     * 
     * @param int R�j�hdyksen aiheuttama vahinko
     */
    public void triggerImpact(int _damage) { }

    /**
     * K�sittelee t�rm�yksien vaikutukset objektiin.
     * 
     * @param int Osuman tyyppi, eli mihin t�rm�ttiin (tyypit l�ytyv�t GameObjectista)
     * @param int Osuman aiheuttama vahinko
     * @param int Osuman kyky l�p�ist� suojat (k�ytet��n, kun t�rm�ttiin ammukseen)
     */
    public void triggerCollision(int _eventType, int _damage, int _armorPiercing)
    {
    	// Ilmoitetaan v��r�n komennon kutsumisesta LogCatiin
    	Log.e("VIRHE", "Kutsuttiin v��r�� triggerCollision-funktiota! T�t� funktiota k�ytt�v�t vain viholliset ja liittolaiset!");
    }

    /**
     * K�sittelee t�rm�yksien vaikutukset objektiin.
     * 
     * @param int Osuman aiheuttama vahinko
     * @param int Osuman kyky l�p�ist� suojat (k�ytet��n, kun t�rm�ttiin ammukseen)
     */
    public void triggerCollision(int _damage, int _armorPiercing)
    {
    	// Ilmoitetaan v��r�n komennon kutsumisesta LogCatiin
    	Log.e("VIRHE", "Kutsuttiin v��r�� triggerCollision-funktiota! T�t� funktiota k�ytt�� vain pelaaja!");
    }
    
    /**
     * P�ivitt�� liikkumisen ja k��ntymisen.
     * 
     * @param long T�m�n hetkinen aika
     */
    public void updateMovement(long _time)
    {
        // Lasketaan liikkumisnopeus objektille
        // Mit� suurempi movementDelay sit� hitaammin objekti liikkuu
        if (_time - movementTime >= movementDelay) {
            movementTime = _time;
            
            x += Math.cos((direction * Math.PI)/180) * movementSpeed * Options.scaleX;
            y += Math.sin((direction * Math.PI)/180) * movementSpeed * Options.scaleY;

            // P�ivitet��n nopeus kiihtyvyyden avulla
            movementDelay -= movementAcceleration;
            
            if (movementDelay < 0) {
                movementDelay = 0;
            }
            else if (movementDelay > 200) {
            	setMovementDelay(1.0f);
            	setMovementSpeed(0.0f);
            	movementAcceleration = 0;
            }
            
        }
        
        // Lasketaan k��ntymisnopeus objektille
        if (_time - turningTime >= turningDelay) {
            turningTime = _time;
            
            /* K��ntymissuunta (liikkuminen) */
            // Jos objektin k��ntymissuunta on vasemmalle
            if (turningDirection == TO_THE_LEFT) {
                direction += turningSpeed;
                
                if (direction == 360) {
                    direction = 0;
                }
            }
            // Jos objektin k��ntymissuunta on oikealle
            else if (turningDirection == TO_THE_RIGHT) {
                direction -= turningSpeed;
                
                if (direction < 0) {
                    direction = 359;
                }
            }
            
            // P�ivitet��n nopeus kiihtyvyyden avulla
            turningDelay -= turningAcceleration;
            
            if (turningDelay < 0) {
                turningDelay = 0;
            }

            /* K��ntymissuunta (katsominen) */
            // Jos objektin k��ntymissuunta on vasemmalle
            if (facingTurningDirection == TO_THE_LEFT) {
                facingDirection += facingTurningSpeed;
                
                if (facingDirection == 360) {
                	facingDirection = 0;
                }
            }
            // Jos objektin k��ntymissuunta on oikealle
            else if (turningDirection == TO_THE_RIGHT) {
            	facingDirection -= facingTurningSpeed;
                
                if (facingDirection < 0) {
                	facingDirection = 359;
                }
            }
            
            // P�ivitet��n nopeus kiihtyvyyden avulla
            facingTurningDelay -= facingTurningAcceleration;
            
            if (facingTurningDelay < 0) {
            	facingTurningDelay = 0;
            }
        }
    }
    
    /**
     * Laskee objektille "nopeuden" (pikselien m��r� / liike).
     * 
     * @param float Nopeuden muutoskerroin
     */
    public final void setMovementSpeed(float _multiplier)
    {
    	movementSpeed = (int) (((float)speed / 2) * _multiplier);
    }

    /**
     * Laskee objektille liikkeen viiveen. 
     * 
     * @param float Nopeuden muutoskerroin
     */
    public final void setMovementDelay(float _multiplier)
    {
    	movementDelay = (int) (80 / (_multiplier * (float)speed));
    }
    
    /**
     * Laskee objektille "k��ntymisnopeuden" (asteiden m��r� / liike).
     * 
     * @param float Nopeuden muutoskerroin
     */
    public final void setTurningSpeed(float _multiplier)
    {
    	turningSpeed = (int) (_multiplier * (float)speed / 2);
    }

    /**
     * Laskee objektille k��ntymisen viiveen. 
     * 
     * @param float Nopeuden muutoskerroin
     */
    public final void setTurningDelay(float _multiplier)
    {
    	turningDelay = (int) (60 / (_multiplier * (float)speed));
    }
    
    /**
     * Laskee objektille "k��ntymisnopeuden" (katselukulma).
     * 
     * @param float Nopeuden muutoskerroin
     */
    public final void setFacingTurningSpeed(float _multiplier)
    {
    	facingTurningSpeed = (int) (_multiplier * (float)speed / 2);
    }

    /**
     * Laskee objektille k��ntymisen viiveen (katselukulma).
     * 
     * @param float Nopeuden muutoskerroin
     */
    public final void setFacingTurningDelay(float _multiplier)
    {
    	facingTurningDelay = (int) (60 / (_multiplier * (float)speed));
    }
}

