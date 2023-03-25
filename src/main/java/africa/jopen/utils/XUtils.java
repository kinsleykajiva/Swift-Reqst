package africa.jopen.utils;

import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.util.Objects.isNull;

public class XUtils {

    public enum OperatingSystem {
        WINDOWS, LINUX, MAC, SOLARIS

    }
    private static OperatingSystem os = null;
    private static final String OPERATING_SYSTEM = System.getProperty("os.name").toLowerCase();

    public static OperatingSystem getOS() {
        if (os == null) {
            if (OPERATING_SYSTEM.contains("win"))
                os = OperatingSystem.WINDOWS;
            else if (OPERATING_SYSTEM.contains("nix") || OPERATING_SYSTEM.contains("nux")
                    || OPERATING_SYSTEM.contains("aix")) {
                os = OperatingSystem.LINUX;
            } else if (OPERATING_SYSTEM.contains("mac")) {
                os = OperatingSystem.MAC;
            } else if (OPERATING_SYSTEM.contains("sunos")) {
                os = OperatingSystem.SOLARIS;
            }
        }
        return os;
    }

    public static boolean isWindows() {
        return OPERATING_SYSTEM.contains("win");
    }

    public static boolean isLinux() {
        return (OPERATING_SYSTEM.contains("nix") || OPERATING_SYSTEM.contains("nux")
                || OPERATING_SYSTEM.contains("aix"));
    }

    public static boolean isMac() {
        return OPERATING_SYSTEM.contains("mac");
    }

    public static boolean isSolaris() {
        return OPERATING_SYSTEM.contains("sunos");
    }


    /**
     * Returns the current hour in format h:mm a
     *
     * @return the Returns the current hour in format h:mm a
     */
    public static String getLocalTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    /**
     * Returns the Local Date in format dd/MM/yyyy
     *
     * @return the local date in format dd/MM/yyyy
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    }
    /**
     * Creates the given File or Folder if not exists and returns the result
     *
     * @param file The absolute path of the File|Folder
     * @param isDirectory         Create DIRECTORY OR FILE ?
     * @return True if exists or have been successfully created , otherwise false
     */
    public static boolean createFileOrFolder(String path, boolean isDirectory) {
        final File file = new File(path);
        // Already exists?
        if (file.exists())
            return true;
        // Directory?
        if (isDirectory)
            return file.mkdir();
        // File?
        try {
            return file.createNewFile();
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static Optional<String> PARENT_FOLDER= Optional.empty();

    public static void loadSystem(){

        String userHomeDir = System.getProperty("user.home");

        try{
            var parentFolder = userHomeDir  + File.separator + ".africaJopen"+ File.separator +"swiftReqst";
            new File(parentFolder).mkdirs();
           // System.out.printf("The User Home Directory is %s", parentFolder);
            PARENT_FOLDER = Optional.of(parentFolder);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Run this Runnable in the JavaFX Application Thread. This method can be
     * called whether or not the current thread is the JavaFX Application
     * Thread.
     *
     * @param runnable The code to be executed in the JavaFX Application Thread.
     */
    public static void invoke (Runnable runnable) {
        if (isNull(runnable)) {
            return;
        }

        try {
            if (Platform.isFxApplicationThread()) {
                runnable.run();
            } else {
                Platform.runLater(runnable);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}
