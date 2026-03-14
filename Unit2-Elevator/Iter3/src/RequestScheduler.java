import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;
import com.oocourse.elevator3.ScheRequest;
import com.oocourse.elevator3.TimableOutput;
import com.oocourse.elevator3.UpdateRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestScheduler implements Runnable {
    private final RequestQue requestQue;
    private final HashMap<Integer, PassengerQue> queueMap;
    private final HashMap<Integer, Elevator> elevatorMap;
    private static final String[] floors = {"F7", "F6", "F5", "F4", "F3", "F2", "F1",
        "B1", "B2", "B3", "B4"};
    private final ArrayList<Integer> beCalledElevatorIDs;
    private final ArrayList<Integer> beUpdatedElevatorIDs;

    public RequestScheduler(RequestQue requestQue, HashMap<Integer, PassengerQue> queueMap,
        HashMap<Integer, Elevator> elevatorMap) {
        this.requestQue = requestQue;
        this.queueMap = queueMap;
        this.elevatorMap = elevatorMap;
        beCalledElevatorIDs = new ArrayList<>();
        beUpdatedElevatorIDs = new ArrayList<>();
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

            //System.out.println("getRequest");
            Request request = requestQue.removeRequest();
            //System.out.println("getRequestEnd");
            String fromFloor = null;
            PersonRequest personRequest = null;
            if (request instanceof ScheRequest) {
                ScheRequest scheRequest = (ScheRequest) request;
                int elevatorID = scheRequest.getElevatorId();
                beCalledElevatorIDs.add(elevatorID);
                queueMap.get(elevatorID).beCalled(scheRequest);
            }
            else if (request instanceof UpdateRequest) {
                update(request);
            }
            else if (request instanceof PersonRequest) {
                //System.out.println("Scheduler_Update");
                fromFloor = ((PersonRequest) request).getFromFloor();
                personRequest = (PersonRequest) request;
            }
            else if (request instanceof BackRequest) {
                fromFloor = ((BackRequest) request).getFromFloor();
                personRequest = ((BackRequest) request).getPersonRequest();
            }

            if (personRequest != null && fromFloor != null) {
                //System.out.println("Schedule");
                schedule(personRequest, fromFloor);
                //System.out.println("ScheduleEnd");
            }
        }
        //System.out.println("SchedulerEnd");
    }

    public void finishUpdate(Integer id) {
        beUpdatedElevatorIDs.remove(id);
    }

    public void finishSche(Integer id) {
        beCalledElevatorIDs.remove(id);
    }

    public void update(Request request) {
        //System.out.println("Scheduler_Update");
        UpdateRequest updateRequest = (UpdateRequest) request;
        Integer elevatorAId = updateRequest.getElevatorAId();
        Integer elevatorBId = updateRequest.getElevatorBId();
        beUpdatedElevatorIDs.add(elevatorAId);
        beUpdatedElevatorIDs.add(elevatorBId);
        UpdateDispatch updateDispatch =
            new UpdateDispatch(queueMap, elevatorAId, elevatorBId, updateRequest, this);
        Elevator elevatorA = elevatorMap.get(elevatorAId);
        synchronized (elevatorA) {
            elevatorA.setUpdateDispatch(updateDispatch);
        }
        Elevator elevatorB = elevatorMap.get(elevatorBId);
        synchronized (elevatorB) {
            elevatorB.setUpdateDispatch(updateDispatch);
        }
        Thread updateThread = new Thread(updateDispatch);
        updateThread.start();
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
            if (beUpdatedElevatorIDs.contains(i)) {
                continue;
            }
            Elevator elevator = elevatorMap.get(i);
            synchronized (elevator) {
                if (elevator.getWaitNum() > 15) {
                    continue;
                }

                int highestFloor = getFloorIndex(elevator.getHighestFloor());
                int lowestFloor = getFloorIndex(elevator.getLowestFloor());
                int fromFloorIndex = getFloorIndex(requestFloor);
                if (fromFloorIndex < highestFloor || fromFloorIndex > lowestFloor) {
                    continue;
                }
                boolean moveDirection = getDirection(requestFloor, toFloor);
                if ((fromFloorIndex == highestFloor && moveDirection)
                    || (fromFloorIndex == lowestFloor) && !moveDirection) {
                    continue;
                }
                int status = elevatorStatus(6, elevator.getCurNum(), elevator.getWaitNum());
                int distance = calDistance(requestFloor,toFloor,
                    elevator.getMoveDirection(), elevator.getCurFloor(), highestFloor, lowestFloor);
                boolean beUpdated = elevator.getBeUpdated();
                double score;
                if (beUpdated) {
                    score = calScore(status, distance, 0.2);
                }
                else {
                    score = calScore(status, distance, 0.4);
                }

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
        boolean moveDirection, String curFloor, int highestFloor, int lowestFloor) {
        boolean direction = getDirection(destination, toFloor);
        boolean move = moveDirection;
        // 获取当前楼层和目标楼层在数组中的索引
        int curIndex = getFloorIndex(curFloor);
        int desIndex = getFloorIndex(destination);

        int distance = 0;
        while (curIndex != desIndex || move != direction) {
            if (move) {
                if (curIndex == highestFloor - 1) {
                    move = false;
                    curIndex++;
                } else {
                    curIndex--;
                    distance++;
                }
            }
            else {
                if (curIndex == lowestFloor + 1) {
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

    public static int getFloorIndex(String floor) {
        for (int i = 0; i < floors.length; i++) {
            if (floors[i].equals(floor)) {
                return i; // 找到匹配的楼层，返回索引
            }
        }
        return -1; // 如果未找到，返回-1
    }
}
