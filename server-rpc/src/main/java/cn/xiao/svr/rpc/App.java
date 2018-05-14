package cn.xiao.svr.rpc;

import com.unionpay.magpie.bootstrap.MagpieBootStrap;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        MagpieBootStrap.getBootStrap().start();
    }
}
