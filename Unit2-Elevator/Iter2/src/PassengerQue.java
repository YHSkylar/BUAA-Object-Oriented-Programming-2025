import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.ScheRequest;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class PassengerQue {
    private final HashMap<String, PriorityQueue<PersonRequest>> fromFloorRequests = new HashMap<>();
    private boolean isEnd = false;
    private final String[] floors = {"F7", "F6", "F5", "F4", "F3", "F2", "F1",
        "B1", "B2", "B3", "B4"};
    private boolean beCalled = false;
    private ScheRequest scheRequest = null;

    public PassengerQue() {
        for (String floor : floors) {
            PriorityQueue<PersonRequest> queue = new PriorityQueue<>(new Comparator<PersonRequest>()
            {
                @Override
                public int compare(PersonRequest pr1, PersonRequest pr2) {
                    return Integer.compare(pr2.getPriority(), pr1.getPriority());
                }
            });
            fromFloorRequests.put(floor, queue);
        }
    }

    public synchronized void take(PersonRequest personRequest) {
        String fromFloor = personRequest.getFromFloor();
        for (String floor : floors) {
            if (fromFloor.equals(floor)) {
                PriorityQueue<PersonRequest> queue = fromFloorRequests.get(fromFloor);
                synchronized (queue) {
                    queue.add(personRequest);
                }
                break;
            }
        }
        notifyAll();
    }

    public synchronized void takeBack(PersonRequest personRequest, String fromFloor) {
        for (String floor : floors) {
            if (fromFloor.equals(floor)) {
                PriorityQueue<PersonRequest> queue = fromFloorRequests.get(fromFloor);
                synchronized (queue) {
                    queue.add(personRequest);
                }
                break;
            }
        }
        notifyAll();
    }

    public synchronized PriorityQueue<PersonRequest> poll(String curFloor) {
        PriorityQueue<PersonRequest> queue = fromFloorRequests.get(curFloor);
        notifyAll();
        if (queue.isEmpty()) {
            return null;
        }
        else {
            return queue;
        }
    }

    public synchronized void setEnd() {
        isEnd = true;
        notifyAll();
    }

    public synchronized boolean isEnd() {
        notifyAll();
        return isEnd;
    }

    public synchronized boolean isEmpty() {
        for (PriorityQueue<PersonRequest> requests : fromFloorRequests.values()) {
            if (!requests.isEmpty()) {
                notifyAll();
                return false;
            }
        }
        notifyAll();
        return true;
    }

    public synchronized void waitForRequest() {
        if (isEmpty() && !isEnd()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        notifyAll();
    }

    public synchronized int getWaitNum() {
        int sum = 0;
        for (String floor : floors) {
            sum += fromFloorRequests.get(floor).size();
        }
        notifyAll();
        return sum;
    }

    public synchronized void beCalled(ScheRequest scheRequest) {
        beCalled = true;
        this.scheRequest = scheRequest;
        notifyAll();
        //System.out.println("elevatorBeCalled: " + id);
    }

    public synchronized boolean isBeCalled() {
        notifyAll();
        return beCalled;
    }

    public synchronized ScheRequest getScheRequest() {
        notifyAll();
        return scheRequest;
    }

    public synchronized void finishCall() {
        beCalled = false;
        scheRequest = null;
        //System.out.println("finishCall_passengerQue");
        notifyAll();
    }
}
