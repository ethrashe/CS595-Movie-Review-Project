This repository contains 3 folders:
1. Report: Includes the PDF of the original proposal, as well as the presentation and final report.
2. Code: Includes java files of the code used in the project.
3. Output: Includes the zip files of the output of each step in the code.

To run the code:
1. Include the JSoup jar in the classpath (available at jsoup.org)

2. Begin with FindMovies.java. 
This program finds movies on Amazon that satisfy the requirements to be included in the dataset.
Arguments: 
URL for Amazon's Coming Soon movies page (http://www.amazon.com/s/ref=sr_nr_p_n_date_3?rh=n%3A2625373011%2Cp_n_format_browse-bin%3A2650304011%2Cn%3A!2625374011%2Cn%3A2649512011%2Cp_72%3A3014478011%2Cp_n_date%3A2693529011&bbn=2649512011&ie=UTF8&qid=1385375743&rnid=2693522011).
URL for Amazon's Last 90 Days movies page (http://www.amazon.com/s/ref=sr_nr_p_72_3?rh=n%3A2625373011%2Cp_n_format_browse-bin%3A2650304011%2Cn%3A!2625374011%2Cn%3A2649512011%2Cp_n_date%3A2693528011%2Cp_72%3A3014478011&bbn=2649512011&ie=UTF8&qid=1385375722&rnid=3014474011).
URL for Amazon's 2000-2009, 2010 & Newer movies page (http://www.amazon.com/s/ref=sr_nr_p_n_feature_three_br_0?rh=n%3A2625373011%2Cp_n_format_browse-bin%3A2650304011%2Cn%3A!2625374011%2Cn%3A2649512011%2Cp_72%3A3014478011%2Cp_n_feature_three_browse-bin%3A2651256011|2651255011&bbn=2649512011&ie=UTF8&qid=1385375763&rnid=2651254011).
A location to output the created files.
Current year (2013).
Review threshold, the minimum number of reviews required on Amazon for a movie to be included in the data (25).
Number of records for each Release Date Category. For testing, I set the Coming Soon number as 50, Last 90 days as 50, Last Year as 100, and Last Decade as 300.

3. Run CollectData.java. 
This program collects reviews from Amazon and Rotten Tomatoes, when they are available.
Arguments:
Location of each file output from FindMovies.java: csOutputData.txt, l90OutputData.txt, yearOutputData.txt, decadeOutputData.txt.
Note: The output of this program will be placed in the same location as the inputs.

4a. Run CompileFeatures.java.
This program analyzes the reviews collected in each release date category and produces an ARFF file with the results.
Arguments: 
The location of the csOutputData_amazon.txt file produced by CollectData
The location of the l90OutputData_amazon.txt file produced by CollectData
The location of the yearOutputData_amazon.txt file produced by CollectData
The location of the decadeOutputData_amazon.txt file produced by CollectData
The location of the csOutputData_rotten.txt file produced by CollectData
The location of the l90OutputData_rotten.txt file produced by CollectData
The location of the yearOutputData_rotten.txt file produced by CollectData
The location of the decadeOutputData_rotten.txt file produced by CollectData
The location to output the created ARFF files.

4b. Run CompileFeatures_allinone.java.
The program analyzes the reviews collected, but ensures the number of Amazon and Balanced entries are equal.
Arguments: 
The location of the csOutputData_amazon.txt file produced by CollectData
The location of the l90OutputData_amazon.txt file produced by CollectData
The location of the yearOutputData_amazon.txt file produced by CollectData
The location of the decadeOutputData_amazon.txt file produced by CollectData
The location of the csOutputData_rotten.txt file produced by CollectData
The location of the l90OutputData_rotten.txt file produced by CollectData
The location of the yearOutputData_rotten.txt file produced by CollectData
The location of the decadeOutputData_rotten.txt file produced by CollectData
The location to output the created ARFF files.

5. Open an ARFF file with Weka (www.cs.waikato.ac.nz/ml/weka/downloading.html).
Remove the MovieTitles feature.
Run the desired Classification on MovieRatings to get the desired results.