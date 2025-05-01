class Player {
    private int chessesCount = 16;
    int side; // 0: white, 1: black
    String name;

    Player(String name, int side) {
        this.name = name;
        this.side = side;
    }

    void costChessCount() {
        this.chessesCount--;
    }
    
    int getChessCount() {
        return this.chessesCount;
    }

    String getName() {
        return this.name;
    }
}

class Chess {
    String name;
    double weight;
    int side; // 0: white, 1: black
    Pair<String, String> loc; // column, row

    Chess(String name, double weight, int side, Pair<String, String> loc) {
        this.name = name;
        this.weight = weight;
        this.side = side;
        this.loc = loc;
    }

    @Override
    public String toString() {
        return String.format("棋型: %s\n等級: %s\n陣營: %s\n位置: %s\n", name, weight, side, loc.first + loc.second);
    }
}