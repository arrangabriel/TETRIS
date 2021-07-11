package project;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;

public class GameController {
	private final HighScoreHandler highScoreHandler = new HighScoreHandler(Game.FILEPATH);
	private Game game = null;
	private GraphicsContext gc;

	@FXML
	Canvas mainCanvas;
	@FXML
	Pane lossScreen;
	@FXML
	VBox highScores;
	@FXML
	Text score;
	@FXML
	ImageView startImage;

	public void initialize(){
		lossScreen.setOpacity(0);
		this.gc = this.mainCanvas.getGraphicsContext2D();
	}

	@FXML
	public void handleKeyPress(KeyEvent event){
		if(game != null){
			if (event.getCode() == KeyCode.SHIFT) {
				restart();
				this.highScoreHandler.write(0);
			}else{
				this.game.handleKeyPress(event);
			}
		}
		if(event.getCode() == KeyCode.ESCAPE){
			startImage.setVisible(false);
			play();
		}
	}

	@FXML
	// is in fact a play/pause toggle
	public void play(){
		if(this.game == null){
			game = new Game((int) this.mainCanvas.getWidth(), (int) this.mainCanvas.getWidth(), new SoundHandler(), this);
			game.start();
		}
		else{
			if(!game.isLost()){
				if(game.isPaused()) {
					game.start();
				}else{
					game.stop();
				}
			}
		}
		mainCanvas.requestFocus();
	}

	@FXML
	public void restart(){
		game.stop();
		game = new Game((int) this.mainCanvas.getWidth(), (int) this.mainCanvas.getWidth(), new SoundHandler(), this);
		lossScreen.setOpacity(0);
		game.restart();
		//mainCanvas.requestFocus();
	}

	public void loss(){
		score.setText("SCORE: " + this.game.getScore());
		highScores.getChildren().clear();
		if(this.game.getScore() > 0){
			highScoreHandler.write(this.game.getScore());
		}
		List<Integer> highScoresList = highScoreHandler.read();
		boolean currentScoreFound = false;
		for(int i = 0; i < 10; i++){
			Text point = new Text();
			if(i < highScoresList.size()){
				int score = highScoresList.get(i);
				point.setText((i + 1) + ".\t" + score);
				if(score == this.game.getScore() && !currentScoreFound){
					point.setFill(Color.RED);
					currentScoreFound = true;
				}
			}else{
				point.setText((i + 1) + ".\t");
			}
			point.setFont(Font.font("Agency FB", 13));
			highScores.getChildren().add(point);
		}
		lossScreen.setOpacity(1);
	}

	public GraphicsContext getGc(){
		return this.gc;
	}
}