package pt.tecnico.myDrive.exception;

public class InvalidFileNameException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _filename;

    public InvalidFileNameException(String filename) {
        _filename = filename;
    }

    public String getFilename() { return _filename; }

    @Override
    public String getMessage() {
        return "Invalid file name format: '" + _filename + "'";
    }
}