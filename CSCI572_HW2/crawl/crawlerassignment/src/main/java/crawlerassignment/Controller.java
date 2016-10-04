package crawlerassignment;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

public class Controller
{

	public static void main(String[] args) throws Exception
	{
		String crawlStorageFolder = "./data/crawl"; int numberOfCrawlers = 7;
		CrawlConfig config = new CrawlConfig(); config.setCrawlStorageFolder(crawlStorageFolder);
		        /*
		         * Instantiate the controller for this crawl.
		         */
		config.setMaxDepthOfCrawling(5);
		config.setMaxPagesToFetch(5000);
		config.setPolitenessDelay(200);
		config.setIncludeBinaryContentInCrawling(true);
		config.setMaxDownloadSize(Integer.MAX_VALUE);
		config.setUserAgentString("mjhbkjvfkdjflsakdfkldsklfjgfdllfrgktimrfedwqkswdfgyrfedwdekcfvngrejfugtrfejdfvgrfedwjruwriekens kgndwkjfw jksfdhshfdewahdehtgnekqrhgoeQ JUHJLERJTHIJEWLRTHBRVEWKWATHUYTRIEWAV JRHJFSKJEVHKSAEHVJGHBJLKJLREJJLENKFMKM;F LDJ YOT;L HJJP'FV LIVGJREPkjrgfkrrwndhbfewlafn gkrenlkgbhjedfbgmf demn bfgbnjfbn fkjwdnbnfdkjsn gkfwdkjbgfejwqbgj,fwefbmfd bng.,fedgbj fewqkngkjfml");;
      // config.s
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher); CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		/*
		* For each crawl, you need to add some seed urls. These are the first
		* URLs that are fetched and then the crawler starts following links * which are found in these pages
		*/
		controller.addSeed("http://www.keck.usc.edu/"); 
		

		/* Start the crawl. This is a blocking operation, meaning that your code
		6
		* will reach the line after this only when crawling is finished.
		*/
		controller.start(MyCrawler.class, numberOfCrawlers);
		generateCsvFileMap_Fetch(MyCrawler.map_fetch);
		generateCsvFileMap_Visit(MyCrawler.map_urls);
		generateCsvFileMap_URL(MyCrawler.map_visit);
		generateCsvFileMap_output(MyCrawler.map_fetch);
		generateCsvFileMap_pageRanks(MyCrawler.pagerank);
		generateCsvFileURLMAP(MyCrawler.urlToPathMapping);
		//System.out.println(MyCrawler.map_urls.values());
		}
	
	static  void generateCsvFileMap_pageRanks(Hashtable<String,Set> table)
	   {
		try
		{
		    FileWriter writer = new FileWriter("pageRank.csv");
        for(String i:table.keySet())
		    {writer.append(i);
		    writer.append(',');
		    Set<WebURL> urls=table.get(i);
		    Iterator<WebURL> weburlsList= urls.iterator();
		    while(weburlsList.hasNext())
		    	{
		    	writer.append(weburlsList.next().getURL()); 
		    	if(weburlsList.hasNext())
		    	    writer.append(',');
		    	}
	        writer.append('\n');
		    }	
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	    }
	
	static  void generateCsvFileURLMAP(Hashtable<String, String> urlToPathMapping)
	   {
		try
		{
		    FileWriter writer = new FileWriter("urlMap.csv");
		
        for(String i:urlToPathMapping.keySet())
		    {writer.append(i);
		    writer.append(',');
		    writer.append(urlToPathMapping.get(i).toString());
	        writer.append('\n');
		    }	
        writer.flush();
	    writer.close();
		}
		catch(Exception e)
		{
			
		}
	   }
	static  void generateCsvFileMap_Fetch(Hashtable<String,Integer> table)
	   {
		try
		{
		    FileWriter writer = new FileWriter("fetch.csv");
			 
		    writer.append("URL");
		    writer.append(',');
		    writer.append("Status");
		    writer.append('\n');
		    
           for(String i:table.keySet())
		    {writer.append(i);
		    writer.append(',');
		    writer.append(table.get(i).toString());
	        writer.append('\n');
		    }	
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	    }
	
	static  void generateCsvFileMap_Visit(Hashtable<String,Downloads> table)
	   {
		try
		{
		    FileWriter writer = new FileWriter("visit.csv");
			 
		    writer.append("URL");
		    writer.append(',');
		    writer.append("Size");
		    writer.append(',');
		    writer.append("OutLinks");
		    writer.append(',');
		    writer.append("ResultType");
		    writer.append('\n');
		    
        for(String i:table.keySet())
		    {writer.append(i);
		    writer.append(',');
		    Downloads value=table.get(i);
		    writer.append(Integer.toString(value.getSize()));
		    writer.append(',');
		    writer.append(Integer.toString(value.getOutlinks()));
		    writer.append(',');
		    writer.append(value.getResultType());
	        writer.append('\n');
		    }	
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	    }
	
	static  void generateCsvFileMap_URL(Hashtable<String,String> table)
	   {
		try
		{
		    FileWriter writer = new FileWriter("URL.csv");
			 
		    writer.append("URL");
		    writer.append(',');
		    writer.append("Status");
		    writer.append('\n');
		    
        for(String i:table.keySet())
		    {
        	if(i.equals("count"))
        		continue;
        	writer.append(i);
		    writer.append(',');
		    writer.append(table.get(i).toString());
	        writer.append('\n');
		    }	
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	

}
	
	static  void generateCsvFileMap_output(Hashtable<String,Integer> table)
	   {
		try
		{
		    FileWriter writer = new FileWriter("CrawlerReport.txt");
			 
		    writer.append("Fetch Statistics");
		    writer.append('\n');
		    writer.append("================");
		    writer.append('\n');
		    writer.append("# fetches attempted: "+MyCrawler.map_fetch.size());
		    writer.append('\n');
		    int code_200=0;
		    int code_failed=0;
		    int code_aborted=0;
		    for(String i:MyCrawler.map_fetch.keySet())
		    {
		    	int x=table.get(i);
		    	if(x==200)
		    		code_200++;
		    	else if(x==301)
		    		code_aborted++;
		    	
		    		
		    }
		    code_failed=MyCrawler.map_fetch.size()-code_200-code_aborted;
		    writer.append("# fetches succeeded: "+code_200);
		    writer.append('\n');
		    writer.append("# fetches aborted: "+code_aborted);
		    writer.append('\n');
		    writer.append("# fetches failed: "+code_failed);
		    writer.append('\n');
		    writer.append('\n');
		    writer.append("Outgoing URLs");
		    writer.append('\n');
		    writer.append("================");
		    writer.append('\n');
		    writer.append("Total URLs extracted: "+MyCrawler.count);
		    writer.append('\n');
		    writer.append("# unique URLs extracted: "+MyCrawler.map_visit.size());
		    writer.append('\n');
		    int withinSchool=0;
		    int inUSC=0;
		    int outUSC=0;
		    for(String i:MyCrawler.map_visit.keySet())
		    {
		    	String x=MyCrawler.map_visit.get(i);
		    	if(x.equals("OK"))
		    		withinSchool++;
		    	else if(x.equals("USC"))
		    		inUSC++;
		    	else
		    		outUSC++;
		    }
		    writer.append("# unique URLs within school: "+withinSchool);
		    writer.append('\n');
		    writer.append("# unique USC URLs outside school: "+inUSC);
		    writer.append('\n');
		    writer.append("# unique URLs outside USC: "+outUSC);
		    writer.append('\n');
		    writer.append('\n');
		    writer.append("Status Codes");
		    writer.append('\n');
		    writer.append("================");
		    writer.append('\n');
		    int code200=0;
		    int code301=0;
		    int code302=0;
		    int code404=0;
		    for(String i:MyCrawler.map_fetch.keySet())
		    {
		    	int x=MyCrawler.map_fetch.get(i);
		    	if(x==200)
		    		code200++;
		    	else if(x==301)
		    		code301++;
		    	else if(x==302)
		    		code302++;
		    	else if(x==404)
		    		code404++;
		    }
		    writer.append("# 200 OK:"+code200);
		    writer.append('\n');
		    writer.append("# 301 Moved Permanently: "+code301);
		    writer.append('\n');
		    writer.append("# 302 Moved Temporarily: "+code302);
		    writer.append('\n');
		    writer.append("# 404 Not Found: "+code404);
		    writer.append('\n');
		    writer.append('\n');
		    writer.append("File Sizes");
		    writer.append('\n');
		    writer.append("================");
		    writer.append('\n');
		    int kb=0;
		    int kb10=0;
		    int kb100=0;
		    int mb=0;
		    int mb10=0;
		    int pdf=0;
		    int doc=0;
		    int html=0;
		    int jpg=0;
		    int png=0;
		    int xml=0;
		    int css=0;
		    for(String i:MyCrawler.map_urls.keySet())
		    {
		    	Downloads x=MyCrawler.map_urls.get(i);
		    	int size=x.getSize();
		    	String content=x.getResultType();
				if(size < 1000)
					kb++;
				else if(size >= 1000 && size < 10000)
					kb10++;
				else if(size >= 10000 && size < 100000)
					kb100++;
				else if(size >= 100000 && size < 1000000)
					mb++;
				else
					mb10++;
				if(content.equals("text/html; charset=UTF-8"))
					html++;
				else if(content.equals("application/pdf"))
				    pdf++;
				else if(content.equals("image/png"))
				    png++;
				else if(content.equals("image/jpeg"))
				    jpg++;
				else if(content.equals("text/css"))
				    css++;
				else if(content.equals("application/rss+xml; charset=UTF-8")||content.equals("application/xml")||content.equals("text/xml") )
					xml++;
				else if(content.equals("application/msword"))
					doc++;
				
				
		    }
		    writer.append("< 1KB: "+kb);
		    writer.append('\n');
		    writer.append("1KB ~ <10KB: "+kb10);
		    writer.append('\n');
		    writer.append("10KB ~ <100KB: "+kb100);
		    writer.append('\n');
		    writer.append("100KB ~ <1MB: "+mb);
		    writer.append('\n');
		    writer.append(">= 1MB: "+mb10);
		    writer.append('\n');
		    writer.append('\n');
		    writer.append("Content Types");
		    writer.append('\n');
		    writer.append("================");
		    writer.append('\n');
		    writer.append("text/html: "+html);
		    writer.append('\n');
		    writer.append("application/pdf: "+pdf);
		    writer.append('\n');
//		    writer.append("text/css: "+css);
//		    writer.append('\n');
		    writer.append("application/doc: "+doc);
		    writer.append('\n');
//		    writer.append("image/jpeg: "+jpg);
//		    writer.append('\n');
//		    writer.append("image/png: "+png);
//		    writer.append('\n');
//		    writer.append("application/xml: "+xml);
//		    writer.append('\n');
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	    }
}
