class Pair<T1, T2> {
    protected T1 first;
    protected T2 second;
    public int x, y;

    Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
        if (first instanceof String && second instanceof String) {
            x = ((String) first).charAt(0) - 'A';
            y = Integer.parseInt((String) second) - 1;
        }
    }
}