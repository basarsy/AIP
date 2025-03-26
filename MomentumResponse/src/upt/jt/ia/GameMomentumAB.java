package upt.jt.ia;

import java.util.ArrayList;
import java.io.PrintWriter;
import java.net.Socket;

public class GameMomentumAB extends NodeGameAB {

    private int[][] board = new int[7][7];
    private int myColor;

    public GameMomentumAB(String node) {
        super(1);
        myColor = getPlayer();
        processNode(node);
    }

    public GameMomentumAB(int[][] p, int myColor, int depth) {
        super(depth);
        for (int l = 0; l < 7; l++)
            for (int c = 0; c < 7; c++)
                this.board[l][c] = p[l][c];
        this.myColor = myColor;
    }

    public void processNode(String node) {
        String[] values = node.trim().split("\\s+");
        if (values.length != 49) {
            throw new IllegalArgumentException("Invalid board state format.");
        }
        for (int l = 0; l < 7; l++) {
            for (int c = 0; c < 7; c++) {
                board[l][c] = Integer.parseInt(values[l * 7 + c]);
            }
        }
    }

    public ArrayList<Move> expandAB() {
        ArrayList<Move> successors = new ArrayList<>();

        for (int l = 0; l < 7; l++) {
            for (int c = 0; c < 7; c++) {
                if (board[l][c] == 0) {
                    int[][] newBoard = makeCopy(board);
                    newBoard[l][c] = myColor;

                    applyMomentum(newBoard, l, c);

                    successors.add(new Move((l + 1) + " " + (c + 1), new GameMomentumAB(newBoard, myColor == 1 ? 2 : 1, getDepth() + 1)));
                }
            }
        }

        // Sort successors based on the evaluation heuristic
        successors.sort((move1, move2) -> {
            double h1 = move1.getNode().getH();
            double h2 = move2.getNode().getH();
            return Double.compare(h2, h1); // Descending order
        });

        return successors;
    }

    private void applyMomentum(int[][] board, int l, int c) {
        for (int[] dir : new int[][] { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } }) {
            int x = l, y = c;
            while (isInBounds(x + dir[0], y + dir[1]) && board[x + dir[0]][y + dir[1]] != 0) {
                x += dir[0];
                y += dir[1];
            }
            if (isInBounds(x + dir[0], y + dir[1])) {
                board[x + dir[0]][y + dir[1]] = board[x][y];
                board[x][y] = 0;
            }
        }
    }

    public double getH() {
        double h = 0;
        int myCount = 0, opponentCount = 0;

        // Center of the board
        int centerX = 3, centerY = 3;

        for (int l = 0; l < 7; l++) {
            for (int c = 0; c < 7; c++) {
                if (board[l][c] == myColor) {
                    myCount++;
                    h += 20 - 2 * (Math.abs(l - centerX) + Math.abs(c - centerY)); // Strongly prefer the center

                    // Bonus for potential pushes
                    if (canPushOthersOut(board, l, c)) {
                        h += 10;
                    }

                } else if (board[l][c] != 0) {
                    opponentCount++;
                    h -= 15 - 1.5 * (Math.abs(l - centerX) + Math.abs(c - centerY)); // Penalize opponent positions closer to the center
                }
            }
        }

        h += myCount - opponentCount; // Bonus for having more pieces
        return h;
    }

    private boolean canPushOthersOut(int[][] board, int l, int c) {
        for (int[] dir : new int[][] { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } }) {
            int x = l + dir[0], y = c + dir[1];
            if (isInBounds(x, y) && board[x][y] != 0) {
                int dx = dir[0], dy = dir[1];
                while (isInBounds(x + dx, y + dy)) {
                    x += dx;
                    y += dy;
                    if (board[x][y] == 0) return true; // Found an empty space to push to
                }
            }
        }
        return false;
    }

    private int[][] makeCopy(int[][] p) {
        int[][] np = new int[7][7];
        for (int l = 0; l < 7; l++)
            for (int c = 0; c < 7; c++)
                np[l][c] = p[l][c];
        return np;
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < 7 && y >= 0 && y < 7;
    }

    public void setMyColor(int color) {
        myColor = color;
    }

    public void updateBoardFromServer(String serverResponse) {
        processNode(serverResponse);
    }

    @Override
    public String getMove() {
        if (getBestMove() != null) {
            Move best = getBestMove();
            String[] parts = best.getAction().replaceAll("[^0-9 ]", "").trim().split("\\s+");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            return myColor + " " + row + " " + col;
        } else {
            return "passo";
        }
    }

    public String toString() {
        StringBuilder st = new StringBuilder();
        for (int l = 0; l < 7; l++) {
            for (int c = 0; c < 7; c++) {
                st.append(" ").append(board[l][c] == 0 ? "." : "" + (board[l][c]));
            }
            st.append("\n");
        }
        st.append("\n");
        return st.toString();
    }

    public void sendMoveToServer(String serverAddress, int serverPort) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            String move = getMove();
            System.out.println("Sending move to server: " + move);
            out.println(move); // Send the move
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send move to server.");
        }
    }

    public static void main(String[] args) {
        GameMomentumAB jogo = new GameMomentumAB("0 0 0 0 0 0 0 " + "0 0 1 0 0 0 0 " + "0 0 1 0 0 0 0 "
                + "0 0 0 2 2 2 2 " + "0 0 0 0 0 0 0 " + "0 0 0 0 0 0 0 " + "0 0 0 0 0 0 0 ");
        jogo.setMyColor(1);
        ArrayList<Move> suc = jogo.expandAB();
        for (Move j : suc)
            System.out.println(j);
    }
}
