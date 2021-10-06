class Pair<A,B> {
    private A a;
    private B b;

    Pair (A a, B b) { this.a = a; this.b = b; }

    A getFirst () { return a; }
    B getSecond () { return b; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        else {
            Pair<A,B> that = (Pair<A,B>) o;
            return a.equals(that.a) && b.equals(that.b);
        }
    }

    @Override
    public int hashCode() {
        return a.hashCode() + 31 * b.hashCode();
    }

    @Override
    public String toString () {
        return "<" + a + "," + b + ">";
    }

}
