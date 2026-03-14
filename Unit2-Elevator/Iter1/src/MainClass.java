import com.oocourse.elevator1.TimableOutput;

import java.util.HashMap;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        RequestQue requestQue = new RequestQue();
        HashMap<Integer, PassengerQue> queMap = new HashMap<>();
        RequestScheduler requestScheduler = new RequestScheduler(requestQue,queMap);
        Thread schedulerThread = new Thread(requestScheduler);
        schedulerThread.start();

        Integer[] elevatorId = {1, 2, 3, 4, 5, 6};
        for (Integer id : elevatorId) {
            PassengerQue queue = new PassengerQue();
            queMap.put(id, queue);
            Elevator elevator = new Elevator(id, queue);
            Thread elevatorThread = new Thread(elevator);
            elevatorThread.start();
        }
        InputThread inputThread = new InputThread(requestQue);
        Thread threadInput = new Thread(inputThread);
        threadInput.start();
    }
}
