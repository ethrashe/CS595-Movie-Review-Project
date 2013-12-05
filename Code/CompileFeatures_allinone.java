package ethrashe.cs.iit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class CompileFeatures_allinone {
	
	static Vector<String> features = new Vector<String>(); //title, features
	static int azCount = 0;
	static int balCount = 0;
	static int rtCount = 0;
	public static void main(String[] args) throws Exception {
		String csListaz = "";
		String l90Listaz = "";
		String decadeListaz = "";
		String yearListaz = "";
		
		String csListrt = "";
		String l90Listrt = "";
		String decadeListrt = "";
		String yearListrt = "";
		
		String outputFolder = "";
		
		if(args.length == 9){
			csListaz = args[0];
			l90Listaz = args[1];
			yearListaz = args[2];
			decadeListaz = args[3];

			csListrt = args[4];
			l90Listrt = args[5];
			yearListrt = args[6];
			decadeListrt = args[7];

			outputFolder = args[8];
		}
		else{
			System.out.println("Needs arguments:");
			System.out.println("1. Coming Soon Amazon Data File");
			System.out.println("2. Last 90 Days Amazon Data File");
			System.out.println("3. Last Year Amazon Data File");
			System.out.println("4. Last Decade Amazon Data File");
			System.out.println("5. Coming Soon Rotten Tomatoes Data File");
			System.out.println("6. Last 90 Days Rotten Tomatoes Data File");
			System.out.println("7. Last Year Rotten Tomatoes Data File");
			System.out.println("8. Last Decade Rotten Tomatoes Data File");
			System.out.println("9. Output Folder");
		}
		
		features = new Vector<String>(); 
		String csoutputFile =outputFolder+"csFeatures.arff";	
		HashMap<String, String> azcsfeatures = azWordCounts(csListaz);
		HashMap<String, String> rtcsfeatures = rtWordCounts(csListrt);
		//createArffFile(csoutputFile, azcsfeatures, rtcsfeatures);
		System.out.println(csoutputFile);
	
		//features = new Vector<String>(); 
		String l90outputFile = outputFolder+"l90Features.arff";	
		HashMap<String, String> azl90features = azWordCounts(l90Listaz);
		HashMap<String, String> rtl90features = rtWordCounts(l90Listrt);
		//createArffFile(l90outputFile, azl90features, rtl90features);
		System.out.println(l90outputFile);
	
		//features = new Vector<String>(); 
		String decadeoutputFile = outputFolder+"decadeFeatures.arff";	
		HashMap<String, String> azdecadefeatures = azWordCounts(decadeListaz);
		HashMap<String, String> rtdecadefeatures = rtWordCounts(decadeListrt);
		//createArffFile(decadeoutputFile, azdecadefeatures, rtdecadefeatures);
		System.out.println(decadeoutputFile);

		//features = new Vector<String>(); 
		String yearoutputFile = outputFolder+"yearFeatures.arff";	
		HashMap<String, String> azyearfeatures = azWordCounts(yearListaz);
		HashMap<String, String> rtyearfeatures = rtWordCounts(yearListrt);
		//createArffFile(yearoutputFile, azyearfeatures, rtyearfeatures);
		System.out.println(yearoutputFile);
	
		HashMap<String,String> allAmazon = new HashMap<String, String>();
		allAmazon.putAll(azcsfeatures);
		allAmazon.putAll(azl90features);
		allAmazon.putAll(azyearfeatures);
		allAmazon.putAll(azdecadefeatures);
		
		HashMap<String,String> allRT = new HashMap<String, String>();
		allRT.putAll(rtcsfeatures);
		allRT.putAll(rtl90features);
		allRT.putAll(rtyearfeatures);
		allRT.putAll(rtdecadefeatures);
		
		String outputFile = outputFolder+"allFeatures_even.arff";	
		createArffFile(outputFile, allAmazon, allRT);
		
	}
	
	public static HashMap<String, String> azWordCounts(String azInput) throws Exception{

		BufferedReader br = new BufferedReader(new FileReader(azInput));
		String line = br.readLine();
		HashMap<String, String> data = new HashMap<String, String>();
		while(line.length() != 0){
			line = br.readLine();
		}
		String title = "";
		String dataLine = "";
		String date = "";
		HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
		Iterator<String> fit = features.iterator();
		while(fit.hasNext()){
			String word = fit.next();
			wordCount.put(word, 0);
		}
		
		while(line != null){
			System.out.println(line);

			if(line.length() == 0 && title.length() != 0){				
				fit = features.iterator();
				while(fit.hasNext()){
					String word = fit.next();
					int count = wordCount.get(word);
					//System.out.print(word+",");
					dataLine = dataLine+","+count;
					wordCount.put(word, 0);

				}
				//System.out.println(features.toString());
	
				//System.out.println(dataLine);
				//System.out.println("az: "+title);
				data.put(title, dataLine);
				title = "";
				date = "";
				dataLine = "";
			}
			else if(line.contains("Buy new")){
			
				if(line.contains("~")){
					title = line.substring(line.indexOf(".")+1, line.indexOf("~")).trim();
				}
				else{
					title =  line.substring(line.indexOf(".")+1, line.indexOf("Buy new")).trim();
				}
				while(title.contains("(") || title.contains("[")){
					int index = 0;
					if(title.contains("(")){
						index = title.indexOf("(");
					}
					else{
						index = title.indexOf("[");
					}
					
					if(title.contains(")")){
						title = title.replace(title.substring(index, title.indexOf(")")+1), "").trim();
					}
					else if(title.contains("]")){
						title = title.replace(title.substring(index, title.indexOf("]")+1), "").trim();
					}
				}
				if(title.contains(", ")){
					title = title.replace(", ",""); 
				}
				if(line.contains("(DVD - ")){
					int marker = line.indexOf("(DVD - ")+7;
					date = line.substring(marker, marker+4);
				}
				
				dataLine = title+" ("+date+")";
			}
			else if (line.contains("people found the following review helpful") || line.contains("This review is from:") ){
				line = line.substring(line.indexOf("This review is from: ")+21, line.indexOf(" Help other customers find the most helpful reviews"));
				line = line.replace(title, "").trim();
				//System.out.println(line);
				String[] words = line.split(" ");
				while(words[0].contains("(") || words[0].contains("[")){

					if(line.contains(")")){
						line = line.substring(line.indexOf(")")+1).trim();
					}
					if(line.contains("]")){
						line = line.substring(line.indexOf("]")+1).trim();
					}
					words = line.split(" ");
				}
				//System.out.println(line);
				for(int i = 0; i < words.length; i++){
					words[i] = words[i].replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();

					if(words[i].length()==0){
						continue;
					}
					if(wordCount.containsKey(words[i])){
						int count = wordCount.get(words[i]);
						wordCount.put(words[i], count+1);
					}
					else{
						wordCount.put(words[i], 1);
						if(!features.contains(words[i])){
							features.add(words[i]);
						}
					}
				}
			}
			else if(line.contains("out of 5 stars")){
				String[] word = line.split(" ");
				String value = word[0];
				dataLine = dataLine+","+Double.parseDouble(value)*2;
			}

			line = br.readLine();
		}
		return data;
	}
	
	public static HashMap<String, String> rtWordCounts(String rtInput) throws Exception{
		System.out.println(rtInput);

		BufferedReader br = new BufferedReader(new FileReader(rtInput));
		String line = br.readLine();
		HashMap<String, String> data = new HashMap<String, String>();
		while(line.length() != 0){
			line = br.readLine();
		}
		String title = "";
		String date = "";
		String dataLine = "";
		HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
		Iterator<String> fit = features.iterator();
		while(fit.hasNext()){
			String word = fit.next();
			wordCount.put(word, 0);
		}
				
		while(line != null){
			System.out.println(line);
			if(line.length() == 0 && title.length() != 0){
				fit = features.iterator();
				while(fit.hasNext()){
					String word = fit.next();
					int count = wordCount.get(word);
					//System.out.print(word+",");
					dataLine = dataLine+","+count;
					wordCount.put(word, 0);
				}
				//System.out.println(features.toString());
				//System.out.println(dataLine);
				//System.out.println("rt: "+title);
				if(data.containsKey(title)){
					data.remove(title);
				}
				data.put(title, dataLine);
				title = "";
				date = "";
				dataLine = "";

			}
			else if(line.length()==0){
				line = br.readLine();
				continue;
			}
			else if(line.contains("Average Rating")){ 
				String rating = line.substring(line.indexOf("Average Rating: ")+16, line.indexOf("/"));
				if(rating.compareTo("N")==0){
					line = br.readLine();
					continue;
				}
				dataLine = dataLine+","+rating;
				line = br.readLine();
				continue;
			}
			else if(line.contains("Submit your review")){
				line = br.readLine();
				continue;
			}
			else if(line.contains("There are no critic reviews yet") || line.contains("There are no audience reviews yet")){
				line = br.readLine();
				continue;
			}
			else if(line.contains("http://www.")){
				line = br.readLine();
				continue;
			}
			else if(line.contains("(") && title.length() ==0){
				title = line.substring(0, line.indexOf("(")).trim();
				if(title.contains(", ")){
					title = title.replace(", ",""); 
				}
				date = line.substring(line.indexOf("(")+1, line.indexOf(")"));
				dataLine = title+" ("+date+")";
				line = br.readLine();

				continue;
			}
			else{
				if(line.contains("Audience Reviews for ")){
					line = line.substring(line.indexOf(title)+title.length(), line.indexOf("View all audience reviews"));
				}
				line = line.replace(title, "");

				String[] words = line.split(" ");
				for(int i = 0; i < words.length; i++){
					words[i] = words[i].replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
					
					if(words[i].length()==0){
						continue;
					}
					if(words[i].compareTo("january")==0 ||words[i].compareTo("february")==0 ||words[i].compareTo("march")==0 ||
						words[i].compareTo("april")==0 ||words[i].compareTo("may")==0 ||words[i].compareTo("june")==0 ||
						words[i].compareTo("july")==0 ||words[i].compareTo("august")==0 ||words[i].compareTo("september")==0 ||
						words[i].compareTo("october")==0 ||words[i].compareTo("november")==0 ||words[i].compareTo("december")==0){

						while(i < words.length && words[i].compareTo("Reviewer")!= 0 && words[i].compareTo("Review")!=0){
							i++;
						}
						continue;
					}
						
					if(wordCount.containsKey(words[i])){
						int count = wordCount.get(words[i]);
						wordCount.put(words[i], count+1);
					}
					else{
						wordCount.put(words[i], 1);
						if(!features.contains(words[i])){
							features.add(words[i]);
						}
					}
				}
			}
			line = br.readLine();
		}
		return data;
	}

	public static void createArffFile(String outputFile, HashMap<String, String> azfeatures, HashMap<String,String> rtfeatures) throws Exception{
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		bw.write("% Title: "); bw.newLine();
		bw.write("% Sources: "); bw.newLine();
		bw.write("% Author: "); bw.newLine();
		bw.newLine();
		
		bw.write("@Relation RatingDiff"); bw.newLine();
		bw.newLine();
		
		bw.write("@attribute MovieTitle string"); bw.newLine();
		bw.write("@attribute MovieRating {Amazon, RotTom, Balanced}"); bw.newLine();
		Iterator<String> fi = features.iterator();
		while(fi.hasNext()){
			bw.write("@attribute "+fi.next()+" numeric");
			bw.newLine();
		}
		bw.newLine();
		
		bw.write("@data"); bw.newLine();

		Iterator<String> azkeys = azfeatures.keySet().iterator();
		//Iterator<String> rtkeys = rtfeatures.keySet().iterator();
		//String rtkey = rtkeys.next();
		while(azkeys.hasNext()){
			String azkey = azkeys.next();
			if(!rtfeatures.containsKey(azkey)){
				System.out.println(azkey+" not found");
				continue;
			}
			else{
				String azstring = azfeatures.get(azkey);
				String[] azdata = azstring.split(",");
				
				String rtstring = rtfeatures.get(azkey);
				String[] rtdata = rtstring.split(",");
				
				String data = "{0 \""+azdata[0]+"\"";
				
				//System.out.println(azstring);
				//System.out.println(rtstring);
				
				double azrating = Double.valueOf(azdata[1]);
				double rtrating = Double.valueOf(rtdata[1]);
				double rating = azrating-rtrating;

				//Amazon, RotTom, Balanced
				if(rating > 1){
					azCount++;
					data = data+", 1 Amazon";
				}
				else if(rating < -1){
					rtCount ++;
					data = data+", 1 RotTom";
				}
				else{
					balCount++;
					data = data+", 1 Balanced";
				}
				if(balCount < azCount && data.endsWith("Amazon")){
					azCount--;
					continue;
				}
				for(int i = 2; i < features.size(); i++){
					if(i >= azdata.length && i >= rtdata.length){
						break;
					}
					else if(i >= azdata.length){
						int rtvalue = 0 - Integer.valueOf(rtdata[i]);
						if(rtvalue < -5){
							data = data+","+i+" "+rtvalue;
						}
					}
					else if(i >= rtdata.length){
						int azvalue = 0 - Integer.valueOf(azdata[i]);
						if(azvalue < -5){
							data = data+","+i+" "+azvalue;
						}
					}
					else{
						int azvalue = Integer.valueOf(azdata[i]);
						int rtvalue = Integer.valueOf(rtdata[i]);
						int newvalue = (azvalue-rtvalue);
						if(newvalue < -5 || newvalue > 5){
							data = data+","+i+" "+newvalue;
						}
						//data = data+","+(azvalue-rtvalue);
					}
				}
				data = data+"}";
				//System.out.println("\t"+data);
				bw.write(data); bw.newLine();
			}
		}
		bw.close();
	}
}
	
