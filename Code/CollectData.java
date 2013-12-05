package ethrashe.cs.iit;

import java.io.*;
import java.util.Map;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CollectData {

	public static void main(String[] args) throws Exception {
		String csList = "";
		String l90List = "";
		String decadeList= "";
		String yearList = "";
		
		if(args.length == 4){
			csList = args[0];
			l90List = args[1];
			yearList = args[2];
			decadeList = args[3];
		}
		else{
			System.out.println("Needs arguments:");
			System.out.println("1. Coming Soon Data File");
			System.out.println("2. Last 90 Days Data File");
			System.out.println("3. Last Year Data File");
			System.out.println("4. Last Decade Data File");
			return;
		}
		
		String rtURL = "http://www.rottentomatoes.com/m/";
		
		//collectAmazonData(csList);
		//collectAmazonData(l90List);
		//collectAmazonData(decadeList);
		//collectAmazonData(yearList);
		
		//collectRTData(csList, rtURL);
		//collectRTData(l90List, rtURL);
		//collectRTData(yearList, rtURL);
		collectRTData(decadeList, rtURL);
	}
	
	public static void collectAmazonData(String inputFile) throws Exception{ 
		String outputFile = inputFile.substring(0, inputFile.lastIndexOf("."))+"_amazon"+inputFile.substring(inputFile.lastIndexOf("."), inputFile.length());
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		
		String line = br.readLine();
		bw.write(line);
		bw.newLine();
		
		while(line.length() != 0){
			line = br.readLine();
			bw.write(line);
			bw.newLine();
		}
		String voteCount ="";
		while(line != null){
			bw.write(line);
			bw.newLine();
			
			if(line.contains("(")){
				voteCount = line.substring(line.lastIndexOf("(")+1, line.lastIndexOf(")"));
			}
			if(line.contains("http://www.amazon.com/")){
				bw.write(voteCount+" ratings");
				bw.newLine();
				amazonPageData(line.replace("/dp/", "/product-reviews/"), bw);
			}
			line = br.readLine();
		}
		br.close();
		bw.close();
		System.out.println(inputFile);
	}
	public static void amazonPageData(String URL, BufferedWriter bw) throws IOException, Exception{
		Thread.sleep(100);
		System.out.println(URL);
		Document azDoc = Jsoup.connect(URL).timeout(0).get();

		Element azReviews = azDoc.getElementById("productReviews");
		String[] reviewArray = azReviews.toString().split("<!-- BOUNDARY -->");
		for(int i = 1; i < reviewArray.length; i++){
			Thread.sleep(100);
			System.out.print(i);
			Document review = Jsoup.parse(reviewArray[i]);
			System.out.println(reviewArray[i]);
			bw.write(review.text());
			bw.newLine();
		}
	}

	public static void collectRTData(String inputFile, String rtURL) throws Exception{
		String outputFile = inputFile.substring(0, inputFile.lastIndexOf("."))+"_rotten"+inputFile.substring(inputFile.lastIndexOf("."), inputFile.length());
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
				
		String line = br.readLine();
		bw.write(line);
		bw.newLine();
		
		while(line.length() != 0){
			line = br.readLine();
			bw.write(line);
			bw.newLine();
		}
		while(line != null){
			if(line.length()!= 0 && line.contains("Buy new")){
				System.out.println(line);
				String date = "";
				if(line.contains("(DVD - ")){
					date = line.substring(line.indexOf("(DVD - ")+7, line.indexOf("(DVD - ")+11);
				}
				else{
					line = br.readLine();
					continue;
				}
				String title = "";
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
				if(title.contains(":")){
					title = title.replace(":", "");
				}
				if(title.contains("'")){
					title = title.replace("'", "");
				}
				if(title.contains(",")){
					title = title.replace(",", "");
				}
				if(title.contains("&")){
					title = title.replace("&", "and");
				}
				if(title.contains(".")){
					title = title.replace(".", "");
				}
				if(title.contains("-")){
					title = title.replace("-", " ");
				}
				
				while(title.contains("  ")){
					title = title.replace("  ", " ");
				}
				String pageURL = rtURL+title.replace(" ", "_")+"/";
				boolean found = rtDatahelper(pageURL, date, false, bw);
				if(!found){
					String newpageURL = rtURL+title.replace(" ", "_");
					newpageURL = newpageURL+"_"+date+"/";
					//System.out.println("try "+newpageURL);
					found = rtDatahelper(newpageURL, date, false, bw);
				}
				if(!found){
					int dateNum = Integer.parseInt(date);
					String newpageURL = rtURL+title.replace(" ", "_");
					newpageURL = newpageURL+"_"+Integer.toString(dateNum-1)+"/";
					//System.out.println("try "+newpageURL);
					found = rtDatahelper(newpageURL, Integer.toString(dateNum-1), false, bw);
				}
				if(!found){
					int dateNum = Integer.parseInt(date);
					String newpageURL = rtURL+title.replace(" ", "_");
					newpageURL = newpageURL+"_"+Integer.toString(dateNum+1)+"/";
					//System.out.println("try "+newpageURL);
					found = rtDatahelper(newpageURL, Integer.toString(dateNum+1), false, bw);
				}
				if(!found){
					String newpageURL = rtURL+"the_"+title.replace(" ", "_")+"/";
					//System.out.println("try "+newpageURL);
					found = rtDatahelper(newpageURL, date, false, bw);
				}
				if(!found && title.contains("The ")){
					String newpageURL = rtURL+title.replace("The ", "").replace(" ", "_")+"/";
					found = rtDatahelper(newpageURL, date, false, bw);
				}
				if(!found){
					System.out.println("\t"+title+" not found");
				}
			}
			/*else{
				System.out.println("\t"+line);
			}*/
			line = br.readLine();
		}
		br.close();
		bw.close();
	}
	public static boolean rtDatahelper(String pageURL, String date, boolean found, BufferedWriter bw) throws Exception{
		Thread.sleep(100);
		
		try{
			Document rtDoc = Jsoup.connect(pageURL).timeout(0).get();
			Element e = rtDoc.getElementsByAttributeValue("itemprop", "name").first();
			System.out.println(date+" "+pageURL);
			int dateNum = Integer.parseInt(date);
			if(e.text().contains(date) || e.text().contains(Integer.toString(dateNum-1)) || e.text().contains(Integer.toString(dateNum+1))){
				System.out.println("found at "+pageURL);
				found = true;
			}
			else if(rtDoc.body().text().contains(date)|| rtDoc.body().text().contains(Integer.toString(dateNum-1))|| rtDoc.body().text().contains(Integer.toString(dateNum+1))){
				System.out.println("date present in "+pageURL);
				found = true;
			}
			else{
				return found;
			}
			bw.write(e.text());
			//System.out.println("from page: "+e.text());
			bw.newLine();
			
			Element scorePanel = rtDoc.getElementById("scorePanel");
			String criticStats = scorePanel.getElementsByClass("critic_stats").text();
			//String criticPercent = rtDoc.getElementById("all-critics-meter").text();
			//String userPercent = scorePanel.text();
			//String userPercent = "%%";
			//System.out.println(criticStats);
			bw.write(criticStats);
			bw.newLine();
			//System.out.println(criticPercent+" : "+userPercent);
			
			Element crreviews = rtDoc.getElementById("reviews");
			//System.out.println(crreviews.text());
			bw.write(crreviews.text());
			bw.newLine();
			
			Element aureviews = rtDoc.getElementById("audience_reviews");
			//System.out.println(aureviews.text());
			bw.write(aureviews.text());
			bw.newLine();
			
			bw.write(pageURL);
			bw.newLine();
			bw.newLine();
		}
		catch(HttpStatusException e){
			//System.out.println("could not find "+pageURL);
		}
		catch(NumberFormatException e){
			return found;
		}
		return found;
	}
}
