import com.oocourse.elevator1.PersonRequest;

import java.util.ArrayList;

public class Strategy {
    private final PassengerQue queue;
    private final String[] floors = {"F7", "F6", "F5", "F4", "F3", "F2", "F1",
        "B1", "B2", "B3", "B4"};

    public Strategy(PassengerQue queue) {
        this.queue = queue;
    }

    public Advice getAdvice(String curFloor, int curNum, boolean moveDirection,
        int capacity, ArrayList<PersonRequest> in) {
        if (canOpenForOut(curFloor, in) ||
            canOpenForIn(curFloor, curNum, capacity, moveDirection)) {
            return Advice.OPEN;
        }

        if (curNum > 0) {
            return Advice.MOVE;
        }
        else {
            if (queue.isEmpty()) {
                if (queue.isEnd()) {
                    return Advice.OVER;
                }
                else {
                    return Advice.WAIT;
                }
            }

            if (hasReqInDirection(curFloor, moveDirection)) {
                return Advice.MOVE;
            }
            else {
                return Advice.REVERSE;
            }
        }
    }

    public boolean canOpenForOut(String curFloor, ArrayList<PersonRequest> in) {
        for (PersonRequest person : in) {
            if (person.getToFloor().equals(curFloor)) {
                return true;
            }
        }
        return false;
    }

    public boolean canOpenForIn(String curFloor, int curNum, int capacity, boolean moveDirection) {
        if (curNum >= capacity) {
            return false;
        }

        if (queue.poll(curFloor) == null || queue.poll(curFloor).isEmpty()) {
            return false;
        }

        for (PersonRequest request : queue.poll(curFloor)) {
            String fromFloor = request.getFromFloor();
            String toFloor = request.getToFloor();

            // 判断乘客的移动方向
            boolean passengerDirection = getDirection(fromFloor, toFloor);

            // 如果当前楼层是乘客的起始楼层，并且移动方向与电梯的移动方向相同
            if (fromFloor.equals(curFloor) && passengerDirection == moveDirection) {
                return true;
            }
        }

        return false;
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

    public boolean hasReqInDirection(String curFloor, boolean moveDirection) {
        if (moveDirection) {
            for (String floor : floors) {
                if (floor.equals(curFloor)) {
                    break;
                }

                if (queue.poll(floor) != null) {
                    return true;
                }
            }
            return false;
        }
        else {
            for (int i = floors.length - 1; i >= 0; i--) {
                String floor = floors[i];
                if (floor.equals(curFloor)) {
                    break;
                }

                if (queue.poll(floor) != null) {
                    return true;
                }
            }
            return false;
        }
    }
}
