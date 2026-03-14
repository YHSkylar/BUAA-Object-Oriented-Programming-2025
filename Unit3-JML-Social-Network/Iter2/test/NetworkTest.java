import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.main.PersonInterface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class NetworkTest {
    private Network network;
    private Network oldNetwork;
    private static Random random = new Random();
    private int expectedCoupleSum;
    private static final int MAX_NODES = 5;
    private static final int MAX_VALUE = 10;

    public NetworkTest(Network network, Network oldNetwork, int expectedCoupleSum) {
        this.network = network;
        this.oldNetwork = oldNetwork;
        this.expectedCoupleSum = expectedCoupleSum;
    }

    @Parameterized.Parameters
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
                queryCoupleSum(network)
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
        final int MAX_EDGE_WEIGHT = 10; // 假设最大边权重为10

        int n = nodes.length;
        Set<String> edgeKeys = new HashSet<>();
        List<int[]> edges = new ArrayList<>();
        Random random = new Random();

        // 步骤1: 确保每个节点至少有一条边
        Set<Integer> uncovered = new HashSet<>();
        for (int node : nodes) {
            uncovered.add(node);
        }

        while (!uncovered.isEmpty()) {
            int u = uncovered.iterator().next();
            int v;
            do {
                v = nodes[random.nextInt(n)];
            } while (v == u);

            int a = Math.min(u, v);
            int b = Math.max(u, v);
            String key = a + "-" + b;

            if (!edgeKeys.contains(key)) {
                int weight = random.nextInt(MAX_EDGE_WEIGHT) + 1;
                edges.add(new int[]{u, v, weight});
                edgeKeys.add(key);
                uncovered.remove(u);
                uncovered.remove(v);
            }
        }

        // 步骤2: 计算剩余可添加的边数
        int currentEdgeCount = edges.size();
        int maxPossible = n * (n - 1) / 2;
        int remainingMax = Math.max(maxPossible - currentEdgeCount, 0);

        // 随机生成剩余边
        int additionalEdges = remainingMax > 0 ? random.nextInt(remainingMax + 1) : 0;
        while (edges.size() < currentEdgeCount + additionalEdges) {
            int idx1 = random.nextInt(n);
            int idx2;
            do {
                idx2 = random.nextInt(n);
            } while (idx1 == idx2);

            int u = nodes[idx1];
            int v = nodes[idx2];
            int a = Math.min(u, v);
            int b = Math.max(u, v);
            String key = a + "-" + b;

            if (!edgeKeys.contains(key)) {
                int weight = random.nextInt(MAX_EDGE_WEIGHT) + 1;
                edges.add(new int[]{u, v, weight});
                edgeKeys.add(key);
            }
        }

        return edges.toArray(new int[0][3]);
    }

    @Test
    public void queryCoupleSum() {
        PersonInterface[] oldPersons = oldNetwork.getPersons();
        int expect = network.queryCoupleSum();
        assertEquals(expect, expectedCoupleSum);

        //比较新的persons与\old persons
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
            PersonInterface person = new Person(id, "person" + id, id);
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

    public static int queryCoupleSum(Network network) {
        HashMap<Integer, Integer> bestIdMap = new HashMap<>();
        PersonInterface[] persons = network.getPersons();
        for (PersonInterface person : persons) {
            int currentId = person.getId();
            try {
                int bestId = network.queryBestAcquaintance(currentId);
                bestIdMap.put(currentId, bestId);
            } catch (PersonIdNotFoundException e) {
                // 理论上不会发生，因为person在persons列表中
            } catch (AcquaintanceNotFoundException e) {
                // 没有熟人，跳过
            }
        }

        int sum = 0;
        List<PersonInterface> personList = new ArrayList<>(Arrays.asList(persons));
        int size = personList.size();

        for (int i = 0; i < size; i++) {
            PersonInterface p1 = personList.get(i);
            Integer best1 = bestIdMap.get(p1.getId());
            if (best1 == null) {
                continue;
            }
            for (int j = i + 1; j < size; j++) {
                PersonInterface p2 = personList.get(j);
                Integer best2 = bestIdMap.get(p2.getId());
                if (best2 != null && best1 == p2.getId() && best2 == p1.getId()) {
                    sum++;
                }
            }
        }

        return sum;
    }
}