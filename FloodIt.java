import java.util.Random;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FloodIt extends Application {
    Paint[] colors;
    Rectangle[][] grid;
    int gridSize;
    int cellSize;
    Circle[] circles;
    int radius;
    int maxMoves;
    int moves;
    Text txtMoves;
    Text txtResult;
    Paint lastColor;

    public FloodIt() {
        this.colors = new Paint[]{Color.PURPLE, Color.DODGERBLUE, Color.GREEN, Color.YELLOW, Color.RED, Color.PINK};
        this.gridSize = 16;
        this.cellSize = 30;
        this.radius = 36;
        this.maxMoves = 35;
        this.moves = 0;
    }

    public Rectangle[][] createGrid(int x, int y, int gridSize, int cellSize, Paint[] colors, Group root) {
        Rectangle[][] grid = new Rectangle[gridSize][gridSize];
        Random random = new Random();

        for(int row = 0; row < gridSize; ++row) {
            for(int col = 0; col < gridSize; ++col) {
                Rectangle rect = new Rectangle((double)(x + col * cellSize), (double)(y + row * cellSize), (double)cellSize, (double)cellSize);
                int colorIndex = random.nextInt(colors.length);
                rect.setFill(colors[colorIndex]);
                root.getChildren().add(rect);
                grid[row][col] = rect;
            }
        }

        return grid;
    }

    public void removeGrid(Group root) {
        for(int row = 0; row < this.grid.length; ++row) {
            for(int col = 0; col < this.grid[0].length; ++col) {
                root.getChildren().remove(this.grid[row][col]);
            }
        }

    }

    public void fillGrid(int row, int col, Paint oldColor, Paint newColor) {
        if (row < this.gridSize && col < this.gridSize) {
            if (this.grid[row][col].getFill() == oldColor) {
                this.grid[row][col].setFill(newColor);
                this.fillGrid(row, col + 1, oldColor, newColor);
                this.fillGrid(row + 1, col, oldColor, newColor);
            }
        }
    }

    public Circle[] createCircles(int x, int y, int radius, Paint[] colors, Group root) {
        Circle[] circles = new Circle[colors.length];

        for(int i = 0; i < colors.length; ++i) {
            final Paint color = colors[i];
            Circle circle = new Circle((double)x, (double)y, (double)radius);
            circle.setFill(color);
            circle.setStroke(Color.BLACK);
            root.getChildren().add(circle);
            x += radius * 2 + 10;
            circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent mouseEvent) {
                    FloodIt.this.makeMove(color);
                }
            });
            circles[i] = circle;
        }

        return circles;
    }

    public void setCirclesVisible(boolean visible) {
        for(int i = 0; i < this.circles.length; ++i) {
            this.circles[i].setVisible(visible);
        }

    }

    public boolean hasWon() {
        Paint color = this.grid[0][0].getFill();

        for(int row = 0; row < this.grid.length; ++row) {
            for(int col = 0; col < this.grid[0].length; ++col) {
                if (color != this.grid[row][col].getFill()) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean hasLost() {
        return this.moves >= this.maxMoves;
    }

    public void makeMove(Paint color) {
        if (this.lastColor != color) {
            ++this.moves;
            this.txtMoves.setText(String.format("%d/%d", this.moves, this.maxMoves));
            Paint oldColor = this.grid[0][0].getFill();
            this.fillGrid(0, 0, oldColor, color);
            if (this.hasWon()) {
                this.setCirclesVisible(false);
                this.txtResult.setText("! YOU WON !");
                this.txtResult.setVisible(true);
            } else if (this.hasLost()) {
                this.setCirclesVisible(false);
                this.txtResult.setText("! YOU LOST !");
                this.txtResult.setVisible(true);
            }

            this.lastColor = color;
        }
    }

    public void reset(Group root) {
        this.removeGrid(root);
        this.grid = this.createGrid(20, 100, this.gridSize, this.cellSize, this.colors, root);
        this.moves = 0;
        this.txtMoves.setText(String.format("%d/%d", this.moves, this.maxMoves));
        this.txtResult.setVisible(false);
        this.setCirclesVisible(true);
        this.lastColor = null;
    }

    public void start(Stage primaryStage) {
        final Group root = new Group();
        Scene scene = new Scene(root, 520.0D, 680.0D);
        Text title = new Text(215.0D, 40.0D, "FLOOD IT");
        title.setFont(Font.font("Calibri", FontWeight.BOLD, 24.0D));
        title.setFill(Color.FIREBRICK);
        root.getChildren().add(title);
        Text lblMoves = new Text(20.0D, 80.0D, "Moves:");
        lblMoves.setFont(new Font("Calibri", 16.0D));
        root.getChildren().add(lblMoves);
        this.txtMoves = new Text(80.0D, 80.0D, String.format("%d/%d", this.moves, this.maxMoves));
        this.txtMoves.setFont(Font.font("Calibri", FontWeight.BOLD, 16.0D));
        root.getChildren().add(this.txtMoves);
        Button reset = new Button("New Game");
        reset.setFont(new Font("Calibri", 16.0D));
        reset.setLayoutX(405.0D);
        reset.setLayoutY(60.0D);
        root.getChildren().add(reset);
        reset.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                FloodIt.this.reset(root);
            }
        });
        this.grid = this.createGrid(20, 100, this.gridSize, this.cellSize, this.colors, root);
        Rectangle rect = new Rectangle(20.0D, 100.0D, (double)(this.gridSize * this.cellSize), (double)(this.gridSize * this.cellSize));
        rect.setFill((Paint)null);
        rect.setStroke(Color.BLACK);
        root.getChildren().add(rect);
        this.circles = this.createCircles(54, 630, this.radius, this.colors, root);
        this.txtResult = new Text(200.0D, 638.0D, "");
        this.txtResult.setFont(Font.font("Calibri", FontWeight.BOLD, 24.0D));
        this.txtResult.setFill(Color.FIREBRICK);
        root.getChildren().add(this.txtResult);
        this.txtResult.setVisible(false);
        primaryStage.setTitle("Flood It");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}