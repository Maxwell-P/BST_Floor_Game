import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

class TreeNode {
    int value;
    TreeNode left, right;

    public TreeNode(int value) {
        this.value = value;
        this.left = this.right = null;
    }
}

class BinarySearchTree {
    TreeNode root;

    public BinarySearchTree() {
        root = null;
    }

    public void insert(int value) {
        root = insertRec(root, value);
    }

    private TreeNode insertRec(TreeNode root, int value) {
        if (root == null) {
            root = new TreeNode(value);
            return root;
        }
        if (value < root.value)
            root.left = insertRec(root.left, value);
        else if (value > root.value)
            root.right = insertRec(root.right, value);
        return root;
    }

    public int findFloor(TreeNode root, int target) {
        int floor = -1;
        while (root != null) {
            if (root.value == target) {
                return root.value;
            } else if (root.value > target) {
                root = root.left;
            } else {
                floor = root.value;
                root = root.right;
            }
        }
        return floor;
    }
}

public class BSTGame extends JFrame implements ActionListener {
    private BinarySearchTree bst;
    private int target;
    private int floorValue;
    private TreeNode currentNode;
    private final JTextArea displayArea;
    private final TreePanel treePanel;
    private final JButton resetButton;

    public BSTGame() {
        initializeGame();

        setTitle("Find the Floor in the BST");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayArea = new JTextArea(5, 30);
        displayArea.setEditable(false);
        displayArea.setText("Target value: " + target + ". Start navigating the tree to find the floor.\n");

        JButton lessButton = new JButton("Less than");
        JButton greaterButton = new JButton("Greater than");
        resetButton = new JButton("Hit if floor value = -1");
        lessButton.addActionListener(this);
        greaterButton.addActionListener(this);
        resetButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(lessButton);
        buttonPanel.add(greaterButton);
        buttonPanel.add(resetButton);

        add(buttonPanel, BorderLayout.SOUTH);
        add(displayArea, BorderLayout.NORTH);

        treePanel = new TreePanel(bst.root);
        add(treePanel, BorderLayout.CENTER);
    }

    private void initializeGame() {
        bst = new BinarySearchTree();
        Random rand = new Random();

        // Insert random values into the BST
        for (int i = 0; i < 10; i++) {
            bst.insert(rand.nextInt(100));
        }

        target = rand.nextInt(100); // Set a random target value
        floorValue = bst.findFloor(bst.root, target); // Find the floor value for the target
        currentNode = bst.root;

        // Debugging and console output
        System.out.println("Debug: Floor value for target " + target + " is " + floorValue);

        // Check if the starting node (root) is already the correct floor value
        if (currentNode != null && currentNode.value == floorValue) {
            System.out.println("The starting node is already the correct floor value. Resetting game.");
            resetGame(); // Reset the game to regenerate the tree and new parameters
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == resetButton) {
            if (floorValue == -1) {
                System.out.println("Floor value is -1. Resetting game.\n");
                resetGame();
            } else {
                System.out.println("Floor value is not -1. Continue playing.\n");
            }
            return;
        }

        if (currentNode == null) {
            System.out.println("Game Over! You reached a leaf node.\n");
            resetGame();
            return;
        }

        String command = e.getActionCommand();
        int currentNodeValue = currentNode.value;

        if (command.equals("Less than")) {
            if (target <= currentNodeValue) {
                System.out.println("Incorrect! Game reset.\n");
                resetGame();
            } else {
                currentNode = currentNode.right;
                if (currentNode != null) {
                    displayArea.append("Moved right to " + currentNode.value + "\n");
                    if (currentNode.value == floorValue) {
                        displayWinMessage();
                    }
                } else {
                    checkWinCondition();
                }
            }
        } else if (command.equals("Greater than")) {
            if (target > currentNodeValue) {
                System.out.println("Incorrect! Game reset.\n");
                resetGame();
            } else {
                currentNode = currentNode.left;
                if (currentNode != null) {
                    displayArea.append("Moved left to " + currentNode.value + "\n");
                    if (currentNode.value == floorValue) {
                        displayWinMessage();
                    }
                } else {
                    checkWinCondition();
                }
            }
        }
        treePanel.repaint();
    }

    private void displayWinMessage() {
        System.out.println("Congratulations! You found the floor value: " + floorValue + "\n");
        resetGame();
    }

    private void checkWinCondition() {
        if (currentNode != null && currentNode.value == floorValue) {
            displayWinMessage();
        } else if (currentNode == null) {
            System.out.println("Reached a leaf without finding the floor. Game reset.\n");
            resetGame();
        }
    }

    private void resetGame() {
        initializeGame();
        displayArea.setText("New target: " + target + ". Navigate the tree again.\n");
        treePanel.updateTree(bst.root);
        treePanel.repaint();
    }

    class TreePanel extends JPanel {
        private TreeNode root;

        public TreePanel(TreeNode root) {
            this.root = root;
        }

        public void updateTree(TreeNode root) {
            this.root = root;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawTree(g, root, getWidth() / 2, 30, getWidth() / 4);
        }

        private void drawTree(Graphics g, TreeNode node, int x, int y, int xOffset) {
            if (node == null) return;

            if (node == currentNode) {
                g.setColor(Color.RED);
                g.fillOval(x - 15, y - 15, 30, 30);
            } else {
                g.setColor(Color.BLACK);
                g.fillOval(x - 15, y - 15, 30, 30);
            }

            g.setColor(Color.WHITE);
            g.drawString(Integer.toString(node.value), x - 5, y + 5);

            if (node.left != null) {
                g.setColor(Color.BLACK);
                g.drawLine(x - 10, y + 10, x - xOffset + 10, y + 50);
                drawTree(g, node.left, x - xOffset, y + 50, xOffset / 2);
            }
            if (node.right != null) {
                g.setColor(Color.BLACK);
                g.drawLine(x + 10, y + 10, x + xOffset - 10, y + 50);
                drawTree(g, node.right, x + xOffset, y + 50, xOffset / 2);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BSTGame game = new BSTGame();
            game.setVisible(true);
        });
    }
}
