package fi.tamk.anpro;

import android.util.Log;

/**
 * Sisältää kaikkien peliobjektien (pelaaja, viholliset, liittolaiset, ammukset yms.)
 * yhteiset tiedot ja toiminnallisuudet.
 * 
 * @extends GfxObject
 */
abstract public class GameObject extends GfxObject
{
    /* Vakioita */
    // Kääntymissuunnat
    public static final int TO_THE_LEFT  = 1;
    public static final int TO_THE_RIGHT = 2;
    
    // Törmäystyypit
    public static final int COLLISION_WITH_PROJECTILE       = 10;
    public static final int COLLISION_WITH_PLAYER           = 11;
    public static final int COLLISION_WITH_ENEMY            = 12;
    public static final int COLLISION_WITH_OBSTACLE         = 13;
    public static final int COLLISION_WITH_COLLECTABLE      = 14;
    
    // Tarkemmat törmäystyyppien määrittelyt ammuksista pisteiden laskemista varten
    public static final int COLLISION_WITH_PLAYERPROJECTILE = 15;
    public static final int COLLISION_WITH_ALLYPROJECTILE   = 16;
    
    /* Objektin tiedot (kaikille) */
    public int speed;
    
    /* Objektin tiedot (pelaajalle, vihollisille ja liittolaisille) */
    protected int armor;
    protected int currentArmor;
    protected int health;
    protected int currentHealth;
    
    // Panssarien läpäisykyky (lisää tehtyä vahinkoa)
    protected int armorPiercing = 0;
    
    /* Törmäystunnistus */
    protected int collisionRadius = 0;
    
    /* Törmäysvahinko */
    public int collisionDamage = 0;
    
    /* Lineaarinen liike */
    protected int movementSpeed;            // Kuinka monta yksikköä objekti liikkuu kerrallaan. Arvot välillä 0-5
    protected int movementDelay;            // Arvot välillä 5-100(ms), mitä suurempi sitä hitaampi kiihtyvyys
    protected int movementAcceleration = 0; // Liikkeen kiihtyminen ja hidastuminen
    
    /* Kääntyminen (liikkumissuunta) */
    protected int turningSpeed;            // Montako astetta käännytään per päivitys
    protected int turningDelay;            // Arvot välillä 5-100(ms), mitä suurempi sitä hitaampi kääntyminen
    protected int turningAcceleration = 0; // Kääntymisen kiihtyvyys
    protected int turningDirection    = 0; // 0 ei käänny, 1 vasen, 2 oikea
    
    /* Kääntyminen (katsomissuunta) */
    protected int facingTurningSpeed;
    protected int facingTurningDelay;
    protected int facingTurningAcceleration = 0;
    protected int facingTurningDirection    = 0;
    
    /* Päivitysajat */
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
    

    /* =======================================================
     * Uudet funktiot
     * ======================================================= */
    /**
     * Käsittelee räjähdyksien vaikutukset objektiin.
     * 
     * @param int Räjähdyksen aiheuttama vahinko
     */
    public void triggerImpact(int _damage) { }

    /**
     * Käsittelee törmäyksien vaikutukset objektiin.
     * 
     * @param _eventType     Osuman tyyppi, eli mihin törmättiin (tyypit löytyvät GameObjectista)
     * @param _damage        Osuman aiheuttama vahinko
     * @param _armorPiercing Osuman kyky läpäistä suojat (käytetään, kun törmättiin ammukseen)
     */
    public void triggerCollision(int _eventType, int _damage, int _armorPiercing) { }

    /**
     * Etsii räjähdyksen vaikutusalueella olevia vihollisia ja kutsuu niiden triggerImpact-funktiota.
     */
    protected void triggerExplosion() { }
    
    /**
     * Aiheuttaa ammuksen erikoistoiminnon.
     */
    protected void triggerSpecialAction() { }
    
    /**
     * Päivittää liikkumisen ja kääntymisen.
     * 
     * @param _time Tämän hetkinen aika
     */
    public void updateMovement(long _time)
    {
        // Lasketaan liikkumisnopeus objektille
        // Mitä suurempi movementDelay sitä hitaammin objekti liikkuu
        if (_time - movementTime >= movementDelay) {
            movementTime = _time;
            
            x += Math.cos((direction * Math.PI)/180) * movementSpeed * Options.scaleX;
            y += Math.sin((direction * Math.PI)/180) * movementSpeed * Options.scaleY;

            // Päivitetään nopeus kiihtyvyyden avulla
            movementDelay -= movementAcceleration;
            
            if (movementDelay < 0) {
                movementDelay = 0;
            }
            else if (movementDelay > 200) {
            	setMovementDelay(1.0f);
            	setMovementSpeed(0.0f);
            	movementAcceleration = 0;
            }
            
            if (movementSpeed > 0 && !(this instanceof AbstractProjectile)) {
                EffectManager.showTrailEffect(this);
            }
        }
        
        // Lasketaan kääntymisnopeus objektille
        if (_time - turningTime >= turningDelay) {
            turningTime = _time;
            
            /* Kääntymissuunta (liikkuminen) */
            // Jos objektin kääntymissuunta on vasemmalle
            if (turningDirection == TO_THE_LEFT) {
                direction += turningSpeed;
                
                if (direction == 360) {
                    direction = 0;
                }
            }
            // Jos objektin kääntymissuunta on oikealle
            else if (turningDirection == TO_THE_RIGHT) {
                direction -= turningSpeed;
                
                if (direction < 0) {
                    direction = 360 + direction;
                }
            }
            
            // Päivitetään nopeus kiihtyvyyden avulla
            turningDelay -= turningAcceleration;
            
            if (turningDelay < 0) {
                turningDelay = 0;
            }

            /* Kääntymissuunta (katsominen) */
            // Jos objektin kääntymissuunta on vasemmalle
            if (facingTurningDirection == TO_THE_LEFT) {
                facingDirection += facingTurningSpeed;
                
                if (facingDirection == 360) {
                	facingDirection = 0;
                }
            }
            // Jos objektin kääntymissuunta on oikealle
            else if (turningDirection == TO_THE_RIGHT) {
            	facingDirection -= facingTurningSpeed;
                
                if (facingDirection < 0) {
                	facingDirection = 359;
                }
            }
            
            // Päivitetään nopeus kiihtyvyyden avulla
            facingTurningDelay -= facingTurningAcceleration;
            
            if (facingTurningDelay < 0) {
            	facingTurningDelay = 0;
            }
        }
    }

    /**
     * Laskee objektille "nopeuden" (pikselien määrä / liike).
     * 
     * @param _multiplier Nopeuden muutoskerroin
     */
    public final void setMovementSpeed(float _multiplier)
    {
    	movementSpeed = (int) (((float)speed / 2) * _multiplier);
    }

    /**
     * Laskee objektille liikkeen viiveen. 
     * 
     * @param _multiplier Nopeuden muutoskerroin
     */
    public final void setMovementDelay(float _multiplier)
    {
    	movementDelay = (int) (80 / (_multiplier * (float)speed));
    }
    
    /**
     * Laskee objektille "kääntymisnopeuden" (asteiden määrä / liike).
     * 
     * @param _multiplier Nopeuden muutoskerroin
     */
    public final void setTurningSpeed(float _multiplier)
    {
    	turningSpeed = (int) (_multiplier * (float)speed / 2);
    }

    /**
     * Laskee objektille kääntymisen viiveen. 
     * 
     * @param _multiplier Nopeuden muutoskerroin
     */
    public final void setTurningDelay(float _multiplier)
    {
    	turningDelay = (int) (60 / (_multiplier * (float)speed));
    }
    
    /**
     * Laskee objektille "kääntymisnopeuden" (katselukulma).
     * 
     * @param _multiplier Nopeuden muutoskerroin
     */
    public final void setFacingTurningSpeed(float _multiplier)
    {
    	facingTurningSpeed = (int) (_multiplier * (float)speed / 2);
    }

    /**
     * Laskee objektille kääntymisen viiveen (katselukulma).
     * 
     * @param _multiplier Nopeuden muutoskerroin
     */
    public final void setFacingTurningDelay(float _multiplier)
    {
    	facingTurningDelay = (int) (60 / (_multiplier * (float)speed));
    }
    
    /**
     * Aktivoi peliobjektin tuhoutumisen, toteutus jokaisella objektilla omassa luokassaan.
     */    
    public void triggerDestroyed() { }

    /**
     * Epäaktivoi peliobjektin, toteutus jokaisella objektilla omassa luokassaan.
     */
    public void triggerDisabled() { }
}
