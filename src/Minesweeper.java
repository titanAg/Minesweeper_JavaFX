// Kyle Orcutt - First Year Programming Project
// Fully functional minesweeper game
// Implemented with JavaFX 

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Minesweeper extends Application {
	BorderPane borderPane = new BorderPane();
	private boolean isGameOver = false;
	private boolean isGameWon = false;
	private boolean firstTurn = true;
	
	private boolean mousePressed = false;
	private int[][] guessed;
	private int[][] flagged;
	private int[][] visited;
	private int[][] mines;
	MSButton[][] buttons;
	Button face = new Button();
	private int flagCount = 0;
	private int initialMines;
	private int xTiles;
	private int yTiles;
	private Integer mineCount;
	Integer timeCount = 0;
	Integer seconds = 0;
	Integer tenSeconds = 0;
	Integer oneHundSeconds = 0;
	HBox top = new HBox(10);
	boolean isSelected = false;
	Stage stage = new Stage();
	Label time = new Label();
	Timeline animation;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		if (isSelected) {
			setMines();
			timer();
			mineCount = initialMines;
			borderPane.setStyle(
	                "-fx-background-color: #bfbfbf; -fx-border-color:  #fafafa #787878 #787878 #fafafa; -fx-border-width:6; -fx-border-radius: 0.001;");
			borderPane.setTop(getBP());
			borderPane.setCenter(getGP());
			stage.setTitle("Minesweeper");
			stage.setScene(new Scene(borderPane));
			stage.show();
		}else {
			showMenu();
		}
	}

	public BorderPane getBP() {
		BorderPane top = new BorderPane();
		Button face = new Button();
		face.setPadding(Insets.EMPTY);
		
		 top.setStyle(
	                "-fx-background-color: #bfbfbf; -fx-border-color: #787878 #fafafa #fafafa #787878; -fx-border-width:6; -fx-border-radius: 0.001;");
		
		if (mousePressed == true && isGameOver == false && isGameWon == false)
			face.setGraphic(new ImageView(new Image("res/face-O.png")));
		else if (isGameOver == false && isGameWon == false)
			face.setGraphic(new ImageView(new Image("res/face-smile.png")));
		else if (isGameWon == true && isGameOver == false)
			face.setGraphic(new ImageView(new Image("res/face-Win.png")));
		else
			face.setGraphic(new ImageView(new Image("res/face-dead.png")));
		face.setOnAction(e -> {
			resetBoard();
		});
		time = new Label(String.format("%03d", timeCount));
		time.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
		top.setLeft(getMines());
		top.setCenter(face);
		top.setRight(getTimer());	
		return top;
	}
	
	private HBox getMines() {
		HBox mines = new HBox();
		mines.setAlignment(Pos.CENTER_LEFT);
		Integer tenMines = 0;
		Integer hundMines = 0;
		Integer oneMines = mineCount % 10;
		if (mineCount % 100 > 9)
			tenMines = mineCount/10;
		else
			tenMines = 0;
		if (mineCount > 99)
			hundMines = mineCount/100;
		else
			hundMines = 0;
		mines.getChildren().add(new ImageView(new Image("res/digits/" + hundMines +".png")));
		mines.getChildren().add(new ImageView(new Image("res/digits/" + tenMines +".png")));
		mines.getChildren().add(new ImageView(new Image("res/digits/" + oneMines +".png")));
		
		return mines; 
	}
	
	private HBox getTimer() {
		HBox timer = new HBox();
		timer.setAlignment(Pos.CENTER_RIGHT);
		timer.getChildren().add(new ImageView(new Image("res/digits/" + oneHundSeconds +".png")));
		timer.getChildren().add(new ImageView(new Image("res/digits/" + tenSeconds +".png")));
		timer.getChildren().add(new ImageView(new Image("res/digits/" + seconds +".png")));
		
		return timer;
	}

	private GridPane getGP() {
		GridPane gridPane = new GridPane();
		gridPane.setStyle(
                "-fx-background-color: #B0C6DA; -fx-border-color: #787878 #fafafa #fafafa #787878; -fx-border-width:6; -fx-border-radius: 0.001;");
		buttons = new MSButton[xTiles][yTiles];
		gridPane.setAlignment(Pos.CENTER);
		for (int i = 0; i < xTiles; i++) {
			for (int j = 0; j < yTiles; j++) {
				buttons[i][j] = new MSButton();
				MSButton b = buttons[i][j];
				int iIndex = i;
				int jIndex = j;
				b.setOnMousePressed(e -> {
					mousePressed = true;
					borderPane.setTop(getBP());
				});
				b.setOnMouseReleased(e -> {
					mousePressed = false;
					borderPane.setTop(getBP());
				});
				b.setOnMouseClicked(e -> {
					if (isGameOver == false && isGameWon == false) {
						if (e.getButton() == MouseButton.PRIMARY) {
							if (flagged[iIndex][jIndex] == 0 && guessed[iIndex][jIndex] == 0) {
								guessed[iIndex][jIndex] = 1;
								if (firstTurn == true) {
									firstTurn = false;
									setMines();
								}
									setImage(b, iIndex, jIndex);						
							}else if (flagged[iIndex][jIndex] == 0 && guessed[iIndex][jIndex] == 1) {
								openFlagged(b,iIndex,jIndex);
								showOpen();
								resetArray(visited);
							}
						}else if (e.getButton() == MouseButton.SECONDARY){
							if (flagged[iIndex][jIndex] == 0 && guessed[iIndex][jIndex] == 0)
								flagged[iIndex][jIndex] = 1;
							else
								flagged[iIndex][jIndex] = 0;
							setImage(b, iIndex, jIndex);
						}
					}
				});
				gridPane.add(buttons[i][j], i, j);
			}
		}
		return gridPane;
	}
	
	private void setDifficulty(int level) {
		int difficulty = level;
		if (difficulty == 1) {
			initialMines = 10;
			xTiles = 8;
			yTiles = 8;
		}else if (difficulty == 2) {
			initialMines = 40;
			xTiles = 16;
			yTiles = 16;
		}else if (difficulty == 3) {
			initialMines = 99;
			xTiles = 32;
			yTiles = 16;
		}
		flagged = new int[xTiles][yTiles];
		guessed = new int[xTiles][yTiles];
		visited = new int[xTiles][yTiles];
	}
	
	private void showMenu() {
		VBox menu = new VBox(10);
		Stage menuStage = new Stage();
		menu.setAlignment(Pos.CENTER);
		menu.setPadding(new Insets(10));
		menu.setStyle("-fx-background-color: lightgrey");
		Font menuFont = new Font("Arial Black", 20);
		Label msg1 = new Label("Welcome to Minesweeper");
		msg1.setFont(menuFont);
		Label msg2 = new Label("Select a Difficulty");
		msg2.setFont(menuFont);
		Button b1 = new Button("Beginner");
		Button b2 = new Button("Intermediate");
		Button b3 = new Button("Expert");
		menu.getChildren().addAll(msg1,msg2);
		menu.getChildren().add(b1);
		menu.getChildren().add(b2);
		menu.getChildren().add(b3);
		b1.setOnAction( e -> {
			setDifficulty(1);
			isSelected = true;
			start(stage);
			menuStage.close();
		});
		b2.setOnAction( e -> {
			setDifficulty(2);
			isSelected = true;
			start(stage);
			menuStage.close();
		});
		b3.setOnAction( e -> {
			setDifficulty(3);
			isSelected = true;
			start(stage);
			menuStage.close();
		});
			menuStage.setScene(new Scene(menu));
			menuStage.show();
	}
	
	
	private void setMines() {
		mines = new int[xTiles][yTiles];
		mineCount = 0;
		if (firstTurn == true) {
			return;
		}else {
			while (mineCount < initialMines) {
				for (int i = 0; i < xTiles; i++) {
					for (int j = 0; j < yTiles; j++) {
						if (Math.random()*101 <= 5 && mineCount < initialMines && mines[i][j] != 1 && guessed[i][j] == 0 && guessNeighbors(i,j) == false) {
							mineCount++;
							mines[i][j] = 1;
						}
					}
				}
			}
			if (flagCount > 0) 
				mineCount-=flagCount;
		}
	}
	
	private void resetBoard() {
		isGameOver = false;
		isGameWon = false;
		isSelected = false;
		flagCount = 0;
		timeCount = 0;
		seconds = 0;
		tenSeconds = 0;
		oneHundSeconds = 0;
		firstTurn = true;
		setMines();
		mineCount = initialMines;
		resetArray(guessed);
		resetArray(visited);
		resetArray(flagged);
		borderPane.setTop(getBP());
		borderPane.setCenter(getGP());
	}

	private void showMines() {
		for (int i = 0; i < xTiles; i++) {
			for (int j = 0; j < yTiles; j++) {
				MSButton b = buttons[i][j];
				if (guessed[i][j] == 1) {
					if (mines[i][j] == 1)
						b.setGraphic(new ImageView(new Image("res/mine-red.png")));
				}else {
					if (isGameWon == false) {
						if (mines[i][j] == 1 && flagged[i][j] == 0)
							b.setGraphic(new ImageView(new Image("res/mine-Grey.png")));
						else if (flagged[i][j] == 1 && mines[i][j] == 0)
							b.setGraphic(new ImageView(new Image("res/mine-misflagged.png")));
					}else {
						b.setGraphic(new ImageView(new Image("res/flag.png")));
					}
				}
			}
		}
	}
	
	private boolean guessNeighbors(int i, int j) {
		if (i > 0 && j > 0 && guessed[i-1][j-1] == 1)
			return true;
		else if (i > 0 && guessed[i-1][j] == 1)
			return true;
		else if (i > 0 && j < yTiles-1 && guessed[i-1][j+1] == 1)
			return true;
		else if (j > 0 && guessed[i][j-1] == 1)
			return true;
		else if (j < yTiles-1 && guessed[i][j+1] == 1)
			return true;
		else if (i < xTiles-1 && j > 0 && guessed[i+1][j-1] == 1)
			return true;
		else if (i < xTiles-1 && guessed[i+1][j] == 1)
			return true;
		else if (i < xTiles-1 && j < yTiles-1 && guessed[i+1][j+1] == 1)
			return true;
		else 
			return false; 
	}

	private void setImage(MSButton b, int i, int j) {
		if (guessed[i][j] == 1 && flagged[i][j] == 0) {
			if (mines[i][j] == 0 && closestNeighs(i,j,mines) >= 0) {
				showNumb(i,j);
				if (closestNeighs(i,j,mines) == 0) {
					open(i,j);
					showOpen();
					resetArray(visited);
				}
			}else if (mines[i][j] == 1) {
				isGameOver = true;
				showMines();
				borderPane.setTop(getBP());
			}
		}else if (guessed[i][j] == 0) {
			if (flagged[i][j] == 1) {
				b.setGraphic(new ImageView(new Image("res/flag.png")));
				flagCount++;
				if (mineCount > 0)
					mineCount--;
				borderPane.setTop(getBP());
			}else {
				b.setGraphic(b.imageCover);
				flagCount--;
				if (flagCount < initialMines && mineCount < initialMines)
					mineCount++;
				borderPane.setTop(getBP());
			}
		}
		isGameWon();
	}
	
	private void open(int i, int j) {
			if (isValid(i,j) == true && visited[i][j] == 0 && flagged[i][j] == 0) {
				if (closestNeighs(i,j,mines) == 0) {
					guessed[i][j] = 1;
					visited[i][j] = 1;
							open(i-1,j-1);
							open(i-1,j);
							open(i-1,j+1);
							open(i,j-1);
							open(i,j+1);
							open(i+1,j-1);
							open(i+1,j);
							open(i+1,j+1);
						
				}else if (closestNeighs(i,j,mines) > 0) {
					guessed[i][j] = 1;
					visited[i][j] = 1;			
				}else
					return;
			}
			isGameWon();
	}
	
	private void openFlagged(MSButton b, int i, int j) {
		if (closestNeighs(i,j,flagged) == closestNeighs(i,j,mines) && closestNeighs(i,j,flagged) > 0) {
			if (i > 0 && j > 0  && flagged[i - 1][j - 1] != 1) {
				guessed[i - 1][j - 1] = 1;
				setImage(b,i-1,j-1);
			}if (j > 0  && flagged[i][j - 1] != 1) {
				guessed[i][j - 1] = 1;
				setImage(b,i,j-1);
			}if (i < xTiles-1 && j > 0  && flagged[i + 1][j - 1] != 1) {
				guessed[i + 1][j - 1] = 1;
				setImage(b,i+1,j-1);
			}if (i > 0 &&  flagged[i - 1][j] != 1) {
				guessed[i - 1][j] = 1;
				setImage(b,i-1,j);
			}if (i < xTiles-1 && flagged[i + 1][j] != 1) {
				guessed[i + 1][j] = 1;
				setImage(b,i+1,j);
			}if (i > 0 && j < yTiles-1 && flagged[i - 1][j + 1] != 1) {
				guessed[i - 1][j + 1] = 1;
				setImage(b,i-1,j+1);
			}if (j < yTiles-1 && flagged[i][j + 1] != 1) {
				guessed[i][j + 1] = 1;
				setImage(b,i,j+1);
			}if (i < xTiles-1 && j < yTiles-1 && flagged[i + 1][j + 1] != 1) {
				guessed[i + 1][j + 1] = 1;
				setImage(b,i+1,j+1);
			}
			showOpen();
		}
	}
	
	private void showOpen() {
		for (int i = 0; i < xTiles; i++) {
			for (int j = 0; j < yTiles; j++) {
				showNumb(i,j);
			}
		}
	}
	
	private boolean isValid(int i, int j) {
		return (i >= 0 && i < xTiles && j >= 0 && j < yTiles);
	}
	
	private void showNumb(int i, int j) {
		String mineString = Integer.toString(closestNeighs(i, j,mines));
		MSButton b = buttons[i][j];
		if (guessed[i][j] == 1 && mines[i][j] == 0)
				b.setGraphic(new ImageView(new Image("res/" + mineString +".png")));
		else if (guessed[i][j] == 1 && mines[i][j] == 1) 
			setImage(b,i,j);
	}

	private int closestNeighs(int i, int j, int[][] arr) {
		int neighCount = 0;
		if (i > 0 && j > 0 && arr[i - 1][j - 1] == 1)
			neighCount++;
		if (j > 0 && arr[i][j - 1] == 1)
			neighCount++;
		if (i < xTiles-1 && j > 0 && arr[i + 1][j - 1] == 1)
			neighCount++;
		if (i > 0 && arr[i - 1][j] == 1)
			neighCount++;
		if (i < xTiles-1 && arr[i + 1][j] == 1)
			neighCount++;
		if (i > 0 && j < yTiles-1 && arr[i - 1][j + 1] == 1)
			neighCount++;
		if (j < yTiles-1 && arr[i][j + 1] == 1)
			neighCount++;
		if (i < xTiles-1 && j < yTiles-1 && arr[i + 1][j + 1] == 1)
			neighCount++;
		return neighCount;
	}

	private void resetArray(int[][] arr) {
		for (int i = 0; i < xTiles; i++) {
			for (int j = 0; j < yTiles; j++) {
				arr[i][j] = 0;
			}
		}
	}

	private void isGameWon() {
		int guessCount = 0;
		for (int i = 0; i < xTiles; i++) {
			for (int j = 0; j < yTiles; j++) {
				if ((guessed[i][j] == 1 && mines[i][j] == 0) || ((guessed[i][j] == 0 || flagged[i][j] == 1) && mines[i][j] == 1))
					guessCount++;
			}
		}
		if (guessCount == xTiles*yTiles) {
			isGameWon = true;
			mineCount = 0;
			showMines();
			borderPane.setTop(getBP());
		}
			
	}
	
	private void timer(){
		Timeline animation;
			animation = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
				if (isGameOver == false && isGameWon == false) {
					timeCount++;
					seconds = timeCount % 10;
					if (timeCount % 10 == 0 && timeCount > 0)
						if (tenSeconds < 9)
							tenSeconds++;
						else
							tenSeconds = 0;
					if (timeCount % 100 == 0 && timeCount > 0)
						if (oneHundSeconds < 9)
							oneHundSeconds++;
						else
							oneHundSeconds = 0;
					borderPane.setTop(getBP());
				}
			}));
			animation.setCycleCount(Timeline.INDEFINITE);
			animation.play();
	}
	
}

class MSButton extends Button {
	ImageView imageCover;
	public MSButton() {
		double size = 30;
		
		setMinWidth(size);
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);
		imageCover = new ImageView(new Image("res/cover.png"));
		imageCover.setFitWidth(size);
		imageCover.setFitHeight(size);
		setGraphic(imageCover);
	}
}
