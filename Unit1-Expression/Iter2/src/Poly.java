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
                if (monoOri.canAdd(monoNew)) {
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
            } else {
                for (Mono monoOri : monomials) {
                    Mono temp = new Mono(new BigInteger("0"), 0);
                    temp.setCoe(monoOri.getCoe().multiply(monoNew.getCoe()));
                    temp.setExp(monoOri.getExp() + monoNew.getExp());
                    if (temp.getCoe().equals(BigInteger.ZERO)) {
                        temp.setExp(0);
                    }
                    temp.mergeSinList(monoNew.getSinList());
                    temp.mergeSinList(monoOri.getSinList());
                    temp.mergeCosList(monoNew.getCosList());
                    temp.mergeCosList(monoOri.getCosList());
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
            Mono mono = new Mono(new BigInteger("1"), 0);
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
        return answer.replaceAll("\\+-", "-");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // 如果是同一个对象，直接返回 true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // 如果对象为空或类型不匹配，返回 false
        }

        Poly other = (Poly) obj; // 将对象强制转换为 Poly 类型

        // 检查单项式数量是否相同
        if (this.monomials.size() != other.monomials.size()) {
            return false;
        }

        // 检查每个单项式是否相等
        for (int i = 0; i < this.monomials.size(); i++) {
            if (!this.monomials.get(i).equals(other.monomials.get(i))) {
                return false;
            }
        }

        return true; // 如果所有检查都通过，返回 true
    }

    @Override
    public int hashCode() {
        int result = 17; // 初始值，通常选择一个质数
        for (Mono mono : this.monomials) {
            result = 31 * result + mono.hashCode(); // 累加每个单项式的哈希码
        }
        return result;
    }
}

