package models;

import java.io.File;

/**
 *
 * @author 8130031
 * @author 8130257
 */
public class UserDirectory {

    private Client owner;
    private File file;

    public UserDirectory(Client owner, File file) {
        this.owner = owner;
        this.file = file;
    }

    public Client getOwner() {
        return owner;
    }

    public void setOwner(Client owner) {
        this.owner = owner;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "UserDirectory{" + "owner=" + owner + ", file=" + file + '}';
    }
}
