import com.oocourse.spec1.main.PersonInterface;
import com.oocourse.spec1.main.TagInterface;

import java.util.HashMap;

public class Person implements PersonInterface {
    private int id;
    private String name;
    private int age;
    private HashMap<Integer, PersonInterface> acquaintance;
    private HashMap<Integer,Integer> value;
    private HashMap<Integer, TagInterface> tags;

    public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        acquaintance = new HashMap<>();
        value = new HashMap<>();
        tags = new HashMap<>();
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

    public int acquaintanceSize() {
        return acquaintance.size();
    }

    public HashMap<Integer, PersonInterface> getAcquaintances() {
        return acquaintance;
    }

    public HashMap<Integer, Integer> getValues() {
        return value;
    }

    public boolean strictEquals(PersonInterface person) { return true; }
}
