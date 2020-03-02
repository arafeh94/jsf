package dynamicore.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SVMModel {
    public static String predictRole(String msg) {
        try {
            ProcessBuilder pb = new ProcessBuilder("C:\\Users\\Arafeh\\Documents\\Projects\\svm\\venv\\Scripts\\python.exe", "C:\\Users\\Arafeh\\Documents\\Projects\\svm\\predict_prof.py", msg);
            Process p = pb.start();
            p.waitFor();
            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String result = bri.readLine();
            p.destroy();
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
