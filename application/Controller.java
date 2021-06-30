package application;




import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;



public class Controller implements Initializable{	
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button play,previous,reset,next;
	@FXML
	private Slider volume;
	@FXML
	private ComboBox<String> comboBox;
	@FXML
	private AnchorPane anchorPane;
	@FXML 
	private Label label;
	private File directory;
	private File[] files;
	
	private MediaPlayer player;
	private Media media;
	private ArrayList<File> songs;
	private int songNumber;
	private Timer timer;
	private TimerTask task;
	private boolean playing;
	private boolean running;
	private int speeds[] = {25,50,75,100,125,150,175,200};
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		songs = new ArrayList<File>();
		directory = new File("Songs");
		files = directory.listFiles();
		if(files!=null) {
			for(File f : files) {
				songs.add(f);
			}
		}
		media = new Media(songs.get(songNumber).toURI().toString());
		player = new MediaPlayer(media);
		label.setText(songs.get(songNumber).getName());
		progressBar.setStyle("-fx-accent:red;");
		for(int i=0;i < speeds.length;i++) {
			comboBox.getItems().add(Integer.toString(speeds[i])+"%");
		}
		volume.valueProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				// TODO Auto-generated method stub
				player.setVolume(volume.getValue() * 0.01);
			}
			
		});
//		comboBox.setOnAction(this::changeSpeed());
        
	}

	public void playOrPause() {
			changeSpeed(null);
			if(!playing) {
				beginTimer();
				player.setVolume(volume.getValue() * 0.01);
				player.play();
				playing = true;
			}
			else {
				endTimer();
				player.pause();
				playing = false;
			}
	}
	public void previous() {
		if(songNumber>0) {
			songNumber -= 1;
			player.stop();
			if(running)
				endTimer();
			playing = false;
			
			media = new Media(songs.get(songNumber).toURI().toString());
			player = new MediaPlayer(media);
			label.setText(songs.get(songNumber).getName());
			playOrPause();
		}
		else {
			player.stop();
			if(running)
				endTimer();
			songNumber = songs.size()-1;
			playing = false;
			media = new Media(songs.get(songNumber).toURI().toString());
			player = new MediaPlayer(media);
			label.setText(songs.get(songNumber).getName());
			playOrPause();
		}
	}
	public void next() {
			if(songNumber<songs.size()-1) {
				songNumber += 1;
				player.stop();
				if(running)
					endTimer();
				playing = false;
				media = new Media(songs.get(songNumber).toURI().toString());
				player = new MediaPlayer(media);
				label.setText(songs.get(songNumber).getName());
				playOrPause();
			}
			else {
				songNumber = 0;
				player.stop();
				if(running)
					endTimer();
				playing = false;
				media = new Media(songs.get(songNumber).toURI().toString());
				player = new MediaPlayer(media);
				label.setText(songs.get(songNumber).getName());
				playOrPause();
			}
	}
	public void reset() {
		progressBar.setProgress(0);
		player.seek(Duration.seconds(0));
	}
	public void changeSpeed(ActionEvent e) {
		if(comboBox.getValue()==null) {
			player.setRate(1);
		}
		else {
		player.setRate(Integer.parseInt(comboBox.getValue().substring(0,comboBox.getValue().length()-1))*0.01);
		}
	}
	public void beginTimer() {
		timer = new Timer();
		task = new TimerTask() {
			public void run() {
				running = true;
				double current = player.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				progressBar.setProgress(current/end);
				if(current == end) {
					endTimer();
				}
			}
		};
		timer.scheduleAtFixedRate(task, 0, 1000);
	}
	public void endTimer() {
			running = false;
			timer.cancel();
	}
}
