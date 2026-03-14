import com.oocourse.elevator3.TimableOutput;

import java.util.HashMap;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        RequestQue requestQue = new RequestQue();
        HashMap<Integer, PassengerQue> queMap = new HashMap<>();
        HashMap<Integer,Elevator> elevatorMap = new HashMap<>();
        RequestScheduler requestScheduler =
            new RequestScheduler(requestQue,queMap,elevatorMap);
        Integer[] elevatorId = {1, 2, 3, 4, 5, 6};
        for (Integer id : elevatorId) {
            PassengerQue queue = new PassengerQue();
            queMap.put(id, queue);
            Elevator elevator = new Elevator(id, queue, requestQue, requestScheduler);
            elevatorMap.put(id, elevator);
            Thread elevatorThread = new Thread(elevator);
            elevatorThread.start();
        }

        Thread schedulerThread = new Thread(requestScheduler);
        schedulerThread.start();
        InputThread inputThread = new InputThread(requestQue);
        Thread threadInput = new Thread(inputThread);
        threadInput.start();
    }
}
