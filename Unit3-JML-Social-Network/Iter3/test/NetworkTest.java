import com.oocourse.spec3.exceptions.*;
import com.oocourse.spec3.main.MessageInterface;
import com.oocourse.spec3.main.PersonInterface;
import com.oocourse.spec3.main.TagInterface;
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
    private int limit;
    private int expectedEmojiLength;
    private static final int MIN_NODES = 50;
    private static final int MAX_NODES = 200;
    private static final int MAX_VALUE = 1000;
    private static MessageInterface[] messages0;
    private static MessageInterface[] messages1;

    public NetworkTest(Network network, Network oldNetwork) {
        this.network = network;
        this.oldNetwork = oldNetwork;
        limit = random.nextInt(10);
        this.expectedEmojiLength = queryEmojiLength(limit, oldNetwork);
    }

    @Parameterized.Parameters
    public static Collection prepareData() {
        return Arrays.asList(new Object[][]{
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase(),
                generateTestCase()
        });
    }

    @Test
    public void deleteColdEmoji() {
        int[] oldEmojiIdList = oldNetwork.getEmojiIdList();
        int[] oldEmojiHeatList = oldNetwork.getEmojiHeatList();
        int expect = network.deleteColdEmoji(limit);
        assertEquals(expect, expectedEmojiLength);

        int[] newEmojiIdList = network.getEmojiIdList();
        int[] newEmojiHeatList = network.getEmojiHeatList();

        System.out.println(Arrays.toString(oldEmojiIdList));
        System.out.println(Arrays.toString(newEmojiIdList));

        //检查emojiIdList,emojiHeatList
        int i = 0;
        for (; i < oldEmojiIdList.length; i++) {
            if (oldEmojiHeatList[i] >= limit) {
                boolean hasEmoji = false;
                for (int j = 0; j < newEmojiIdList.length; j++) {
                    if (oldEmojiIdList[i] == newEmojiIdList[j]) {
                        hasEmoji = true;
                        break;
                    }
                }
                assertTrue(hasEmoji);
            }
        }

        i = 0;
        for (; i < newEmojiIdList.length; i++) {
            int newEmojiId = newEmojiIdList[i];
            boolean hasEmoji = false;
            int j = 0;
            for (; j < oldEmojiIdList.length; j++) {
                if (oldEmojiIdList[j] == newEmojiId) {
                    hasEmoji = true;
                    break;
                }
            }
            assertTrue(hasEmoji);
            assertEquals(oldEmojiHeatList[j], newEmojiHeatList[i]);
            assertTrue(newEmojiHeatList[i] >= limit);
        }

        assertEquals(newEmojiIdList.length, newEmojiHeatList.length);

        //检查messages
        MessageInterface[] oldMessages = oldNetwork.getMessages();
        MessageInterface[] newMessages = network.getMessages();

        System.out.println(Arrays.toString(newMessages));

        i = 0;
        int messageTrueSize = 0;
        for (; i < oldMessages.length; i++) {
            if (oldMessages[i] instanceof EmojiMessage) {
                int emojiId = ((EmojiMessage) oldMessages[i]).getEmojiId();
                boolean hasEmoji = false;
                for (int j = 0; j < newEmojiIdList.length; j++) {
                    if (newEmojiIdList[j] == emojiId) {
                        hasEmoji = true;
                        break;
                    }
                }

                if (hasEmoji) {
                    boolean hasMessage = false;
                    for (int j = 0; j < newMessages.length; j++) {
                        if (newMessages[j].equals(oldMessages[i])) {
                            hasMessage = true;
                            break;
                        }
                    }
                    assertTrue(hasMessage);
                    messageTrueSize++;
                } else {
                    boolean hasMessage = false;
                    for (int j = 0; j < newMessages.length; j++) {
                        if (newMessages[j].equals(oldMessages[i])) {
                            hasMessage = true;
                            break;
                        }
                    }
                    assertFalse(hasMessage);
                }
            } else {
                boolean hasMessage = false;
                for (int j = 0; j < newMessages.length; j++) {
                    if (newMessages[j].equals(oldMessages[i])) {
                        hasMessage = true;
                        break;
                    }
                }
                assertTrue(hasMessage);
                messageTrueSize++;
            }
        }

        assertEquals(messageTrueSize, newMessages.length);

        i = 0;
        for (; i < newMessages.length; i++) {
            boolean hasMessage = false;
            for (int j = 0; j < oldMessages.length; j++) {
                if (newMessages[i].equals(oldMessages[j])) {
                    hasMessage = true;
                    break;
                }
            }
            assertTrue(hasMessage);
        }
    }

    private static Object[] generateTestCase() {
        int[] nodes = generateUniqueNodes();
        int[][] edges = generateValidEdges(nodes);
        // 此处结果假设为0，实际应根据逻辑计算
        Network network = createNetwork(nodes, edges);
        return new Object[] {
                network,
                copyNetwork(nodes, edges)
        };
    }

    private static int[] generateUniqueNodes() {
        Set<Integer> nodes = new HashSet<>();
        int size = MIN_NODES + random.nextInt(MAX_NODES - MIN_NODES + 1); // 0~5个节点

        while (nodes.size() < size) {
            nodes.add(random.nextInt(MAX_VALUE) + 1); // 1~10的节点值
        }
        return nodes.stream().mapToInt(i -> i).toArray();
    }

    private static int[][] generateValidEdges(int[] nodes) {
        if (nodes.length < 2) return new int[0][3];
        final int MAX_EDGE_WEIGHT = 100;

        int n = nodes.length;
        Set<String> edgeKeys = new HashSet<>();
        List<int[]> edges = new ArrayList<>();
        Random random = new Random();

        // 检查所有节点是否相同，避免死循环
        boolean allSame = true;
        for (int i = 1; i < nodes.length; i++) {
            if (nodes[i] != nodes[0]) {
                allSame = false;
                break;
            }
        }
        if (allSame) return new int[0][3]; // 无法生成非自环边

        // 计算最大可能的边数（无自环且无重复）
        int maxPossible = n * (n - 1) / 2;
        int minEdgeCount = 2 * n * (n - 1) / 5;
        int edgeCount;

        if (maxPossible >= minEdgeCount) {
            edgeCount = minEdgeCount + random.nextInt(maxPossible - minEdgeCount + 1);
        } else if (maxPossible > 0) {
            edgeCount = maxPossible;
        } else {
            edgeCount = 0;
        }

        // 生成指定数量的无重复边
        while (edges.size() < edgeCount) {
            int u, v;
            int idx1, idx2;

            // 确保u和v的值不同
            do {
                idx1 = random.nextInt(n);
                idx2 = random.nextInt(n);
                u = nodes[idx1];
                v = nodes[idx2];
            } while (u == v);

            // 生成唯一键并添加边
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

    private static MessageInterface[] generateType0Messages(int[][] edges, HashMap<Integer, PersonInterface> persons) {
        List<MessageInterface> messages = new ArrayList<>();
        int messageId = 0;

        if (edges.length == 0) {
            return new MessageInterface[0];
        }
        List<Integer> emojiPool = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            emojiPool.add(random.nextInt(1000));
        }
        // Generate first 10 EmojiMessages
        int edgeIndexEmoji = 0;
        while (messages.size() < 200) {
            int[] edge = edges[edgeIndexEmoji % edges.length];
            edgeIndexEmoji++;
            PersonInterface person1 = persons.get(edge[0]);
            PersonInterface person2 = persons.get(edge[1]);
            messageId = random.nextInt(500);
            int socialValue = emojiPool.get(random.nextInt(emojiPool.size()));
            messages.add(new EmojiMessage(messageId, socialValue, person1, person2));
        }

        // Generate last 5 RedEnvelopeMessages
        int edgeIndexRed = 0;
        while (messages.size() < 250) {
            int[] edge = edges[edgeIndexRed % edges.length];
            edgeIndexRed++;
            PersonInterface person1 = persons.get(edge[0]);
            PersonInterface person2 = persons.get(edge[1]);
            messageId = random.nextInt(500);
            int socialValue = random.nextInt(300);
            messages.add(new RedEnvelopeMessage(messageId, socialValue, person1, person2));
        }

        return messages.toArray(new MessageInterface[0]);
    }

    private static MessageInterface[] generateType1Messages(int[] nodes, HashMap<Integer, PersonInterface> persons) {
        List<MessageInterface> messages = new ArrayList<>();
        int messageId;
        int tagId = 0;

        if (nodes.length == 0) {
            return new MessageInterface[0];
        }
        List<Integer> emojiPool = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            emojiPool.add(random.nextInt(1000));
        }
        // Generate first 10 EmojiMessages
        int nodeIndexEmoji = 0;
        while (messages.size() < 200) {
            int node = nodes[nodeIndexEmoji % nodes.length];
            nodeIndexEmoji++;
            PersonInterface person1 = persons.get(node);
            TagInterface tag = new Tag(tagId++);
            person1.addTag(tag);
            messageId = random.nextInt(500) - 500;
            int socialValue = emojiPool.get(random.nextInt(emojiPool.size()));
            messages.add(new EmojiMessage(messageId, socialValue, person1, tag));
        }

        // Generate last 5 RedEnvelopeMessages
        int nodeIndexRed = 0;
        while (messages.size() < 250) {
            int node = nodes[nodeIndexRed % nodes.length];
            nodeIndexRed++;
            PersonInterface person1 = persons.get(node);
            TagInterface tag = new Tag(tagId++);
            person1.addTag(tag);
            messageId = random.nextInt(500) - 500;
            int socialValue = random.nextInt(300);
            messages.add(new RedEnvelopeMessage(messageId, socialValue, person1, tag));
        }

        return messages.toArray(new MessageInterface[0]);
    }

    private static Network createNetwork(int[] ids, int[][] links) {
        Network network = new Network();
        HashMap<Integer, PersonInterface> persons = new HashMap<>();
        for (int id : ids) {
            PersonInterface person = new Person(id, "person" + id, id);
            persons.put(id, person);
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

        messages0 = generateType0Messages(links, persons);
        for (int i = 0; i < messages0.length; i++) {
            if (i <= 100 || (i >= 200 && i<= 220)) {
                if (messages0[i] instanceof EmojiMessage) {
                    try {
                        network.storeEmojiId(((EmojiMessage) messages0[i]).getEmojiId());
                    } catch (EqualEmojiIdException ignored) {
                    }
                }

                try {
                    network.addMessage(messages0[i]);
                } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException |
                         ArticleIdNotFoundException ignored) {
                }

                try {
                    network.sendMessage(messages0[i].getId());
                } catch (RelationNotFoundException | MessageIdNotFoundException | TagIdNotFoundException ignored) {
                }
            } else {
                if (messages0[i] instanceof EmojiMessage) {
                    try {
                        network.storeEmojiId(((EmojiMessage) messages0[i]).getEmojiId());
                    } catch (EqualEmojiIdException ignored) {
                    }
                }

                try {
                    network.addMessage(messages0[i]);
                } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException |
                         ArticleIdNotFoundException ignored) {
                }
            }
        }

        messages1 = generateType1Messages(ids, persons);
        for (int i = 0; i < messages1.length; i++) {
            if (i <= 100 || (i >= 200 && i<= 220)) {
                if (messages1[i] instanceof EmojiMessage) {
                    try {
                        network.storeEmojiId(((EmojiMessage) messages1[i]).getEmojiId());
                    } catch (EqualEmojiIdException ignored) {
                    }
                }

                try {
                    network.addMessage(messages1[i]);
                } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException |
                         ArticleIdNotFoundException ignored) {
                }

                try {
                    network.sendMessage(messages1[i].getId());
                } catch (RelationNotFoundException | MessageIdNotFoundException | TagIdNotFoundException ignored) {
                }
            } else {
                if (messages1[i] instanceof EmojiMessage) {
                    try {
                        network.storeEmojiId(((EmojiMessage) messages1[i]).getEmojiId());
                    } catch (EqualEmojiIdException ignored) {
                    }
                }

                try {
                    network.addMessage(messages1[i]);
                } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException |
                         ArticleIdNotFoundException ignored) {
                }
            }
        }
        //System.out.println(Arrays.toString(network.getEmojiIdList()));
        //System.out.println(Arrays.toString(network.getMessages()));
        return network;
    }

    public static int queryEmojiLength(int num, Network network) {
        int[] emojiHeatList = network.getEmojiHeatList();
        int sum = 0;
        for (int heat : emojiHeatList) {
            if (heat >= num) {
                sum++;
            }
        }
        return sum;
    }

    public static Network copyNetwork(int[] ids, int[][] links) {
        Network copyNetwork = new Network();
        for (int id : ids) {
            PersonInterface person = new Person(id, "person" + id, id);
            try {
                copyNetwork.addPerson(person);
            } catch (EqualPersonIdException e) {
                throw new RuntimeException(e);
            }
        }

        for (int[] link : links) {
            int id1 = link[0];
            int id2 = link[1];
            int value = link[2];

            try {
                copyNetwork.addRelation(id1, id2, value);
            } catch (PersonIdNotFoundException | EqualRelationException e) {
                throw new RuntimeException(e);
            }
        }

        for (int i = 0; i < messages0.length; i++) {
            if (i <= 100 || (i >= 200 && i<= 220)) {
                if (messages0[i] instanceof EmojiMessage) {
                    try {
                        copyNetwork.storeEmojiId(((EmojiMessage) messages0[i]).getEmojiId());
                    } catch (EqualEmojiIdException ignored) {
                    }
                }

                try {
                    copyNetwork.addMessage(messages0[i]);
                } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException |
                         ArticleIdNotFoundException ignored) {
                }

                try {
                    copyNetwork.sendMessage(messages0[i].getId());
                } catch (RelationNotFoundException | MessageIdNotFoundException | TagIdNotFoundException ignored) {
                }
            } else {
                if (messages0[i] instanceof EmojiMessage) {
                    try {
                        copyNetwork.storeEmojiId(((EmojiMessage) messages0[i]).getEmojiId());
                    } catch (EqualEmojiIdException ignored) {
                    }
                }

                try {
                    copyNetwork.addMessage(messages0[i]);
                } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException |
                         ArticleIdNotFoundException ignored) {
                }
            }
        }

        for (int i = 0; i < messages1.length; i++) {
            if (i <= 100 || (i >= 200 && i<= 220)) {
                if (messages1[i] instanceof EmojiMessage) {
                    try {
                        copyNetwork.storeEmojiId(((EmojiMessage) messages1[i]).getEmojiId());
                    } catch (EqualEmojiIdException ignored) {
                    }
                }

                try {
                    copyNetwork.addMessage(messages1[i]);
                } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException |
                         ArticleIdNotFoundException ignored) {
                }

                try {
                    copyNetwork.sendMessage(messages1[i].getId());
                } catch (RelationNotFoundException | MessageIdNotFoundException | TagIdNotFoundException ignored) {
                }
            } else {
                if (messages1[i] instanceof EmojiMessage) {
                    try {
                        copyNetwork.storeEmojiId(((EmojiMessage) messages1[i]).getEmojiId());
                    } catch (EqualEmojiIdException ignored) {
                    }
                }

                try {
                    copyNetwork.addMessage(messages1[i]);
                } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException |
                         ArticleIdNotFoundException ignored) {
                }
            }
        }

        return copyNetwork;
    }
}