import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import com.oocourse.elevator2.ScheRequest;
import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestScheduler implements Runnable {
    private final RequestQue requestQue;
    private final HashMap<Integer, PassengerQue> queueMap;
    private final HashMap<Integer, Elevator> elevatorMap;
    private static final String[] floors = {"F7", "F6", "F5", "F4", "F3", "F2", "F1",
        "B1", "B2", "B3", "B4"};
    private final ArrayList<Integer> beCalledElevatorIDs;

    public RequestScheduler(RequestQue requestQue, HashMap<Integer, PassengerQue> queueMap,
        HashMap<Integer, Elevator> elevatorMap) {
        this.requestQue = requestQue;
        this.queueMap = queueMap;
        this.elevatorMap = elevatorMap;
        beCalledElevatorIDs = new ArrayList<>();
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

            Request request = requestQue.removeRequest();
            String fromFloor = null;
            PersonRequest personRequest = null;
            if (request instanceof ScheRequest) {
                ScheRequest scheRequest = (ScheRequest) request;
                int elevatorID = scheRequest.getElevatorId();
                beCalledElevatorIDs.add(elevatorID);
                queueMap.get(elevatorID).beCalled(scheRequest);
            }
            else if (request instanceof PersonRequest) {
                fromFloor = ((PersonRequest) request).getFromFloor();
                personRequest = (PersonRequest) request;
            }
            else if (request instanceof BackRequest) {
                fromFloor = ((BackRequest) request).getFromFloor();
                personRequest = ((BackRequest) request).getPersonRequest();
            }

            if (personRequest != null && fromFloor != null) {
                schedule(personRequest, fromFloor);
            }
        }
        //System.out.println("SchedulerEnd");
    }

    public void finishSche(Integer id) {
        beCalledElevatorIDs.remove(id);
    }

    public void schedule(PersonRequest personRequest,String curFloor) {
        int elevatorId;
        if (curFloor == null) {
            elevatorId = bestElevator(personRequest.getFromFloor(), personRequest.getToFloor());
            if (elevatorId == 0) {
                BackRequest backRequest =
                    new BackRequest(personRequest.getFromFloor(), personRequest);
                requestQue.addBackRequest(backRequest);
                return;
            }
        }
        else {
            elevatorId = bestElevator(curFloor, personRequest.getToFloor());
            if (elevatorId == 0) {
                BackRequest backRequest = new BackRequest(curFloor, personRequest);
                requestQue.addBackRequest(backRequest);
                return;
            }
        }

        if (elevatorId < 1 || elevatorId > 6) {
            System.out.println("Invalid order type");
        }
        TimableOutput.println(
            String.format("RECEIVE-%d-%d",personRequest.getPersonId(),elevatorId));
        if (curFloor == null) {
            queueMap.get(elevatorId).take(personRequest);
        }
        else {
            queueMap.get(elevatorId).takeBack(personRequest, curFloor);
        }
    }

    public int bestElevator(String requestFloor, String toFloor) {
        int bestID = 0;
        double maxScore = -2000;
        for (int i = 1; i <= 6; i++) {
            if (beCalledElevatorIDs.contains(i)) {
                continue;
            }
            Elevator elevator = elevatorMap.get(i);
            synchronized (elevator) {
                if (elevator.getWaitNum() > 15) {
                    continue;
                }
                int status = elevatorStatus(6, elevator.getCurNum(), elevator.getWaitNum());
                int distance = calDistance(requestFloor,toFloor,
                    elevator.getMoveDirection(), elevator.getCurFloor());
                double score = calScore(status, distance, 0.4);
                if (score > maxScore) {
                    maxScore = score;
                    bestID = i;
                }
                else if (score == maxScore) {
                    bestID = Math.random() > 0.5 ? bestID : i;
                }
            }
        }
        return bestID;
    }

    public int elevatorStatus(int capacity, int inNum, int waitNum) {
        double busyIndex = capacity * 1.3 - inNum * 1.1 - waitNum;
        if (busyIndex > 2) {
            return -2;
        } else if (busyIndex > 0) {
            return 0;
        } else if (busyIndex > -3) {
            return 3;
        } else {
            return 5 + waitNum;
        }
    }

    public int calDistance(String destination, String toFloor,
        boolean moveDirection, String curFloor) {
        boolean direction = getDirection(destination, toFloor);
        boolean move = moveDirection;
        // 获取当前楼层和目标楼层在数组中的索引
        int curIndex = -1;
        int desIndex = -1;
        // 遍历楼层数组，找到当前楼层和目标楼层的索引
        for (int i = 0; i < floors.length; i++) {
            if (floors[i].equals(curFloor)) {
                curIndex = i;
            }
            if (floors[i].equals(destination)) {
                desIndex = i;
            }
        }

        int distance = 0;
        while (curIndex != desIndex || move != direction) {
            if (move) {
                if (curIndex == -1) {
                    move = false;
                    curIndex++;
                } else {
                    curIndex--;
                    distance++;
                }
            }
            else {
                if (curIndex == 11) {
                    move = true;
                    curIndex--;
                } else {
                    curIndex++;
                    distance++;
                }
            }
        }
        return distance;
    }

    public double calScore(int distance, int status, double speed) {
        return (25 - distance - status - speed * 5) / Math.sqrt(speed);
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
