package cn.xiao.svr.rpc;

/**
 * Created by Smile on 2018/5/5.
 */
public class HelloServiceImpl implements IHelloService {
    @Override
    public String sayHello(String name) {
        return "Hello from IHelloService";
    }
}
