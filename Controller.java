package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller implements Initializable{
	
	@FXML
	private Pane pane;
	@FXML
	private Label songLabel;
	@FXML
	private Button playButton, pauseButton, resetButton, previousButton, nextButton, browseButton;
	@FXML
	private ComboBox<String> speedBox;
	@FXML
	private Slider volumeSlider;
	@FXML
	private ProgressBar songProgressBar;
	@FXML
	private ListView<String> musicList;
	
	private Media media;
	private MediaPlayer mediaPlayer;
	
	private File filepath;
	private DirectoryChooser dirChooser;
	private File directory;			// variabila pentru directory folder
	private File[] files;			// vector prentru foldere
	
	private String songsName = "";
	private ArrayList<File> songs;		// avem tipul generic "File" (PlayList)
	
	private int songNumber;			// pentru a salva numarul melodiei la care suntem
	private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};
									// viteza cu care se poate derula melodia
	
	private Timer timer;			// variabila utilizata impreuna cu bara de progres
	private TimerTask task;
	
	private boolean running;		// variabila pentru a tine cont daca ruleaza o melodie sau nu
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {			// metoda abstracta preluata de la interfata "Initializable"
		
		songs = new ArrayList<File>();
		
		if (filepath != null) {				// daca a fost ales un folder de catre utilizator se utilizeaza acesta
			directory = filepath;
			System.out.print("Path: " + filepath.getAbsoluteFile());
		}
		else
			directory = new File("music");		// In caz contrar setam folderul care contine muzica din directorul curent
		
		files = directory.listFiles();		// salveaza melodiile
		
		if(files != null) {				
			
			for(File file : files) {
				
				songs.add(file);			// adaug fiecare melodie in variabila cu cantece
			}
		}
		
		media = new Media(songs.get(songNumber).toURI().toString());	// avem ca index numarul melodiei
		mediaPlayer = new MediaPlayer(media);
		
		songLabel.setText(songs.get(songNumber).getName());			// afisaza numele melodiei curente utilizand numarul melodiei pentru a afla numele
		
		for(File file : files) {
			
			songsName = songsName + file.getName() + "\n";			// adaug numele fiecarei melodii intr-o variabila
		}
		musicList.getItems().add(songsName);			// afisez numele melodiilor
		
		for(int i = 0; i < speeds.length; i++) {
													// adauga valorile pentru schimbarea vitezei
			speedBox.getItems().add(Integer.toString(speeds[i])+"%");
		}
		
		speedBox.setOnAction(this::changeSpeed);   // utilizam metoda "setOnAction" facand o referinta la metoda "changeSpeed"
		
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {  // adaugam un "Listener", si implementam metoda neinitializata din "ChangeListener"

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				
				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01); // aici schimbam volumul corespunzator cu bara de volum
			}
		});
		
		songProgressBar.setStyle("-fx-accent: #00FF00;");		// culoare initiala este albastra, o setam cu verde folosin css
	}

	
	public void fileSelect()
	{
		if(running == true)
			pauseMedia();
		songsName = "";
		musicList.getItems().clear();
		songs = null;
		songs = new ArrayList<File>();
		if (dirChooser != null)				// resetez variabila care contine directorul curent
			dirChooser = null;
		dirChooser = new DirectoryChooser();
		Stage stage = (Stage) pane.getScene().getWindow();		// creez un nou "stage" pentru afisarea optiunii de browse
		filepath = dirChooser.showDialog(stage);
		
		if (filepath != null) {
			directory = filepath;
			System.out.print("Path: " + filepath.getAbsoluteFile());			// utilizez noul director
		}
		else
			directory = new File("music");		// setam folderul care contine muzica
		
		files = directory.listFiles();		// salveaza melodiile
		
		if(files != null) {				
			
			for(File file : files) {
				
				songs.add(file);			// adaug fiecare melodie in variabila cu cantece
			}
		}
		songNumber = 0;
		media = new Media(songs.get(songNumber).toURI().toString());	// avem ca index numarul melodiei
		mediaPlayer = new MediaPlayer(media);
		
		songLabel.setText(songs.get(songNumber).getName());
		for(File file : files) {
			
			songsName = songsName + file.getName() + "\n";		// adaug numele melodiilor din noul director
		}
		musicList.getItems().add(songsName);    // afisez numele melodiilor din noul director
		playMedia();
	}
	
	public void playMedia() {			// metoda pentru a porni melodia
		
		beginTimer();					// pornim timer-ul
		changeSpeed(null);				// folosim medota "changeSpeed" pentru a retine viteza pentru toate melodiile, valoarea "null" este data deoarece valoarea metoda respenctiva necesita un argument
		mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);  // setam valoarea pentru a pastra volumul setat	 
		mediaPlayer.play();
	}
	
	public void pauseMedia() {			// metoda pentru a pune pe pauza melodia
		
		cancelTimer();					// oprim timer-ul 
		mediaPlayer.pause();			// oprim melodia
	}
	
	public void resetMedia() {			// metoda pentru a reincepe melodia
		
		songProgressBar.setProgress(0);		// setam bara de progres la 0
		mediaPlayer.seek(Duration.seconds(0));	// reluam melodia de la inceput
	}
	
	public void previousMedia() {		// metoda pentru a  trece la melodia anterioara
		
		if(songNumber > 0) {		// verific daca melodia este este prima
			
			songNumber--;			// daca nu este prima melodie decrementez indexul 
			
			mediaPlayer.stop();		
			
			if(running) {
				
				cancelTimer();		// opresc timer-ul
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			playMedia();
		}
		else {						// daca este prima melodie 
			
			songNumber = songs.size() - 1;
			
			mediaPlayer.stop();
			
			if(running) {			// verificam sa vedem daca running are valoarea adevarat
				
				cancelTimer();			//opresc timer-ul
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			playMedia();
		}
	}
	
	public void nextMedia() {			// metoda pentru a trece la urmatoare melodie
		
		if(songNumber < songs.size() - 1) {			// verific daca ma aflu la ultima melodie din playList
			
			songNumber++;				// daca nu ma aflu la ultima melodie incrementez indexul melodiei curente cu 1
			
			mediaPlayer.stop();			// oprim muzica cu metoda respectiva, pentru a o porni mai tarziu
										
			if(running) {				// verificam sa vedem daca running are valoarea adevarat
				
				cancelTimer();			// opresc timerul
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			playMedia();				// utilizez metoda respectiva pentru a rula melodia automat
		}
		else {
			
			songNumber = 0;			// daca ma aflu la sfarsitul playlist-ului setez indexul cu 0 pentru a revenii la inceputul acestuia
			
			mediaPlayer.stop();
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			playMedia();			// utilizez metoda respectiva pentru a rula melodia automat
		}
	}
	
	public void changeSpeed(ActionEvent event) {		// metoda pentru a schimba viteza melodiei
		
		if(speedBox.getValue() == null) {			// daca viteza nu a fost setata inainte de apasarea butonului play
			
			mediaPlayer.setRate(1);					// setam viteza la 1
		}
		else {
			
			//mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);  // produce o eroare si nu functioneaza din cauza semnului "%"
			mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
			// utilizam un "substring" in care specificam finalul valorii dorite sa fie pana in caracterul "%"
		}
	}
	
	public void beginTimer() {			// motoda care porneste bara de progres (mai exact timer-ul)
		
		timer = new Timer();
		
		task = new TimerTask() {
			
			public void run() {
				
				running = true;			// setam variabila cu true, deoarece aceasta ruleaza
				double current = mediaPlayer.getCurrentTime().toSeconds();  // salvam progresul melodiei
				double end = media.getDuration().toSeconds();		// salvam lungimea(in secunde) melodiei
				songProgressBar.setProgress(current/end);  // setam bara cu timpul curent
				
				if(current/end == 1) {
					
					cancelTimer();			// resetam timer-ul deoarece melodia a ajus la final
				}
			}
		};
		
		timer.scheduleAtFixedRate(task, 0, 1000);		// resetam valoarea
	}
	
	public void cancelTimer() {
		
		running = false;			// setam valoare fals deoarece nu ruleaza nici o melodie
		timer.cancel();
	}
}