package pt.tecnico.myDrive.exception;

public class FileNotFoundException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _filename;

    public FileNotFoundException(String filename) {
        _filename = filename;
    }

    public String getFilename() { return _filename; }

    @Override
    public String getMessage() {
        return "The file '" + _filename + "' does not exist";
    }
}