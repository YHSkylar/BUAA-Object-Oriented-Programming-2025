import com.oocourse.spec1.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.EqualTagIdException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.exceptions.TagIdNotFoundException;
import com.oocourse.spec1.main.NetworkInterface;
import com.oocourse.spec1.main.PersonInterface;
import com.oocourse.spec1.main.TagInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Network implements NetworkInterface {
    private HashMap<Integer, PersonInterface> persons;
    private int tripleSum;

    public Network() {
        persons = new HashMap<>();
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
            tripleSum += queryThree((Person) person1, (Person) person2);
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
            } else {
                ((Person)person1).delAcquaintance(person2);
                ((Person)person2).delAcquaintance(person1);
                tripleSum -= queryThree((Person) person1, (Person) person2);
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

        int bestId = 0;
        int maxValue = 0;
        boolean first = true;
        for (Map.Entry<Integer, Integer> entry : ((Person) getPerson(id)).getValues().entrySet()) {
            if (first) {
                bestId = entry.getKey();
                maxValue = entry.getValue();
                first = false;
                continue;
            }

            if (maxValue < entry.getValue()) {
                bestId = entry.getKey();
                maxValue = entry.getValue();
            } else if (maxValue == entry.getValue()) {
                if (bestId > entry.getKey()) {
                    bestId = entry.getKey();
                }
            }
        }
        return bestId;
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
