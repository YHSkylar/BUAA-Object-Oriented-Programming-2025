import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.ScheRequest;
import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Elevator implements Runnable {
    private final int id;
    private final PassengerQue queue;
    private final RequestQue requestQue;
    private final RequestScheduler scheduler;
    private String curFloor;
    private int curNum;
    private boolean moveDirection; // true: 下一步上行； false: 下一步下行
    private static final int capacity = 6;
    private final ArrayList<PersonRequest> inQue = new ArrayList<>();
    private final Strategy strategy;
    private static final String[] floors = {"F7", "F6", "F5", "F4", "F3", "F2", "F1",
        "B1", "B2", "B3", "B4"};

    public Elevator(int id, PassengerQue queue, RequestQue requestQue,
        RequestScheduler scheduler) {
        this.id = id;
        this.queue = queue;
        this.requestQue = requestQue;
        this.scheduler = scheduler;
        this.curFloor = "F1";
        this.curNum = 0;
        this.moveDirection = true;
        this.strategy = new Strategy(queue);
    }

    @Override
    public void run() {
        while (true) {
            Advice advice = strategy.getAdvice(curFloor, curNum, moveDirection,
                capacity, inQue);

            switch (advice) {
                case OPEN:
                    openAndClose();
                    break;

                case WAIT:
                    //System.out.println("WAIT!" + id);
                    queue.waitForRequest();
                    break;

                case OVER:
                    //System.out.println("OVER!");
                    return;

                case MOVE:
                    //System.out.println("MOVE!");
                    move(0.4);
                    break;

                case REVERSE:
                    moveDirection = !moveDirection;
                    break;

                case CALL:
                    //System.out.println("beCalled" + id);
                    temporaryCall(queue.getScheRequest());
                    break;

                default:
            }
        }
    }

    public void move(double speed) {
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
            Thread.sleep((long) (speed * 1000));
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
                    String.format("OUT-S-%d-%s-%d", personRequest.getPersonId(), curFloor, id));
                requestQue.personArrive();
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
                boolean passengerDirection = getDirection(curFloor,
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

    public void temporaryCall(ScheRequest scheRequest) {
        double speed = scheRequest.getSpeed();
        String destination = scheRequest.getToFloor();
        boolean needDirection = getDirection(curFloor, destination);
        //不同方向，所有乘客下电梯加入重排队列；相同方向，下不下再考虑
        //电梯while(move)运行到目标楼层，开门，下电梯，检修，关门，输出结束
        if (moveDirection != needDirection) {
            if (!inQue.isEmpty()) {
                TimableOutput.println(
                    String.format("OPEN-%s-%d",curFloor,id));
                allPassengerOut(); //开门，下乘客，没有关门
                TimableOutput.println(
                    String.format("CLOSE-%s-%d",curFloor,id));
            }
            moveDirection = !moveDirection;
        }
        TimableOutput.println(
            String.format("SCHE-BEGIN-%d",id));
        while (! curFloor.equals(destination)) {
            move(speed);
        }

        allReceiveBack();

        TimableOutput.println(
            String.format("OPEN-%s-%d",curFloor,id));
        if (!inQue.isEmpty()) {
            allPassengerOut();
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } //stop
        }

        TimableOutput.println(
            String.format("CLOSE-%s-%d",curFloor,id));
        TimableOutput.println(
            String.format("SCHE-END-%d",id));
        queue.finishCall();
        synchronized (scheduler) {
            scheduler.finishSche(id);
        }
    }

    public void allPassengerOut() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Iterator<PersonRequest> iterator = inQue.iterator();
        while (iterator.hasNext()) {
            PersonRequest personRequest = iterator.next();
            if (! personRequest.getToFloor().equals(curFloor)) {
                BackRequest backRequest = new BackRequest(curFloor, personRequest);
                iterator.remove();
                TimableOutput.println(
                    String.format("OUT-F-%d-%s-%d", personRequest.getPersonId(), curFloor, id));
                requestQue.addBackRequest(backRequest);
            }
            else {
                iterator.remove();
                TimableOutput.println(
                    String.format("OUT-S-%d-%s-%d", personRequest.getPersonId(), curFloor, id));
                requestQue.personArrive();
            }
            curNum--;
        }
    }

    public void allReceiveBack() {
        for (String floor : floors) {
            PriorityQueue<PersonRequest> persons = queue.poll(floor);
            if (persons != null) {
                synchronized (persons) {
                    Iterator<PersonRequest> iterator = persons.iterator();
                    while (iterator.hasNext()) {
                        PersonRequest personRequest = iterator.next();
                        BackRequest backRequest = new BackRequest(floor, personRequest);
                        requestQue.addBackRequest(backRequest);
                        iterator.remove();
                    }
                }
            }
        }
    }

    public int getCurNum() {
        return curNum;
    }

    public int getWaitNum() {
        return queue.getWaitNum();
    }

    public boolean getMoveDirection() {
        return moveDirection;
    }

    public String getCurFloor() {
        return curFloor;
    }
}
