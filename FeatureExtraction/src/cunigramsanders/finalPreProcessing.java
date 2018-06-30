/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cunigramsanders;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kz
 */
public class finalPreProcessing {
    
    /*
    1. englishTweetOnly.java
    2. finalPreProcessing.java
    3. CUnigramSandersWithProbability.java -> function extractFeatures
    4. divideDataLibSVM.java
    */
    
    public static HashMap<String, Double> m = new HashMap<String, Double>();
    public static HashMap<String, Double> m2 = new HashMap<String, Double>();
    
    public static Integer idx = 5;
    public static Integer number_of_words = 0;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        finalPreProcessing.englishTweetOnly();
        finalPreProcessing.finalPreProcessing();
        finalPreProcessing.extractFeatures();
        
        double percentTopic[] = {1,5,10,20};
        String numberOfTopic[] = {"1","2","3","4"};
//        double percentTopic[] = {1};
//        String numberOfTopic[] = {"1"};
        for(int b = 0; b < numberOfTopic.length; b++){
            for(int a = 0; a < percentTopic.length; a++){
                finalPreProcessing.divideDataLibSVM(percentTopic[a] , numberOfTopic[b]);
            }
            finalPreProcessing.oneTopicOnly(numberOfTopic[b]);
            finalPreProcessing.divideDataOnly(numberOfTopic[b]);
        }
        
        System.out.println("done.");
    }
    
    public static void englishTweetOnly() throws FileNotFoundException, UnsupportedEncodingException, IOException{
        try (PrintWriter writer = new PrintWriter("englishTweetOnly.txt", "UTF-8")) {
            String strLine;
            String[] splitted;
            
            // Open the file
//        FileInputStream fstream = new FileInputStream("resultText+sentiment - Copy.txt");
            File jarPath=new File(finalPreProcessing.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            String propertiesPath=jarPath.getParentFile().getAbsolutePath();
            propertiesPath += "/../";
            
            FileInputStream fstream = new FileInputStream(propertiesPath + "resultText.txt");
//            FileInputStream fstream = new FileInputStream("resultText.txt");
//            InputStream is = this.getClass().getResourceAsStream("resources/copy.csv");
            // Get the object of DataInputStream
            DataInputStream input = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(input,"UTF-8"));
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                String index="";
                splitted = strLine.split("\t");
                //lang : 5
                if(splitted[5].equals("en")){
                    writer.println(strLine);
                }
                
            } //end of while per line
        }
    }
    
    public static void finalPreProcessing() throws FileNotFoundException, UnsupportedEncodingException, IOException{
        try (PrintWriter writer = new PrintWriter("preprocessedEnglishTweetOnly.txt", "UTF-8")) {
            String strLine;
            String[] splitted;
            
            // Open the file
//        FileInputStream fstream = new FileInputStream("resultText+sentiment - Copy.txt");
            File jarPath=new File(finalPreProcessing.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            String propertiesPath=jarPath.getParentFile().getAbsolutePath();
            propertiesPath += "/../";
            FileInputStream fstream = new FileInputStream(propertiesPath+"englishTweetOnly.txt");
            // Get the object of DataInputStream
            DataInputStream input = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(input,"UTF-8"));
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                String index="";
                splitted = strLine.split("\t");
                String tweet = splitted[4];
                String preprocessedTweet = finalPreProcessing.doPreprocessing(tweet);
                writer.println(preprocessedTweet);
            } //end of while per line
        }
    }
    
    public static String doPreprocessing(String txt){ 
        //yang dilakukan preprocessing masih dictionary yang dipakai sebagai text feature
        String preprocessed = txt;
        preprocessed = finalPreProcessing.removeMention(preprocessed);
        preprocessed = finalPreProcessing.removeRT(preprocessed);
        preprocessed = finalPreProcessing.removeLinks(preprocessed);
        preprocessed = finalPreProcessing.removePunctuation(preprocessed);
        preprocessed = finalPreProcessing.allLowerCase(preprocessed);
        preprocessed = finalPreProcessing.removeNonAscii(preprocessed);
        preprocessed = finalPreProcessing.replaceDoubleSpace(preprocessed);
        return preprocessed.trim();
    }
    
    public static String replaceDoubleSpace(String txt){
        String preprocessed;
        preprocessed = txt.replaceAll(" +", " ");
        return preprocessed;
    }
    
    public static String removeMention(String txt){
        String preprocessed;
        preprocessed = txt.replaceAll("(@)\\.?[^\\s]+", " ");
        return preprocessed;
    }
    
    public static String removeRT(String txt){
        String preprocessed;
        preprocessed = txt.replaceAll("RT", " ");
        return preprocessed;
    }
    
    public static String removePunctuation(String txt){
        String preprocessed;
        preprocessed = txt.replaceAll("[.,-\\/#!?\"$%\\^&\\*;:{}=\\-_`~()]", " "); //with hashtag removal
        preprocessed = preprocessed.replaceAll("[^\\w\\s]"," ");
//        preprocessed = txt.replaceAll("[.,-\\/!$%\\^&\\*;:{}=\\-_`~()]", ""); //without hashtag removal
        return preprocessed;
    }
    
    public static String removeLinks(String txt){
        String preprocessed;
        preprocessed = txt.replaceAll("((https?|ftp|gopher|telnet|file):((//)|(\\\\\\\\))+[\\\\w\\\\d:#@%/;$()~_?\\\\+-=\\\\\\\\\\\\.&]*)", " ");
        return preprocessed;
    }
    
    public static String allLowerCase(String txt){
        String preprocessed;
        preprocessed = txt.toLowerCase();
        return preprocessed;
    }
    
    public static String removeNonAscii(String txt){
        String preprocessed;
        preprocessed = txt.replaceAll("[^\\x00-\\x7F]", " ");
        return preprocessed;
    }
    
    public static void extractFeatures() throws FileNotFoundException, UnsupportedEncodingException, IOException{
        //        FileInputStream fstreamdi = new FileInputStream("tweetOnly-Copy.txt");
        File jarPath=new File(finalPreProcessing.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String propertiesPath=jarPath.getParentFile().getAbsolutePath();
        propertiesPath += "/../";
        FileInputStream fstreamdi = new FileInputStream(propertiesPath+"preprocessedEnglishTweetOnly.txt");
        PrintWriter writer = new PrintWriter(propertiesPath+"test_output_"+idx+".txt", "UTF-8");
        
        // Get the object of DataInputStream
        DataInputStream inputdi = new DataInputStream(fstreamdi);
        BufferedReader brdi = new BufferedReader(new InputStreamReader(inputdi,"UTF-8"));
        //Read File Line By Line
        String strLinedi;

        while ((strLinedi = brdi.readLine()) != null) {
            String index="";
            String[] words = strLinedi.split("\\s");
//            if(lineSeq.length() <= 0) continue;
            for (int i = 0; i <  words.length; i++) {
                if (words[i].length() > 0) {
                    Double frequency = (Double) m.get(words[i]);
                    if(frequency == null){
                        frequency = 1.0;
                    }
                    else{
                        frequency++;
                    }
                    //System.out.println(words[i]+"#####\n");
                    m.put(words[i].toLowerCase(), frequency);
                    number_of_words++;
                }
            }
        }
        
        Set set2 = m.entrySet();
        Iterator iterator2 = set2.iterator();
        int da = 0;
        while(iterator2.hasNext()) {
            Map.Entry mentry2 = (Map.Entry)iterator2.next();
            Double mVal = (Double) mentry2.getValue();
            da++;
//            System.out.println(da + " : " + mentry2.getKey() + " : " + mVal);
            if(mVal > idx){
                da++;
//                m2.put((String) mentry2.getKey(), (Integer) mentry2.getValue());
                m2.put((String) mentry2.getKey(), 0.0);
//                System.out.println(da + " : " + mentry2.getKey() + "\n");
            }
//            index += " "+ a + ":" + mentry2.getValue();
            //System.out.println(index);
        } //end of while
       
        String strLine;
        String []splitted,final_splitted;
        
        // Open the file
//        FileInputStream fstream = new FileInputStream("resultText+sentiment - Copy.txt");
        FileInputStream fstream = new FileInputStream(propertiesPath+"englishTweetOnly.txt");
        // Get the object of DataInputStream
        DataInputStream input = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(input,"UTF-8"));
        //Read File Line By Line
        while ((strLine = br.readLine()) != null) {
            String index="";
            splitted = strLine.split("\t");
            final_splitted = splitted[4].split("\\s");
//            System.out.println(splitted[5]);
            /*-------------------FOR CLASS-----------------*/
            if(splitted[6].equals("negative")){
                index="1";
            }
            else if(splitted[6].equals("neutral")){
                index="2";
            }
            else if(splitted[6].equals("positive")){
                index="3";
            }
            else if(splitted[6].equals("irrelevant")){
                index="4";
            }
            else{
                index="4";
            }
            /*-------------------FOR CLASS-----------------*/
            
            /*-------------------FOR DATE-----------------*/
            /*
            year 5
            month 1 case
            date 2
            hour 3split0
            min 3split1
            */
            String datesInText = splitted[3];
            String[] datesSplitted = datesInText.split("\\s");
            String year = datesSplitted[5];
            String tanggal = datesSplitted[2];
            String hari = datesSplitted[0];
            String jam = datesSplitted[3];
            String[] jamSplitted = jam.split(":");
            String hour = jamSplitted[0];
            String min = jamSplitted[1];
            String bulan = datesSplitted[1];
            String month = "";
            if(bulan.equals("Jan"))  month = "1";
            else if(bulan.equals("Feb"))  month = "1";
            else if(bulan.equals("Mar"))  month = "2";
            else if(bulan.equals("Apr"))  month = "2";
            else if(bulan.equals("May"))  month = "2";
            else if(bulan.equals("Jun"))  month = "3";
            else if(bulan.equals("Jul"))  month = "3";
            else if(bulan.equals("Aug"))  month = "3";
            else if(bulan.equals("Sep"))  month = "4";
            else if(bulan.equals("Oct"))  month = "4";
            else if(bulan.equals("Nov"))  month = "4";
            else if(bulan.equals("Dec"))  month = "1";
            else month = "0";
            
            String day = "";
            if(hari.equals("Mon"))  day = "1";
            else if(hari.equals("Tue"))  day = "2";
            else if(hari.equals("Wed"))  day = "3";
            else if(hari.equals("Thu"))  day = "4";
            else if(hari.equals("Fri"))  day = "5";
            else if(hari.equals("Sat"))  day = "6";
            else if(hari.equals("Sun"))  day = "7";
            /*-------------------FOR DATE-----------------*/
            
            /*-------------------FOR TOPIC---------------*/
            String topik = splitted[7];
            String topic = "";
            if(topik.equals("apple"))  topic = "1";
            else if(topik.equals("microsoft"))  topic = "2";
            else if(topik.equals("google"))  topic = "3";
            else if(topik.equals("twitter"))  topic = "4";
            else topic = "0";
            /*-------------------FOR TOPIC---------------*/
            
            String tweets = splitted[4];
            /*------------------FOR PUNCTUATION----------*/
            //REGEX : [.,-\/#!$%\^&\*;:{}=\-_`~()]
            Pattern pattern = Pattern.compile("[.,-\\/#!$%\\^&\\*;:{}=\\-_`~()]");
            Matcher matcher = pattern.matcher(tweets);
            //.find() checks for all occurrances
            int puct = 0;
            //you will see that only 2 are the matchine string
            while (matcher.find()) {
              puct++;
            }
//            System.out.println("jumlah punctuation : "+puct);
            
            /*------------------FOR PUNCTUATION----------*/
            
            
            /*------------------FOR POSITIVE EMOTICONS----------*/
            Pattern patternemop = Pattern.compile("(\\:‑\\))|(\\:\\))|(\\:D)|(\\:o\\))|(\\:\\])|"
                    + "(\\:3)|(\\:c\\))|(\\:>)|(\\=])|(8\\))|(\\=\\))|(\\:\\))|(\\:\\})|(\\:\\^\\))|"
                    + "(\\:\\?\\))|(8D)|(\\=D)|(\\=3)|(\\:-\\)\\))|(\\:\\'\\))|(\\;\\))|(:-D)|(8-D)|"
                    + "(x-D)|(xD)|(XD)|(X-D)|(=-D)|(=-3)|(B\\^D)|(;-\\))|(:'-\\))|(\\*-\\))|(\\*\\))|"
                    + "(;-\\])|(;\\])|(;D)|(;\\^\\))|(:-,)|(:\\*)|(:\\^\\*)|(<3)|(\\(\\s'\\}\\{'\\s\\))");
            Matcher matcheremop = patternemop.matcher(tweets);
            //.find() checks for all occurrances
            int posemo = 0;
            //you will see that only 2 are the matchine string
            while (matcheremop.find()) {
              posemo++;
            }
            /*------------------FOR POSITIVE EMOTICONS----------*/
            
            /*------------------FOR NEGATIVE EMOTICONS----------*/
            Pattern patternemon = Pattern.compile("(\\>:\\[)|(:-\\()|(:\\()|(:-c)|(:c)|(:-<)|(:\\?C)|(:<)|(:-\\[)|(:\\[)|(:\\{)|(;\\()|(:-\\|\\|)|(:@)|(>:\\()|(:'-\\()|(:'\\()|(:\\|)|(:-\\|)|(</3)");
            Matcher matcheremon = patternemon.matcher(tweets);
            //.find() checks for all occurrances
            int negemo = 0;
            //you will see that only 2 are the matchine string
            while (matcheremon.find()) {
              negemo++;
            }
            
            int totalEmo = posemo - negemo;
            /*------------------FOR NEGATIVE EMOTICONS----------*/
            //System.out.println(index);
            
//            curM = null;
            HashMap<String, Double> curM = new HashMap<String, Double>();
//            curM = m2;
//            m2.keySet().removeAll(set);
            Map tmp = new HashMap(m2);
            tmp.keySet().removeAll(curM.keySet());
            curM.putAll(tmp);
            int a = 0;
            for(int i=0; i<final_splitted.length; i++){
                String word = final_splitted[i];

                Double freq = (Double) m2.get(word);
                if(freq != null){
                    //freq++;
                    //curM.put(word.toLowerCase(), freq);
                    Double nums = (Double) m.get(word);
                    Double prob = nums/number_of_words;
                    curM.put(word.toLowerCase(), prob);
                }
                else{
                    freq = 0.0;
                    continue;
                }
                //System.out.println(word);
                
                
                //index += " " + a + ":" + m.get(word);
                
            }//end of for
            
            Set set = curM.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
                a++;
                Map.Entry mentry = (Map.Entry)iterator.next();
//                index += " "+ mentry.getKey() + ":" + mentry.getValue();
                index += " "+ a + ":" + mentry.getValue();
                //System.out.println(index);
            } //end of while
            index += " " + (Integer)(a+1) + ":" + totalEmo; //1007
            index += " " + (Integer)(a+2) + ":0"; //neg //1008
            index += " " + (Integer)(a+3) + ":" + year; //1009
            index += " " + (Integer)(a+4) + ":" + month; //1010
            index += " " + (Integer)(a+5) + ":" + day; //1011
            index += " " + (Integer)(a+6) + ":" + hour; //1012
            index += " " + (Integer)(a+7) + ":" + min; //1013
            index += " " + (Integer)(a+8) + ":0"; //parents //1014
            index += " " + (Integer)(a+9) + ":0"; //children //1015
            index += " " + (Integer)(a+10) + ":" + puct; //1016
            index += " " + (Integer)(a+11) + ":" + topic; //1017
            
            writer.println(index);
        } //end of while per line
        
        System.out.println("libSVM format generated on : "+propertiesPath+"test_output_"+idx+".txt");
        
       writer.close();
    }
    
    public static void divideDataLibSVM(double percentTopic, String numberOfTopics) throws IOException{
        File jarPath=new File(finalPreProcessing.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String propertiesPath=jarPath.getParentFile().getAbsolutePath();
        propertiesPath += "/../";
        
        double training = 70; //%
        double testing = 30; //%
        String topicSelected = "1018"+numberOfTopics;
//        double percentTopic = 20; //%
//            double numOfData = 3051; //a
        double numOfData = 3045; //r
        double trainingData = Math.floor(numOfData*(training/100));

//        System.out.println("trainingData : "+trainingData);
        int testingData = (int) (numOfData - trainingData);
        String fileName = "test_output_5.txt";
        String curTraining[] = new String[(int) trainingData];
        String curTesting[] = new String[testingData];
        
        String topicName = "";
        if(numberOfTopics.equals("1")) topicName = "apple";
        else if(numberOfTopics.equals("2")) topicName = "microsoft";
        else if(numberOfTopics.equals("3")) topicName = "google";
        else if(numberOfTopics.equals("4")) topicName = "twitter";
        
        String trainingFileName = propertiesPath+"dataExtracted/forLibSVM/topic_"+topicName+"_data_training"+training+"_topic_"+percentTopic+"_percent.txt";
        String testingFileName = propertiesPath+"dataExtracted/forLibSVM/topic_"+topicName+"_data_testing"+testing+"_topic_"+percentTopic+"_percent.txt";
        
//        String trainingFileName = "../dataExtracted/forLibSVM/topic_"+topicName+"_data_training"+training+"_topic_"+percentTopic+"_percent.txt";
//        String testingFileName = "../dataExtracted/forLibSVM/topic_"+topicName+"_data_testing"+testing+"_topic_"+percentTopic+"_percent.txt";
//        String trainingFileName = "../libsvm/windows/data_training"+training+"_topic_"+percentTopic+"_percent.txt";
//        String testingFileName = "../libsvm/windows/data_testing"+testing+"_topic_"+percentTopic+"_percent.txt";
        PrintWriter writer1 = new PrintWriter(trainingFileName, "UTF-8");
        PrintWriter writer2 = new PrintWriter(testingFileName, "UTF-8");
        String strLine;
        String[] splitted;

        FileInputStream fstream = new FileInputStream(fileName);
        DataInputStream input = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(input,"UTF-8"));
        double k = 0;
        int i = 0, j = 0;
        int isStart = 0;
        int numOfTopic = 0;
        int numOfTopicTrain = 0;
        int numOfTopicTest = 0;
        String arffHeader = "";

        while ((strLine = br.readLine()) != null) {
            if(k < trainingData){
                curTraining[j] = strLine;
                j++;
                if(strLine.matches("(.*)"+topicSelected+"(.*)")) numOfTopicTrain++;
            }
            else{
                curTesting[i] = strLine;
                i++;
                if(strLine.matches("(.*)"+topicSelected+"(.*)")) numOfTopicTest++;
            }
            if(strLine.matches("(.*)"+topicSelected+"(.*)")) numOfTopic++;
            k++;
//                    System.out.println(k);
//                    System.out.println(trainingData);

        } //end of while per line
//        System.out.println("number of topic : "+numOfTopic);
//            double topicData = Math.floor(numOfTopic*(percentTopic/100));
        double topicData = Math.floor(trainingData*(percentTopic/100));
        double topicTestingData =numOfTopic - topicData;
//        System.out.println("topic data : "+topicData);
        double differentNumOfTopicData = numOfTopicTrain - topicData;
//        System.out.println("difference : "+differentNumOfTopicData);
        if(differentNumOfTopicData < 0){
            int y = 0;
            int numOfCurTopic = 0;
            for(int x = 0; x < curTesting.length; x++){
                if(curTesting[x].matches("(.*)"+topicSelected+"(.*)")){
                    numOfCurTopic++;
                    if(numOfCurTopic > topicTestingData){
                        String curDataTesting = curTesting[x];
                        int isFound = 0;
                        do{
                            if(!curTraining[y].matches("(.*)"+topicSelected+"(.*)")){
                                String curDataTraining = curTraining[y];
                                curTesting[x] = curDataTraining;
                                curTraining[y] = curDataTesting;
                                isFound = 1;
//                                System.out.println(y);
                            }
                            y++;
                        }
                        while(isFound == 0);
                    }
                }
            }
        }
        else if(differentNumOfTopicData > 0){
            int y = 0;
            int numOfCurTopic = 0;
            for(int x = 0; x < curTraining.length; x++){
                if(curTraining[x].matches("(.*)"+topicSelected+"(.*)")){
                    numOfCurTopic++;
                    if(numOfCurTopic > topicData){
                        String curDataTraining = curTraining[x];
                        int isFound = 0;
                        do{
                            if(!curTesting[y].matches("(.*)"+topicSelected+"(.*)")){
                                String curDataTesting = curTesting[y];
                                curTraining[x] = curDataTesting;
                                curTesting[y] = curDataTraining;
                                isFound = 1;
//                                System.out.println(y);
                            }
                            y++;
                        }
                        while(isFound == 0);
                    }
                }
            }
        }

        FileInputStream fstream2 = new FileInputStream(fileName);
        DataInputStream input2 = new DataInputStream(fstream2);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(input2,"UTF-8"));
        isStart = 0;
        i = 0; j = 0;
        while ((strLine = br2.readLine()) != null) {
            if(isStart != 0){
                if(differentNumOfTopicData < 0){

                }
                else if(differentNumOfTopicData > 0){

                }
                String row = strLine;
                String[] eachFeature = row.split(",");

            }
            if(strLine.equals("@data")){
                isStart = 1;
            }

        } //end of while per line

//            writer1.println(arffHeader);
//            writer2.println(arffHeader);
        for(int a = 0; a < curTraining.length; a++){
//            System.out.println("curTraining[a]");
            writer1.println(curTraining[a]);
        }
//        System.out.println("length curTesting : " + curTesting.length);
        for(int b = 0; b < curTesting.length; b++){
            writer2.println(curTesting[b]);
//            System.out.println("curTesting[b]");
        }

        writer1.close();
        writer2.close();
        System.out.println("libSVM format (data train and test divided) generated on : "+propertiesPath+"dataExtracted/forLibSVM");
        
        String percentTopicstring = String.valueOf(percentTopic);
        
        finalPreProcessing.convToArff(trainingFileName, propertiesPath+"dataExtracted/forCoTraining/Train/" + percentTopicstring + "_output_5_1_t.arff");
        finalPreProcessing.convToArff(testingFileName, propertiesPath+"dataExtracted/forCoTraining/Test/" + percentTopicstring + "_output_5_1_t.arff");
        finalPreProcessing.convToArff(testingFileName, propertiesPath+"dataExtracted/forCoTraining/Test/" + percentTopicstring + "_output_5_00.arff");
        
        System.out.println("Weka format (data train and test divided) generated on : "+propertiesPath+"dataExtracted/forCoTraining");
        
//        finalPreProcessing.convToArff(trainingFileName, "../dataExtracted/forCoTraining/Train/" + percentTopicstring + "_output_5_1_t.arff");
//        finalPreProcessing.convToArff(testingFileName, "../dataExtracted/forCoTraining/Test/" + percentTopicstring + "_output_5_1_t.arff");
//        finalPreProcessing.convToArff(testingFileName, "../dataExtracted/forCoTraining/Test/" + percentTopicstring + "_output_5_00.arff");
        
//        finalPreProcessing.convToArff(trainingFileName, "../TASC-AdaptiveCotrainMSVM-master/data/train/output_5_1_t.arff");
//        finalPreProcessing.convToArff(testingFileName, "../TASC-AdaptiveCotrainMSVM-master/data/test/day1/output_5_1_t.arff");
//        finalPreProcessing.convToArff(testingFileName, "../TASC-AdaptiveCotrainMSVM-master/data/test/day1/output_5_00.arff");
    }
    
    public static void convToArff(String libSvmFileName, String outputFile) throws FileNotFoundException, UnsupportedEncodingException, IOException{
//        String libSvmFileName = "4_data_training70.0.txt";
//        String outputFile = "output_5_1_t.arff";
        String resultArff = "";
        PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
        FileInputStream fstream = new FileInputStream(libSvmFileName);
        
        DataInputStream input = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(input,"UTF-8"));
        String strLine;
        
        String strArffFileHeader = "@RELATION tweet\n";
        writer.println(strArffFileHeader);
        
        String strArffFileAttribute = "";
        
        int j = 0;
        while ((strLine = br.readLine()) != null) {
            j++;
            String[] perFeature = strLine.split("\\s");
            int i;
            for(i = 1; i < perFeature.length; i++){
               if(j == 1){
                   strArffFileAttribute += "@ATTRIBUTE attr_"+(i-1)+" NUMERIC\n";
               }
            }
            if(j == 1){
                strArffFileAttribute += "@ATTRIBUTE class {-1,0,1,2}\n";
                writer.println(strArffFileAttribute);
            }
        }
        
        String strArffFileBody = "@data";
        writer.println(strArffFileBody);
        
        FileInputStream fstream2 = new FileInputStream(libSvmFileName);
        DataInputStream input2 = new DataInputStream(fstream2);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(input2,"UTF-8"));
        j = 0;
        while ((strLine = br2.readLine()) != null) {
            strArffFileBody = "";
            j++;
            String[] perFeature = strLine.split("\\s");
            strArffFileBody += "{";
            int k = 0;
            for(int i = 1; i < perFeature.length; i++){
               String[] perFeatureSplitted = perFeature[i].split(":");
               if(!perFeatureSplitted[1].equals("0.0")) {
                   String arffVal = perFeatureSplitted[1];
                   arffVal = arffVal.replaceAll("0.0","0");
                   arffVal = arffVal.replaceAll("1.0","1");
//                   strArffFileBody += k + " " + Double.parseDouble(perFeatureSplitted[1]) + ",";
                   strArffFileBody += k + " " + arffVal + ",";
               }
//               strArffFileBody += i + " " + perFeatureSplitted[1] + ",";
               k++;
            }
            strArffFileBody += k + " " + perFeature[0];
            strArffFileBody += "}";
//            System.out.println("\nrow : "+j);
            writer.println(strArffFileBody);
        }
        
//        resultArff = strArffFileHeader + strArffFileAttribute + strArffFileBody;
//        System.out.println(resultArff);
        
        writer.close();
    }
    
    public static void oneTopicOnly(String topics) throws FileNotFoundException, IOException {
        File jarPath=new File(finalPreProcessing.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String propertiesPath=jarPath.getParentFile().getAbsolutePath();
        propertiesPath += "/../";
        
        try (PrintWriter writer = new PrintWriter(propertiesPath+"dataExtracted/oneTopic/all/" + topics + "_topicOnly.txt", "UTF-8")) {
            String strLine;
            String[] splitted;
            String topicSelected = "1018:"+topics;
            
            FileInputStream fstream = new FileInputStream(propertiesPath +"test_output_5.txt");
            DataInputStream input = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(input,"UTF-8"));
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                if(strLine.matches("(.*)"+topicSelected+"(.*)")) writer.println(strLine);
            }
        }
    }
    
    public static void divideDataOnly(String topics) throws FileNotFoundException, IOException {
        File jarPath=new File(finalPreProcessing.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String propertiesPath=jarPath.getParentFile().getAbsolutePath();
        propertiesPath += "/../";
        
        double training = 70; //%
        double testing = 30; //%
        String topicSelected = "1018:"+topics;
        double percentTopic = 20; //%
        double numOfData = 0; //a
        String nameOfTopics = "";
        if(topics.equals("1")){
            numOfData = 899;
            nameOfTopics = "apple";
        } //r
        else if(topics.equals("2")){
            numOfData = 796;
            nameOfTopics = "microsoft";
        } //r
        else if(topics.equals("3")){
            numOfData = 794;
            nameOfTopics = "google";
        } //r\
        else if(topics.equals("4")){
            numOfData = 556;
            nameOfTopics = "twitter";
        } //r
        
        double trainingData = Math.floor(numOfData*(training/100));

//        System.out.println("trainingData : "+trainingData);
        
        int testingData = (int) (numOfData - trainingData);
//        System.out.println("testingData : "+testingData);
//        System.out.println("numOfData : "+numOfData);
        String fileName = propertiesPath +"dataExtracted/oneTopic/all/" + topics+"_topicOnly.txt";
        String curTraining[] = new String[(int) trainingData];
        String curTesting[] = new String[testingData];

        PrintWriter writer1 = new PrintWriter(propertiesPath+"dataExtracted/oneTopic/divided/" + nameOfTopics+"_data_training"+training+".txt", "UTF-8");
        PrintWriter writer2 = new PrintWriter(propertiesPath+"dataExtracted/oneTopic/divided/" + nameOfTopics+"_data_testing"+testing+".txt", "UTF-8");
        String strLine;
        String[] splitted;

        FileInputStream fstream = new FileInputStream(fileName);
        DataInputStream input = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(input,"UTF-8"));
        double k = 0;
        int i = 0, j = 0;
        int isStart = 0;
        int numOfTopic = 0;
        int numOfTopicTrain = 0;
        int numOfTopicTest = 0;
        String arffHeader = "";

        while ((strLine = br.readLine()) != null) {
            if(k < trainingData){
                curTraining[j] = strLine;
                j++;
            }
            else{
                curTesting[i] = strLine;
                i++;
            }
            k++;
        } //end of while per line

        for(int a = 0; a < curTraining.length; a++){
            writer1.println(curTraining[a]);
        }
        for(int b = 0; b < curTesting.length; b++){
            writer2.println(curTesting[b]);
        }

        writer1.close();
        writer2.close();
    }
    
}
