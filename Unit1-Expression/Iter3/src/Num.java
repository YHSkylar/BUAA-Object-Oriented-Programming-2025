import java.math.BigInteger;

public class Num implements Factor {
    private final BigInteger num;

    public Num(BigInteger num) {
        this.num = num;
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        Mono mono = new Mono(num,0);
        poly.addMono(mono);
        return poly;
    }

    public Poly derive() {
        Poly poly = new Poly();
        Mono mono = new Mono(BigInteger.ZERO,0);
        poly.addMono(mono);
        return poly;
    }
}
