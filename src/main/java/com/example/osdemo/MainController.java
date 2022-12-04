package com.example.osdemo;

import com.example.osdemo.utils.Memory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    @ResponseBody
    @GetMapping("/allocate")
    public Object allocateMemory(@RequestParam int size,@RequestParam int algorithm) {
        Memory memory = Memory.getMemory();
        String msg = memory.allocation(size,algorithm);
        if (msg.equals("分配成功")) {
            return memory.showZones();
        }else {
            return msg;
        }
    }

    @ResponseBody
    @GetMapping("/free")
    public Object freeMemory(@RequestParam int id) {
        return Memory.getMemory().collection(id);
    }

    @ResponseBody
    @GetMapping("/view")
    public Object viewMemoryAllocation(){
        return Memory.getMemory().showZones();
    }
}
