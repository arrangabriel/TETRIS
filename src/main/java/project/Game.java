package project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

/**
 * The game class is a wrapper for the majority of game logic.
 */
public class Game {
	// piece color constants
	private static final Color tColor = Color.BLUE;
	private static final Color iColor = Color.CORAL;
	private static final Color sqColor = Color.YELLOW;
	private static final Color l_1Color = Color.GREEN;
	private static final Color l_2Color = Color.CHOCOLATE;
	private static final Color z_1Color = Color.ORANGE;
	private static final Color z_2Color = Color.DARKBLUE;

	// piece template array
	public static final Color[][][] PIECE_TEMPLATES = {
			{
				// t shape
				{null, null, null},
				{tColor, tColor, tColor},
				{null, tColor, null}
			},
			{
				// long shape
				{null, null, null, null},
				{iColor, iColor, iColor, iColor},
				{null, null, null, null},
				{null, null, null, null}
			},
			{
				// square shape
				{sqColor, sqColor},
				{sqColor, sqColor}
			},
			{
				// right L shape
				{l_1Color, l_1Color, null},
				{null, l_1Color, null},
				{null, l_1Color, null}
			},
			{
				// left L shape
				{null, l_2Color, l_2Color},
				{null, l_2Color, null},
				{null, l_2Color, null}
			},
			{
				// right Z shape
				{null, null, null},
				{null, z_1Color, z_1Color},
				{z_1Color, z_1Color, null}
			},
			{
				// left Z shape
				{null, null, null},
				{z_2Color, z_2Color, null},
				{null, z_2Color, z_2Color}
			}
	};

	// constants
	private final GameController GAME_CONTROLLER;
	private final GameLoop GAME_LOOP;
	private final GraphicsContext GRAPHICS_CONTEXT;
	private final SoundHandler SOUND_HANDLER;
	private final Board BOARD;
	private final int GRID_WIDTH;
	private final int GRID_HEIGHT;

	// game settings
	public static final int TILE_SIZE = 15;
	public static final int BUFFER_MAX = 8;
	public static final int ROTATION_LIFT = 3;
	public static final long FRAME_LENGTH = 50_000_000;
	public static final String FILEPATH = "/save/highscores.txt";

	private Piece currentPiece;
	private boolean lost = false;
	private boolean paused = true;
	private int score = 0;
	private int buffer = 0;

	/**
	 * The game object functions as a container for game logic and interface between JavaFX and the backend.
	 * @param CANVAS_WIDTH: The width of drawing surface in pixels.
	 * @param CANVAS_HEIGHT: The height of drawing surface in pixels.
	 * @param SOUND_HANDLER: Sound handler instance.
	 * @param GAME_CONTROLLER: JavaFX controller instance.
	 */
	public Game(int CANVAS_WIDTH, int CANVAS_HEIGHT, SoundHandler SOUND_HANDLER, GameController GAME_CONTROLLER) {
		if(SOUND_HANDLER == null){
			throw new IllegalArgumentException("SOUND_HANDLER cannot be null.");
		}
		if(GAME_CONTROLLER == null){
			throw new IllegalArgumentException("GAME_CONTROLLER cannot be null.");
		}

		this.GAME_CONTROLLER = GAME_CONTROLLER;
		this.GRID_WIDTH = CANVAS_WIDTH / TILE_SIZE;
		this.GRID_HEIGHT = CANVAS_HEIGHT / TILE_SIZE;
		this.GRAPHICS_CONTEXT = GAME_CONTROLLER.getGc();
		this.SOUND_HANDLER = SOUND_HANDLER;
		BOARD = new Board(this.GRID_WIDTH, this.GRID_HEIGHT);
		currentPiece = new Piece(Game.PIECE_TEMPLATES, this.GRID_WIDTH, this.GRID_HEIGHT);
		GAME_LOOP = new GameLoop(this);
	}

	/**
	 * Constructor only to be used for testing! May produce null-pointer exceptions!
	 */
	public Game(int CANVAS_WIDTH, int CANVAS_HEIGHT){
		this.GRID_WIDTH = CANVAS_WIDTH / TILE_SIZE;
		this.GRID_HEIGHT = CANVAS_HEIGHT / TILE_SIZE;
		BOARD = new Board(this.GRID_WIDTH, this.GRID_HEIGHT);
		currentPiece = new Piece(Game.PIECE_TEMPLATES, this.GRID_WIDTH, this.GRID_HEIGHT);
		this.SOUND_HANDLER = null;
		this.GAME_CONTROLLER = null;
		this.GRAPHICS_CONTEXT = null;
		this.GAME_LOOP = null;
	}

	/**
	 * Starts, or unpauses, game loop and music.
	 */
	public void start(){
		this.paused = false;
		if(this.GAME_LOOP != null && this.SOUND_HANDLER != null){
			this.GAME_LOOP.start();
			this.SOUND_HANDLER.playMusic();
		}
	}

	/**
	 * Stops (READ: pauses) the game loop and music.
	 */
	public void stop(){
		this.paused = true;
		if(this.GAME_LOOP != null && this.SOUND_HANDLER != null){
			this.GAME_LOOP.stop();
			this.SOUND_HANDLER.pauseMusic();
		}
	}

	/**
	 * Starts game loop and resets music. Note: does not refresh the game instance.
	 */
	public void restart(){
		this.paused = false;
		if(GAME_LOOP != null && this.SOUND_HANDLER != null){
			this.GAME_LOOP.start();
			this.SOUND_HANDLER.stopMusic();
			this.SOUND_HANDLER.playMusic();
		}
	}

	/**
	 * Performs one step of game logic, returns nothing, but invokes other methods based on game state.
	 */
	public void tick(){
		// if a piece lands
		if(!this.currentPiece.fall(this.BOARD, false)){
			// play sound
			if(this.buffer == 0 && this.SOUND_HANDLER != null){
				this.SOUND_HANDLER.playSFX();
			}
			// if a piece has been on ground for frames = buffer
			if(this.buffer == BUFFER_MAX){
				if(this.currentPiece.outOfBounds(this.GRID_WIDTH, this.GRID_HEIGHT)){
					this.loss();
					return;
				}
				this.buffer = 0;
				this.BOARD.makeCurrentBoardArr(this.currentPiece);
				this.currentPiece = new Piece(Game.PIECE_TEMPLATES, this.GRID_WIDTH, this.GRID_HEIGHT);
			}
			else{
				this.buffer++;
			}
		}
		else{
			if(this.currentPiece.getLossState()){
				this.loss();
			}
			else{
				this.buffer = 0;
			}
		}
		if(this.GRAPHICS_CONTEXT != null){
			this.BOARD.drawCurrent(GRAPHICS_CONTEXT, currentPiece);
		}
	}

	/**
	 * Sets relevant states when a game is lost.
	 */
	public void loss(){
		this.lost = true;
		if(GAME_LOOP != null && SOUND_HANDLER != null){
			this.GAME_LOOP.stop();
			this.SOUND_HANDLER.stopMusic();
		}
		this.score = this.BOARD.getScore();
		if(GAME_CONTROLLER != null){
			this.GAME_CONTROLLER.loss();
		}
	}

	/**
	 * Handles user input from JavaFX.
	 * @param key: The key event to be handled.
	 */
	public void handleKeyPress(KeyEvent key){
		if(!isLost() && !isPaused()) {
			if(this.GRAPHICS_CONTEXT != null){
				this.GRAPHICS_CONTEXT.clearRect(0, 0, GRID_HEIGHT * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
			}
			switch (key.getCode()) {
				case UP:
				case DOWN:
				case LEFT:
				case RIGHT:
					this.currentPiece.move(key.getCode(), BOARD);
					break;
				case SPACE:
					this.currentPiece.rotate(this.BOARD);
					break;
				case F:
					this.currentPiece.fall(this.BOARD, false);
					break;
				case M:
					if(this.SOUND_HANDLER != null){
						this.SOUND_HANDLER.toggleMute();
					}
				default:
					break;
			}
			if(this.GRAPHICS_CONTEXT != null){
				this.BOARD.drawCurrent(this.GRAPHICS_CONTEXT, currentPiece);
			}
		}
	}

	public void setPiece(Piece piece){
		this.currentPiece = piece;
	}

	/*
	 * --------------
	 * Getter methods
	 * --------------
	 */
	public boolean isPaused(){
		return paused;
	}

	public boolean isLost(){
		return lost;
	}

	public int getScore(){
		return score;
	}
}