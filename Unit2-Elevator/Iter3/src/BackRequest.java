import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

public class BackRequest extends Request {
    private final String fromFloor;
    private final PersonRequest personRequest;

    public BackRequest(String fromFloor, PersonRequest personRequest) {
        this.fromFloor = fromFloor;
        this.personRequest = personRequest;
    }

    public String getFromFloor() {
        return this.fromFloor;
    }

    public PersonRequest getPersonRequest() {
        return this.personRequest;
    }
}

