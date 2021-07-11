package project;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A board instance stores the current board tiles, and handles drawing to the graphics context.
 */
public class Board {
	private final int gridWidth;
	private final int gridHeight;
	private final int tileSize;
	private Color[][] boardArr;

	/**
	 * The board class stores the current static board tiles, the size of the board and the size of the tiles.
	 * It also handles drawing to a passed graphics-context.
	 * @param gridWidth Columns making up the board.
	 * @param gridHeight Rows making up the board.
	 */
	public Board(int gridWidth, int gridHeight) {
		if(gridWidth < 0 || gridHeight < 0){
			throw new IllegalArgumentException("Dimensions cannot be negative.");
		}
		this.tileSize = Game.TILE_SIZE;
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;

		boardArr = new Color[gridWidth][gridHeight];

		Color centerColor = Color.BLACK;
		boardArr[gridWidth / 2][gridHeight / 2] = centerColor;
	}

	/**
	 * Creates a deep copy of a square array.
	 * @param template The array to be copied.
	 * @return Copy of input array.
	 */
	private static Color[][] arrayDeepCopy(Color[][] template){
		Color[][] copy = new Color[template.length][template.length];
		for (int x = 0; x < template.length; x++) {
			System.arraycopy(template[x], 0, copy[x], 0, template.length);
		}
		return copy;
	}

	/**
	 * Merges the tile sets from the passed piece instance and the current board array.
	 * @param currentPiece The piece to be merged with board.
	 * @return The merged array.
	 */
	public Color[][] placeOnBoard(Piece currentPiece){
		if(currentPiece == null){
			throw new IllegalArgumentException("Current piece cannot be null");
		}
		Color[][] boardArrCurrent = arrayDeepCopy(this.boardArr);
		int pieceX = currentPiece.getXPos();
		int pieceY = currentPiece.getYPos();
		List<List<Color>> tiles = currentPiece.getTiles();
		for(int x = 0; x < tiles.size(); x++) {
			for (int y = 0; y < tiles.size(); y++) {
				if((pieceX + x >= 0 && pieceX + x < boardArrCurrent.length) && (pieceY + y >= 0 && pieceY + y < boardArrCurrent.length)) {
					if(tiles.get(x).get(y) != null){
						boardArrCurrent[pieceX + x][pieceY + y] = tiles.get(x).get(y);
					}
				}
			}
		}
		return boardArrCurrent;
	}

	/**
	 * Places the current piece on the static board.
	 * @param currentPiece The piece to be placed onto board.
	 */
	public void makeCurrentBoardArr(Piece currentPiece) {
		this.boardArr = arrayDeepCopy(placeOnBoard(currentPiece));
	}

	/**
	 * Draw the current game state. Both board and piece tiles.
	 * @param gc Graphics context.
	 * @param currentPiece The current piece to be drawn.
	 */
	public void drawCurrent(GraphicsContext gc, Piece currentPiece) {
		if(gc == null){
			throw new IllegalArgumentException("gc cannot be null.");
		}
		if(currentPiece ==  null){
			throw new IllegalArgumentException("currentPiece cannot be null.");
		}
		gc.clearRect(0, 0, gridHeight * tileSize, gridHeight * tileSize);
		drawRect(gc, 1);
		drawRect(gc, 0.8);
		Color[][] boardArrCurrent = getBoardArrCurrent(currentPiece);
		for(int x = 0; x < this.gridWidth; x++) {
			for(int y = 0; y < this.gridHeight; y++) {
				if(boardArrCurrent[x][y] != null) {
					Color color = boardArrCurrent[x][y];
					// here is where accommodations according to the frontend framework used have to happen.
					gc.setFill(color);
					gc.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
				}
			}
		}
	}

	/**
	 * Quite the wacky method, draws some squares using the graphics context, dont ask.
	 * It's bad but it does the job.
	 * @param gc Graphics context.
	 * @param percentage I'm not sure, but it ain't like no percentage I've ever seen.
	 */
	public void drawRect(GraphicsContext gc, double percentage){
		if(gc == null){
			throw new IllegalArgumentException("gc cannot be null.");
		}
		if(percentage < 0){
			throw new IllegalArgumentException("percentage cannot be negative.");
		}
		gc.save();
		gc.setStroke(Color.RED);
		double size = gridHeight * tileSize;
		double transformed = Math.floor(gridHeight * percentage) * tileSize;
		gc.strokePolyline(new double[]{size - transformed, size - transformed, transformed, transformed, size - transformed}, new double[]{size - transformed, transformed, transformed, size - transformed, size - transformed}, 5);
		gc.restore();
	}

	/*
	 * ---------------
	 * Getter methods.
	 * ---------------
	 */

	/**
	 * The calculate score method returns the amount of static tiles on the board.
	 * @return Score as integer.
	 */
	public int getScore(){
		int counter = -1;
		for (Color[] colors : this.boardArr) {
			for (int y = 0; y < this.boardArr.length; y++) {
				if (colors[y] != null) {
					counter++;
				}
			}
		}
		return counter;
	}

	/**
	 * @param currentPiece The current piece.
	 * @return The merged tileset of the current board and piece.
	 */
	public Color[][] getBoardArrCurrent(Piece currentPiece){
		return placeOnBoard(currentPiece);
	}

	public Color[][] getBoardArr(){
		return boardArr;
	}
}