import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;
import com.oocourse.elevator3.ScheRequest;
import com.oocourse.elevator3.UpdateRequest;

import java.util.ArrayList;

public class RequestQue {
    private final ArrayList<PersonRequest> personRequests = new ArrayList<>();
    private final ArrayList<ScheRequest> scheRequests = new ArrayList<>();
    private final ArrayList<BackRequest> backRequests = new ArrayList<>();
    private final ArrayList<UpdateRequest> updateRequests = new ArrayList<>();
    private boolean isEnd = false;
    private int inputPersonRequestCount = 0;
    private int arrivePersonRequestCount = 0;

    public synchronized void addPersonRequest(PersonRequest personRequest) {
        personRequests.add(personRequest);
        inputPersonRequestCount++;
        notifyAll();
    }

    public synchronized void addScheRequest(ScheRequest scheRequest) {
        scheRequests.add(scheRequest);
        notifyAll();
    }

    public synchronized void addBackRequest(BackRequest backRequest) {
        backRequests.add(backRequest);
        notifyAll();
    }

    public synchronized void addUpdateRequest(UpdateRequest updateRequest) {
        updateRequests.add(updateRequest);
        notifyAll();
    }

    public synchronized Request removeRequest() {
        if (personRequests.isEmpty() && backRequests.isEmpty() && scheRequests.isEmpty()
            && updateRequests.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (!scheRequests.isEmpty()) {
            notifyAll();
            return scheRequests.remove(0);
        }
        if (!updateRequests.isEmpty()) {
            notifyAll();
            return updateRequests.remove(0);
        }
        if (!backRequests.isEmpty()) {
            notifyAll();
            return backRequests.remove(0);
        }
        if (personRequests.isEmpty()) {
            notifyAll();
            return null;
        }
        notifyAll();
        return personRequests.remove(0);
    }

    public synchronized void setEnd() {
        isEnd = true;
        notifyAll();
    }

    public synchronized boolean isEnd() {
        notifyAll();
        return isEnd && inputPersonRequestCount == arrivePersonRequestCount;
    }

    public synchronized boolean isEmpty() {
        notifyAll();
        return personRequests.isEmpty() && scheRequests.isEmpty() &&
            backRequests.isEmpty() && updateRequests.isEmpty();
    }

    public synchronized void personArrive() {
        arrivePersonRequestCount += 1;
        notifyAll();
    }
}
