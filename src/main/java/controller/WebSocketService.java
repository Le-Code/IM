package controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket/{info}")
public class WebSocketService {
    //创建时间格式对象
    private static SimpleDateFormat df = new SimpleDateFormat("\"HH:mm:ss\"");
    //创建一个房间的集合
    private static ConcurrentHashMap<String,ConcurrentHashMap<String,WebSocketService>> roomList = new ConcurrentHashMap<>();

    //与某个客户端的连接回话，需要通过他来给客户端发送数据
    private Session session;

    //重新加入房间的标识
    private int rejoin = 0;

    static {
        roomList.put("room1",new ConcurrentHashMap<String, WebSocketService>());
        roomList.put("room2",new ConcurrentHashMap<String, WebSocketService>());
    }

    @OnOpen
    public void onOpen(@PathParam(value = "info") String param, Session session){
        System.out.println("连接成功");
        this.session = session;
        String flag = param.split("[|]")[0];//标示
        String member = param.split("[|]")[1];//成员名
        if (flag.equals("join")){
            String user = param.split("[|]")[2];
            joinRoom(member,user);
        }
    }

    private void joinRoom(String member, String user) {
        ConcurrentHashMap<String,WebSocketService> r = roomList.get(member);
        if (r.get(user) != null){
            this.rejoin = 1;
        }
        r.put(user,this);
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    @OnMessage
    public void onMessage(String message,Session session){
        JSONObject obj = new JSONObject(message);
        if (obj.getString("flag").equals("exitroom")){
            String roomId = obj.getString("roomid");
            //将用户从聊天室中移除
            int f2 = 1;
            //将用户直接移除
            roomList.get(roomId).remove(obj.getString("nickname"));
            //判断房间还有多少成员，如果没有则直接移除房间
            if (roomList.get(roomId).size()==0){
                f2 = 2;
            }
            if (f2==1){
                obj.put("flag","exitroom");
                String m= obj.getString("nickname")+" 退出了房间";
                obj.put("message",m);
                ConcurrentHashMap<String,WebSocketService> r = roomList.get(roomId);
                List<String>uname = new ArrayList<>();
                for (String u:r.keySet()){
                    uname.add(u);
                }
                obj.put("uname",uname.toArray());
                //便利该房间
                for (String i:r.keySet()){
                    try {
                        r.get(i).sendMessage(obj.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if (obj.getString("flag").equals("chatroom")){
            //想json对象中添加发送时间
            obj.put("data",df.format(new Date()));
            //获取客户端发送的数据中的内容---房间号
            String roomId = obj.getString("target");
            //获取客户端发送数据中的内容---用户
            String username = obj.getString("nickname");
            //从房间列表中定位到该房间
            ConcurrentHashMap<String,WebSocketService> r = roomList.get(roomId);
            List<String>uname = new ArrayList<>();
            for (String u:r.keySet()){
                uname.add(u);
            }
            obj.put("uname",uname.toArray());
            //证明不是退出重连
            if (r.get(username).rejoin==0){
                for (String i:r.keySet()){
                    //设置消息是否是自己的
                    obj.put("isSelf",username.equals("i"));
                    try {
                        r.get(i).sendMessage(obj.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                obj.put("isSelf",true);
                try {
                    r.get(username).sendMessage(obj.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            r.get(username).rejoin = 0;
        }
    }

    @OnClose
    public void onClose(Session session){

    }

    @OnError
    public void onError(Throwable t){
        t.printStackTrace();
    }
}
