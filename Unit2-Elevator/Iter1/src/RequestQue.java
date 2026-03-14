import com.oocourse.elevator1.PersonRequest;

import java.util.ArrayList;

public class RequestQue {
    private final ArrayList<PersonRequest> personRequests = new ArrayList<>();
    private boolean isEnd = false;

    public synchronized void addPersonRequest(PersonRequest personRequest) {
        personRequests.add(personRequest);
        notifyAll();
    }

    public synchronized PersonRequest removePersonRequest() {
        if (personRequests.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
        return isEnd;
    }

    public synchronized boolean isEmpty() {
        notifyAll();
        return personRequests.isEmpty();
    }
}
