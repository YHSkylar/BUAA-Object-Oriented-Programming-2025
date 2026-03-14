import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class MonoTest {

    @Test
    public void testEquals() {
        // 创建两个完全相同的 Mono 对象
        Mono mono1 = new Mono(BigInteger.valueOf(2), 3);
        Mono mono2 = new Mono(BigInteger.valueOf(2), 3);

        // 测试完全相同的 Mono 对象
        assertTrue("Two identical Mono objects should be equal", mono1.equals(mono2));

        // 测试不同的系数
        Mono mono3 = new Mono(BigInteger.valueOf(4), 3);
        assertFalse("Mono objects with different coefficients should not be equal", mono1.equals(mono3));

        // 测试不同的指数
        Mono mono4 = new Mono(BigInteger.valueOf(2), 5);
        assertFalse("Mono objects with different exponents should not be equal", mono1.equals(mono4));

        // 测试不同的 sinList
        Mono mono5 = new Mono(BigInteger.valueOf(2), 3);
        mono5.getSinList().put(new Poly(), 1); // 添加一个条目到 sinList
        assertFalse("Mono objects with different sinList should not be equal", mono1.equals(mono5));

        // 测试不同的 cosList
        Mono mono6 = new Mono(BigInteger.valueOf(2), 3);
        mono6.getCosList().put(new Poly(), 1); // 添加一个条目到 cosList
        assertFalse("Mono objects with different cosList should not be equal", mono1.equals(mono6));

        // 测试空的 sinList 和 cosList
        Mono mono7 = new Mono(BigInteger.valueOf(2), 3);
        Mono mono8 = new Mono(BigInteger.valueOf(2), 3);
        assertTrue("Two Mono objects with empty sinList and cosList should be equal", mono7.equals(mono8));

        // 测试与非 Mono 对象比较
        assertFalse("Mono object should not be equal to a non-Mono object", mono1.equals("not a Mono object"));
    }

}