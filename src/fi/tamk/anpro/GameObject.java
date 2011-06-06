package fi.tamk.anpro;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class GameObject extends GfxObject {
	// Vakioita t�rm�yksentunnistukseen
	public static final int NO_COLLISION = 0;
	public static final int CIRCLE_COLLISION = 1;
	public static final int COLLISION_WITH_PROJECTILE = 10;
	public static final int COLLISION_WITH_PLAYER = 11;
	
	// Vakioita valintaan (ampumisessa)
	public static final int NO_SELECTION = 0;
	public static final int SELECTABLE = 1;
	
	// T�rm�yksentunnistuksen muuttujat
	public int collisionType = 0;
	public int collisionRadius = 0;
	
	// Valinnan muuttujat
	public int selectionRadius = 0;
	
	// Liikkeen muuttujat
	private int movementSpeed = 0; // Kuinka monta yksikk�� objekti liikkuu kerrallaan. Arvot v�lill� 0-5
	private int movementDelay = 0; // Arvot v�lill� 5-100(ms), mit� suurempi sit� hitaampi kiihtyvyys
	private int movementAcceleration = 0; // Liikkeen kiihtyminen ja hidastuminen
	
	// Suunnan ja k��ntymisen muuttujat
	private int direction = 0; // 0 on suoraan yl�sp�in, 90 oikealle
	private int turningDelay = 0; // Arvot v�lill� 5-100(ms), mit� suurempi sit� hitaampi k��ntyminen
	private int turningAcceleration = 0; // K��ntymisen kiihtyvyys
	private int turningDirection = 0; // 0 ei k��nny, 1 vasen, 2 oikea
	
	// Tallennetaan aika
	private long time;
	
	/*
	 public GameObject() {
		super();
	}
	*/	
	
	// Tekee osumalaskennat r�j�hdyksiss� (ei kahden objektin osumisessa toisiinsa)
	public void triggerImpact(int _damage, int _armorPiercing) {
		// VIRTUAALINEN
	}
	
	// Tekee osumalaskennat suorassa osumassa toiseen objektiin
	public void handleCollision(int _eventType, int _damage, int _armorPiercing) {
		// VIRTUAALINEN
	}
	
	// 
	public void updateMovement(long _time) {
		// Lasketaan k��ntymisnopeus objektille
		if (_time - time >= turningDelay) {
			// Jos objektin k��ntymissuunta on ei mihink��n
			if (turningDirection == 0) {
				// �L� TEE MIT��N
			}
			// Jos objektin k��ntymissuunta on vasemmalle
			else if (turningDirection == 1) {
				--direction;
				// 
				turningDelay = turningDelay - turningAcceleration;
				// Teko�ly k�sittelee turningAccelerationin
			}
			// Jos objektin k��ntymissuunta on oikealle
			else if (turningDirection == 2) {
				++direction;
				// Teko�ly k�sittelee turningAccelerationin
				turningDelay = turningDelay - turningAcceleration;
			}
		}
		
		// Lasketaan liikkumisnopeus objektille
		// Mit� suurempi movementDelay sit� hitaammin objekti liikkuu
		if (_time - time >= movementDelay) {
			// Jos objekti liikkuu eteenp�in
			if (movementSpeed > 0 ) {
				xf += (movementSpeed * Math.cos(direction)); // Jos objekti liikkuu liian nopeasti -> movementSpeed*kerroin (esim. 0.1)
				yf += (movementSpeed * Math.sin(direction));
				// Teko�ly k�sittelee movementAccelerationin
				movementDelay = movementDelay - movementAcceleration;
			}
			// Jos objekti ei liiku
			else {
				// �L� TEE MIT��N
			}
		}
	}
}
