package cn.xiao.svr.rpc;

import com.unionpay.magpie.server.AbstractServerPayloadListener;

/**
 * Created by Smile on 2018/5/5.
 */
public class ServerPayloadListener extends AbstractServerPayloadListener {
    @Override
    public byte[] handle(String serviceId, byte[] requestBytes) {
        System.out.println("new req!!! " + new String(requestBytes));
        //return requestBytes;
        return "Hello Magpie!".getBytes();
    }
}
