import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;
public class PolyTest {

    @Test
    public void testEquals() {
        // 创建两个完全相同的 Poly 对象
        Poly poly1 = new Poly();
        Poly poly2 = new Poly();

        // 添加相同的 Mono 对象
        Mono mono1 = new Mono(BigInteger.valueOf(2), 3);
        Mono mono2 = new Mono(BigInteger.valueOf(2), 3); // 相同的 Mono 对象
        Mono mono3 = new Mono(BigInteger.valueOf(4), 5); // 不同的 Mono 对象

        poly1.addMono(mono1);
        poly2.addMono(mono2);

        // 测试完全相同的 Poly 对象
        assertTrue("Two identical Poly objects should be equal", poly1.equals(poly2));

        // 添加不同的 Mono 对象到 poly2
        poly2.addMono(mono3);
        assertFalse("Poly objects with different Monos should not be equal", poly1.equals(poly2));

        // 创建另一个 Poly 对象，包含相同的 Mono 对象但顺序不同
        Poly poly3 = new Poly();
        poly3.addMono(mono3);
        poly3.addMono(mono1);

        assertFalse("Poly objects with different order of Monos should not be equal", poly1.equals(poly3));

        // 测试空 Poly 对象
        Poly emptyPoly1 = new Poly();
        Poly emptyPoly2 = new Poly();
        assertTrue("Two empty Poly objects should be equal", emptyPoly1.equals(emptyPoly2));

        // 测试与非 Poly 对象比较
        assertFalse("Poly object should not be equal to a non-Poly object", poly1.equals("not a Poly object"));
    }
}