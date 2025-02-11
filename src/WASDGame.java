
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class WASDGame extends JFrame {

    private int playerX = 200;  // Initial X position of the player
    private int playerY = 200;  // Initial Y position of the player
    private BufferedImage playerImage;  // Image for the player
    private boolean showHitbox = false; // Tracks if the hitbox should be highlighted

    public WASDGame() {
        setTitle("WASD Movement Game with Image");
        setSize(600, 600);  // Set the size of the game window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // Load the player image from the directory
        try {
            // Make sure to place your image file in the correct directory
            playerImage = ImageIO.read(new File("player.png"));  // Replace "player.png" with your image file name
        } catch (IOException e) {
            System.out.println("Error loading player image: " + e.getMessage());
            System.exit(1);  // Exit if the image cannot be loaded
        }

        // Add a custom JPanel for drawing
        add(new GamePanel());

        // Add key listener for WASD movement
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:  // Move up
                        playerY -= 10;
                        break;
                    case KeyEvent.VK_A:  // Move left
                        playerX -= 10;
                        break;
                    case KeyEvent.VK_S:  // Move down
                        playerY += 10;
                        break;
                    case KeyEvent.VK_D:  // Move right
                        playerX += 10;
                        break;
                    case KeyEvent.VK_B: // Toggle hitbox visibility
                        showHitbox = !showHitbox;
                        repaint(); // Repaint to update the screen
                        break;
                }
                repaint();  // Repaint the screen after movement
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Not used in this example
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // Not used in this example
            }
        });

        setVisible(true);  // Make the window visible
    }

    // Custom JPanel for drawing the game elements
    private class GamePanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Set background color
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Draw the player image
            if (playerImage != null) {
                g.drawImage(playerImage, playerX, playerY, this);
            }

            // Draw the hitbox if showHitbox is true
            if (showHitbox) {
                g.setColor(Color.RED);
                g.drawRect(playerX, playerY, playerImage.getWidth(), playerImage.getHeight());
            }
        }
    }

    public static void main(String[] args) {
        // Run the game on the Event Dispatch Thread
        SwingUtilities.invokeLater(WASDGame::new);
    }
}
