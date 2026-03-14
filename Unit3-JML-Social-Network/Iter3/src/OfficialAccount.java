import com.oocourse.spec3.main.OfficialAccountInterface;
import com.oocourse.spec3.main.PersonInterface;

import java.util.HashMap;
import java.util.Map;

public class OfficialAccount implements OfficialAccountInterface {
    private int ownerId;
    private int id;
    private String name;
    private HashMap<Integer, PersonInterface> followers;
    private HashMap<Integer, Integer> articles; // Key: articleId  Value: personId
    private HashMap<Integer, Integer> contributions;

    public OfficialAccount(int ownerId, int id, String name) {
        this.ownerId = ownerId;
        this.id = id;
        this.name = name;
        followers = new HashMap<>();
        articles = new HashMap<>();
        contributions = new HashMap<>();
    }

    @Override
    public int getOwnerId() {
        return ownerId;
    }

    @Override
    public void addFollower(PersonInterface person) {
        followers.put(person.getId(), person);
    }

    @Override
    public boolean containsFollower(PersonInterface person) {
        return followers.containsKey(person.getId());
    }

    @Override
    public void addArticle(PersonInterface person, int id) {
        int personId = person.getId();
        articles.put(id, personId);
        contributions.put(personId, contributions.getOrDefault(personId, 0) + 1);
    }

    @Override
    public boolean containsArticle(int id) {
        return articles.containsKey(id);
    }

    @Override
    public void removeArticle(int id) {
        articles.remove(id);
    }

    @Override
    public int getBestContributor() {
        int bestId = 0;
        int maxContributions = 0;
        boolean first = true;
        for (Map.Entry<Integer, Integer> entry : contributions.entrySet()) {
            if (first) {
                bestId = entry.getKey();
                maxContributions = entry.getValue();
                first = false;
                continue;
            }

            if (entry.getValue() > maxContributions) {
                bestId = entry.getKey();
                maxContributions = entry.getValue();
            } else if (entry.getValue() == maxContributions) {
                if (entry.getKey() < bestId) {
                    bestId = entry.getKey();
                }
            }
        }
        return bestId;
    }

    public void initContributions(int personId) {
        contributions.put(personId, 0);
    }

    public void publishArticle(int articleId) {
        for (PersonInterface person : followers.values()) {
            ((Person)person).receiveArticle(articleId);
        }
    }

    public void deleteArticle(int articleId) {
        for (PersonInterface person : followers.values()) {
            ((Person)person).deleteArticle(articleId);
        }
    }

    public void minusContribution(int personId) {
        contributions.put(personId, contributions.get(personId) - 1);
    }
}
