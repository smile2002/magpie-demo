package cn.xiao.svr.bin;

import com.unionpay.magpie.server.AbstractServerPayloadListener;


public class ServicePayloadListener extends AbstractServerPayloadListener {
    @Override
    public byte[] handle(String protocolType, byte[] rawReq) {
        System.out.println("new request, protocolType=" + protocolType);
        return "Hello Magpie!".getBytes();
    }
}

