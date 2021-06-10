package app;

import search.*;
import models.*;
import wrappers.*;
import java.util.*;
import java.io.File;
// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppReadCritical {
    // Define a static logger variable so that it references the
    // Logger instance named "App".
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        String filePath = args.length > 0 ? args[0] : "";

        ArrayList<Instance> m9Instances = new ArrayList<>();
        File file = new File(filePath);
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine(); // m = re.findall('[0-9]+', line_list[0])
            if (line.charAt(0) != '(')
                continue;
            String critical = line.split(" ")[0];
            ArrayList<Integer> waitingTimesStr = new ArrayList<>();
            Matcher m = Pattern.compile("[0-9]+").matcher(critical);
            while (m.find()) {
                waitingTimesStr.add(Integer.parseInt(m.group()));
            }
            int[] waitingTimes = convertIntegers(waitingTimesStr);
            Instance ins = new Instance(waitingTimes);
            m9Instances.add(ins);

        }
        sc.close();
        for (Instance ins : m9Instances) {
            //System.out.println("solving :" + ins.waitingTimesToString());
            if (ins.isCritical())
                System.out.println(ins.waitingTimesToString());
               
        }

    }

    public static int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }
}
