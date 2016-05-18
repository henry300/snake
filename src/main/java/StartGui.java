import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class StartGui extends Application{
    Tile[][] tiles;
    Stage stage;
    StackPane gameArea;
    TileOccupationCalculator toc;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("Snake");
        stage.setResizable(false);
        Scene scene = new Scene(getContent());
        stage.setScene(scene);
        stage.show();
        gameArea.requestFocus();
        stage.setOnCloseRequest(e -> System.exit(0));
        startGame();
    }

    public Parent getContent() {
        gameArea = new StackPane();
        gameArea.setPrefSize(500,500);
        addTiles();



        gameArea.setOnKeyPressed(e->{
            if (e.getCode() == KeyCode.UP) {
                toc.setDirection(Direction.UP);
            } else if (e.getCode() == KeyCode.DOWN) {
                toc.setDirection(Direction.DOWN);
            } else if (e.getCode() == KeyCode.LEFT) {
                toc.setDirection(Direction.LEFT);
            } else if (e.getCode() == KeyCode.RIGHT) {
                toc.setDirection(Direction.RIGHT);
            }
        });
        return gameArea;
    }

    public void startGame() {
        toc = new TileOccupationCalculator();
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        toc.update();
                    }
                }, 0, 1000);
    }

    public void addTiles() {
        int tileEdgeLen = 10;
        int nrOfTilesHor = (int)gameArea.getPrefWidth() / tileEdgeLen;
        int nrOfTilesVert = (int)gameArea.getPrefHeight() / tileEdgeLen;
        tiles = new Tile[nrOfTilesHor][nrOfTilesVert];
        for (int i = 0; i < nrOfTilesHor; i++) {
            for (int j = 0; j < nrOfTilesVert; j++) {
                Tile tile = new Tile(tileEdgeLen);
                tile.setTranslateX(i * tileEdgeLen + tileEdgeLen / 2 - gameArea.getPrefWidth() / 2);
                tile.setTranslateY(j * tileEdgeLen + tileEdgeLen / 2 - gameArea.getPrefHeight() / 2);
                tiles[i][j] = tile;
                gameArea.getChildren().add(tile);
            }
        }
    }

    private class TileOccupationCalculator {
        Direction currentDirection = Direction.RIGHT;
        Queue<Tile> occupiedTiles = new LinkedList<Tile>();

        TileOccupationCalculator() {
            // Generate initial snake
            occupy(23,23);
            occupy(24,23);
            occupy(25,23);
        }

        public void occupy(int x, int y) {
            tiles[x][y].setOccupied(true);
            occupiedTiles.add(tiles[x][y]);
        }


        public void update() {

        }

        public void setDirection(Direction direction) {
            if (!(direction == Direction.LEFT && currentDirection == Direction.RIGHT ||
                direction == Direction.RIGHT && currentDirection == Direction.LEFT ||
                direction == Direction.DOWN && currentDirection == Direction.UP ||
                direction == Direction.UP && currentDirection == Direction.DOWN)) {
                this.currentDirection = direction;
            }
        }
    }

    private class Tile extends StackPane{
        boolean isOccupied = false;

        public Tile(int edgeLen) {
            this.setMaxSize(edgeLen, edgeLen);
        }

        public boolean isOccupied() {
            return isOccupied;
        }

        public void setOccupied(boolean value) {
            if (value == true) {
                isOccupied = true;
                this.setStyle("-fx-background-color: black");
            } else {
                isOccupied = false;
                this.setStyle(null);
            }
        }
    }
}
