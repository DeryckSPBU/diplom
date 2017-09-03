import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.net.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg {
    private List<String> links = new LinkedList<String>();
    private String Title;
    private List<List<String>> urlToUrl = new LinkedList<List<String>>();
    public Document htmlDocument;
    private String message = "";


    public boolean crawl(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            htmlDocument = connection.get();
            if(htmlDocument == null){return false;}
            if (connection.response().statusCode() == 200) {
                System.out.println("\nVisiting " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("Retrieved something other than HTML");
                return false;
            }
            return true;
        }
        catch (Exception ex) {
            message = ex.getMessage();
            System.out.println(ex.getMessage());
            return false;
        }
    }
    public void links(String url,String domain){
        String dName="";
        String temp_="";
        try {
            this.Title = this.htmlDocument.title();
            URL currUrl = new URL(url);
            dName = currUrl.getHost();
            System.out.println(dName);
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for (Element link : linksOnPage) {
                List<String> templink = new ArrayList<String>();
                templink.add(url);
                String temp = link.absUrl("href");
                try {
                    URL URL = new URL(temp);
                    temp_ = URL.getHost();
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    continue;
                }
                //System.out.println(temp_);
                templink.add(temp);
                if (temp_.isEmpty()) {
                    continue;
                }
                if (temp_.contains(domain)) {
                    this.links.add(temp);
                    if (temp_.equals(dName) || temp_.equals("www." + dName)) {
                        continue;
                    }
                    this.urlToUrl.add(templink);
                }
            }
    }
    public String getTitle(){
        return this.Title;
    }
    public List<String> getLinks()
    {
        return this.links;
    }
    public List<List<String>> getUrlToUrl() {return this.urlToUrl; }
    public String getMessage(){return this.message;}

}
