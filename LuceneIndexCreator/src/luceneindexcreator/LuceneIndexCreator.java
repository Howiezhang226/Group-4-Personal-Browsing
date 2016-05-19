/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package luceneindexcreator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.misc.HighFreqTerms;
//import org.junit.test;
//import org.junit.Assert.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.util.Bits;


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
            //Comparator<TermStats> comparator = new Comparator<TermStats>();
            Comparator<TermStats> comparator = new Comparator<TermStats>() {
                @Override
                public int compare(TermStats t1, TermStats t2) {
                //int result = t1.totalTermFreq < t2.totalTermFreq);
                        return t1.totalTermFreq < t2.totalTermFreq ? -1 : 1;
                };
            };
            
            LuceneIndexCreator lw = new LuceneIndexCreator(INDEX_PATH, JSON_FILE_PATH_WEEKLY);
            lw.createIndex();
            
            
            //lw.finishfunc();

            //Check the index has been created successfully
            Directory indexDirectory = FSDirectory.open(new File(INDEX_PATH));
            IndexReader indexReader = DirectoryReader.open(indexDirectory);

            int numDocs = indexReader.numDocs();
    /* SORTED BY DATE
            //generation of Date indexes and the associated json files of keyword freq            
            ArrayList<String> indexedDates = new ArrayList<String>();
            for ( int i = 0; i < numDocs; i++){
                Document document = indexReader.document(i);
                //indexReader.toString(i);
                String date = document.get("Date");
                if (!contains(indexedDates, date)) {
                    LuceneIndexCreator lwd = new LuceneIndexCreator(PARENT_INDEX_PATH + date, JSON_FILE_PATH_WEEKLY);
                    lwd.createSubindexDate(date);
                    indexedDates.add(date);
                }
                Directory indexDirectoryDate = FSDirectory.open(new File(PARENT_INDEX_PATH + date));
                IndexReader indexReaderDate = DirectoryReader.open(indexDirectoryDate);
                HighFreqTerms hTerms = new HighFreqTerms();
                JSONArray termResultJSONArray = new JSONArray();
                TermStats[] hTermResult = hTerms.getHighFreqTerms(indexReaderDate, 50, "content", comparator);
                //creating json object
                for (int j = 0; j < hTermResult.length; j++) {
                    JSONObject termResultJSON = new JSONObject();
                    termResultJSON.put("Term", hTermResult[j].termtext.utf8ToString());
                    termResultJSON.put("Frequency", hTermResult[j].totalTermFreq);
                    termResultJSONArray.add(termResultJSON);
                    //System.out.println("" + hTermResult[i].termtext.utf8ToString() + " " +  hTermResult[i].totalTermFreq);
                }
                //outputting json
                try(FileWriter file = new FileWriter("JSONResults/" + date + ".json")) {
                    file.write(termResultJSONArray.toJSONString());
                    System.out.println("Successfully Copied JSON Object to File...");
                    System.out.println("\nJSON Object: " + termResultJSONArray );

                }
                    //date = date.substring(5, 16).trim();
                    //System.out.println( "d=" + document.get("content"));
                    //System.out.println("date: " + date + ".");
                }
    */   
    
    //WEEK
    //generation of Date indexes and the associated json files of keyword freq                      
            ArrayList<String> indexedWeeks = new ArrayList<String>();
            //ArrayList<String> outputedWeeks = new ArrayList<String>();
            
            //creating subindexes
            for ( int i = 0; i < numDocs; i++){
                Document document = indexReader.document(i);
                //indexReader.toString(i);
                //System.out.println(document.get("Week_number"));
                //System.out.println(document.get("Date"));
                String weekNum = document.get("Week_number");
                //System.out.println(weekNum);
                if (!contains(indexedWeeks, weekNum)) {
                    LuceneIndexCreator lww = new LuceneIndexCreator(PARENT_INDEX_PATH + "week" + weekNum, JSON_FILE_PATH_WEEKLY);
                    lww.createSubindexWeek(weekNum);
                    indexedWeeks.add(weekNum);
                }
                //}
            }
            JSONArray json1 = new JSONArray();
            for (String weekNum: indexedWeeks) {
                Directory indexDirectoryWeek = FSDirectory.open(new File(PARENT_INDEX_PATH + "week" + weekNum));
                IndexReader indexReaderWeek = DirectoryReader.open(indexDirectoryWeek);
                HighFreqTerms hTerms = new HighFreqTerms();
                //JSONArray termResultJSONArray = new JSONArray();
                TermStats[] hTermResult = hTerms.getHighFreqTerms(indexReaderWeek, 100, "content", comparator);
                
                //if (!contains(outputedWeeks, weekNum)) {
                //creating json object 
                JSONObject json2 = new JSONObject();
                json2.put("Week", weekNum);
                JSONArray json3 = new JSONArray();
                for (int j = 0; j < hTermResult.length; j++) {
                    //weeklyTermFreq.put("Week_number", weekNum);
                    JSONObject json4 = new JSONObject();
                    json4.put("Term", hTermResult[j].termtext.utf8ToString());
                    json4.put("Frequency", hTermResult[j].totalTermFreq);
                    json3.add(json4);
                        //JSONObject termResultJSON = new JSONObject();
                        //termResultJSON.put("Term", hTermResult[j].termtext.utf8ToString());
                        //termResultJSON.put("Frequency", hTermResult[j].totalTermFreq);
                        //termResultJSONArray.add(termResultJSON);
                        //System.out.println("" + hTermResult[i].termtext.utf8ToString() + " " +  hTermResult[i].totalTermFreq);
                }
                json2.put("Terms", json3);
                json1.add(json2);
                //outputedWeeks.add(weekNum);
            }
            //output json
            try(FileWriter file = new FileWriter("JSONResults/allWeeklyTerms.json")) {
                file.write(json1.toJSONString());
                System.out.println("Successfully Copied JSON Object to File...");
                System.out.println("\nJSON Object: " + json1 );
            }
    
            
            // gets term freq for all docs 
            HighFreqTerms hTerms = new HighFreqTerms();
            //JSONObject termFreqJSON = new JSONObject();
            
            //JSONObject termResultJSON = new JSONObject();
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
            
            //String[] dateList = new String[0];
            
            
            //TermQuery categoryQuery = new TermQuery(new Term("Date", "23 Oct 2001")); 
            //Filter categoryFilter = new QueryWrapperFilter(categoryQuery); 
            
            //IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            //Query allDocs = new Query();
            //TopDocs hits = indexSearcher.search(allDocs, categoryFilter, 20); 
            //assertEquals("only tao te ching", 1, hits.scoreDocs.length); 
            
            /*
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
            String field = "Date";
            String line = "Date:\"23 Oct 2001\" and right";
            QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, field, analyzer);
            Query query = parser.parse(line);
            
            TopDocs results = indexSearcher.search(query, 30000);
            System.out.println(results);
            ScoreDoc[] hits = results.scoreDocs;
            
            //System.out.println(results.);
            
            for (int i=0;i<10;i++) {    
                Document doc = indexSearcher.doc(hits[i].doc);
                String path = doc.get("path");
                if (path != null) 
                    System.out.println((i+1) + ". " + path);                          
            }
            */
            
            /*
            for ( int i = 0; i < numDocs; i++){
                Document document = indexReader.document(i);
                //indexReader.toString(i);
                String date = document.get("Date");
                //date = date.substring(5, 16).trim();
                System.out.println( "d=" + document.get("content"));
                //System.out.println("date: " + date + ".");
                
               
                
                //TermDocs termDocs = indexReader.termDocs( new Term("content", "failed"));
                //Terms termVector = indexReader.getTermVector(numDocs, "content");
                //TermsEnum itr = termVector.iterator(TermsEnum.EMPTY);
                //BytesRef term = null;
                
                //while ((term = itr.next()) != null) {
                //    String termText = term.utf8ToString();
                //    Term termInstance = new Term("contents", term);
                //    long termFreq = indexReader.totalTermFreq(termInstance);
                //    long docCount = indexReader.docFreq(termInstance);
                    
                //    System.out.println("term: "+termText+", termFreq = "+termFreq+", docCount = "+docCount);
                //}
            }
            */
             
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
   

    public void createIndex(){
        JSONArray jsonObjects = parseJSONFile();
        openIndex();
        addDocuments(jsonObjects);
        //addDocumentsByDate(jsonObjects, "23 Oct 2001");
        finish();
    }
    
    public void createSubindexDate(String date){
        JSONArray jsonObjects = parseJSONFile();
        openIndex();
        //addDocuments(jsonObjects);
        addDocumentsByDate(jsonObjects, date);
        finish();
    }
    
    public void createSubindexWeek(String weekNum){
        JSONArray jsonObjects = parseJSONFile();
        openIndex();
        //addDocuments(jsonObjects);
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

        //Get the JSON file, in this case is in ~/resources/test.json
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
        /* 
            //populating email-specific stopwords
            stopWords.add("subject");stopWords.add("message");stopWords.add("sent");stopWords.add("mail");stopWords.add("re");
            stopWords.add("cc");stopWords.add("re");
            //populating common english stopwords
            stopWords.add("a");stopWords.add("about");stopWords.add("above");stopWords.add("after");stopWords.add("again");stopWords.add("against");stopWords.add("all");stopWords.add("am");stopWords.add("an");stopWords.add("and");stopWords.add("any");stopWords.add("are");stopWords.add("aren't");stopWords.add("as");stopWords.add("at");
            stopWords.add("be");stopWords.add("because");stopWords.add("been");stopWords.add("before");stopWords.add("being");stopWords.add("below");stopWords.add("between");stopWords.add("both");stopWords.add("but");stopWords.add("by");
            stopWords.add("can");stopWords.add("can't");stopWords.add("cannot");stopWords.add("could");stopWords.add("couldn't");
            stopWords.add("did");stopWords.add("didnt");stopWords.add("do");stopWords.add("does");stopWords.add("doesn't");stopWords.add("doing");stopWords.add("don't");stopWords.add("down");stopWords.add("during");
            stopWords.add("e");stopWords.add("each");
            stopWords.add("few");stopWords.add("for");stopWords.add("from");stopWords.add("further");
            stopWords.add("had");stopWords.add("hadn't");stopWords.add("has");stopWords.add("hasn't");stopWords.add("have");stopWords.add("haven't");stopWords.add("having");stopWords.add("he");stopWords.add("he'd");stopWords.add("he'll");stopWords.add("he's");stopWords.add("her");stopWords.add("here");stopWords.add("here's");stopWords.add("hers");stopWords.add("herself");stopWords.add("him");stopWords.add("himself");stopWords.add("his");stopWords.add("how");stopWords.add("how's");
            stopWords.add("i");stopWords.add("i'd");stopWords.add("i'll");stopWords.add("i'm");stopWords.add("i've");stopWords.add("if");stopWords.add("in");stopWords.add("into");stopWords.add("is");stopWords.add("isn't");stopWords.add("it");stopWords.add("it's");stopWords.add("its");stopWords.add("itself");stopWords.add("let's");
            stopWords.add("me");stopWords.add("more");stopWords.add("most");stopWords.add("mustn't");stopWords.add("my");stopWords.add("myself");
            stopWords.add("no");stopWords.add("nor");stopWords.add("not");
            stopWords.add("of");stopWords.add("off");stopWords.add("on");stopWords.add("once");stopWords.add("only");stopWords.add("or");stopWords.add("other");stopWords.add("ought");stopWords.add("our");stopWords.add("ours");
            stopWords.add("ourself");stopWords.add("ourselves");stopWords.add("out");stopWords.add("over");stopWords.add("own");
            stopWords.add("same");stopWords.add("shan't");stopWords.add("she");stopWords.add("she'd");stopWords.add("she'll");stopWords.add("she's");stopWords.add("should");stopWords.add("shouldn't");stopWords.add("so");stopWords.add("some");stopWords.add("such");
            stopWords.add("then");stopWords.add("that");stopWords.add("that's");stopWords.add("than");stopWords.add("the");stopWords.add("there");stopWords.add("theres");stopWords.add("them");stopWords.add("themselves");stopWords.add("their");stopWords.add("theres");stopWords.add("these");stopWords.add("they");stopWords.add("they'd");stopWords.add("they'll");stopWords.add("they're");stopWords.add("they've");stopWords.add("this");stopWords.add("those");stopWords.add("through");stopWords.add("to");stopWords.add("too");
            stopWords.add("under");stopWords.add("until");stopWords.add("up");
            stopWords.add("very");
            stopWords.add("was");stopWords.add("wasn't");stopWords.add("we");stopWords.add("we'd");stopWords.add("we'll");stopWords.add("we're");stopWords.add("we've");stopWords.add("were");stopWords.add("weren't");stopWords.add("what");stopWords.add("what's");stopWords.add("when");stopWords.add("when's");stopWords.add("where");stopWords.add("where's");stopWords.add("which");stopWords.add("while");stopWords.add("who");stopWords.add("whose");stopWords.add("whom");stopWords.add("why");stopWords.add("why's");stopWords.add("with");stopWords.add("will");stopWords.add("won't");stopWords.add("would");stopWords.add("wouldn't");
            stopWords.add("you");stopWords.add("you'd");stopWords.add("you'll");stopWords.add("you're");stopWords.add("you've");stopWords.add("your");stopWords.add("you've");stopWords.add("yours");stopWords.add("yourselves");stopWords.add("yourself");
        */
            //char[] stopWordList = {"a", "about"};
            //stopWords.add(stopWordList);
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
                    //Field contentField = new Field(LuceneConstants.CONTENTS, new FileReader(file));
                    //System.out.println(object.get(field));        
                    //doc.add(new Field(field, (String)object.get(field), Field.Store.YES));
                    doc.add(new TextField(field, (String)object.get(field), Field.Store.YES));
                    //doc.add(new StringField(field, (String)object.get(field), Field.Store.YES));
                }
                /*
                if(type.equals(String.class)){
                    doc.add(new StringField(field, (String)object.get(field), Field.Store.NO));
                }else if(type.equals(Long.class)){
                    doc.add(new LongField(field, (long)object.get(field), Field.Store.YES));
                }else if(type.equals(Double.class)){
                    doc.add(new DoubleField(field, (double)object.get(field), Field.Store.YES));
                }else if(type.equals(Boolean.class)){
                    doc.add(new StringField(field, object.get(field).toString(), Field.Store.YES));
                }
                */
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
            //date = date.substring(5, 16).trim();
            
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