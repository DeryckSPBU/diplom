import javenue.csv.Csv;

import java.util.*;
import java.net.*;
import java.io.IOException;

public class Spider {
    private Set<String> pagesVisited = new HashSet<String>();
    private Set<String> pagesFoundedName = new HashSet<String>();
    private List<List<String>> pagesFounded = new ArrayList<List<String>>();
    private List<String> pagesToVisit = new LinkedList<String>();
    private List<String> pagesToVisitThisLayer = new LinkedList<String>();
    private List<List<String>> urlToUrl = new ArrayList<List<String>>();
    private List<List<String>> dNameToDName = new ArrayList<List<String>>();


    public void search(String url, String domain, int setLayer) {
        try {
            Csv.Writer writer1 = new Csv.Writer("D:\\csvV\\"+domain+"1.csv").delimiter(';');
            Csv.Writer writer2 = new Csv.Writer("D:\\csvV\\"+domain+"2.csv").delimiter(';');
            Csv.Writer writer3 = new Csv.Writer("D:\\csvV\\"+domain+"3.csv").delimiter(';');
            int layer = 0;
            while (true) {
                String currentUrl;
                SpiderLeg leg = new SpiderLeg();
                if (this.pagesToVisit.isEmpty()) {
                    currentUrl = url;
                    this.pagesVisited.add(url);
                } else {
                    currentUrl = this.nextUrl();
                }
                if(currentUrl.contains("://")){
                    if(!currentUrl.startsWith("http://") && !currentUrl.startsWith("https://")){
                        continue;
                    }
                }
                if(currentUrl.startsWith("http://www.")){
                    currentUrl = currentUrl.replaceFirst("http://www.","http://");
                }
                if(currentUrl.startsWith("https://www.")){
                    currentUrl = currentUrl.replaceFirst("https://www.","https://");
                }
                URL currURL = new URL(currentUrl);
                String dName = currURL.getHost();
                if(dName.isEmpty()) {continue;}
                if(!this.pagesFoundedName.contains(dName) && dName.contains(domain)){
                    boolean success = leg.crawl("http://"+dName);
                    /*if(leg.getMessage().equals(dName)){
                        if(currentUrl.startsWith("http://")){
                            currentUrl = currentUrl.replaceFirst("http://", "http://www.");
                        }
                        if(currentUrl.startsWith("https://")){
                            currentUrl = currentUrl.replaceFirst("https://", "https://www.");
                        }
                        if(leg.crawl(currentUrl));
                        {
                            leg.links(currentUrl, domain);
                            List<String> pagesName = new LinkedList<String>();
                            pagesName.add(dName);
                            String title = leg.getTitle();
                            pagesName.add(title);

                            this.pagesFoundedName.add(dName);
                            this.pagesFounded.add(pagesName);
                            leg = new SpiderLeg();
                        }
                    }*/
                    if(success) {
                        leg.links(currentUrl,domain);
                        List<String> pagesName = new LinkedList<String>();
                        pagesName.add(dName);
                        String title = leg.getTitle();
                        pagesName.add(title);

                        this.pagesFoundedName.add(dName);
                        this.pagesFounded.add(pagesName);
                        leg = new SpiderLeg();
                    }
                }
                boolean succes = leg.crawl(currentUrl);
                if(succes) {
                    leg.links(currentUrl, domain);
                }
                for(int i=0;i<leg.getUrlToUrl().size();i++) {
                    if (!this.urlToUrl.contains(leg.getUrlToUrl().get(i))) {
                        this.urlToUrl.add(leg.getUrlToUrl().get(i));
                    }
                }
                System.out.println(this.pagesFounded.size() + " size of pagesFounded");
                System.out.println(this.pagesFounded);
                System.out.println(this.urlToUrl.size() + " size of urlToUrl");
                for(int i=0;i<leg.getLinks().size();i++) {
                    if(!this.pagesToVisit.contains(leg.getLinks().get(i)) && !this.pagesVisited.contains(leg.getLinks().get(i))) {
                        if(leg.getLinks().get(i).endsWith("/") && this.pagesVisited.contains(leg.getLinks().get(i).substring(0,leg.getLinks().get(i).length()-1))){continue;}
                        if(layer == setLayer-1){continue;}
                        this.pagesToVisit.add(leg.getLinks().get(i));
                    }
                }
                if(this.pagesToVisitThisLayer.isEmpty()){
                    layer++;
                    if(layer == setLayer){break;}
                    this.pagesToVisitThisLayer.addAll(this.pagesToVisit);
                }
                System.out.println(pagesToVisitThisLayer.size());
                System.out.println(pagesToVisit.size());
                if (!succes && this.pagesToVisit.isEmpty()) {
                    break;
                }
                if (this.pagesToVisit.isEmpty()) {
                    break;
                }

            }
            System.out.println(this.pagesVisited.size() + " pages visited");
            System.out.println(this.pagesFounded);
            for(int i=0; i<this.pagesFounded.size();i++){
                writer1.value(this.pagesFounded.get(i).get(0)).value(this.pagesFounded.get(i).get(1)).newLine();
            }
            for(int i=0; i<this.urlToUrl.size();i++){
                writer2.value(this.urlToUrl.get(i).get(0)).value(this.urlToUrl.get(i).get(1)).newLine();
            }
            writer1.close();
            writer2.close();
            for(int i=0;i<this.pagesFounded.size();i++){
                for(int j=0;j<this.pagesFounded.size();j++){
                    if(j==i){continue;}
                    int count = 0;
                    List<String> temp = new ArrayList<String>();
                    for(int w=0;w<this.urlToUrl.size();w++){
                        URL url1 = new URL(this.urlToUrl.get(w).get(0));
                        String domain1 = url1.getHost();
                        URL url2 = new URL(this.urlToUrl.get(w).get(1));
                        String domain2 = url2.getHost();
                        if((domain1.equals(this.pagesFounded.get(i).get(0)) || domain1.equals("www."+this.pagesFounded.get(i).get(0)))
                                && (domain2.equals(this.pagesFounded.get(j).get(0)) || domain2.equals("www."+this.pagesFounded.get(j).get(0)))){
                            count++;
                        }
                    }
                    if(count==0){continue;}
                    temp.add(this.pagesFounded.get(i).get(0));
                    temp.add(this.pagesFounded.get(j).get(0));
                    temp.add(String.valueOf(count));
                    this.dNameToDName.add(temp);
                    writer3.value(temp.get(0)).value(temp.get(1)).value(temp.get(2)).newLine();
                }
            }
            writer3.close();
        }
        catch(Exception ioe){
            System.out.println(ioe.getMessage());
        }

    }

    private String nextUrl() {
        String nextUrl;
        nextUrl = this.pagesToVisitThisLayer.remove(0);
        this.pagesToVisit.remove(0);
        System.out.println(nextUrl + " next page to visit");
        this.pagesVisited.add(nextUrl);
        return nextUrl;
    }
}