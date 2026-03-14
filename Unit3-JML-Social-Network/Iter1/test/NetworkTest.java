import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.main.PersonInterface;
import com.oocourse.spec1.main.TagInterface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class NetworkTest {
    private Network network;
    private Network oldNetwork;
    private int expectedTriples;
    private static Random random = new Random();
    private static final int MAX_NODES = 5;
    private static final int MAX_EDGE_WEIGHT = 10;
    private static final int MAX_VALUE = 10;

    public NetworkTest(Network network, Network oldNetwork, int expectedTriples) {
        this.network = network;
        this.oldNetwork = oldNetwork;
        this.expectedTriples = expectedTriples;
    }

    @Parameters
    public static Collection prepareData() {
        return Arrays.asList(new Object[][]{
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase()
        });
    }

    private static Object[] generateTestCase() {
        int[] nodes = generateUniqueNodes();
        int[][] edges = generateValidEdges(nodes);
        // 此处结果假设为0，实际应根据逻辑计算
        Network network = createNetwork(nodes, edges);
        return new Object[] {
                network,
                createNetwork(nodes, edges),
                queryTripleSum(network)
        };
    }

    private static int[] generateUniqueNodes() {
        Set<Integer> nodes = new HashSet<>();
        int size = random.nextInt(MAX_NODES + 1); // 0~5个节点

        while (nodes.size() < size) {
            nodes.add(random.nextInt(MAX_VALUE) + 1); // 1~10的节点值
        }
        return nodes.stream().mapToInt(i -> i).toArray();
    }

    private static int[][] generateValidEdges(int[] nodes) {
        if (nodes.length < 2) return new int[0][3];

        // 计算最大可能边数（组合数公式）
        int maxPossible = nodes.length * (nodes.length - 1) / 2;
        int edgeCount = random.nextInt(maxPossible + 1);

        Set<String> edgeKeys = new HashSet<>();
        List<int[]> edges = new ArrayList<>();

        while (edges.size() < edgeCount) {
            // 随机选择两个不同节点
            int idx1 = random.nextInt(nodes.length);
            int idx2 = random.nextInt(nodes.length);
            if (idx1 == idx2) continue;

            // 创建规范化边标识（小节点在前）
            int a = Math.min(nodes[idx1], nodes[idx2]);
            int b = Math.max(nodes[idx1], nodes[idx2]);
            String key = a + "-" + b;

            if (!edgeKeys.contains(key)) {
                // 保留原始节点顺序，但存储规范化键
                edges.add(new int[]{
                        nodes[idx1],
                        nodes[idx2],
                        random.nextInt(MAX_EDGE_WEIGHT) + 1
                });
                edgeKeys.add(key);
            }
        }
        return edges.toArray(new int[0][3]);
    }

    @Test
    public void queryTripleSum() {
        PersonInterface[] oldPersons = oldNetwork.getPersons();
        int expect = network.queryTripleSum();
        assertEquals(expect, expectedTriples);

        //比较新的persons与\old persons
        /*TODO*/
        PersonInterface[] persons = network.getPersons();
        assertEquals(persons.length, oldPersons.length);
        for (int i = 0; i < persons.length; i++) {
            PersonInterface person = persons[i];
            boolean flag = false;
            for (int j = 0; j < oldPersons.length; j++) {
                if (person.getId() == oldPersons[j].getId()) {
                    flag = true;
                    assertTrue(((Person)person).strictEquals(oldPersons[j]));
                    break;
                }
            }
            assertTrue(flag);
        }
    }

    private static Network createNetwork(int[] ids, int[][] links) {
        Network network = new Network();
        for (int id : ids) {
            PersonInterface person = new Person(id, "person" + id, 20);
            try {
                network.addPerson(person);
            } catch (EqualPersonIdException e) {
                throw new RuntimeException(e);
            }
        }

        for (int[] link : links) {
            int id1 = link[0];
            int id2 = link[1];
            int value = link[2];

            try {
                network.addRelation(id1, id2, value);
            } catch (PersonIdNotFoundException | EqualRelationException e) {
                throw new RuntimeException(e);
            }
        }
        return network;
    }

    private static int queryTripleSum(Network network) {
        PersonInterface[] persons = network.getPersons();
        List<PersonInterface> personList = new ArrayList<>(Arrays.asList(persons));
        int n = persons.length;
        if (n < 3) {
            return 0;
        }

        //邻接表
        List<Set<Integer>> adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new HashSet<>());
        }
        for (int i = 0; i < n; i++) {
            PersonInterface person1 = personList.get(i);
            for (int j = i + 1; j < n; j++) {
                PersonInterface person2 = personList.get(j);
                if (person1.isLinked(person2)) {
                    adj.get(i).add(j);
                }
            }
        }

        int sum = 0;
        // 遍历所有i < j < k的组合
        for (int i = 0; i < n; i++) {
            Set<Integer> iiNeighbors = adj.get(i);
            for (int j : iiNeighbors) {
                Set<Integer> jjNeighbors = adj.get(j);
                for (int k : jjNeighbors) {
                    if (iiNeighbors.contains(k)) { // 检查i和k是否相连
                        sum++;
                    }
                }
            }
        }
        return sum;
    }
}