package Exceptions;


public class BBExceptions extends Exception {

    public BBExceptions(String message){
        super();
    }
    public BBExceptions(String message, Throwable cause) {
        super(message, cause);
    }

    public BBExceptions(Throwable cause) {
        super(cause);
    }
}