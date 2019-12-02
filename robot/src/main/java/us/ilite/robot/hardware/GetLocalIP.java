package us.ilite.robot.hardware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

/**
 * Utility class to get the local IP by running a netstat. 
 * @see GetLocalIP#getIp()
 */
public final class GetLocalIP {

    /**
     * In windows we want to run in the git bash shell. Update to the correct
     * directory
     */
    private static final String kWindowsShell = "C:\\Program Files\\Git\\bin\\bash";
    private static final String kWindowsCommand = "robot/src/main/resources/sampleOutput.sh";
    private static final String kUnixShell = "/bin/sh";
    private static final String UNIX_SCRIPT = "netstat -t -n | grep tcp | grep -v 127.0.0.1 | awk '{print $5}' | awk -F: '{print $1}'";

    private static final String kShell;
    private static final String kScript;

    /**
     * Regex provided by:
     * https://www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
     */
    private static final String kIpAddressRegex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static final Pattern kIPPattern = Pattern.compile(kIpAddressRegex);

    static {
        String os = System.getProperty("os.name");
        String winScript = null;
        if (os.toLowerCase().contains("windows")) {
            String userDir = System.getProperty("user.dir");

            // Git-bash uses unix file system representation.
            String userDirUnix = FilenameUtils.separatorsToUnix(userDir);

            winScript = userDirUnix + "/" + kWindowsCommand;
            kShell = kWindowsShell;
            kScript = winScript;
        } else {
            kShell = kUnixShell;
            kScript = UNIX_SCRIPT;
        }
    }

    /**
     * Method to get the local IP of the DS
     * @return
     *  An optional that will contain the IP if it was sucessfully retrieved. Otherwise it will be 
     * empty. It will never be null.
     */
    public static Optional<String> getIp() {
        //TODO - clean this up
        Optional<String> returnVal = Optional.of(getAllIps().get(0));

        return returnVal;
    }

    /**
     * @return a list of all non-localhost IP's attached to the robot at this time
     */
    public static List<String> getAllIps() {
        List<String> results = new ArrayList<>();
        ProcessBuilder procBuild = new ProcessBuilder(Arrays.asList(kShell, "-c", kScript));
        procBuild.redirectErrorStream(true);

        BufferedReader reader = null;
        try {
            Process proc = procBuild.start();
            reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            results.addAll(getIPFromInputStream(reader));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }

    /**
     * Method to extract the IP from a process's InputStreamReader. This method will
     * keep reading from the passed in reader until all IPs are found. Then the method 
     * will return the first match. If there are no IPs or any other errors, this method 
     * will return an empty list
     * 
     * @param reader the reader from the process's inputstream. If this is null, 
     * the method will return an empty optional
     */
    protected static List<String> getIPFromInputStream(BufferedReader reader) {

        if (reader != null) {
            Set<String> allLines = new LinkedHashSet<>();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = kIPPattern.matcher(line);
                    if (matcher.matches()) {
                        allLines.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return allLines.stream().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Private class so this does not get instantiated
     */
    private GetLocalIP() {

    }

    public static void main(String[] pArgs) {
        Optional<String> ip = getIp();
        if (ip.isPresent()) {
            System.out.println("IP: " + ip.get());
        } else {
            System.out.println("No IP");
        }
    }
}
