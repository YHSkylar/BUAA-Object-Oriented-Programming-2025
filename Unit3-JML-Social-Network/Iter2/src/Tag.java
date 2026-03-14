import com.oocourse.spec2.main.PersonInterface;
import com.oocourse.spec2.main.TagInterface;

import java.util.HashMap;

public class Tag implements TagInterface {
    private int id;
    private HashMap<Integer, PersonInterface> persons;
    private int valueSum;

    public Tag(int id) {
        this.id = id;
        persons = new HashMap<>();
        valueSum = 0;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void addPerson(PersonInterface person) {
        for (PersonInterface p : persons.values()) {
            if (person.isLinked(p)) {
                valueSum += 2 * person.queryValue(p);
            }
        }
        persons.put(person.getId(), person);
    }

    @Override
    public boolean hasPerson(PersonInterface person) {
        return persons.containsKey(person.getId());
    }

    @Override
    public int getValueSum() {
        return valueSum;
    }

    @Override
    public int getAgeMean() {
        if (persons.isEmpty()) {
            return 0;
        } else {
            int sum = 0;
            for (PersonInterface person : persons.values()) {
                sum += person.getAge();
            }
            return sum / persons.size();
        }
    }

    @Override
    public int getAgeVar() {
        if (persons.isEmpty()) {
            return 0;
        } else {
            int sum = 0;
            int ageMean = getAgeMean();
            for (PersonInterface person : persons.values()) {
                int minusAge = person.getAge() - ageMean;
                sum += minusAge * minusAge;
            }
            return sum / persons.size();
        }
    }

    @Override
    public void delPerson(PersonInterface person) {
        persons.remove(person.getId());
        for (PersonInterface p : persons.values()) {
            if (person.isLinked(p)) {
                valueSum -= 2 * person.queryValue(p);
            }
        }
    }

    @Override
    public int getSize() {
        return persons.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TagInterface) {
            return ((TagInterface) obj).getId() == this.id;
        } else {
            return false;
        }
    }

    public void modifyValueSum(PersonInterface person1, PersonInterface person2,
        int value, boolean mode) {
        if (hasPerson(person1) && hasPerson(person2)) {
            if (mode) {
                valueSum += 2 * value;
            } else {
                valueSum -= 2 * value;
            }
        }
    }
}
