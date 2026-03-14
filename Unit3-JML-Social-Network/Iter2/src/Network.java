import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.ArticleIdNotFoundException;
import com.oocourse.spec2.exceptions.ContributePermissionDeniedException;
import com.oocourse.spec2.exceptions.DeleteArticlePermissionDeniedException;
import com.oocourse.spec2.exceptions.DeleteOfficialAccountPermissionDeniedException;
import com.oocourse.spec2.exceptions.EqualArticleIdException;
import com.oocourse.spec2.exceptions.EqualOfficialAccountIdException;
import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.EqualTagIdException;
import com.oocourse.spec2.exceptions.OfficialAccountIdNotFoundException;
import com.oocourse.spec2.exceptions.PathNotFoundException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.exceptions.TagIdNotFoundException;
import com.oocourse.spec2.main.NetworkInterface;
import com.oocourse.spec2.main.OfficialAccountInterface;
import com.oocourse.spec2.main.PersonInterface;
import com.oocourse.spec2.main.TagInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Network implements NetworkInterface {
    private HashMap<Integer, PersonInterface> persons;
    private HashMap<Integer, OfficialAccountInterface> accounts;
    private HashMap<Integer, Integer> articles;  // Key: articleId  Value: personId
    private int tripleSum;

    public Network() {
        persons = new HashMap<>();
        accounts = new HashMap<>();
        articles = new HashMap<>();
        tripleSum = 0;
    }

    @Override
    public boolean containsPerson(int id) {
        return persons.containsKey(id);
    }

    @Override
    public PersonInterface getPerson(int id) {
        if (containsPerson(id)) {
            return persons.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void addPerson(PersonInterface person) throws EqualPersonIdException {
        if (!containsPerson(person.getId())) {
            persons.put(person.getId(), person);
        } else {
            throw new EqualPersonIdException(person.getId());
        }
    }

    @Override
    public void addRelation(int id1, int id2, int value)
        throws PersonIdNotFoundException, EqualRelationException {
        if (containsPerson(id1) && containsPerson(id2) &&
            !getPerson(id1).isLinked(getPerson(id2))) {
            PersonInterface person1 = getPerson(id1);
            PersonInterface person2 = getPerson(id2);

            ((Person)person1).addAcquaintance(person2);
            ((Person)person2).addAcquaintance(person1);

            ((Person)person1).setPersonValue(id2, value);
            ((Person)person2).setPersonValue(id1, value);

            for (PersonInterface person : persons.values()) {
                ((Person)person).modifyTagValueSum(person1, person2, value, true);
            }

            tripleSum += queryThree((Person) person1, (Person) person2);

            ((Person)person1).queryBestAcquaintance(id2, value, 0);
            ((Person)person2).queryBestAcquaintance(id1, value, 0);
        } else if (!containsPerson(id1)) {
            throw new PersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new PersonIdNotFoundException(id2);
        } else {
            throw new EqualRelationException(id1,id2);
        }
    }

    @Override
    public void modifyRelation(int id1, int id2, int value)
        throws PersonIdNotFoundException, EqualPersonIdException, RelationNotFoundException {
        PersonInterface person1 = getPerson(id1);
        PersonInterface person2 = getPerson(id2);
        if (containsPerson(id1) && containsPerson(id2) && id1 != id2 && person1.isLinked(person2)) {
            if (person1.queryValue(person2) + value > 0) {
                ((Person)person1).setPersonValue(id2, person1.queryValue(person2) + value);
                ((Person)person2).setPersonValue(id1, person2.queryValue(person1) + value);

                for (PersonInterface person : persons.values()) {
                    ((Person)person).modifyTagValueSum(person1, person2, value, true);
                }

                ((Person)person1).queryBestAcquaintance(id2, person1.queryValue(person2), 1);
                ((Person)person2).queryBestAcquaintance(id1, person2.queryValue(person1), 1);
            } else {
                for (PersonInterface person : persons.values()) {
                    ((Person)person).modifyTagValueSum(person1, person2,
                        person1.queryValue(person2), false);
                }

                ((Person)person1).delAcquaintance(person2);
                ((Person)person2).delAcquaintance(person1);

                tripleSum -= queryThree((Person) person1, (Person) person2);

                ((Person)person1).queryBestAcquaintance(id2, 0, 2);
                ((Person)person2).queryBestAcquaintance(id1, 0, 2);
            }
        } else if (!containsPerson(id1)) {
            throw new PersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new PersonIdNotFoundException(id2);
        } else if (id1 == id2) {
            throw new EqualPersonIdException(id1);
        } else {
            throw new RelationNotFoundException(id1, id2);
        }
    }

    @Override
    public int queryValue(int id1, int id2)
        throws PersonIdNotFoundException, RelationNotFoundException {
        PersonInterface person1 = getPerson(id1);
        PersonInterface person2 = getPerson(id2);
        if (containsPerson(id1) && containsPerson(id2) && person1.isLinked(person2)) {
            return person1.queryValue(person2);
        } else if (!containsPerson(id1)) {
            throw new PersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new PersonIdNotFoundException(id2);
        } else {
            throw new RelationNotFoundException(id1, id2);
        }
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!containsPerson(id1)) {
            throw new PersonIdNotFoundException(id1);
        }
        if (!containsPerson(id2)) {
            throw new PersonIdNotFoundException(id2);
        }

        if (id1 == id2) {
            return true;
        }

        // 从 person1 开始 BFS
        PersonInterface person1 = getPerson(id1);
        PersonInterface person2 = getPerson(id2);
        Queue<PersonInterface> queue = new LinkedList<>();
        ArrayList<PersonInterface> visited = new ArrayList<>();
        queue.add(person1);
        visited.add(person1);

        while (!queue.isEmpty()) {
            PersonInterface current = queue.poll();
            // 遍历当前节点的所有邻居
            for (PersonInterface acquaintance : ((Person)current).getAcquaintances().values()) {
                if (acquaintance.getId() == person2.getId()) {
                    return true; // 找到目标 person2，存在路径
                }
                if (!visited.contains(acquaintance)) {
                    visited.add(acquaintance); // 标记为已访问
                    queue.add(acquaintance);    // 邻居入队
                }
            }
        }
        return false; // 遍历完未找到路径
    }

    @Override
    public int queryTripleSum() {
        return tripleSum;
    }

    @Override
    public void addTag(int personId, TagInterface tag)
        throws PersonIdNotFoundException, EqualTagIdException {
        if (containsPerson(personId) && !getPerson(personId).containsTag(tag.getId())) {
            getPerson(personId).addTag(tag);
        } else if (!containsPerson(personId)) {
            throw new PersonIdNotFoundException(personId);
        } else {
            throw new EqualTagIdException(tag.getId());
        }
    }

    @Override
    public void addPersonToTag(int personId1, int personId2, int tagId)
        throws PersonIdNotFoundException, RelationNotFoundException,
        TagIdNotFoundException, EqualPersonIdException {
        if (!containsPerson(personId1)) {
            throw new PersonIdNotFoundException(personId1);
        }
        if (!containsPerson(personId2)) {
            throw new PersonIdNotFoundException(personId2);
        }
        if (personId1 == personId2) {
            throw new EqualPersonIdException(personId1);
        }
        PersonInterface person1 = getPerson(personId1);
        PersonInterface person2 = getPerson(personId2);
        if (!person2.isLinked(person1)) {
            throw new RelationNotFoundException(personId2, personId1);
        }
        if (!person2.containsTag(tagId)) {
            throw new TagIdNotFoundException(tagId);
        }
        if (person2.getTag(tagId).hasPerson(person1)) {
            throw new EqualPersonIdException(personId1);
        }

        if (person2.getTag(tagId).getSize() <= 999) {
            person2.getTag(tagId).addPerson(person1);
        }
    }

    @Override
    public int queryTagValueSum(int personId, int tagId)
        throws PersonIdNotFoundException, TagIdNotFoundException {
        if (!containsPerson(personId)) {
            throw new PersonIdNotFoundException(personId);
        }
        if (!getPerson(personId).containsTag(tagId)) {
            throw new TagIdNotFoundException(tagId);
        }

        return getPerson(personId).getTag(tagId).getValueSum();
    }

    @Override
    public int queryTagAgeVar(int personId, int tagId)
        throws PersonIdNotFoundException, TagIdNotFoundException {
        if (containsPerson(personId) && getPerson(personId).containsTag(tagId)) {
            return getPerson(personId).getTag(tagId).getAgeVar();
        } else if (!containsPerson(personId)) {
            throw new PersonIdNotFoundException(personId);
        } else {
            throw new TagIdNotFoundException(tagId);
        }
    }

    @Override
    public void delPersonFromTag(int personId1, int personId2, int tagId)
        throws PersonIdNotFoundException, TagIdNotFoundException {
        if (!containsPerson(personId1)) {
            throw new PersonIdNotFoundException(personId1);
        }
        if (!containsPerson(personId2)) {
            throw new PersonIdNotFoundException(personId2);
        }
        PersonInterface person1 = getPerson(personId1);
        PersonInterface person2 = getPerson(personId2);
        if (!person2.containsTag(tagId)) {
            throw new TagIdNotFoundException(tagId);
        }
        if (!person2.getTag(tagId).hasPerson(person1)) {
            throw new PersonIdNotFoundException(personId1);
        }

        person2.getTag(tagId).delPerson(person1);
    }

    @Override
    public void delTag(int personId, int tagId)
        throws PersonIdNotFoundException, TagIdNotFoundException {
        PersonInterface person = getPerson(personId);
        if (!containsPerson(personId)) {
            throw new PersonIdNotFoundException(personId);
        }
        if (!person.containsTag(tagId)) {
            throw new TagIdNotFoundException(tagId);
        }

        person.delTag(tagId);
    }

    @Override
    public int queryBestAcquaintance(int id)
        throws PersonIdNotFoundException, AcquaintanceNotFoundException {
        if (!containsPerson(id)) {
            throw new PersonIdNotFoundException(id);
        }
        if (((Person)getPerson(id)).acquaintanceSize() == 0) {
            throw new AcquaintanceNotFoundException(id);
        }

        return ((Person)getPerson(id)).getBestId();
    }

    @Override
    public int queryCoupleSum() {
        int coupleSum = 0;
        for (PersonInterface person : persons.values()) {
            if (((Person)person).acquaintanceSize() == 0) {
                continue;
            }
            int id = ((Person) person).getBestId();
            if (((Person) getPerson(id)).getBestId() == person.getId()) {
                coupleSum++;
            }
        }
        return coupleSum / 2;
    }

    @Override
    public int queryShortestPath(int id1, int id2)
        throws PersonIdNotFoundException, PathNotFoundException {
        if (!containsPerson(id1)) {
            throw new PersonIdNotFoundException(id1);
        }
        if (!containsPerson(id2)) {
            throw new PersonIdNotFoundException(id2);
        }

        if (id1 == id2) {
            return 0;
        }

        PersonInterface person1 = getPerson(id1);

        // 使用队列进行BFS，并记录每个节点的距离
        Queue<PersonInterface> queue = new LinkedList<>();
        Map<PersonInterface, Integer> distanceMap = new HashMap<>();

        queue.add(person1);
        distanceMap.put(person1, 0);

        while (!queue.isEmpty()) {
            PersonInterface current = queue.poll();
            int currentDistance = distanceMap.get(current);

            // 遍历当前节点的所有邻居
            for (PersonInterface neighbor : ((Person) current).getAcquaintances().values()) {
                if (neighbor.getId() == id2) {
                    return currentDistance + 1; // 找到目标，返回最短路径长度
                }
                if (!distanceMap.containsKey(neighbor)) {
                    distanceMap.put(neighbor, currentDistance + 1);
                    queue.add(neighbor);
                }
            }
        }

        throw new PathNotFoundException(id1, id2); // 没有连通路径
    }

    @Override
    public boolean containsAccount(int id) {
        return accounts.containsKey(id);
    }

    @Override
    public void createOfficialAccount(int personId, int accountId, String name)
        throws PersonIdNotFoundException, EqualOfficialAccountIdException {
        if (!containsPerson(personId)) {
            throw new PersonIdNotFoundException(personId);
        }
        if (containsAccount(accountId)) {
            throw new EqualOfficialAccountIdException(accountId);
        }

        OfficialAccount account = new OfficialAccount(personId, accountId, name);
        account.addFollower(getPerson(personId));
        accounts.put(accountId, account);
        account.initContributions(personId);
    }

    @Override
    public void deleteOfficialAccount(int personId, int accountId)
        throws PersonIdNotFoundException, OfficialAccountIdNotFoundException,
        DeleteOfficialAccountPermissionDeniedException {
        if (!containsPerson(personId)) {
            throw new PersonIdNotFoundException(personId);
        }
        if (!containsAccount(accountId)) {
            throw new OfficialAccountIdNotFoundException(accountId);
        }
        if (accounts.get(accountId).getOwnerId() != personId) {
            throw new DeleteOfficialAccountPermissionDeniedException(personId, accountId);
        }

        accounts.remove(accountId);
    }

    @Override
    public boolean containsArticle(int id) {
        return articles.containsKey(id);
    }

    @Override
    public void contributeArticle(int personId, int accountId, int articleId)
        throws PersonIdNotFoundException, OfficialAccountIdNotFoundException,
        EqualArticleIdException, ContributePermissionDeniedException {
        if (!containsPerson(personId)) {
            throw new PersonIdNotFoundException(personId);
        }
        if (!containsAccount(accountId)) {
            throw new OfficialAccountIdNotFoundException(accountId);
        }
        if (containsArticle(articleId)) {
            throw new EqualArticleIdException(articleId);
        }
        if (!accounts.get(accountId).containsFollower(getPerson(personId))) {
            throw new ContributePermissionDeniedException(personId, articleId);
        }

        PersonInterface person = getPerson(personId);
        OfficialAccountInterface account = accounts.get(accountId);
        articles.put(articleId, personId);
        account.addArticle(person, articleId);
        ((OfficialAccount)account).publishArticle(articleId);
    }

    @Override
    public void deleteArticle(int personId, int accountId, int articleId)
        throws PersonIdNotFoundException, OfficialAccountIdNotFoundException,
        ArticleIdNotFoundException, DeleteArticlePermissionDeniedException {
        if (!containsPerson(personId)) {
            throw new PersonIdNotFoundException(personId);
        }
        if (!containsAccount(accountId)) {
            throw new OfficialAccountIdNotFoundException(accountId);
        }
        if (!accounts.get(accountId).containsArticle(articleId)) {
            throw new ArticleIdNotFoundException(articleId);
        }
        if (accounts.get(accountId).getOwnerId() != personId) {
            throw new DeleteArticlePermissionDeniedException(personId, articleId);
        }

        OfficialAccountInterface account = accounts.get(accountId);
        account.removeArticle(articleId);
        ((OfficialAccount)account).deleteArticle(articleId);
        ((OfficialAccount)account).minusContribution(articles.get(articleId));
    }

    @Override
    public void followOfficialAccount(int personId, int accountId)
        throws PersonIdNotFoundException, OfficialAccountIdNotFoundException,
        EqualPersonIdException {
        if (!containsPerson(personId)) {
            throw new PersonIdNotFoundException(personId);
        }
        if (!containsAccount(accountId)) {
            throw new OfficialAccountIdNotFoundException(accountId);
        }
        OfficialAccountInterface account = accounts.get(accountId);
        PersonInterface person = getPerson(personId);
        if (account.containsFollower(person)) {
            throw new EqualPersonIdException(personId);
        }

        account.addFollower(person);
        ((OfficialAccount)account).initContributions(personId);
    }

    @Override
    public int queryBestContributor(int id) throws OfficialAccountIdNotFoundException {
        if (!containsAccount(id)) {
            throw new OfficialAccountIdNotFoundException(id);
        }

        return accounts.get(id).getBestContributor();
    }

    @Override
    public List<Integer> queryReceivedArticles(int id) throws PersonIdNotFoundException {
        if (!containsPerson(id)) {
            throw new PersonIdNotFoundException(id);
        }

        return getPerson(id).queryReceivedArticles();
    }

    public PersonInterface[] getPersons() {
        if (persons == null) {
            return new PersonInterface[0];
        } else {
            return persons.values().toArray(new PersonInterface[0]);
        }
    }

    public int queryThree(Person person1, Person person2) {
        HashMap<Integer, PersonInterface> person1Acq = person1.getAcquaintances();
        HashMap<Integer, PersonInterface> person2Acq = person2.getAcquaintances();
        int sum = 0;
        if (person1.acquaintanceSize() < person2.acquaintanceSize()) {
            for (int id : person1Acq.keySet()) {
                if (person2Acq.containsKey(id)) {
                    sum += 1;
                }
            }
        } else {
            for (int id : person2Acq.keySet()) {
                if (person1Acq.containsKey(id)) {
                    sum += 1;
                }
            }
        }
        return sum;
    }
}
