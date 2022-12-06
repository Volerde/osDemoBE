package com.example.osdemo.utils;

import com.example.osdemo.pojo.BDModel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Memory {
    private int size;// 内存大小
    private static final int MIN_SIZE = 2;// 最小剩余分区大小
    private static LinkedList<Zone> zones;// 内存分区
    private int pointer;// 上次分配的空闲区位置

    public static LinkedList<Zone> getZone(){
        if (zones == null) {
            zones = new LinkedList<>();
        }
        return zones;
    }

    // 新建一个内部类，分区节点类
    static class Zone {
        int size;// 分区大小
        int head;// 分区始址
        boolean isFree;// 空闲状态

        public Zone(int head, int size) {
            this.head = head;
            this.size = size;
            this.isFree = true;
        }
    }

    private static Memory memory;
    public static Memory getMemory(){
        if (memory == null) {
            memory = new Memory();
        }
        return memory;
    }

    // 默认内存大小512K
    Memory() {
        this.size = 512;
        this.pointer = 0;// 默认首次运行程序，上次分配的空闲区位置为0
        this.zones = new LinkedList<>();
        zones.add(new Zone(0, size));
    }

    Memory(int size) {
        this.size = size;
        this.pointer = 0;
        this.zones = new LinkedList<>();
        zones.add(new Zone(0, size));
    }

    /** 内存分配菜单
     *
     * @param size 空间大小
     */
    public String allocation(int size,int algorithm) {
        String msg = "";
        switch (algorithm) {
            case 1 -> msg = FF(size);
            case 2 -> msg = NF(size);
            case 3 -> msg = BF(size);
            case 4 -> msg = WF(size);
            case 5 -> msg = BD(size);
            default -> {
                msg = "请重新选择";
                System.out.println("请重新选择");
            }
        }
        return msg;
    }

    /** 首次适应算法
     *
     * @param size 空间大小
     */
    private String FF(int size) {
        // 遍历分区链表
        for (pointer = 0; pointer < zones.size(); pointer++) {
            Zone tmp = zones.get(pointer);
            // 找到可用分区（空闲且大小足够）
            if (tmp.isFree && (tmp.size > size)) {
                doAllocation(size, pointer, tmp);
                return "分配成功";
            }
        }
        // 遍历结束后未找到可用分区, 则内存分配失败
        System.out.println("无可用内存空间!");
        return "无可用内存空间!";
    }

    // 循环首次适应算法
    private String NF(int size) {
        Zone tmp = zones.get(pointer);
        if (tmp.isFree && (tmp.size > size)) {
            doAllocation(size, pointer, tmp);
            return "分配成功";
        }
        int len = zones.size();
        int i = (pointer + 1) % len;
        for (; i != pointer; i = (i + 1) % len) {
            tmp = zones.get(i);
            if (tmp.isFree && (tmp.size > size)) {
                doAllocation(size, i, tmp);
                return "分配成功";
            }
        }
        // 全遍历后如果未分配则失败
        System.out.println("无可用内存空间!");
        return "无可用内存空间!";
    }

    // 最佳适应算法
    private String BF(int size) {
        int flag = -1;
        int min = this.size;
        for (pointer = 0; pointer < zones.size(); pointer++) {
            Zone tmp = zones.get(pointer);
            if (tmp.isFree && (tmp.size > size)) {
                if (min > tmp.size - size) {
                    min = tmp.size - size;
                    flag = pointer;
                }
            }
        }
        if (flag == -1) {
            System.out.println("无可用内存空间!");
            return "无可用内存空间!";
        } else {
            doAllocation(size, flag, zones.get(flag));
            return "分配成功";
        }
    }

    // 最坏适应算法
    private String WF(int size) {
        int flag = -1;
        int max = 0;
        for (pointer = 0; pointer < zones.size(); pointer++) {
            Zone tmp = zones.get(pointer);
            if (tmp.isFree && (tmp.size > size)) {
                if (max < tmp.size - size) {
                    max = tmp.size - size;
                    flag = pointer;
                }
            }
        }
        if (flag == -1) {
            System.out.println("无可用内存空间!");
            return "无可用内存空间!";
        } else {
            doAllocation(size, flag, zones.get(flag));
            return "分配成功";
        }
    }

    private static LinkedList<BDModel> startend;
    private static Integer index = 0;
    public static Integer getIndex(){
        return index;
    }
    public static void setIndex(int num){
        index = num;
    }
    public static LinkedList<BDModel> getStartend(){
        if (startend == null){
            startend = new LinkedList<>();
        }
        return startend;
    }

    private String BD(int size) {
        for(pointer = 0; pointer < zones.size();pointer++){
            Zone tmp = zones.get(pointer);
            if (tmp.isFree && (tmp.size > size)){
                boolean success = Buddy.request_mem(size,pointer,tmp);
                if (success) {
                    return "分配成功";
                }
            }
        }
        return "分配失败";
    }

    private boolean breakMemory(int size) {

        return false;
    }

    // 开始分配
    private void doAllocation(int size, int location, Zone tmp) {
        // 要是剩的比最小分区MIN_SIZE小，则把剩下那点给前一个进程
        if (tmp.size - size <= MIN_SIZE) {
            tmp.isFree = false;
        } else {
            Zone split = new Zone(tmp.head + size, tmp.size - size);
            zones.add(location + 1, split);
            tmp.size = size;
            tmp.isFree = false;
        }
        System.out.println("成功分配 " + size + "KB 内存!");
    }

    // 内存回收
    public Object collection(int id) {
        if (id >= zones.size()) {
            return "无此分区编号";
        }
        Zone tmp = zones.get(id);
        int size = tmp.size;
        if (tmp.isFree) {
            return "指定分区未被分配, 无需回收";
        }
        // 如果回收的分区后一个是空闲就和后一个合并
        if (id < zones.size() - 1 && zones.get(id + 1).isFree) {
            Zone next = zones.get(id + 1);
            tmp.size += next.size;
            zones.remove(next);
        }
        // 回收的分区要是前一个是空闲就和前分区合并
        if (id > 0 && zones.get(id - 1).isFree) {
            Zone previous = zones.get(id - 1);
            previous.size += tmp.size;
            zones.remove(id);
            id--;
        }
        zones.get(id).isFree = true;
        return "内存回收成功!, 本次回收了 " + size + "KB 空间!";
    }

    // 展示分区状况
    public List<Map<String, Object>> showZones() {
        List<Map<String, Object>> list = new LinkedList<>();
        for (int i = 0; i < zones.size(); i++) {
            Zone tmp = zones.get(i);
            System.out.println(i + "\t\t" + tmp.head + "\t\t" + tmp.size + "  \t" + tmp.isFree);
            Map<String, Object> map = new HashMap<>();
            map.put("id",i);
            map.put("start",tmp.head);
            map.put("size",tmp.size);
            map.put("status",tmp.isFree);
            list.add(map);
        }
        return list;
    }
}
