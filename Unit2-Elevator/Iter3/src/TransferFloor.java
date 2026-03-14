public class TransferFloor {
    private boolean ifOccupied;

    public TransferFloor() {
        ifOccupied = false;
    }

    public synchronized void use() {
        while (ifOccupied) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ifOccupied = true;
    }

    public synchronized void finish() {
        ifOccupied = false;
        notifyAll();
    }
}
