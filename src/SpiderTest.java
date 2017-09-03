public class SpiderTest
{

    public static void main(String[] args)
    {
        String domain = "spbu.ru";
        Spider spider = new Spider();
        spider.search("http://"+domain,domain,4);
    }
}