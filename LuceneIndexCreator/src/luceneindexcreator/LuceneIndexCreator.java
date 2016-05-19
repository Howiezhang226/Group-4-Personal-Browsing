/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package luceneindexcreator;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.misc.HighFreqTerms;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.misc.TermStats;


/**
 * AnQi Liu
 */
public class LuceneIndexCreator {
    static final String PARENT_INDEX_PATH = "indexDir/";
    static final String INDEX_PATH = "indexDir/all";
    //static final String JSON_FILE_PATH = "jeff.json";
    static final String JSON_FILE_PATH_WEEKLY = "jeff_weekly.json";
    static final String STOPWORDS_LIST_PATH = "stopwords.txt";

    String indexPath = "";

    String jsonFilePath = "";

    IndexWriter indexWriter = null;

    public LuceneIndexCreator(String indexPath, String jsonFilePath) {
        this.indexPath = indexPath;
        this.jsonFilePath = jsonFilePath;
    }
    
    public static void main(String[] args) {
        try {
            Comparator<TermStats> comparator = new Comparator<TermStats>() {
                @Override
                public int compare(TermStats t1, TermStats t2) {
                    return t1.totalTermFreq < t2.totalTermFreq ? -1 : 1;
                };
            };
            
            LuceneIndexCreator lw = new LuceneIndexCreator(INDEX_PATH, JSON_FILE_PATH_WEEKLY);
            lw.createIndex();

            //Check the index has been created successfully
            Directory indexDirectory = FSDirectory.open(new File(INDEX_PATH));
            IndexReader indexReader = DirectoryReader.open(indexDirectory);

            int numDocs = indexReader.numDocs();
    /* Keywords SORTED BY DATE
     *      //generation of Date indexes and the associated json files of keyword freq            
     *      ArrayList<String> indexedDates = new ArrayList<String>();
     *      for ( int i = 0; i < numDocs; i++){
     *          Document document = indexReader.document(i);
     *          //indexRader.toString(i);
     *          String date = document.get("Date");
     *          if (!contains(indexedDates, date)) {
     *              LuceneIndexCreator lwd = new LuceneIndexCreator(PARENT_INDEX_PATH + date, JSON_FILE_PATH_WEEKLY);
     *              lwd.createSubindexDate(date);
     *              indexedDates.add(date);
     *          }
     *          Directory indexDirectoryDate = FSDirectory.open(new File(PARENT_INDEX_PATH + date));
     *          IndexReader indexReaderDate = DirectoryReader.open(indexDirectoryDate);
     *          HighFreqTerms hTerms = new HighFreqTerms();
     *          JSONArray termResultJSONArray = new JSONArray();
     *          TermStats[] hTermResult = hTerms.getHighFreqTerms(indexReaderDate, 50, "content", comparator);
     *          //creating json object
     *          for (int j = 0; j < hTermResult.length; j++) {
     *              JSONObject termResultJSON = new JSONObject();
     *              termResultJSON.put("Term", hTermResult[j].termtext.utf8ToString());
     *              termResultJSON.put("Frequency", hTermResult[j].totalTermFreq);
     *              termResultJSONArray.add(termResultJSON);
     *              //System.out.println("" + hTermResult[i].termtext.utf8ToString() + " " +  hTermResult[i].totalTermFreq);
     *          }
     *          //outputting json
     *          try(FileWriter file = new FileWriter("JSONResults/" + date + ".json")) {
     *              file.write(termResultJSONArray.toJSONString());
     *              System.out.println("Successfully Copied JSON Object to File...");
     *              System.out.println("\nJSON Object: " + termResultJSONArray );
     *
     *          }
     *              //date = date.substring(5, 16).trim();
     *              //System.out.println( "d=" + document.get("content"));
     *              //System.out.println("date: " + date + ".");
     *      }
    */   
    
    // keywords sorted by week
    //generation of Date indexes and the associated json files of keyword freq                      
            ArrayList<String> indexedWeeks = new ArrayList<String>();
            
            //creating subindexes for each week
            for ( int i = 0; i < numDocs; i++){
                Document document = indexReader.document(i);
                //System.out.println(document.get("Week_number"));
                //System.out.println(document.get("Date"));
                String weekNum = document.get("Week_number");
                //System.out.println(weekNum);
                if (!contains(indexedWeeks, weekNum)) {
                    LuceneIndexCreator lww = new LuceneIndexCreator(PARENT_INDEX_PATH + "week" + weekNum, JSON_FILE_PATH_WEEKLY);
                    lww.createSubindexWeek(weekNum);
                    indexedWeeks.add(weekNum);
                }
            }
            JSONArray json1 = new JSONArray();
            for (String weekNum: indexedWeeks) {
                Directory indexDirectoryWeek = FSDirectory.open(new File(PARENT_INDEX_PATH + "week" + weekNum));
                IndexReader indexReaderWeek = DirectoryReader.open(indexDirectoryWeek);
                HighFreqTerms hTerms = new HighFreqTerms();
                TermStats[] hTermResult = hTerms.getHighFreqTerms(indexReaderWeek, 100, "content", comparator);
                
                //creating json object 
                JSONObject json2 = new JSONObject();
                json2.put("Week", weekNum);
                JSONArray json3 = new JSONArray();
                for (int j = 0; j < hTermResult.length; j++) {
                    JSONObject json4 = new JSONObject();
                    json4.put("Term", hTermResult[j].termtext.utf8ToString());
                    json4.put("Frequency", hTermResult[j].totalTermFreq);
                    json3.add(json4);
                }
                json2.put("Terms", json3);
                json1.add(json2);
            }
            //output json
            try(FileWriter file = new FileWriter("JSONResults/allWeeklyTerms.json")) {
                file.write(json1.toJSONString());
                System.out.println("Successfully Copied JSON Object to File...");
                System.out.println("\nJSON Object: " + json1 );
            }
            
            // gets term freq for all docs 
            HighFreqTerms hTerms = new HighFreqTerms();
            JSONArray termResultJSONArray = new JSONArray();
            
            //array of termStats
            TermStats[] hTermResult = hTerms.getHighFreqTerms(indexReader, 150, "content", comparator);
            
            //creating json object
            for (int i = 0; i < hTermResult.length; i++) {
                JSONObject termResultJSON = new JSONObject();
                termResultJSON.put("Term", hTermResult[i].termtext.utf8ToString());
                termResultJSON.put("Frequency", hTermResult[i].totalTermFreq);
                termResultJSONArray.add(termResultJSON);
                //System.out.println("" + hTermResult[i].termtext.utf8ToString() + " " +  hTermResult[i].totalTermFreq);
            }
            //outputting json
            try(FileWriter file = new FileWriter("JSONResults/allTermFreq.json")) {
                file.write(termResultJSONArray.toJSONString());
                System.out.println("Successfully Copied JSON Object to File...");
                System.out.println("\nJSON Object: " + termResultJSONArray );
            }  
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createIndex(){
        JSONArray jsonObjects = parseJSONFile();
        openIndex();
        addDocuments(jsonObjects);
        finish();
    }
    
    public void createSubindexDate(String date){
        JSONArray jsonObjects = parseJSONFile();
        openIndex();
        addDocumentsByDate(jsonObjects, date);
        finish();
    }
    
    public void createSubindexWeek(String weekNum){
        JSONArray jsonObjects = parseJSONFile();
        openIndex();
        addDocumentsByWeek(jsonObjects, weekNum);
        finish();
    }
    
    public void finishfunc() {
        finish();
    }

    /**
     * Parse a Json file. The file path should be included in the constructor
     */
    public JSONArray parseJSONFile(){
        //Get the JSON file
        InputStream jsonFile =  getClass().getResourceAsStream(jsonFilePath);
        Reader readerJson = new InputStreamReader(jsonFile);

        //Parse the json file using simple-json library
        Object fileObjects= JSONValue.parse(readerJson);
        JSONArray arrayObjects=(JSONArray)fileObjects;
        
        return arrayObjects;
    }

    public boolean openIndex(){
        try {
            Directory dir = FSDirectory.open(new File(indexPath));
            
            //populating stopwords with stopwords.txt file
            CharArraySet stopWords = new CharArraySet(Version.LUCENE_CURRENT, 0, true);
            try{
                File file = new File("stopwords.txt");
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    stopWords.add(line);
                }
                fr.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_47, stopWords);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47, analyzer);

            //Always overwrite the directory
            iwc.setOpenMode(OpenMode.CREATE);
            indexWriter = new IndexWriter(dir, iwc);

            return true;
        } catch (Exception e) {
            System.err.println("Error opening the index. " + e.getMessage());

        }
        return false;

    }

    /**
     * Add documents to the index
     */
    public void addDocuments(JSONArray jsonObjects){
        for(JSONObject object : (List<JSONObject>) jsonObjects){
            //System.out.println(object);
            Document doc = new Document();
            for(String field : (Set<String>) object.keySet()){
                
                Class type = object.get(field).getClass();
                
                // adds everything to 1 field
                if (field.equalsIgnoreCase("date")) {
                    String date = (String)object.get(field);
                    date = date.substring(5, 16).trim();
                    //System.out.println(date + ".");
                    
                    doc.add(new TextField(field, date, Field.Store.YES));
                }
                else if (field.equalsIgnoreCase("Week_number")) {
                    String weekNum = (String)object.get(field);
                    //System.out.println("week: " + weekNum);
                    doc.add(new TextField(field, weekNum, Field.Store.YES));
                }
                else if (field.equalsIgnoreCase("content")) {
                    doc.add(new TextField(field, (String)object.get(field), Field.Store.YES));
                }
            }
            try {
                indexWriter.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " +  ex.getMessage());
            }
        }
    }
    
    public void addDocumentsByDate(JSONArray jsonObjects, String targetDate) {
        for(JSONObject object : (List<JSONObject>) jsonObjects){
            //System.out.println(object);
            Document doc = new Document();
            String date = (String)object.get("Date");
            date = date.substring(5, 16).trim();
            
            if (date.equalsIgnoreCase(targetDate)) {
                doc.add(new TextField("date", date, Field.Store.YES));
                doc.add(new TextField("content", (String)object.get("content"), Field.Store.YES));
            }
            
            try {
                indexWriter.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " +  ex.getMessage());
            }
        }
    }
    
    public void addDocumentsByWeek(JSONArray jsonObjects, String targetWeek) {
        for(JSONObject object : (List<JSONObject>) jsonObjects){
            //System.out.println(object);
            Document doc = new Document();
            String weekNumber = (String)object.get("Week_number");
            //System.out.println("weekNumber");
            
            if (weekNumber.equalsIgnoreCase(targetWeek)) {
                doc.add(new TextField("Week_number", weekNumber, Field.Store.YES));
                doc.add(new TextField("content", (String)object.get("content"), Field.Store.YES));
                System.out.println("week: " + weekNumber);
            }
            try {
                indexWriter.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " +  ex.getMessage());
            }
        }
    }

    /**
     * Write the document to the index and close it
     */
    public void finish(){
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
    }

    public static boolean contains(ArrayList<String> array, String target) {
        for (String string: array) {
            if (string.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }
}