import com.oocourse.spec1.main.PersonInterface;
import com.oocourse.spec1.main.TagInterface;

import java.util.HashMap;

public class Tag implements TagInterface {
    private int id;
    private HashMap<Integer, PersonInterface> persons;

    public Tag(int id) {
        this.id = id;
        persons = new HashMap<>();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void addPerson(PersonInterface person) {
        persons.put(person.getId(), person);
    }

    @Override
    public boolean hasPerson(PersonInterface person) {
        return persons.containsKey(person.getId());
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
}
