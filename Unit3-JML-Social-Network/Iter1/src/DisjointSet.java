import java.util.HashMap;

public class DisjointSet {
    private HashMap<Integer, Integer> pre;
    private HashMap<Integer, Integer> rank;

    public DisjointSet() {
        this.pre = new HashMap<>();
        this.rank = new HashMap<>();
    }

    public void add(int id) {
        if (!pre.containsKey(id)) {
            pre.put(id, id);
            rank.put(id, 0);
        }
    }

    public int find(int id) {
        int rep = id;
        while (rep != pre.get(rep)) {
            rep = pre.get(rep);
        }

        int now = id;
        while (now != rep) {
            int before = pre.get(now);
            pre.put(now, rep);
            now = before;
        }
        return rep;
    }

    public int merge(int id1, int id2) {
        int rep1 = find(id1);
        int rep2 = find(id2);
        if (rep1 == rep2) {
            return -1;
        }

        int rank1 = rank.get(rep1);
        int rank2 = rank.get(rep2);
        if (rank1 < rank2) {
            pre.put(rep1, rep2);
        } else if (rank1 == rank2) {
            rank.put(rep1, rank1 + 1);
            pre.put(rep1, rep2);
        } else {
            pre.put(rep2,rep1);
        }
        return 0;
    }
}
