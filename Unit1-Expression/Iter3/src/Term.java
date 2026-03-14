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

    public Poly derive() {
        Poly result = new Poly();
        for (int i = 0; i < factors.size(); i++) {
            Poly poly = factors.get(i).derive();
            for (int j = 0; j < factors.size(); j++) {
                if (i != j) {
                    poly = poly.multPoly(factors.get(j).toPoly());
                }
            }
            result = result.addPoly(poly);
        }

        if (sign == -1) {
            result = result.opposite();
        }
        return result;
    }
}
