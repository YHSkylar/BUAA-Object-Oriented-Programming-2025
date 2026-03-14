import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;
import com.oocourse.elevator1.Request;

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
                    requestQue.addPersonRequest(personRequest);
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
