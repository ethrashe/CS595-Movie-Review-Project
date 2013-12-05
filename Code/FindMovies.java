package ethrashe.cs.iit;

import java.io.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class FindMovies {

	/**
	 * jsoup jar loaded from 
	 * http://jsoup.org/
	 */
	/**
	 * load list of movie names
	 * 		coming soon
	 * 		new releases (< 3 months)
	 * 		recent releases (< 1 year)
	 * 		stable releases (<10 years)
	 *
	 * load websites
	 * 		load amazon pages of movies
	 * 		load rottentomatoes pages
	 * 			load imdb pages?
	 * collect info for each movie
	 * 		date released
	 * 			rating on each site
	 * 			number of reviews
	 * 		review features
	 * 			unigrams
	 * 			bigrams
	 * 			date from release (theatrical or dvd)
	 * output file for classification
	 * 		simplify rating to classification difference
	 * 		predict rating difference from features
	 * 		also predict site-specific rating from features
	 * 		record results of different methods
	 * @throws IOException 
	 */

	public static void main(String[] args) throws Exception {
		
		/*
		String comingSoonURL = "http://www.amazon.com/s/ref=sr_nr_p_n_date_3?rh=n%3A2625373011%2Cp_n_format_browse-bin%3A2650304011%2Cn%3A!2625374011%2Cn%3A2649512011%2Cp_72%3A3014478011%2Cp_n_date%3A2693529011&bbn=2649512011&ie=UTF8&qid=1385375743&rnid=2693522011";
		String last90DaysURL = "http://www.amazon.com/s/ref=sr_nr_p_72_3?rh=n%3A2625373011%2Cp_n_format_browse-bin%3A2650304011%2Cn%3A!2625374011%2Cn%3A2649512011%2Cp_n_date%3A2693528011%2Cp_72%3A3014478011&bbn=2649512011&ie=UTF8&qid=1385375722&rnid=3014474011";
		String lastDecadeURL = "http://www.amazon.com/s/ref=sr_nr_p_n_feature_three_br_0?rh=n%3A2625373011%2Cp_n_format_browse-bin%3A2650304011%2Cn%3A!2625374011%2Cn%3A2649512011%2Cp_72%3A3014478011%2Cp_n_feature_three_browse-bin%3A2651256011|2651255011&bbn=2649512011&ie=UTF8&qid=1385375763&rnid=2651254011";
		
		int year = 2013;
		int reviewThreshold = 25;
		
		int csnumRecords = 50;		
		int l90numRecords = 50;
		int lynumRecords = 100;
		int ldnumRecords = 300;
		*/
		String comingSoonURL = "";
		String last90DaysURL = "";
		String lastDecadeURL = "";
		
		String outputFolder = "";
		
		int year;
		int reviewThreshold;
		
		int csnumRecords;		
		int l90numRecords;
		int lynumRecords;
		int ldnumRecords;
		
		if(args.length == 10){
			comingSoonURL = args[0];
			last90DaysURL = args[1];
			lastDecadeURL = args[2];
			
			outputFolder = args[3];
			
			year = Integer.parseInt(args[4]);
			reviewThreshold = Integer.parseInt(args[5]);
			
			csnumRecords = Integer.parseInt(args[6]);		
			l90numRecords = Integer.parseInt(args[7]);
			lynumRecords = Integer.parseInt(args[8]);
			ldnumRecords = Integer.parseInt(args[9]);
		}
		else{
			System.out.println("Needs arguments:");
			System.out.println("1. comingSoon URL");
			System.out.println("2. last90Days URL");
			System.out.println("3. lastDecade URL");
			System.out.println("4. Output Folder");
			System.out.println("5. Current Year (4-digit)");
			System.out.println("6. Review Threshold (how many amazon reviews are required)");
			System.out.println("7. Number of 'Coming Soon' movies to collect");
			System.out.println("8. Number of 'Last 90 Days' movies to collect");
			System.out.println("9. Number of 'Last Year' movies to collect");
			System.out.println("10. Number of 'Last Decade' movies to collect");
			return;
		}
		
		
		String csOutputData = outputFolder+"csOutputData.txt";
		String l90OutputData = outputFolder+"l90OutputData.txt";
		String decadeOutputData = outputFolder+"decadeOutputData.txt";
		String yearOutputData = outputFolder+"yearOutputData.txt";

		Set<String> movies = findCSRecords(comingSoonURL, csOutputData, reviewThreshold, year, csnumRecords);
		movies.addAll(findl90Records(last90DaysURL, l90OutputData, reviewThreshold, year, l90numRecords));
		findYearRecords(lastDecadeURL, yearOutputData, movies, reviewThreshold*4, year, lynumRecords);
		//findDecadeRecords(lastDecadeURL, decadeOutputData, movies, reviewThreshold, year, ldnumRecords);
		
		System.out.println("closed");
	}
	
	public static Set<String> findCSRecords(String comingSoonURL, String csFilename, int reviewThreshold, int year, int numRecords) throws Exception{
		BufferedWriter bw = new BufferedWriter(new FileWriter(csFilename));
		Document csDoc = Jsoup.connect(comingSoonURL).get();
		
		bw.write(csFilename);
		bw.newLine();
		bw.write(csDoc.title());
		bw.newLine();
		bw.write(comingSoonURL);
		bw.newLine();
		bw.write(new Date().toString());
		bw.newLine();
		bw.newLine();

		Set<String> movies = (findCSRecordsHelper(comingSoonURL, 0, bw, 1, new HashSet<String>(), reviewThreshold));

		bw.close();
		return movies;
	}
	public static Set<String> findCSRecordsHelper(String comingSoonURL, int count, BufferedWriter bw, int pageNum, Set<String> movies, int reviewThreshold) throws Exception{
		Thread.sleep(100);

		String newURL = comingSoonURL+"&page="+pageNum;
		Document csDoc = Jsoup.connect(newURL).get();
		int currRecord = 0;
		for(int i = count; i < 40; i++){
			System.out.println(i);

			Element csresult = csDoc.getElementById("result_"+currRecord);
			if(csresult == null){
				pageNum++;
				movies.addAll(findCSRecordsHelper(comingSoonURL, i, bw, pageNum, new HashSet<String>(), reviewThreshold));
				return movies;
			}
			else{
				String text = csresult.text();
				String link = csresult.getElementsByAttribute("href").attr("href");
				String stars = csresult.getElementsByAttributeValueContaining("alt", "stars").attr("alt");
				boolean OKtoPrint = true;
				if(!(text.contains("Available for Pre-order."))){
					OKtoPrint = false;
					System.out.println("incorrect info: "+text);
				}
				int numReviews = Integer.parseInt(text.substring(text.lastIndexOf("(")+1, text.lastIndexOf(")")).replace(",", ""));
				if(numReviews < reviewThreshold){
					OKtoPrint = false;
					System.out.println("too few reviews: "+text);
				}
				
				if(OKtoPrint)
				{
					System.out.println(text);
					System.out.println(link);
					System.out.println(stars);

					movies.add(text);
					
					bw.write(text);
					bw.newLine();
					bw.write(link);
					bw.newLine();
					bw.write(stars);
					bw.newLine();
					bw.newLine();

				}
				else{
					i--;
				}
			}
			currRecord++;
		}
		return movies;
	}

	public static Set<String> findl90Records(String last90DaysURL, String l90Filename, int reviewThreshold, int year, int numRecords) throws Exception{
		BufferedWriter bw = new BufferedWriter(new FileWriter(l90Filename));
		Document l90Doc = Jsoup.connect(last90DaysURL).get();
		
		bw.write(l90Filename);
		bw.newLine();
		bw.write(l90Doc.title());
		bw.newLine();
		bw.write(last90DaysURL);
		bw.newLine();
		bw.write(new Date().toString());
		bw.newLine();
		bw.newLine();

		Set<String> movies = (findL90RecordsHelper(last90DaysURL, 0, bw, 1, new HashSet<String>(), reviewThreshold, year, numRecords));

		bw.close();
		return movies;
	}
	public static Set<String> findL90RecordsHelper(String last90DaysURL, int count, BufferedWriter bw, int pageNum, Set<String> movies, int reviewThreshold, int year, int numRecords) throws Exception{
		Thread.sleep(100);

		String newURL = last90DaysURL+"&page="+pageNum;
		Document l90Doc = Jsoup.connect(newURL).get();
		int currRecord = 0;
		for(int i = count; i < numRecords; i++){
			System.out.println(i);

			Element csresult = l90Doc.getElementById("result_"+currRecord);
			if(csresult == null){
				pageNum++;
				movies.addAll(findL90RecordsHelper(last90DaysURL, i, bw, pageNum, new HashSet<String>(), reviewThreshold, year, numRecords));
				return movies;
			}
			else{
				String text = csresult.text();
				String link = csresult.getElementsByAttribute("href").attr("href");
				String stars = csresult.getElementsByAttributeValueContaining("alt", "stars").attr("alt");
				boolean OKtoPrint = true;
				if(!text.contains(Integer.toString(year))){
					OKtoPrint = false;
					System.out.println("incorrect info: "+text);
				}
				if(text.contains("Available for Pre-order.")){
					OKtoPrint = false;
					System.out.println("incorrect info: "+text);
				}
				int numReviews = Integer.parseInt(text.substring(text.lastIndexOf("(")+1, text.lastIndexOf(")")).replace(",", ""));
				if(numReviews < reviewThreshold){
					OKtoPrint = false;
					System.out.println("too few reviews: "+text);
				}
				
				if(OKtoPrint)
				{
					System.out.println(text);
					System.out.println(link);
					System.out.println(stars);

					
					bw.write(text);
					bw.newLine();
					bw.write(link);
					bw.newLine();
					bw.write(stars);
					bw.newLine();
					bw.newLine();

				}
				else{
					i--;
				}
			}
			currRecord++;
		}
		return movies;
	}

	public static void findYearRecords(String lastDecadeURL, String yearFilename, Set<String> movies, int reviewThreshold, int year, int numRecords) throws Exception{
		BufferedWriter bw = new BufferedWriter(new FileWriter(yearFilename));
		Document l90Doc = Jsoup.connect(lastDecadeURL).get();
		
		bw.write(yearFilename);
		bw.newLine();
		bw.write(l90Doc.title());
		bw.newLine();
		bw.write(lastDecadeURL);
		bw.newLine();
		bw.write(new Date().toString());
		bw.newLine();
		bw.newLine();

		findYearRecordsHelper(lastDecadeURL, 0, bw, 1, movies, reviewThreshold, year, numRecords);

		bw.close();
	}
	public static void findYearRecordsHelper(String lastDecadeURL, int count, BufferedWriter bw, int pageNum, Set<String> movies, int reviewThreshold, int year, int numRecords) throws Exception{
		Thread.sleep(100);
		
		String newURL = lastDecadeURL+"&page="+pageNum;
		Document l90Doc = Jsoup.connect(newURL).get();
		int currRecord = 0;
		for(int i = count; i < numRecords; i++)
		{
			System.out.println(i);

			Element csresult = l90Doc.getElementById("result_"+currRecord);
			if(csresult == null){
				pageNum++;
				findYearRecordsHelper(lastDecadeURL, i, bw, pageNum, movies, reviewThreshold, year, numRecords);
				break;
			}
			else{
				String text = csresult.text();
				String link = csresult.getElementsByAttribute("href").attr("href");
				String stars = csresult.getElementsByAttributeValueContaining("alt", "stars").attr("alt");
				boolean OKtoPrint = true;
				if(movies.contains(text)){
					OKtoPrint = false;
					System.out.println("title found elsewhere: "+text);
				}
				if(!text.contains(Integer.toString(year)) && !text.contains(Integer.toString(year+1))){
					OKtoPrint = false;
					System.out.println("incorrect info (year): "+text);
				}

				if(text.contains("Available for Pre-order.")){
					OKtoPrint = false;
					System.out.println("incorrect info: "+text);
				}
				int numReviews = 0;
				if(text.substring(text.length()-10, text.length()).contains("(")){
					numReviews = Integer.parseInt(text.substring(text.lastIndexOf("(")+1, text.lastIndexOf(")")).replace(",", ""));
				}
				if(numReviews < reviewThreshold){
					OKtoPrint = false;
					System.out.println("too few reviews: "+text);
				}
				
				if(OKtoPrint)
				{
					System.out.println(text);
					System.out.println(link);
					System.out.println(stars);

					
					bw.write(text);
					bw.newLine();
					bw.write(link);
					bw.newLine();
					bw.write(stars);
					bw.newLine();
					bw.newLine();

				}
				else{
					i--;
				}
			}
			currRecord++;
		}
	}

	public static void findDecadeRecords(String lastDecadeURL, String decadeFilename, Set<String> movies, int reviewThreshold, int year, int numRecords) throws Exception{
		BufferedWriter bw = new BufferedWriter(new FileWriter(decadeFilename));
		Document l90Doc = Jsoup.connect(lastDecadeURL).get();
		
		bw.write(decadeFilename);
		bw.newLine();
		bw.write(l90Doc.title());
		bw.newLine();
		bw.write(lastDecadeURL);
		bw.newLine();
		bw.write(new Date().toString());
		bw.newLine();
		bw.newLine();

		findDecadeRecordsHelper(lastDecadeURL, 0, bw, 1, movies, reviewThreshold, year, numRecords);

		bw.close();
	}
	public static void findDecadeRecordsHelper(String lastDecadeURL, int count, BufferedWriter bw, int pageNum, Set<String> movies, int reviewThreshold, int year, int numRecords) throws Exception{
		Thread.sleep(100);
		
		String newURL = lastDecadeURL+"&page="+pageNum;
		Document l90Doc = Jsoup.connect(newURL).get();
		int currRecord = 0;
		for(int i = count; i < numRecords; i++){
			System.out.println(i);

			Element csresult = l90Doc.getElementById("result_"+currRecord);
			if(csresult == null){
				pageNum++;
				findDecadeRecordsHelper(lastDecadeURL, i, bw, pageNum, movies, reviewThreshold, year, numRecords);
				break;
			}
			else{
				String text = csresult.text();
				String link = csresult.getElementsByAttribute("href").attr("href");
				String stars = csresult.getElementsByAttributeValueContaining("alt", "stars").attr("alt");
				boolean OKtoPrint = true;
				if(movies.contains(text)){
					OKtoPrint = false;
					System.out.println("title found elsewhere: "+text);
				}
				if(text.contains(Integer.toString(year)) || text.contains(Integer.toString(year+1))){
					OKtoPrint = false;
					System.out.println("incorrect info: "+text);
				}
				
				if(text.contains("Available for Pre-order.")){
					OKtoPrint = false;
					System.out.println("incorrect info: "+text);
				}
				int numReviews = Integer.parseInt(text.substring(text.lastIndexOf("(")+1, text.lastIndexOf(")")).replace(",", ""));
				if(numReviews < reviewThreshold){
					OKtoPrint = false;
					System.out.println("too few reviews: "+text);
				}
				
				if(OKtoPrint)
				{
					System.out.println(text);
					System.out.println(link);
					System.out.println(stars);

					
					bw.write(text);
					bw.newLine();
					bw.write(link);
					bw.newLine();
					bw.write(stars);
					bw.newLine();
					bw.newLine();

				}
				else{
					i--;
				}
			}
			currRecord++;
		}
	}
}
