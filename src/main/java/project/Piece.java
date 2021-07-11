package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

/**
 * A piece instance stores an array of tiles, their orientation, movement vector and position on the board.
 * Piece has methods controlling movement and rotation of its tiles as well as collision detection with the board.
 */
public class Piece {
	private int xPos;
	private int yPos;
	// orientation[0] stores the absolute value of velocity in the y-direction, orientation[1] does the same for x.
	private int[] orientation = new int[2];
	private boolean positiveVel;
	private boolean lossState = false;
	List<List<Color>> tiles = new ArrayList<>();

	/**
	 * Standard constructor, used when creating a new piece, chooses a set of tiles,
	 * then calculates random placement -> velocity.
	 * @param pieceTemplates: the selection of tile configurations to choose from.
	 * @param gridWidth: the amount of columns making up the board.
	 * @param gridHeight: the amount of rows making up the board.
	 */
	public Piece(Color[][][] pieceTemplates, int gridWidth, int gridHeight) {
		Random random = new Random();

		// choose random piece from set
		int randomIndex = random.nextInt(pieceTemplates.length);
		Color[][] pieceTemplate = pieceTemplates[randomIndex];

		for (Color[] colors : pieceTemplate) {
			List<Color> column = new ArrayList<>(Arrays.asList(colors).subList(0, pieceTemplate.length));
			this.tiles.add(column);
		}

		// choose random placement and matching velocity.
		switch (random.nextInt(4)) {
			case 0 -> {
				// vertical and positive velocity
				this.orientation[0] = 0;
				this.orientation[1] = 1;
				this.positiveVel = true;
				this.xPos = gridWidth / 2;
				this.yPos = 0;
			}
			case 1 -> {
				// vertical and negative velocity
				this.orientation[0] = 0;
				this.orientation[1] = 1;
				this.positiveVel = false;
				this.xPos = gridWidth / 2;
				this.yPos = gridHeight - this.getTiles().size();
			}
			case 2 -> {
				// horizontal and positive velocity
				this.orientation[0] = 1;
				this.orientation[1] = 0;
				this.positiveVel = true;
				this.xPos = 0;
				this.yPos = gridHeight / 2;
			}
			case 3 -> {
				// horizontal and negative velocity
				this.orientation[0] = 1;
				this.orientation[1] = 0;
				this.positiveVel = false;
				this.xPos = gridWidth - this.getTiles().size();
				this.yPos = gridHeight / 2;
			}
			default -> System.out.println("hm");
		}
	}

	/*
	 * --------------------------------------------------------------------------------------------------------
	 * These constructors are used in movement and rotation to change certain properties of the current piece,
	 * whilst retaining others.
	 * --------------------------------------------------------------------------------------------------------
	 */

	/**
	 * This constructor is used to change position, whilst retaining tile configuration.
	 * @param prePiece: the piece to be 'transformed'.
	 * @param xPos: new x-position.
	 * @param yPos: new y-position.
	 */
	public Piece(Piece prePiece, int xPos, int yPos){
		this.xPos = xPos;
		this.yPos = yPos;
		this.orientation = prePiece.getOrientation();
		this.positiveVel = prePiece.getVel();
		this.tiles = prePiece.getTiles();
	}

	/**
	 * This constructor is used to change tile configuration, whilst retaining position.
	 * @param prePiece: the piece to be 'transformed'.
	 * @param tiles: new tileset.
	 */
	public Piece(Piece prePiece, List<List<Color>> tiles){
		this.xPos = prePiece.getXPos();
		this.yPos = prePiece.getYPos();
		this.orientation = prePiece.getOrientation();
		this.positiveVel = prePiece.getVel();
		this.tiles = tiles;
	}

	/**
	 * Changes position of piece object in relation to velocity and input-key.
	 * @param key: KeyCode (UP, DOWN, LEFT, RIGHT) used to control movement.
	 * @param board: Current board.
	 */
	public void move(KeyCode key, Board board) {
		switch(key) {
			case UP -> {
				if (!(new Piece(this, this.xPos, this.yPos - this.orientation[0]).collides(board, this))) {
					this.yPos -= this.orientation[0];
				}
			}
			case DOWN -> {
				if (!(new Piece(this, this.xPos, this.yPos + this.orientation[0]).collides(board, this))) {
					this.yPos += this.orientation[0];
				}
			}
			case LEFT -> {
				if (!(new Piece(this, this.xPos - this.orientation[1], this.yPos).collides(board, this))) {
					this.xPos -= this.orientation[1];
				}
			}
			case RIGHT -> {
				if (!(new Piece(this, this.xPos + this.orientation[1], this.yPos).collides(board, this))) {
					this.xPos += this.orientation[1];
				}
			}
		}
	}

	/**
	 * Method for checking if piece tiles collide with board tiles.
	 * Calls the loss() method on game if piece has fallen out of board (this may be somewhat unexpected).
	 * @param board: Current board, to be collided against.
	 * @return Returns true is piece and board collide, false if they don't.
	 */
	public boolean collides(Board board, Piece piece){
		for(int x = 0; x < this.getTiles().size(); x++){
			for(int y = 0; y < this.getTiles().size(); y++){
				try {
					if(board.getBoardArr()[this.getXPos() + x][this.getYPos() + y] != null && this.getTiles().get(x).get(y) != null){
						return true;
					}
				}
				// index out of bounds error = piece has fallen out of board = loss
				catch (Exception ignored){
					//game.loss();
					piece.lossState = true;
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Method for checking if piece is out bounds (small inner square on the board).
	 * @param gridWidth: The amount of columns making up the board.
	 * @param gridHeight: The amount of rows making up the board.
	 * @return Returns true if piece is out of bounds, false if it isn't.
	 */
	public boolean outOfBounds(int gridWidth, int gridHeight){
		List<List<Color>> tiles = this.getTiles();
		int xPos = this.getXPos();
		int yPos = this.getYPos();
		for(int x = 0; x < tiles.size(); x++){
			for(int y = 0; y < tiles.size(); y++){
				if(tiles.get(x).get(y) != null){
					if(xPos + x > gridWidth/2 + 11
							|| xPos + x < gridWidth/2 - 12
							|| yPos + y > gridHeight/2 + 11
							|| yPos + y < gridHeight/2 - 12){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Attempts to 'fall' the piece (moving in velocity direction).
	 * If it doesn't collide, move the piece and return true.
	 * If it collides, keep in place and return false.
	 * @param reverse: Set to true to 'fall' up.
	 * @param board: Current board.
	 * @return Returns true if successful, false if not.
	 */
	public boolean fall(Board board, boolean reverse){
		int velocity = positiveVel ? 1 : -1;
		if(reverse){
			velocity *= -1;
		}
		if(!(new Piece(this, this.xPos + orientation[0]*velocity, this.yPos + orientation[1]*velocity).collides(board, this))){
			this.yPos += orientation[1] * velocity;
			this.xPos += orientation[0] * velocity;
			return true;
		}
		return false;
	}

	/**
	 * Tries to rotate the current tileset 90 degrees.
	 * If the tiles collide after rotation, perform a reverse fall up to the rotationLift setting.
	 * @param board: Current board.
	 */
	public void rotate (Board board){
		Piece rotated = new Piece(this, matrixRotate(this.getTiles()));
		if(!rotated.collides(board, this)){
			this.tiles = rotated.getTiles();
		}
		else{
			for(int i = 0; i < Game.ROTATION_LIFT; i++){
				rotated.fall(board, true);
				if(!rotated.collides(board, this)){
					this.tiles = rotated.getTiles();
					this.xPos = rotated.getXPos();
					this.yPos = rotated.getYPos();
					break;
				}
			}
		}
	}

	/**
	 * Rotates a "square 2d" ArrayList by 90 degrees.
	 * @param matrix: Matrix to be rotated.
	 * @return (deep copied) rotated matrix.
	 */
	private static List<List<Color>> matrixRotate(List<List<Color>> matrix){
		int n = matrix.size();
		List<List<Color>> matrixCopy = new ArrayList<>();
		for(int i = 0; i < n; i++){
			matrixCopy.add(new ArrayList<>());
			for(int j = 0; j < n; j++){
				matrixCopy.get(i).add(matrix.get(n-1-j).get(i));
			}
		}
		return matrixCopy;
	}

	/*
	 * ---------------
	 * Getter methods.
	 * ---------------
	 */

	public int[] getOrientation(){
		return orientation;
	}

	public boolean getVel(){
		return positiveVel;
	}

	public List<List<Color>> getTiles() {
		return tiles;
	}

	public int getXPos() {
		return this.xPos;
	}

	public int getYPos() {
		return this.yPos;
	}

	public boolean getLossState(){
		return this.lossState;
	}
}