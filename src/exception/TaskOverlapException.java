package exception;

public class TaskOverlapException extends RuntimeException {
    public TaskOverlapException(String massage) {
        super(massage);
    }
}