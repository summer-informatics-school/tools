import java.io.*;
import java.math.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

/**                                    
 * @author Anton Feskov and Oleg Pestov
 * @version $Id: CheatChecking.java 2008-08-11 23:13$
 */


public class Prepare implements Runnable {
    public static void main(String[] args) {
    if (args.length < 1) {
        throw new Error("Contest number expected");
    }
    cName = args[0];
    prefix = prefix + cName;
    new Thread(new Prepare()).start();
    }

    static String cName;
    static String csvPath;

    static String prefix = "contest-";
    static String rootDirPath = ".";
    static String toDirPath;

    public void run() {
    try {
        solve();
    } catch (Throwable e) {
        e.printStackTrace();
        System.exit(-1);
    }
    }

    void download() throws Exception {
    PrintWriter out = new PrintWriter("script");
    out.println("!#/bin/bash");
    out.println("");
    // out.println("open ejudge:[jhjijbltnptktysqckjy@10.0.0.35:22");
    // out.println("option transfer binary");
    String s = "000000".substring(0, 6 - cName.length()) + cName;
    out.println("mkdir -p scp-temp");
    out.println("rm -rf scp-temp/*");
    out.println("scp -r ejudge@ejudge.lksh.ru:/var/lib/ejudge/" + s + "/var/archive/runs/0 ./scp-temp/");
    out.println("ls -la scp-temp/ >> log");
    out.println("scp ejudge@ejudge.lksh.ru:/var/lib/ejudge/" + s + "/var/status/dir/external.xml ./");
    out.close();
    File temp = new File("scp-temp");
    if (temp.isDirectory()) {
        cleanUp(temp);
    }
    Process scp = Runtime.getRuntime().exec("bash script");
    scp.waitFor();
    }

    Element getChild(Node root, String path) {
    String[] s = path.split("\\/");
    for (int i = 0; i < s.length; i++) {
        NodeList nl = root.getChildNodes();
        for (int k = 0; k < nl.getLength(); k++) {
        Element e = null;
        try {
            e = (Element) nl.item(k);
            if (e.getTagName().equals(s[i])) {
            root = e;
            }
        } catch (ClassCastException ex) {
            // do nothing
        }
        }
    }
    return (Element) root;
    }

    void solve() throws Exception {
    download();

    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Element root = db.parse(new File("external.xml")).getDocumentElement();
    
    NodeList users = getChild(root, "users").getElementsByTagName("user");
    Map<Integer, String> usersMap = new HashMap<Integer, String>();
    for (int i = 0; i < users.getLength(); i++) {
        Node user = users.item(i);
        String id = user.getAttributes().getNamedItem("id").getNodeValue();
        String name = user.getAttributes().getNamedItem("name").getNodeValue();
        usersMap.put(Integer.parseInt(id), name);
    }
    
    
    NodeList problems = getChild(root, "problems").getElementsByTagName("problem");
    runMap = new HashMap<Integer, String>();
    Map<String, String> problemsMap = new HashMap<String, String>();
    for (int i = 0; i < problems.getLength(); i++) {
        Node problem = problems.item(i);
        String id = problem.getAttributes().getNamedItem("id").getNodeValue();
        String name = problem.getAttributes().getNamedItem("short_name").getNodeValue();
        problemsMap.put(id, name);
    }

    NodeList runs = getChild(root, "runs").getElementsByTagName("run");
    map = new HashMap<Integer, String>();
    for (int i = 0; i < runs.getLength(); i++) {
        Node run = runs.item(i);
        String rid = run.getAttributes().getNamedItem("run_id").getNodeValue();
        String uid = run.getAttributes().getNamedItem("user_id").getNodeValue();
        String problemName = problemsMap.get(run.getAttributes().getNamedItem("prob_id").getNodeValue());

        map.put(Integer.parseInt(rid), prefix + "-" + uid + "-" + problemName + "-" + rid);
        runMap.put(Integer.parseInt(rid), usersMap.get(Integer.parseInt(uid)));
    }

    toDir = new File("contest" + cName);
    if (toDir.isDirectory()) {
        System.err.println("cleaning up");
        cleanUp(toDir);
    }
    toDir.mkdir();

    File dir = new File("scp-temp");
    System.err.println("unzipping");
    unzip(dir);

    System.err.println("renaming");
    move(dir);
    /*
    System.err.println("compiling");
    Process compile = Runtime.getRuntime().exec("javac RunComparator.java");
    compile.waitFor();

    System.err.println("checking");
    Process checking = Runtime.getRuntime().exec("java -Xss64M -Xmx128M RunComparator contest" + cName);
    checking.waitFor();
    */
    /*
    System.err.println("cleaning up");
    File temp = new File("scp-temp");
    cleanUp(temp);
    temp = new File("script");
    temp.delete();
    (new File("external.xml")).delete();
    */

    }

    Map<Integer, String> map;
    Map<Integer, String> runMap;
    File toDir;

    void unzip(File dir) {
    for (File f : dir.listFiles()) {
        if (f.isDirectory()) {
        unzip(f);
        } else {
        String[] arr = f.getName().split("\\.");
        if (arr.length > 1 && arr[1].equals("gz")) {
            try {
            Process process = Runtime.getRuntime().exec(
                "7za x " + f.getAbsolutePath() + " -o"
                    + dir.getAbsolutePath());
            process.waitFor();
            } catch (Exception e) {
            System.err.println("Botva!!!!!!!!");
            }
            f.delete();
        }
        }
    }
    }

    void cleanUp(File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                cleanUp(f);
            } else {
                f.delete();
            }
        }
        dir.delete();
    }

    void move(File dir) {
    for (File f : dir.listFiles()) {
        if (f.isDirectory()) {
        move(f);
        f.delete();
        } else {
        if (f.getName().split("\\.").length == 1) {
            if (!map.containsKey(Integer.parseInt(f.getName()))) {
            //throw new Error("No name for this run_id " + f.getName());
            f.delete();
            } else {
            myRename(f, new File(toDir.getName() + "/" + map.get(Integer.parseInt(f.getName()))), runMap.get(Integer.parseInt(f.getName())));
            //f.renameTo(new File(toDir.getName() + "\\" + map.get(Integer.parseInt(f.getName()))));
            }
        }
        }
    }
    }

    void myRename(File f1, File f2, String uName) {
    try {
        BufferedReader br = new BufferedReader(new FileReader(f1));
        PrintWriter out = new PrintWriter(f2.getAbsolutePath(), "UTF-8");
        out.println("//" + uName + " solution");
        String s;
        while (true) {
        s = br.readLine();
        if (s == null) break;
        out.println(s);
        }
        out.close();
        br.close();
    } catch (Exception e) {
        
    }
    }
    
    int search(ArrayList<String> a, String s) {
    for (int i = 0; i < a.size(); i++) {
        if (a.get(i).equals(s))
        return i;
    }
    return -1;
    }

}
