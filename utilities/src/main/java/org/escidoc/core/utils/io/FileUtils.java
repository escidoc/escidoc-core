package org.esidoc.core.utils.io;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class FileUtils {

    private static final Random RANDOM = new Random();
    private static final long RETRY_SLEEP_MILLIS = 10;

    private static File defaultTempDirectory;

    private FileUtils() {
    }

    public static File createTempFile(final String prefix, final String suffix) throws IOException {
        return createTempFile(prefix, suffix, null, false);
    }

    public static File createTempFile(String prefix, String suffix, final File parentDir, final boolean deleteOnExit)
            throws IOException {
        final File parent = (parentDir == null) ? getDefaultTempDirectory() : parentDir;
        if(suffix == null) {
            suffix = ".tmp";
        }
        if(prefix == null) {
            prefix = "escidoc";
        } else if(prefix.length() < 3) {
            prefix += "escidoc";
        }
        final File result = File.createTempFile(prefix, suffix, parent);
        //if parentDir is null, we're in our default dir
        //which will get completely wiped on exit from our exit
        //hook.  No need to set deleteOnExit() which leaks memory.
        if(deleteOnExit && parentDir != null) {
            result.deleteOnExit();
        }
        return result;
    }

    public static synchronized File getDefaultTempDirectory() {
        if(defaultTempDirectory != null && defaultTempDirectory.exists()) {
            return defaultTempDirectory;
        }
        String tempDirectoryProperty = null;
        try {
            tempDirectoryProperty = System.getProperty(FileUtils.class.getName() + ".TempDirectory");
        } catch(SecurityException e) {
            //Ignorable, we'll use the default
        }
        if(tempDirectoryProperty != null) {
            // assume someone outside of us will manage the directory
            final File tempDirectory = new File(tempDirectoryProperty);
            if(tempDirectory.mkdirs()) {
                defaultTempDirectory = tempDirectory;
            }
        }
        if(defaultTempDirectory == null) {
            int x = RANDOM.nextInt();
            tempDirectoryProperty = System.getProperty("java.io.tmpdir");
            final File tmpDirectory = new File(tempDirectoryProperty);
            if(! tmpDirectory.exists()) {
                throw new RuntimeException("The directory " + tmpDirectory.getAbsolutePath() +
                        " does not exist, please set java.io.tempdir" + " to an existing directory");
            }
            File tempFile = new File(tempDirectoryProperty, "escidoc-tmp-" + x);
            while(! tempFile.mkdir()) {
                x = RANDOM.nextInt();
                tempFile = new File(tempDirectoryProperty, "escidoc-tmp-" + x);
            }
            defaultTempDirectory = tempFile;
            final File f2 = tempFile;
            final Thread hook = new Thread() {
                @Override
                public void run() {
                    removeDirectory(f2, true);
                }
            };
            Runtime.getRuntime().addShutdownHook(hook);
        }
        return defaultTempDirectory;
    }

    public static void removeDirectory(@NotNull final File directory) {
        removeDirectory(directory, false);
    }

    private static void removeDirectory(@NotNull final File directory, final boolean inShutdown) {
        String[] list = directory.list();
        if(list == null) {
            list = new String[0];
        }
        for(final String s : list) {
            final File file = new File(directory, s);
            if(file.isDirectory()) {
                removeDirectory(file, inShutdown);
            } else {
                delete(file, inShutdown);
            }
        }
        delete(directory, inShutdown);
    }

    public static void delete(@NotNull final File file) {
        delete(file, false);
    }

    public static void delete(@NotNull final File file, final boolean inShutdown) {
        if(! file.delete()) {
            if(isWindows()) {
                System.gc(); // Ignore FindBugs warning!
            }
            try {
                Thread.sleep(RETRY_SLEEP_MILLIS);
            } catch(final InterruptedException e) {
                // ignore exception
            }
            if(! file.delete() && ! inShutdown) {
                file.deleteOnExit();
            }
        }
    }

    private static boolean isWindows() {
        final String osName = System.getProperty("os.name").toLowerCase(Locale.US);
        return osName.contains("windows");
    }

}
