import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;
import com.oocourse.elevator3.ScheRequest;
import com.oocourse.elevator3.UpdateRequest;

import java.io.IOException;

public class InputThread implements Runnable {
    private final RequestQue requestQue;

    public InputThread(RequestQue requestQue) {
        this.requestQue = requestQue;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                requestQue.setEnd();
                break;
            }
            else {
                if (request instanceof PersonRequest) {
                    PersonRequest personRequest = (PersonRequest) request;
                    //如果加入person，则在此将personRequest的属性全部交给person，person进入requestQue队列
                    requestQue.addPersonRequest(personRequest);
                }
                else if (request instanceof ScheRequest) {
                    ScheRequest scheRequest = (ScheRequest) request;
                    requestQue.addScheRequest(scheRequest);
                }
                else if (request instanceof UpdateRequest) {
                    UpdateRequest updateRequest = (UpdateRequest) request;
                    requestQue.addUpdateRequest(updateRequest);
                }
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("inputEnd");
    }
}
