package pt.tecnico.myDrive.exception;

public class NameAlreadyExistsException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String conflictingName;

    public NameAlreadyExistsException(String conflictingName) {
        conflictingName = conflictingName;
    }

    public String getConflictingName() { return conflictingName; }

    @Override
    public String getMessage() {
        return "This name '" + conflictingName + "' is already being used";
    }
}