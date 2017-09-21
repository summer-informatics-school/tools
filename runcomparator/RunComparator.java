//package geo.micro.runs;C:\Temp\sis\runcomparator\extended\RunComparator.java

import java.io.*;
import java.util.*;

/**
 * // TODO Document
 *
 * @author Georgiy Korneev
 * @version $Id: RunComparator.java 3931 2007-08-20 20:00:41Z ejudge $
 */
public class RunComparator {
    private static long COUNT = 0;
    private static int sameCount = 0 ;
    public static class Source {
        private final File file;
        private final String userId;
        private final String problemId;
        private final int runId;
        private final List<String> tokens = new ArrayList<String>();

        public Source(String pId, File file) {
            this.file = file;
            //System.out.println(file.getName());
            String[] parts = file.getName().split("-|\\.");
            userId = parts[2];
            String[] problem_id = Arrays.copyOfRange (parts, 3, parts.length - 1);
            problemId = String.join ("-", Arrays.asList (problem_id));
            // problemId = pId + "#" + '-'.join (parts[3:parts.length - 1]);
            runId = Integer.parseInt(parts[parts.length - 1]);
        }

        @Override
        public String toString() {
            return file.getName();
        }

        public void parse() throws IOException {
            clear();
            StreamTokenizer tokenizer = new StreamTokenizer(new FileReader(file));
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                switch (tokenizer.ttype) {
                    case StreamTokenizer.TT_WORD:
                        tokens.add(tokenizer.sval.toLowerCase());
                        break;
                    case StreamTokenizer.TT_NUMBER:
                        tokens.add("" + tokenizer.nval);
                        break;
                    default:
                        tokens.add(("" + (char) tokenizer.ttype).toLowerCase());
                }
            }
        }

        public void clear() {
            tokens.clear();
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 1) {
            throw new Error("Folder path expected");
        }
        Map<String, Map<String, Source>> sources = new TreeMap<String, Map<String, Source>>();
        //for (int day = 1; day <= 12; day++) {
        //    File dir = new File("day-" + day / 10 + "" + day % 10);
            String pName = args[0];
            File dir = new File(pName);
            if (!dir.exists()) {
                throw new Error("No such directory found");
            }
            for (File file : dir.listFiles()) {
                Source source = new Source(pName, file);

                Map<String, Source> problemSources = sources.get(source.problemId);
                if (problemSources == null) {
                    problemSources = new HashMap<String, Source>();
                    sources.put(source.problemId, problemSources);
                }

                Source oldSource = problemSources.get(source.userId);
                if (oldSource == null || oldSource.runId < source.runId) {
                    problemSources.put(source.userId, source);
                }
            }
        //}
        int count = 0;
        PrintWriter equalsfile = new PrintWriter("equals");
        for (Map.Entry<String, Map<String, Source>> entry : sources.entrySet()) {
            String problemId = entry.getKey();
            List<Source> problemSources = new ArrayList<Source>(entry.getValue().values());
//            if (problemId.indexOf("A") != -1) {
//            	continue;
//            }
//            if (problemId.indexOf("#B") != -1) {
//            	continue;
//            }
            System.out.println("Checking " + problemId);
            for (Source source : problemSources) {
                source.parse();
            }

            HashSet<String> blacklist = new HashSet<String>();
//            blacklist.add("198");
//            blacklist.add("176");
//            blacklist.add("180");
//            blacklist.add("199");
//            blacklist.add("200");
//            blacklist.add("211");
//            blacklist.add("216");
//            blacklist.add("160");
//            blacklist.add("157");
//            blacklist.add("174");
//            blacklist.add("207");
//            blacklist.add("190");
//            blacklist.add("221");
//            blacklist.add("179");
            HashSet<String> blackproblem = new HashSet<String>();
//            blackproblem.add("A");
                     
            for (int i = 0; i < problemSources.size(); i++) {
                for (int j = i + 1; j < problemSources.size(); j++) {
                    Source source1 = problemSources.get(i);
                    Source source2 = problemSources.get(j);
                    if (sourceEquals2(source1.tokens, source2.tokens)) {
                    	boolean flag = false;
                    	String ts1 = source1.file.getAbsolutePath();
                    	String ts2 = source2.file.getAbsolutePath();
                    	int t1 = ts1.lastIndexOf("-");
                    	int tt1 = t1;
                    	t1 = ts1.lastIndexOf("-", t1 - 1);
                    	int ttt1 = t1;
                    	t1 = ts1.lastIndexOf("-", t1 - 1);
                    	int t2 = ts2.lastIndexOf("-");
                    	int tt2 = t2;
                    	t2 = ts2.lastIndexOf("-", t2 - 1);
                    	int ttt2 = t2;
                    	t2 = ts2.lastIndexOf("-", t2 - 1);
                    	if (blacklist.contains(ts2.substring(t2 + 1, t2 + 4)) && blacklist.contains(ts1.substring(t1 + 1, t1 + 4))) {
                    		flag = true;
                    	}
			if (blackproblem.contains(ts1.substring(ttt1 + 1, tt1)) && blackproblem.contains(ts2.substring(ttt2 + 1, tt2))) {
				flag = true;
			}
                        System.out.println(source1.file.getAbsolutePath() + " == " + source2.file.getAbsolutePath());
                        equalsfile.println(source1.file.getAbsolutePath() + " == " + source2.file.getAbsolutePath());
                        if (flag) {
                        	continue;
                        }

                       // System.out.println("Executing KDiff3");    C:/Program Files/KDiff3/
                        // Process process = Runtime.getRuntime().exec("kdiff3 --cs \"EncodingForA=UTF-8\" --cs \"EncodingForB=UTF-8\" " + source1.file.getAbsolutePath() + " " + source2.file.getAbsolutePath());
                        Process process = Runtime.getRuntime().exec("kdiff3 " + source1.file.getAbsolutePath() + " " + source2.file.getAbsolutePath());
                        process.waitFor();
                        sameCount++;
                    }
                    count++;
                }
            }

            for (Source source : problemSources) {
                source.clear();
            }
        }
        equalsfile.close();
        System.out.println(count);
        System.out.println(sameCount);
//        System.out.println(COUNT);
    }

    private static boolean sourceEquals(List<String> tokens1, List<String> tokens2) {
        Map<String, String> remap = new HashMap<String, String>();
        Iterator<String> iterator1 = tokens1.iterator();
        Iterator<String> iterator2 = tokens2.iterator();

        int diff = 0;
        while (iterator1.hasNext() && iterator2.hasNext()) {
            String token1 = iterator1.next();
            String token2 = iterator2.next();
            if (!token1.equals(token2)) {
                String value = remap.get(token1);
                if (value == null) {
                    remap.put(token1, token2);
                } else {
                    if (!value.equals(token2)) {
                        diff++;
                    }
                }
            }
        }
        while (iterator1.hasNext()) {
            iterator1.next();
            diff++;
        }
        while (iterator2.hasNext()) {
            iterator2.next();
            diff++;
        }
//        System.out.println(diff / (tokens1.size() + tokens2.size() + 0.0));
        COUNT += tokens1.size() * tokens2.size();
        return diff / (tokens1.size() + tokens2.size() + 0.0) < 0.3;
    }
    private static boolean sourceEquals2(List<String> tokens1, List<String> tokens2) {
        int[][] a = new int[tokens1.size() + 1][tokens2.size() + 1];
        for (int i = 0; i < tokens1.size(); i++) {
            for (int j = 0; j < tokens2.size(); j++) {
                if (tokens1.get(i).equals(tokens2.get(j))) {
                    a[i + 1][j + 1] = a[i][j] + 1;
                } else {
                    a[i + 1][j + 1] = Math.max(a[i + 1][j], a[i][j + 1]);
                }
            }
        }

        int diff = tokens1.size() + tokens2.size() - 2 * a[tokens1.size()][tokens2.size()];
//        System.out.println(diff / (tokens1.size() + tokens2.size() + 0.0));
        double eval = diff / (tokens1.size() + tokens2.size() + 0.0);
        if (tokens1.size() + tokens2.size() <= 2000) {
            eval *= 1.5;
        }
        return (eval <= 0.23);
    }
    private static boolean sourceEquals3(List<String> tokens1, List<String> tokens2) {
        return sourceEquals(tokens1, tokens2) || sourceEquals2(tokens1, tokens2);
    }
}
              
