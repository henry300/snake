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
    TileOccupationCalculator toc = new TileOccupationCalculator();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("Snake");
        stage.setHeight(500);
        stage.setWidth(500);
        Scene scene = new Scene(getContent());
        stage.setScene(scene);
        stage.show();
        gameArea.requestFocus();
        stage.setOnCloseRequest(e -> System.exit(0));
        startGame();
    }

    public Parent getContent() {
        gameArea = new StackPane();
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
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        toc.update();
                    }
                }, 0, 100);
    }

    public void addTiles() {
        int tileEdgeLen = 10;
        int nrOfTilesHor = (int)stage.getHeight() / tileEdgeLen;
        int nrOfTilesVert = (int)stage.getWidth() / tileEdgeLen;
        tiles = new Tile[nrOfTilesVert][nrOfTilesHor];
        for (int i = 0; i < nrOfTilesVert; i++) {
            for (int j = 0; j < nrOfTilesHor; j++) {
                Tile tile = new Tile(tileEdgeLen);
                tile.setTranslateX(j * tileEdgeLen - stage.getWidth() / 2 + tileEdgeLen / 2);
                tile.setTranslateY(i * tileEdgeLen - stage.getHeight() / 2 + tileEdgeLen / 2);
                tiles[i][j] = tile;
                gameArea.getChildren().add(tile);
            }
        }
    }

    private class TileOccupationCalculator {
        Direction currentDirection = Direction.RIGHT;

        Queue<Tile> occupiedTiles = new LinkedList<Tile>();


        public void update() {
            System.out.println(currentDirection);
        }

        public void setDirection(Direction direction) {
            this.currentDirection = direction;
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
