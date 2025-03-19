import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

	Clip clip;
	Clip musicClip;
	URL soundURL[] = new URL[20];

	public Sound() {
		soundURL[0] = getClass().getResource("/sound/bubblePop.wav"); //done
		soundURL[1] = getClass().getResource("/sound/dragonFightThemeMusic.wav"); //done
		soundURL[2] = getClass().getResource("/sound/buttonClicked.wav"); //done
		soundURL[3] = getClass().getResource("/sound/enemyDead.wav"); //done
		soundURL[4] = getClass().getResource("/sound/enemyProjectile.wav");
		soundURL[5] = getClass().getResource("/sound/enemyShield.wav");
		soundURL[6] = getClass().getResource("/sound/fireball.wav"); //done
		soundURL[7] = getClass().getResource("/sound/footsteps.wav");
		soundURL[8] = getClass().getResource("/sound/hitGround.wav");
		soundURL[9] = getClass().getResource("/sound/waterSplash.wav"); //done
		soundURL[10] = getClass().getResource("/sound/wingFlap.wav"); //done
		soundURL[11] = getClass().getResource("/sound/enemyRespawn.wav");//done

	}

	public void play() {
		clip.start();
	}
	
	public void playMusic() {
		musicClip.start();
	}
	
	public void stopMusic() {
		musicClip.stop();
		musicClip.close();
	}
	
	public void stop() {
		clip.stop();
		clip.close();
	}
	
	public void setMusicClip () {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL[1]);
			musicClip = AudioSystem.getClip();
			musicClip.open(audioInputStream);
		} catch (Exception e) {
			
		}
	}

	public void setSound(int i) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL[i]);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
		} catch (Exception e) {
			
		}
	}

	public void loopMusic () {
		musicClip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
}
