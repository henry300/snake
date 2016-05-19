import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.*;

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
        gameArea.setPrefSize(200,200);



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

    public void startGame() throws InterruptedException {
        toc = new TileOccupationCalculator();
        Thread.sleep(300); // To remove bug where some tiles remained occupied
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            toc.update();
                        });
                    }
                }, 0, 2);
    }

    public void addTiles() {
        int tileEdgeLen = 10;
        int nrOfTilesHor = (int)gameArea.getPrefWidth() / tileEdgeLen;
        int nrOfTilesVert = (int)gameArea.getPrefHeight() / tileEdgeLen;
        tiles = new Tile[nrOfTilesHor][nrOfTilesVert];
        for (int i = 0; i < nrOfTilesHor; i++) {
            for (int j = 0; j < nrOfTilesVert; j++) {
                Tile tile = new Tile(tileEdgeLen, i, j);
                tile.setTranslateX(i * tileEdgeLen + tileEdgeLen / 2 - gameArea.getPrefWidth() / 2);
                tile.setTranslateY(j * tileEdgeLen + tileEdgeLen / 2 - gameArea.getPrefHeight() / 2);
                tiles[i][j] = tile;
                gameArea.getChildren().add(tile);
            }
        }
    }

    private class TileOccupationCalculator {
        Direction currentDirection = Direction.RIGHT;
        Direction plannedDirectionDuringNextUpdate = Direction.RIGHT;
        List<Tile> occupiedTiles = new ArrayList<>();
        Tile foodTile = null;
        boolean eating = false;
        int frames = 0;
        int speed = 40; // 1 - 130
        int score = 0;


        TileOccupationCalculator() {
            // Generate initial snake
            occupy(14,13);
            occupy(15,13);
        }

        public void update() {
            if (frames % (131 - speed) == 0) {
                foodCalculator();
                currentDirection = plannedDirectionDuringNextUpdate;
                move();
            }
            frames++;
        }

        public void occupy(int x, int y) {
            tiles[x][y].setOccupied(true);
            occupiedTiles.add(tiles[x][y]);
        }

        public void foodCalculator() {
            if (foodTile == null) {
                int maximumX = tiles.length - 1;
                int maximumY = tiles[0].length - 1;
                Random r = new Random();
                int randomX = r.nextInt(maximumX);
                int randomY = r.nextInt(maximumY);
                foodTile = tiles[randomX][randomY];
                while (occupiedTiles.contains(foodTile)) {
                    randomX = r.nextInt(maximumX);
                    randomY = r.nextInt(maximumY);
                    foodTile = tiles[randomX][randomY];
                }
                foodTile.setFood(true);
            }

        }

        public void move() {
            addToHead();
            if (eating) {
                eating = false;
            } else {
                removeFromTale();
            }
        }

        public void removeFromTale() {
            occupiedTiles.get(0).setOccupied(false);
            occupiedTiles.remove(0);
        }

        public void eat() {
            eating = true;
            foodTile.setFood(false);
            foodTile = null;
            score += 1;
            stage.setTitle("Score: " + score);
            
            if (speed < 100) {
                speed += 1;
            }

        }

        public void addToHead() {
            Tile headTile = occupiedTiles.get(occupiedTiles.size() - 1);
            int maximumX = tiles.length - 1;
            int maximumY = tiles[0].length - 1;
            int headX = headTile.getX();
            int headY = headTile.getY();
            int nextHeadX = headX;
            int nextHeadY = headY;

            if (currentDirection == Direction.UP) {
                if (headY == 0) {
                    nextHeadY = maximumY;
                } else {
                    nextHeadY -= 1;
                }
            } else if (currentDirection == Direction.DOWN) {
                if (headY == maximumY) {
                    nextHeadY = 0;
                } else {
                    nextHeadY += 1;
                }
            } else if (currentDirection == Direction.LEFT) {
                if (headX == 0) {
                    nextHeadX = maximumX;
                } else {
                    nextHeadX -= 1;
                }
            } else {
                if (headX == maximumX) {
                    nextHeadX = 0;
                } else {
                    nextHeadX += 1;
                }
            }

            Tile nextHead = tiles[nextHeadX][nextHeadY];

            if (nextHead == foodTile) {
                eat();
            }

            occupiedTiles.add(nextHead);
            nextHead.setOccupied(true);

        }

        public void setDirection(Direction direction) {
            if (!(direction == Direction.LEFT && currentDirection == Direction.RIGHT ||
                direction == Direction.RIGHT && currentDirection == Direction.LEFT ||
                direction == Direction.DOWN && currentDirection == Direction.UP ||
                direction == Direction.UP && currentDirection == Direction.DOWN)) {

                this.plannedDirectionDuringNextUpdate = direction;
            }
        }
    }

    private class Tile extends StackPane{
        boolean isOccupied = false;
        boolean isFood = false;
        int x;
        int y;

        public Tile(int edgeLen, int x, int y) {
            this.setMaxSize(edgeLen, edgeLen);
            this.x = x;
            this.y = y;
        }

        public void setFood(boolean value) {
            if (value == true) {
                isFood = true;
                setOccupied(true);
                this.setStyle("-fx-background-color: green");
            } else {
                isFood = false;
                setOccupied(false);
                this.setStyle("-fx-background-color: black");
            }
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
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
