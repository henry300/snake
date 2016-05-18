import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class StartGui extends Application{
    Tile[][] tiles;
    Stage stage;
    StackPane gameArea;

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
    }

    public Parent getContent() {
        gameArea = new StackPane();
        addTiles();
        return gameArea;




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


    private class Tile extends StackPane{
        public Tile(int edgeLen) {
            this.setMaxSize(edgeLen, edgeLen);
        }

        public void setOccupied(boolean isOccupied) {
            if (isOccupied == true) {
                this.setStyle("-fx-background-color: black");
            } else {
                this.setStyle(null);
            }
        }
    }
}
