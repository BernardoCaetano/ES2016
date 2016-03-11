package pt.tecnico.myDrive.exception;

public class FileNotFoundException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String unknownPath;

    public FileNotFoundException(String path) {
        unknownPath = path;
    }

    public String getUnknownPath() { return unknownPath; }

    @Override
    public String getMessage() {
        return "The file '" + unknownPath + "' does not exist";
    }
}