import com.oocourse.elevator3.TimableOutput;
import com.oocourse.elevator3.UpdateRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateDispatch implements Runnable {
    private final HashMap<Integer, PassengerQue> queueMap;
    private final Integer elevatorAId;
    private final Integer elevatorBId;
    private final UpdateRequest updateRequest;
    private final ArrayList<Integer> stopSignal;
    private final RequestScheduler scheduler;

    public UpdateDispatch(HashMap<Integer, PassengerQue> queueMap,
        Integer elevatorIdA, Integer elevatorIdB,
        UpdateRequest updateRequest, RequestScheduler requestScheduler) {
        this.queueMap = queueMap;
        this.elevatorAId = elevatorIdA;
        this.elevatorBId = elevatorIdB;
        this.updateRequest = updateRequest;
        stopSignal = new ArrayList<>();
        this.scheduler = requestScheduler;
    }

    @Override
    public synchronized void run() {
        TransferFloor transferFloor = new TransferFloor();
        queueMap.get(elevatorAId).beUpdated(updateRequest, transferFloor);
        queueMap.get(elevatorBId).beUpdated(updateRequest, transferFloor);
        while (true) {
            if (stopSignal.contains(elevatorAId) && stopSignal.contains(elevatorBId)) {
                TimableOutput.println(
                    String.format("UPDATE-BEGIN-%d-%d",elevatorAId,elevatorBId));
                stopSignal.remove(elevatorAId);
                stopSignal.remove(elevatorBId);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                TimableOutput.println(
                    String.format("UPDATE-END-%d-%d",elevatorAId,elevatorBId));
                synchronized (scheduler) {
                    scheduler.finishUpdate(elevatorAId);
                    scheduler.finishUpdate(elevatorBId);
                }
                break;
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public synchronized void elevatorStop(Integer id) {
        stopSignal.add(id);
        notifyAll();
    }
}
