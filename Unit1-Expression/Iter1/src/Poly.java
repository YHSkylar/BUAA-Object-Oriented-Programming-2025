import java.math.BigInteger;
import java.util.ArrayList;

public class Poly {
    private final ArrayList<Mono> monomials = new ArrayList<>();

    public void addMono(Mono mono) {
        this.monomials.add(mono);
    }

    public Poly addPoly(Poly another) {
        for (Mono monoNew : another.monomials) {
            int flag = 0;
            for (Mono monoOri : monomials) {
                if (monoNew.getExp() == monoOri.getExp()) {
                    monoOri.setCoe(monoNew.getCoe().add(monoOri.getCoe()));
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                monomials.add(monoNew);
            }
        }

        return this;
    }

    public Poly multPoly(Poly another) {
        Poly poly = new Poly();
        for (Mono monoNew : another.monomials) {
            if (monomials.isEmpty()) {
                poly.monomials.addAll(another.monomials);
                break;
            }
            else {
                for (Mono monoOri : monomials) {
                    Mono temp = new Mono(new BigInteger("0"),0);
                    temp.setCoe(monoOri.getCoe().multiply(monoNew.getCoe()));
                    temp.setExp(monoOri.getExp() + monoNew.getExp());
                    if (temp.getCoe().equals(BigInteger.ZERO)) {
                        temp.setExp(0);
                    }
                    poly.addMono(temp);
                }
            }
        }
        return poly;
    }

    public Poly powPoly(int exponent) {
        Poly poly = new Poly();
        for (int i = 0; i < exponent; i++) {
            Poly temp = new Poly();
            poly = poly.multPoly(this);
            poly = temp.addPoly(poly);
        }

        if (exponent == 0) {
            Mono mono = new Mono(new BigInteger("1"),0);
            poly.addMono(mono);
        }
        return poly;
    }

    public Poly opposite() {
        for (Mono mono : monomials) {
            mono.setCoe(mono.getCoe().negate());
        }
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Mono mono : monomials) {
            sb.append(mono.toString());
            sb.append("+");
        }
        String answer = sb.substring(0, sb.length() - 1);
        return answer.replaceAll("\\+-","-");
    }
}
