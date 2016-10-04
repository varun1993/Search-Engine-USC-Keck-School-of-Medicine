package crawlerassignment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Pattern;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler
{
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|xml" + "|png|mp3|mp3|zip|gz))$");
			/**
			* This method receives two parameters. The first parameter is the page
			* in which we have discovered this new url and the second parameter is
			* the new url. You should implement this function to specify whether
			* the given url should be crawled or not (based on your crawling logic). * In this example, we are instructing the crawler to ignore urls that
			* have css, js, git, ... extensions and to only accept urls that start * with "http://www.viterbi.usc.edu/". In this case, we didn't need the * referringPage parameter to make the decision.
			*/
	       static Hashtable<String,Integer> map_fetch=new Hashtable<String, Integer>();
	       static Hashtable<String,Downloads> map_urls=new Hashtable<String, Downloads>();
	       static Hashtable<String,String> map_visit=new Hashtable<String, String>();
	       static Hashtable<String,Set> pagerank= new Hashtable<>();
	       static Hashtable<String,String> urlToPathMapping=new Hashtable<>();
		   static int count=0;
		   
	       @Override
			public boolean shouldVisit(Page referringPage, WebURL url) {
			String href = url.getURL().toLowerCase();
			if(href.contains("www.keck.usc.edu"))
				map_visit.put(url.getURL(), "OK");
			else if(href.contains(".usc.edu"))
				map_visit.put(url.getURL(), "USC");
			else
				map_visit.put(url.getURL(), "outUSC");
			updateCount();
			
			return !FILTERS.matcher(href).matches() && (href.startsWith("http://www.keck.usc.edu/")||href.startsWith("http://keck.usc.edu/"));
			
			
			}
			
			@Override
			 protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		    	//System.out.println("URL="+webUrl.getURL()+","+"StatusCode:"+statusCode);
		    	
		    	map_fetch.put(webUrl.getURL(), statusCode);
		    	};
			/**
			* This function is called when a page is fetched and ready * to be processed by your program.
			*/
			// @Override
	
			public void visit(Page page) {
			String url = page.getWebURL().getURL(); 
			System.out.println("URL: " + url);
			//System.out.println(page.getContentType());
			
			if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			//String text = htmlParseData.getText();
			//String html = htmlParseData.getHtml();
			Set<WebURL> links = page.getParseData().getOutgoingUrls();
			
			map_urls.put(url, new Downloads(url,page.getContentData().length,page.getParseData().getOutgoingUrls().size(), page.getContentType()));
			pagerank.put(url.toString(), links);
			try{
				String[] urls=url.split("/");
			     String path=page.getWebURL().getPath();
			     createFile(path,urls[urls.length-1]+".html", page);
			     urlToPathMapping.put(url.toString(), path+urls[urls.length-1]+".html");
			}catch(Exception e){}
			}
			if (page.getContentType().equals("application/pdf")) {
			Set<WebURL> links = page.getParseData().getOutgoingUrls();
				
				try{
					String[] urls=url.split("/");
				     String path=page.getWebURL().getPath();
				     createFile(path,urls[urls.length-1], page);
				     urlToPathMapping.put(url.toString(), path+urls[urls.length-1]);
				}catch(Exception e){}
				map_urls.put(url, new Downloads(url,page.getContentData().length,0, page.getContentType()));
				pagerank.put(url.toString(), links);
			}
			if (page.getContentType().equals("application/msword")||page.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
				Set<WebURL> links = page.getParseData().getOutgoingUrls();
					
				try{
					
					String[] urls=url.split("/");
				     String path=page.getWebURL().getPath();
				     createFile(path,urls[urls.length-1], page);
				     urlToPathMapping.put(url.toString(), path+urls[urls.length-1]);
				
				}catch(Exception e){}
				pagerank.put(url.toString(), links);
			
				map_urls.put(url, new Downloads(url,page.getContentData().length,0, page.getContentType()));
				}	
			
			 }
			static void createFile(String path,String filename,Page t) throws IOException{
				File f1;
				String path1 = "./tmp"+path;
				path1= path1.substring(0,path1.lastIndexOf("/"));
				File f=new File(path1);
				byte[] data=t.getContentData();
				f.mkdirs();
				System.out.println("path="+path1);
				if(path.endsWith("/"))
					System.out.println(f1=new File(path1+"/index.html"));
				else
					 f1=new File(path1+"/"+filename);
				FileOutputStream out = new FileOutputStream(f1);	
				out.write(data);
				out.close();
				
			}
			synchronized void updateCount()
			{
				count++;
			}
					
}

class Downloads{
	@Override
	public String toString()
	{
		return "Downloads [url=" + url + ", size=" + size + ", outlinks=" + outlinks + ", resultType=" + resultType
				+ "]"+"\n";
	}
	String url;
	int size;
	int outlinks;
	String resultType;
	public Downloads(String url, int size, int outlinks, String resultType)
	{
		super();
		this.url = url;
		this.size = size;
		this.outlinks = outlinks;
		this.resultType = resultType;
	}
	public String getUrl()
	{
		return url;
	}
	public int getSize()
	{
		return size;
	}
	public int getOutlinks()
	{
		return outlinks;
	}
	public String getResultType()
	{
		return resultType;
	}
	
}
