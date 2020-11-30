import org.apache.commons.net.ftp.FTPClient;

import java.net.URI;
import java.util.Arrays;


public class Test {
    public static void main(String[] args) throws Exception {

        URI url = new URI("ssh://userid:passwd@2.2.2.2:22");

        System.out.println("passwd = " + url.getAuthority());
        System.out.println("protocol = " + url.getScheme());
        System.out.println("info = " + url.getUserInfo());
        System.out.println("host = " + url.getHost());
        System.out.println("port = " + url.getPort());
        System.out.println("path = " + url.getPath());
        System.out.println("query = " + url.getQuery());
        String[] userInfo = url.getUserInfo().split(":");
    }
}