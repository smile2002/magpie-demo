package cn.xiao.svr.bin;

import com.unionpay.magpie.bootstrap.BootStrap;
import com.unionpay.magpie.bootstrap.MagpieBootStrap;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            System.out.println("Hello World!");
            BootStrap bootStrap = MagpieBootStrap.getBootStrap(new ServicePayloadListener());
            System.setProperty("magpie.config.file", "magpieConfig.xml");
            bootStrap.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
