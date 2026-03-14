import com.oocourse.spec2.main.PersonInterface;
import com.oocourse.spec2.main.TagInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Person implements PersonInterface {
    private int id;
    private String name;
    private int age;
    private HashMap<Integer, PersonInterface> acquaintance;
    private HashMap<Integer, Integer> value;
    private HashMap<Integer, TagInterface> tags;
    private HashMap<Integer, ArticleNode> receivedArticles;
    private ArticleNode head;
    private int bestId;
    private int bestValue;

    public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        acquaintance = new HashMap<>();
        value = new HashMap<>();
        tags = new HashMap<>();
        receivedArticles = new HashMap<>();
        head = new ArticleNode(null, null);
        bestId = 0;
        bestValue = 0;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public boolean containsTag(int id) {
        return tags.containsKey(id);
    }

    @Override
    public TagInterface getTag(int id) {
        if (containsTag(id)) {
            return tags.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void addTag(TagInterface tag) {
        tags.put(tag.getId(), tag);
    }

    @Override
    public void delTag(int id) {
        tags.remove(id);
    }

    @Override
    public boolean isLinked(PersonInterface person) {
        if (person.getId() == this.id) {
            return true;
        }
        return acquaintance.containsKey(person.getId());
    }

    @Override
    public int queryValue(PersonInterface person) {
        if (acquaintance.containsKey(person.getId())) {
            return value.get(person.getId());
        } else {
            return 0;
        }
    }

    @Override
    public List<Integer> getReceivedArticles() {
        List<Integer> receivedArticles = new ArrayList<>();
        ArticleNode current = head.getNext();
        while (current != null) {
            receivedArticles.add(current.getId());
            current = current.getNext();
        }
        return receivedArticles;
    }

    @Override
    public List<Integer> queryReceivedArticles() {
        List<Integer> receivedArticles = new ArrayList<>();
        ArticleNode current = head.getNext();
        int count = 0;
        while (current != null && count < 5) {
            receivedArticles.add(current.getId());
            current = current.getNext();
            count++;
        }
        return receivedArticles;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PersonInterface) {
            return ((PersonInterface) obj).getId() == this.id;
        } else {
            return false;
        }
    }

    public void addAcquaintance(PersonInterface person) {
        acquaintance.put(person.getId(), person);
    }

    public void setPersonValue(int id, int personValue) {
        value.put(id, personValue);
    }

    public void delAcquaintance(PersonInterface person) {
        int id = person.getId();
        acquaintance.remove(id);
        value.remove(id);
        for (TagInterface tag : tags.values()) {
            if (tag.hasPerson(person)) {
                tag.delPerson(person);
            }
        }
    }

    public void modifyTagValueSum(PersonInterface person1, PersonInterface person2,
        int value, boolean mode) {
        for (TagInterface tag : tags.values()) {
            ((Tag)tag).modifyValueSum(person1, person2, value, mode);
        }
    }

    public int acquaintanceSize() {
        return acquaintance.size();
    }

    public HashMap<Integer, PersonInterface> getAcquaintances() {
        return acquaintance;
    }

    public void receiveArticle(int articleId) {
        ArticleNode currentHead = head.getNext();
        ArticleNode articleNode = new ArticleNode(articleId, head, currentHead);
        head.setNext(articleNode);
        receivedArticles.put(articleId, articleNode);
        if (currentHead != null) {
            currentHead.setPrev(articleNode);
        }
    }

    public void deleteArticle(int articleId) {
        ArticleNode article = receivedArticles.get(articleId);
        if (article == null) {
            return;
        }
        ArticleNode prev = article.getPrev();
        ArticleNode next = article.getNext();
        receivedArticles.remove(articleId);
        if (next == null) {
            prev.setNext(null);
        } else {
            prev.setNext(next);
            next.setPrev(prev);
        }
    }

    public void queryBestAcquaintance(int id, int value, int mode) {
        if (mode == 0) {
            if (acquaintanceSize() == 0) {
                bestId = id;
                bestValue = value;
            } else {
                if ((value > bestValue) || (value == bestValue && id < bestId)) {
                    bestId = id;
                    bestValue = value;
                }
            }
        } else if (mode == 1) {
            if (bestId != id) {
                if ((value > bestValue) || (value == bestValue && id < bestId)) {
                    bestId = id;
                    bestValue = value;
                }
            } else {
                bestValue = 0;
                for (Map.Entry<Integer, Integer> entry : this.value.entrySet()) {
                    if (bestValue < entry.getValue()) {
                        bestId = entry.getKey();
                        bestValue = entry.getValue();
                    } else if (bestValue == entry.getValue()) {
                        if (bestId > entry.getKey()) {
                            bestId = entry.getKey();
                        }
                    }
                }
            }
        } else if (mode == 2) {
            if (bestId == id) {
                bestValue = 0;
                for (Map.Entry<Integer, Integer> entry : this.value.entrySet()) {
                    if (bestValue < entry.getValue()) {
                        bestId = entry.getKey();
                        bestValue = entry.getValue();
                    } else if (bestValue == entry.getValue()) {
                        if (bestId > entry.getKey()) {
                            bestId = entry.getKey();
                        }
                    }
                }
            }
        }
    }

    public int getBestId() {
        return bestId;
    }

    public boolean strictEquals(PersonInterface person) { return true; }
}
