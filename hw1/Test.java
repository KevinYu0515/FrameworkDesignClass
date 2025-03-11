class Test {
    public static void main(String [] args) {

        Player player1 = new Player("Player1", 0);
        Player player2 = new Player("Player2", 1);

        ChessGame game = new ChessGame(player1, player2);
        game.start();
    }
}