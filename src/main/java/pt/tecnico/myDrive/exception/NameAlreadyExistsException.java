package pt.tecnico.myDrive.exception;

public class NameAlreadyExistsException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _conflictingName;

    public NameAlreadyExistsException(String conflictingName) {
        _conflictingName = conflictingName;
    }

    public String getConflictingName() { return _conflictingName; }

    @Override
    public String getMessage() {
        return "This name '" + _conflictingName + "' is already being used";
    }
}