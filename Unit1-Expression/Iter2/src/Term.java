import java.util.ArrayList;

public class Term {
    private final ArrayList<Factor> factors;
    private int sign;

    public Term(int sign) {
        this.factors = new ArrayList<>();
        this.sign = sign;
    }

    public void addFactor(Factor factor) {
        this.factors.add(factor);
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        for (Factor factor : factors) {
            poly = poly.multPoly(factor.toPoly());
        }

        if (sign == -1) {
            poly = poly.opposite();
        }

        return poly;
    }

}
