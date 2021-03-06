package fi.tamk.anpro;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

/**
 * Sis�lt�� pelaajan omat ominaisuudet ja tiedot, kuten asettamisen aktiiviseksi ja
 * ep�aktiiviseksi, piirt�misen ja t�rm�yksenhallinnan (ei tunnistusta).
 * 
 * @extends GameObject
 */
public class Player extends AiObject
{
	/* Osoittimet muihin luokkiin */
    private Wrapper  wrapper;
    private GameMode gameMode;
    private Hud      hud;
    
    /* Suojien palautumisen ajastin */
    public long outOfBattleTime = 0;
    
    /* Asetutoriaalien esittelyjen tarkastus */
    private boolean clusterTutorialHasShown;
    private boolean missileTutorialHasShown;
    private boolean spinninglaserTutorialHasShown;
    private boolean empTutorialHasShown;
    private boolean spitfireTutorialHasShown;
    private boolean swarmTutorialHasShown;
    
    /**
     * Alustaa luokan muuttujat.
     * 
     * @param _health Pelaajan el�m�t/kest�vyys
     * @param _armor Pelaajan puolustus
     * @param _gameMode Osoitin SurvivalModeen
     */
    public Player(int _health, int _armor, GameMode _gameMode, Hud _hud)
    {
        super(8); // TODO: Pelaajalle voisi mieluummin antaa nopeuden suoraan rakentajassa
        		  // Muiden GameObjectien tapaan.

        /* Tallennetaan muuttujat */
        gameMode      = _gameMode;
        hud           = _hud;
        health  	  = _health;
        currentHealth = _health;
        armor         = _armor;
        currentArmor  = _armor;
        
        /* Haetaan tarvittavat luokat k�ytt��n */
        wrapper = Wrapper.getInstance();
        
        /* M��ritet��n Health- ja Armor-palkit */
    	hud.healthBar.initBar(health);
    	hud.armorBar.initBar(armor);
        
    	/* Alustetaan muuttujat */
    	z = 3;
    	
        // Alustetaan tutoriaalien esittelymuuttujat
        clusterTutorialHasShown       = false;
        missileTutorialHasShown       = false;
        spinninglaserTutorialHasShown = false;
        empTutorialHasShown           = false;
        spitfireTutorialHasShown      = false;
        swarmTutorialHasShown         = false;
    	
        // M��ritet��n asetukset
    	// TODO: SCALING (Options.scale)
        collisionRadius = (int) (25 * Options.scale);
        setMovementSpeed(0.0f);
        
        // Haetaan k�ytett�vien animaatioiden pituudet
        animationLength = new int[GLRenderer.AMOUNT_OF_PLAYER_ANIMATIONS];
        
        for (int i = 0; i < GLRenderer.AMOUNT_OF_PLAYER_ANIMATIONS; ++i) {
            if (GLRenderer.playerAnimations[i] != null) {
                animationLength[i] = GLRenderer.playerAnimations[i].length;
            }
        }
        
        /* M��ritet��n objektin tila (piirtolista ja teko�ly) */
        wrapper.addToDrawables(this);
        ai = new PlayerAi(this, Wrapper.CLASS_TYPE_PLAYER);
    }

	/* =======================================================
	 * Perityt funktiot
	 * ======================================================= */
    /**
     * Asettaa pelaajan aktiiviseksi.
     */
    @Override
    public final void setActive()
    {
        state = Wrapper.FULL_ACTIVITY;
    }

    /**
     * M��ritt�� objektin ep�aktiiviseksi. Sammuttaa my�s teko�lyn jos se on tarpeen.
     */
    @Override
    public final void setUnactive()
    {
        state = Wrapper.INACTIVE;
    }

    /**
     * Piirt�� k�yt�ss� olevan animaation tai tekstuurin ruudulle.
     * 
     * @param _gl OpenGL-konteksti
     */
    @Override
    public final void draw(GL10 _gl)
    {
        if (usedAnimation >= 0){
            GLRenderer.playerAnimations[usedAnimation].draw(_gl, x, y, direction, currentFrame);
        }
        else{
            GLRenderer.playerTextures[usedTexture].draw(_gl, x, y, direction, currentFrame);
        }
    }
	
	/**
     * K�sittelee objektin t�rm�ystarkistukset.
     */
    public final void checkCollision()
    {
    	/* Tarkistaa t�rm�ykset ker�tt�viin piste-esineiseen */
    	for (int i = wrapper.scoreCollectables.size()-1; i >= 0; --i) {
    		
    		if (wrapper.scoreCollectables.get(i).state == Wrapper.FULL_ACTIVITY) {
    		
				if (Math.abs(x - wrapper.scoreCollectables.get(i).x) <= Wrapper.gridSize) {
		        	if (Math.abs(y - wrapper.scoreCollectables.get(i).y) <= Wrapper.gridSize) {
		        		
		        		if (Utility.isColliding(wrapper.scoreCollectables.get(i), this)) {
		    				
		        			SoundManager.playSound(SoundManager.SOUND_PICKUP_SCORE, 1);
		        			
		        			wrapper.scoreCollectables.get(i).triggerCollision(COLLISION_WITH_PLAYER, 0, 0);
		           		}
		        	}
				}
    		}
    	}
    	
    	/* Tarkistaa t�rm�ykset ker�tt�viin aseisiin */
    	if (wrapper.weaponCollectable.state == Wrapper.FULL_ACTIVITY) {
			if (Math.abs(x - wrapper.weaponCollectable.x) <= Wrapper.gridSize) {
	        	if (Math.abs(y - wrapper.weaponCollectable.y) <= Wrapper.gridSize) {
	        		
	        		if (Utility.isColliding(wrapper.weaponCollectable, this)) {
	        			wrapper.weaponCollectable.triggerCollision(COLLISION_WITH_PLAYER, 0, 0);
	        			if (!clusterTutorialHasShown || !missileTutorialHasShown || !spinninglaserTutorialHasShown ||
	        				!empTutorialHasShown || !spitfireTutorialHasShown || !swarmTutorialHasShown) {
	        					if (!clusterTutorialHasShown && wrapper.weaponCollectable.weaponType == WeaponManager.WEAPON_CLUSTER) {
	    		        			SoundManager.playSound(SoundManager.SOUND_PICKUP_WEAPON, 1);
	        						gameMode.moveToTutorial(GameMode.TUTORIAL_NEW_WEAPON_CLUSTER);
	        						clusterTutorialHasShown = true;
	        					}
	        					else if (!empTutorialHasShown && wrapper.weaponCollectable.weaponType == WeaponManager.WEAPON_EMP) {
	    		        			SoundManager.playSound(SoundManager.SOUND_PICKUP_WEAPON, 1);
	        						gameMode.moveToTutorial(GameMode.TUTORIAL_NEW_WEAPON_EMP);
	        						empTutorialHasShown = true;
	        					}
	        					else if (!missileTutorialHasShown && wrapper.weaponCollectable.weaponType == WeaponManager.WEAPON_MISSILE) {
	    		        			SoundManager.playSound(SoundManager.SOUND_PICKUP_WEAPON, 1);
	        						gameMode.moveToTutorial(GameMode.TUTORIAL_NEW_WEAPON_MISSILE);
	        						missileTutorialHasShown = true;
	        					}
	        					else if (!spinninglaserTutorialHasShown && wrapper.weaponCollectable.weaponType == WeaponManager.WEAPON_SPINNING_LASER) {
	    		        			SoundManager.playSound(SoundManager.SOUND_PICKUP_WEAPON, 1);
	        						gameMode.moveToTutorial(GameMode.TUTORIAL_NEW_WEAPON_SPINNINGLASER);
	        						spinninglaserTutorialHasShown = true;
	        					}
	        					else if (!spitfireTutorialHasShown && wrapper.weaponCollectable.weaponType == WeaponManager.WEAPON_SPITFIRE) {
	    		        			SoundManager.playSound(SoundManager.SOUND_PICKUP_WEAPON, 1);
	        						gameMode.moveToTutorial(GameMode.TUTORIAL_NEW_WEAPON_SPITFIRE);
	        						spitfireTutorialHasShown = true;
	        					}
	        					else if (!swarmTutorialHasShown && wrapper.weaponCollectable.weaponType == WeaponManager.WEAPON_SWARM) {
	    		        			SoundManager.playSound(SoundManager.SOUND_PICKUP_WEAPON, 1);
	        						gameMode.moveToTutorial(GameMode.TUTORIAL_NEW_WEAPON_SWARM);
	        						swarmTutorialHasShown = true;
	        					}
	        			}
	        		}
	        	}
			}
    	}
    }
    
    /**
     * K�sittelee t�rm�ykset.
     * 
     * @param _damage Osuman aiheuttama vahinko
     * @param _armorPiercing Osuman kyky l�p�ist� suojat (k�ytet��n, kun t�rm�ttiin ammukseen)
     */
    @Override
    public final void triggerCollision(int _eventType, int _damage, int _armorPiercing)
    {
    	VibrateManager.vibrateOnHit();
		
	    
	    int armorTemp = currentArmor;
	    int healthTemp = currentHealth;
	    
	    Utility.checkDamage(this, _damage, _armorPiercing);
	    
	    if (currentArmor < armorTemp) {
	    	SoundManager.playSound(SoundManager.SOUND_HIT_ARMOR, 1);
	    	EffectManager.showPlayerArmorEffect(this);
	    	EffectManager.showArmorHitEffect(hud.armorBar);
	    }
	    if (currentHealth < healthTemp) {
	    	SoundManager.playSound(SoundManager.SOUND_HIT_HEALTH, 1);
	    	EffectManager.showHealthHitEffect(hud.healthBar);
	    }
	    
	    hud.armorBar.updateValue(currentArmor);
	    hud.healthBar.updateValue(currentHealth);
	    
	    if (currentHealth <= 0 && state == Wrapper.FULL_ACTIVITY) {
	    	SoundManager.playSound(SoundManager.SOUND_DEATH, 1);
	    	state = Wrapper.ONLY_ANIMATION;
	    	setAction(GLRenderer.ANIMATION_DESTROY, 1, 1, ACTION_DESTROYED, 0, 0);
	    }
	    
	    if (_eventType == COLLISION_WITH_OBSTACLE && currentHealth > 0) {
	    	
	    	SoundManager.playSound(SoundManager.SOUND_HIT_ARMOR, 1);
    		
	    	state = Wrapper.ONLY_ANIMATION;
    		
    		turningDirection = 0;
            
            setMovementSpeed(0.0f);

            x -= Math.cos((direction * Math.PI)/180) * 100 * Options.scaleX;
            y -= Math.sin((direction * Math.PI)/180) * 100 * Options.scaleY;
            
            CameraManager.updateCameraPosition();
            
            direction -= 180;
            
            if (direction < 0) {
            	direction *= -1;
            }
            
            // Toistetaan r�j�hdys��ni
        	EffectManager.showExplosionEffect(x, y);
        	
        	// Vaihtaa animaation
            setAction(GLRenderer.ANIMATION_RESPAWN, 1, 2, ACTION_RESPAWN, 0, 0);
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
     * 
     * Toimintojen vakiot l�ytyv�t GfxObject-luokan alusta.
     */
    @Override
    protected void triggerEndOfAction()
    {    	
        // Tuhotaan pelaaja ja siirryt��n pois pelitilasta
        if (actionId == ACTION_DESTROYED) {
            setUnactive();
            
            gameMode.endGameMode();
        }
        else if (actionId == ACTION_RESPAWN) {
        	setMovementSpeed(1.0f);
        	
        	state = Wrapper.FULL_ACTIVITY;
        }
    }
}

