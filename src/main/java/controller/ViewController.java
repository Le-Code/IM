package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("home")
@SessionAttributes("uName")
public class ViewController {

    @RequestMapping("list")
    public String cc(){
        return "index";
    }

    @RequestMapping("room")
    public String h(ModelMap model, String uname, String roomid){
        model.put("uname",uname);
        model.put("roomid", roomid);
        return "room";
    }

}
