import com.oocourse.elevator1.PersonRequest;
import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Elevator implements Runnable {
    private final int id;
    private final PassengerQue queue;
    private String curFloor;
    private int curNum;
    private boolean moveDirection; // true: 下一步上行； false: 下一步下行
    private static final int capacity = 6;
    private final ArrayList<PersonRequest> inQue = new ArrayList<>();
    private final Strategy strategy;
    private static final String[] floors = {"F7", "F6", "F5", "F4", "F3", "F2", "F1",
        "B1", "B2", "B3", "B4"};

    public Elevator(int id, PassengerQue queue) {
        this.id = id;
        this.queue = queue;
        this.curFloor = "F1";
        this.curNum = 0;
        this.moveDirection = true;
        this.strategy = new Strategy(queue);
    }

    @Override
    public void run() {
        while (true) {
            Advice advice = strategy.getAdvice(curFloor, curNum, moveDirection, capacity, inQue);

            switch (advice) {
                case OPEN:
                    openAndClose();
                    break;

                case WAIT:
                    //System.out.println("WAIT!");
                    queue.waitForRequest();
                    break;

                case OVER:
                    //System.out.println("OVER!");
                    return;

                case MOVE:
                    //System.out.println("MOVE!");
                    move();
                    break;

                case REVERSE:
                    moveDirection = !moveDirection;
                    break;

                default:
            }
        }
    }

    public void move() {
        int position = -1;
        for (int i = 0; i < floors.length; i++) {
            if (floors[i].equals(curFloor)) {
                position = i;
                break;
            }
        }

        if (moveDirection) {
            curFloor = floors[position - 1];
        }
        else {
            curFloor = floors[position + 1];
        }

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        TimableOutput.println(
            String.format("ARRIVE-%s-%d",curFloor,id));

    }

    public void openAndClose() {
        TimableOutput.println(
            String.format("OPEN-%s-%d",curFloor,id));

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        passengerOut();
        passengerIn();

        TimableOutput.println(
            String.format("CLOSE-%s-%d",curFloor,id));
    }

    public void passengerOut() {
        if (inQue.isEmpty()) {
            return;
        }

        Iterator<PersonRequest> iterator = inQue.iterator();
        while (iterator.hasNext()) {
            PersonRequest personRequest = iterator.next();
            if (personRequest.getToFloor().equals(curFloor)) {
                iterator.remove();
                TimableOutput.println(
                    String.format("OUT-%d-%s-%d", personRequest.getPersonId(), curFloor, id));
                curNum--;
            }
        }
    }

    public void passengerIn() {
        if (queue.poll(curFloor) == null) {
            return;
        }

        PriorityQueue<PersonRequest> persons = queue.poll(curFloor);
        synchronized (persons) {
            Iterator<PersonRequest> iterator = persons.iterator();
            while (curNum < capacity && iterator.hasNext()) {
                PersonRequest personRequest = iterator.next();
                boolean passengerDirection = getDirection(personRequest.getFromFloor(),
                    personRequest.getToFloor());
                if (passengerDirection == moveDirection) {
                    iterator.remove();
                    inQue.add(personRequest);
                    TimableOutput.println(String.format("IN-%d-%s-%d",
                        personRequest.getPersonId(), curFloor, id));
                    curNum++;
                }
            }
        }
    }

    private boolean getDirection(String fromFloor, String toFloor) {
        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 0; i < floors.length; i++) {
            if (floors[i].equals(fromFloor)) {
                fromIndex = i;
            }
            if (floors[i].equals(toFloor)) {
                toIndex = i;
            }
        }

        // 如果目标楼层索引小于起始楼层索引，说明是上行方向；否则是下行方向
        return toIndex < fromIndex;
    }
}
