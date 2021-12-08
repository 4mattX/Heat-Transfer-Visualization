import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Scanner;

public class Display extends Canvas implements Runnable{

    private JFrame frame;
    private Thread thread;
    private boolean running = false;

    public Display() {
        this.frame = new JFrame();
        Dimension size = new Dimension(SIZE_COL * 8, SIZE_ROW * 8);
        this.setPreferredSize(size);


    }

    public static double[][] grid1;
    public static double[][] grid2;
    public static final int SIZE_COL = 70;
    public static final int SIZE_ROW = 35;
    public static final double percentError = 0.000000000001;

    public static double testValue;


    public static void main(String[] args) throws InterruptedException {
        grid1 = new double[SIZE_ROW][SIZE_COL];
        grid2 = new double[SIZE_ROW][SIZE_COL];

        initializeGrid(grid1);
        initializeGrid(grid2);

        Display display = new Display();
        display.frame.setTitle("Heat Transfer");
        display.frame.add(display);
        display.frame.pack();
        display.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display.frame.setLocationRelativeTo(null);

        display.frame.setVisible(true);
        display.start();




    }

    private void render() throws InterruptedException {
        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();


        g.setColor(Color.black);
        g.fillRect(0 ,0, SIZE_ROW * 100, SIZE_COL * 100);

        // Logic for rendering Heat Transfer

        for (int i = 0; i < SIZE_COL; i++) {
            for (int j = 0; j < SIZE_ROW; j++) {

                if (grid2[j][i] != 0) {
                    Color color = new Color( (int) grid1[j][i], 0, 100 - (int) grid1[j][i]);
                    g.setColor(color);
                    g.fillRect(i * 8, j * 8, 8, 8);

                }
            }
        }

        g.dispose();
        bs.show();
    }

    public static void printGrid(double[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                System.out.print( (int)  grid[i][j] +" ");
            }
            System.out.println();
        }
    }

    public static void heatTransfer() {
        // Do heat transfer
        for (int i = 1; i < SIZE_ROW - 1; i++) {
            for (int j = 1; j < SIZE_COL - 1; j++) {
                grid2[i][j] = (grid1[i - 1][j] + grid1[i][j - 1] + grid1[i][j + 1] + grid1[i + 1][j]) / 4;
            }
        }

        // Compute insulation values
        for (int row = 0; row < SIZE_ROW; row++) {
            grid2[row][SIZE_COL - 1] = grid2[row][SIZE_COL - 2];
        }

        testValue = grid1[SIZE_ROW / 2][SIZE_COL / 2];

        // Copy grid2 to grid1
        for (int i = 0; i < SIZE_ROW; i++) {
            for (int j = 0; j < SIZE_COL; j++) {
                grid1[i][j] = grid2[i][j];
            }
        }

    }

    public static void initializeGrid(double[][] grid) {
        // Initializing first grid
        for (int row = 0; row < SIZE_ROW; row++) {
            for (int col = 0; col < SIZE_COL; col++) {

                if (row == 0) {
                    grid[0][col] = 100;
                }

                if (row == SIZE_ROW - 1) {
                    grid[SIZE_ROW - 1][col] = 100;
                }

                if (col == SIZE_COL - 1) {
                    grid[row][0] = 50;
                }
            }
        }
    }

    public static boolean isWithinError() {
        if (Math.abs(testValue - grid1[SIZE_ROW / 2][SIZE_COL / 2]) < percentError && grid1[SIZE_ROW / 2][SIZE_COL / 2] != 0) {
            return true;
        }
        return false;
    }

    public synchronized void start() {
        running = true;
        this.thread = new Thread(this,"Display");
        this.thread.start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(10);
                while (!isWithinError()) {
                    heatTransfer();
                    printGrid(grid1);
                    render();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}