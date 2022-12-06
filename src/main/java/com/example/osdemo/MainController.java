package com.example.osdemo;

import com.example.osdemo.pojo.BDModel;
import com.example.osdemo.utils.Memory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@Controller
public class MainController {

    @ResponseBody
    @GetMapping("/allocate")
    public Object allocateMemory(@RequestParam int size, @RequestParam int algorithm) {
        Memory memory = Memory.getMemory();
        String msg = memory.allocation(size, algorithm);
        if (msg.equals("分配成功") && algorithm != 5) {
            return memory.showZones();
        } else if (algorithm == 5) {
            return Memory.getStartend();
        } else {
            return msg;
        }
    }

    @ResponseBody
    @GetMapping("/free")
    public Object freeMemory(@RequestParam int id, @RequestParam int algorithm) {
        if (algorithm == 5) {
            LinkedList<BDModel> list = Memory.getStartend();
            try {
                for(int i = 0;i < list.size(); i++){
                    if (list.get(i).getId() == id){
                        int size = list.get(i).getEnd() - list.get(i).getStart() + 1;
                        list.remove(i);
                        return "内存回收成功!, 本次回收了 " + size + "KB 空间!";
                    }
                }
                return "回收失败";
            }catch (IndexOutOfBoundsException e){
                return "回收失败";
            }
        }
        return Memory.getMemory().collection(id);
    }

    @ResponseBody
    @GetMapping("/view")
    public Object viewMemoryAllocation(@RequestParam int algorithm) {
        if (algorithm == 5) {
            return Memory.getStartend();
        }
        return Memory.getMemory().showZones();
    }
}
