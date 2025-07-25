import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.Controls;
import util.UnitTests;

/*
 * Created by Abraham Campbell on 15/01/2020.
 *   Copyright (c) 2020  Abraham Campbell

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
   
   (MIT LICENSE ) e.g do what you want with this :-) 
 */ 



public class MainWindow {
	 private static  JFrame frame = new JFrame("Game");   // Change to the name of your game 
	 private static   Model gameworld= new Model();
	 private static   Viewer canvas = new  Viewer( gameworld);
	 private KeyListener KeyController =new Controller();
	 private static   int TargetFPS = 100;
	 private static boolean startGame= false;
	 private static boolean endlessGame = false;
	 private static JLabel BackgroundImageForStartMenu ;
	 private static File BackroundToLoad = new File("res/images/lakehouse.png");
	 private static Clip themeMusic;
	 private static JPanel panel = new JPanel(new BorderLayout());
	  
	public MainWindow() {
	        frame.setSize(1024, 1024);  // you can customise this later and adapt it to change on size.  
	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //If exit // you can modify with your way of quitting , just is a template.
	        frame.setLayout(null);
	        frame.add(canvas);  
			frame.setTitle("Catch the Fish, not the calamari!");
	        canvas.setBounds(0, 0, 1024, 1024); 
			   canvas.setBackground(new Color(255,255,255)); //white background  replaced by Space background but if you remove the background method this will draw a white screen 
		      canvas.setVisible(false);   // this will become visible after you press the key. 
				panel.setBounds((frame.getWidth()- 400) / 2, (frame.getHeight() - 200) / 2, 400, 200);
				panel.setBackground(new Color(30, 30, 30, 220));
				frame.add(panel);
				panel.setVisible(false);
		          
			  JButton startMenuButton = new JButton("Start Game");  // start button 
			  frame.add(startMenuButton); 
			  JButton mouseOption = new JButton("Mouse controls (easier)");
			  JButton keyboardOption = new JButton("Keyboard controls (harder)");
			  mouseOption.addActionListener(new ActionListener() {
				  @Override
				  public void actionPerformed(ActionEvent e) {
					mouseOption.setVisible(false);
					keyboardOption.setVisible(false);
					canvas.gameworld.setControlType(Controls.MOUSE);
					initialiseGame();
			  }});
			  mouseOption.setBounds(400, 500, 200, 40);
	  
			  keyboardOption.addActionListener(new ActionListener() {
				  @Override
				  public void actionPerformed(ActionEvent e) {
					keyboardOption.setVisible(false);
					mouseOption.setVisible(false);
					canvas.gameworld.setControlType(Controls.KEYBOARD);
					initialiseGame();
			  }});  
			  keyboardOption.setBounds(400, 550, 200, 40);
		       
	        startMenuButton.addActionListener(new ActionListener()
	           { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					startMenuButton.setVisible(false);
					frame.add(mouseOption);
					frame.add(keyboardOption);
					mouseOption.requestFocusInWindow();
					keyboardOption.requestFocusInWindow();
					loadThemeMusic();

					frame.revalidate();
					frame.repaint();
				}});  
	        startMenuButton.setBounds(400, 500, 200, 40);

			try {
				 
				 BufferedImage myPicture = ImageIO.read(BackroundToLoad);
				 BackgroundImageForStartMenu = new JLabel(new ImageIcon(myPicture));
				 BackgroundImageForStartMenu.setBounds(0, 0, 1000, 1000);
				frame.add(BackgroundImageForStartMenu); 
			}  catch (IOException e) { 
				e.printStackTrace();
			}   
			 
	       frame.setVisible(true);   
	}


	private void initialiseGame() {
		BackgroundImageForStartMenu.setVisible(false); 
		canvas.setVisible(true); 
		canvas.addKeyListener(KeyController);    //adding the controller to the Canvas
		canvas.addMouseListener((MouseListener) KeyController);
		canvas.addMouseMotionListener((MouseMotionListener) KeyController);
		canvas.requestFocusInWindow();   // making sure that the Canvas is in focus so keyboard input will be taking in .
		startGame=true;
	}

	public static void main(String[] args) {
		MainWindow hello = new MainWindow();  //sets up environment 
		while(true)   //not nice but remember we do just want to keep looping till the end.  // this could be replaced by a thread but again we want to keep things simple 
		{ 
			panel.setBounds((frame.getWidth()- 400) / 2, (frame.getHeight() - 200) / 2, 400, 200);
			//swing has timer class to help us time this but I'm writing my own, you can of course use the timer, but I want to set FPS and display it 
			
			int TimeBetweenFrames =  1000 / TargetFPS;
			long FrameCheck = System.currentTimeMillis() + (long) TimeBetweenFrames; 
			
			//wait till next time step 
		 while (FrameCheck > System.currentTimeMillis()){} 

		 if (gameworld.getScore() == 15 && gameworld.getGameLevel() == 1 && startGame) {
			startGame = false;
			showLevelTransition();
			gameworld.setGameLevel(gameworld.getGameLevel() + 1);
		 }
		 else if (gameworld.getScore() == 25 && gameworld.getGameLevel() == 2 && startGame) {
			themeMusic.stop();
			startGame = false;
			playWinSound();
			gameworld.setGameLevel(3);
			endGame("<html><center>You have won! Congratulations!");
		 }
		 else if (gameworld.getLives() <= 0 && startGame) {
			themeMusic.stop();
			startGame = false;
			playLoseSound();
			endGame("<html><center>You have lost, oopsy!");
		 }
			if(startGame)
				 {
					gameloop(gameworld.getGameLevel());
				 }
			
			//UNIT test to see if framerate matches 
		 UnitTests.CheckFrameRate(System.currentTimeMillis(),FrameCheck, TargetFPS); 
			  
		}
		
		
	} 
	//Basic Model-View-Controller pattern 
	private static void gameloop(int level) { 
		// GAMELOOP  
		
		// controller input  will happen on its own thread 
		// So no need to call it explicitly 
		
		// model update   
		gameworld.gamelogic(frame.getSize());
		// view update 
		
		  canvas.updateview(); 
		  canvas.setSize(frame.getSize());
		// Both these calls could be setup as  a thread but we want to simplify the game logic for you.  
		//score update  
		 frame.setTitle("Score =  "+ gameworld.getScore() + " ... Level " + level + " ... Lives = " + gameworld.getLives()); 		 
	}

	private static void showLevelTransition() {		
		JLabel message = new JLabel("<html><center>Level 1 Complete! Get ready for Level 2" +
		"<br>There are now red octupi enemies that will damage you" +
		"<br>Steer clear of them!</center></html>", JLabel.CENTER);
		message.setForeground(Color.WHITE);
		
		JButton continueButton = new JButton("Continue to Level 2");
		continueButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonEffects(false);
		}});
		
		panel.add(message, BorderLayout.CENTER);
		panel.add(continueButton, BorderLayout.SOUTH);
		panel.setVisible(true);
		frame.setComponentZOrder(panel, 0);
		frame.setComponentZOrder(canvas, 1);

		frame.revalidate();
		frame.repaint();
	}

	private static void endGame(String text) {		
		JLabel message = new JLabel(text + (!endlessGame ? "</center></html>" : "<br>Score: " + gameworld.getScore() + "</center></html>"), JLabel.CENTER);
		message.setForeground(Color.WHITE);
		
		JButton exitButton = new JButton("Exit Game");
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}});

		JButton endlessModeButton = new JButton("Endless mode");
		endlessModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			endlessGame = true;
			buttonEffects(false);
			if (themeMusic.isRunning()) {
				themeMusic.stop();  // Stop the clip if it's still playing
			}
			themeMusic.setFramePosition(0); // Rewind to the beginning
			themeMusic.loop(Clip.LOOP_CONTINUOUSLY);  // Play the sound again
			}
		});

		JButton restartButton = new JButton("Restart game");
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			endlessGame = false;
			buttonEffects(true);
			if (themeMusic.isRunning()) {
				themeMusic.stop();  // Stop the clip if it's still playing
			}
			themeMusic.setFramePosition(0); // Rewind to the beginning
			themeMusic.loop(Clip.LOOP_CONTINUOUSLY);  // Play the sound again
		}});
		
		panel.add(message, BorderLayout.CENTER);
		panel.add(restartButton, BorderLayout.WEST);
		if(gameworld.getGameLevel() == 3) {
			panel.add(endlessModeButton, BorderLayout.SOUTH);	
		}
		panel.add(exitButton, BorderLayout.EAST);
		panel.setVisible(true);

		frame.setComponentZOrder(panel, 0);
		frame.setComponentZOrder(canvas, 1);
		
		frame.revalidate();
		frame.repaint();
	}

	private static void buttonEffects(boolean restart) {
		frame.setComponentZOrder(panel, 1);
		frame.setComponentZOrder(canvas, 0);
		panel.removeAll();
		panel.setVisible(false);
		frame.revalidate();
		frame.repaint();      // Refresh frame
		if (restart) {
			gameworld.restartGame();
		} else {
			gameworld.setup();
		}
		canvas.requestFocusInWindow();
		startGame = true;  // Resume game loop
	}

	private static void playLoseSound() {
		try {
        	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("res/audio/Jingle_Lose_00.wav").getAbsoluteFile());
        	Clip clip = AudioSystem.getClip();
        	clip.open(audioInputStream);
        	clip.start();
    } catch (Exception e) {
        	e.printStackTrace();
    }
	}

	private static void playWinSound() {
		try {
        	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("res/audio/Jingle_Win_00.wav").getAbsoluteFile());
        	Clip clip = AudioSystem.getClip();
        	clip.open(audioInputStream);
        	clip.start();
    } catch (Exception e) {
        	e.printStackTrace();
    }
	}

	private void loadThemeMusic() {
		try {
        	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("res/audio/Adventure.wav").getAbsoluteFile());
        	themeMusic = AudioSystem.getClip();
        	themeMusic.open(audioInputStream);
        	themeMusic.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
        	e.printStackTrace();
    }
	}
}

/*
 * 
 * 

Hand shake agreement 
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,=+++
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,:::::,=+++????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,:++++????+??
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,:,,:,:,,,,,,,,,,,,,,,,,,,,++++++?+++++????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,=++?+++++++++++??????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,~+++?+++?++?++++++++++?????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:::,,,,,,,,,,,,,,,,,,,,,,,,,,,~+++++++++++++++????+++++++???????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,,,,,,,,,,,,,,,,,,,:===+=++++++++++++++++++++?+++????????????????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,~=~~~======++++++++++++++++++++++++++????????????????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,::::,,,,,,=~.,,,,,,,+===~~~~~~====++++++++++++++++++++++++++++???????????????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,,~~.~??++~.,~~~~~======~=======++++++++++++++++++++++++++????????????????II
:::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,:=+++??=====~~~~~~====================+++++++++++++++++++++?????????????????III
:::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,++~~~=+=~~~~~~==~~~::::~~==+++++++==++++++++++++++++++++++++++?????????????????IIIII
::::::::::::::::::::::::::::::::::::::::::::::::,:,,,:++++==+??+=======~~~~=~::~~===++=+??++++++++++++++++++++++++?????????????????I?IIIIIII
::::::::::::::::::::::::::::::::::::::::::::::::,,:+????+==??+++++?++====~~~~~:~~~++??+=+++++++++?++++++++++??+???????????????I?IIIIIIII7I77
::::::::::::::::::::::::::::::::::::::::::::,,,,+???????++?+?+++???7?++======~~+=====??+???++++++??+?+++???????????????????IIIIIIIIIIIIIII77
:::::::::::::::::::::::::::::::::::::::,,,,,,=??????IIII7???+?+II$Z77??+++?+=+++++=~==?++?+?++?????????????III?II?IIIIIIIIIIIIIIIIIIIIIIIIII
::::::::::::::::::::::::::::::,,,,,,~=======++++???III7$???+++++Z77ZDZI?????I?777I+~~+=7+?II??????????????IIIIIIIIIIIIIIIIIIIIII??=:,,,,,,,,
::::::::,:,:,,,,,,,:::~==+=++++++++++++=+=+++++++???I7$7I?+~~~I$I??++??I78DDDO$7?++==~I+7I7IIIIIIIIIIIIIIIIII777I?=:,,,,,,,,,,,,,,,,,,,,,,,,
++=++=++++++++++++++?+????+??????????+===+++++????I7$$ZZ$I+=~$7I???++++++===~~==7??++==7II?~,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
+++++++++++++?+++?++????????????IIIII?I+??I???????I7$ZOOZ7+=~7II?+++?II?I?+++=+=~~~7?++:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
+?+++++????????????????I?I??I??IIIIIIII???II7II??I77$ZO8ZZ?~~7I?+==++?O7II??+??+=====.,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
?????????????III?II?????I?????IIIII???????II777IIII7$ZOO7?+~+7I?+=~~+???7NNN7II?+=+=++,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
????????????IIIIIIIIII?IIIIIIIIIIII????II?III7I7777$ZZOO7++=$77I???==+++????7ZDN87I??=~,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
IIII?II??IIIIIIIIIIIIIIIIIIIIIIIIIII???+??II7777II7$$OZZI?+$$$$77IIII?????????++=+.,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII?+++?IIIII7777$$$$$$7$$$$7IIII7I$IIIIII???I+=,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII???????IIIIII77I7777$7$$$II????I??I7Z87IIII?=,,,,,,,,,,,:,,::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
777777777777777777777I7I777777777~,,,,,,,+77IIIIIIIIIII7II7$$$Z$?I????III???II?,,,,,,,,,,::,::::::::,,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
777777777777$77777777777+::::::::::::::,,,,,,,=7IIIII78ZI?II78$7++D7?7O777II??:,,,:,,,::::::::::::::,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
$$$$$$$$$$$$$77=:,:::::::::::::::::::::::::::,,7II$,,8ZZI++$8ZZ?+=ZI==IIII,+7:,,,,:::::::::::::::::,:::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
$$$I~::::::::::::::::::::::::::::::::::::::::::II+,,,OOO7?$DOZII$I$I7=77?,,,,,,:::::::::::::::::::::,,,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
::::::::::::::::::::::::::::::::::::::::::::::::::::::+ZZ?,$ZZ$77ZZ$?,,,,,::::::::::::::::::::::::::,::::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::I$:::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,
                                                                                                                             GlassGiant.com
 * 
 * 
 */
