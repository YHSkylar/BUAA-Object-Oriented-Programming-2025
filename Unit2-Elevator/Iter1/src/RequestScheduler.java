import com.oocourse.elevator1.PersonRequest;

import java.util.HashMap;

public class RequestScheduler implements Runnable {
    private final RequestQue requestQue;
    private final HashMap<Integer, PassengerQue> queueMap;

    public RequestScheduler(RequestQue requestQue, HashMap<Integer, PassengerQue> queueMap) {
        this.requestQue = requestQue;
        this.queueMap = queueMap;
    }

    @Override
    public void run() {
        while (true) {
            if (requestQue.isEmpty() && requestQue.isEnd()) {
                for (PassengerQue queue : queueMap.values()) {
                    queue.setEnd();
                }
                break;
            }

            PersonRequest personRequest = requestQue.removePersonRequest();
            if (personRequest == null) {
                continue;
            }
            schedule(personRequest);
        }
    }

    public void schedule(PersonRequest personRequest) {
        int elevatorId = personRequest.getElevatorId();
        if (elevatorId < 1 || elevatorId > 6) {
            throw new IllegalArgumentException("Invalid order type");
        }
        queueMap.get(elevatorId).take(personRequest);
    }
}
