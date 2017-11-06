package Util;

/**
 * 4元组
 *
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 */
public class FourTuple<A,B,C,D> {
    public final A first;
    public final B second;
    public final C third;
    public final D fourth;

    public FourTuple(A a, B b,C c,D d) {
        first = a;
        second = b;
        third = c;
        fourth = d;
    }

    public String toString() {
        return "("+first+","+second+","+third+","+fourth+")";
    }
}
