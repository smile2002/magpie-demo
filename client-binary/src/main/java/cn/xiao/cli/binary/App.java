package cn.xiao.cli.binary;

import com.unionpay.magpie.bootstrap.BootStrap;
import com.unionpay.magpie.bootstrap.MagpieBootStrap;
import com.unionpay.magpie.client.ServiceRegistry;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            BootStrap bootStrap = MagpieBootStrap.getBootStrap();
            bootStrap.start();
            Thread.sleep(500);
            byte[] resultBytes = ServiceRegistry.getService("myService1").call("hello magpie".getBytes());
            System.out.println("result = " + new String(resultBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
