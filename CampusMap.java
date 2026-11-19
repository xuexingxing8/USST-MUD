public class CampusMap {
    private static final Room[] ROOMS = {
        new Room("516 校门",
                new String[]{"前"},
                new String[]{"湛恩大道入口"}),

        new Room("湛恩大道入口",
                new String[]{"后", "前", "右"},
                new String[]{"516 校门", "湛恩大道中段", "实验楼小道"}),

        new Room("实验楼小道",
                new String[]{"出口", "前"},
                new String[]{"湛恩大道入口", "实验楼"}),

        new Room("实验楼",
                new String[]{"出口"},
                new String[]{"实验楼小道"}),

        new Room("湛恩大道中段",
                new String[]{"后", "前", "右"},
                new String[]{"湛恩大道入口", "湛恩大道上段", "第一教学楼"}),

        new Room("第一教学楼",
                new String[]{"出口"},
                new String[]{"湛恩大道中段"}),

        new Room("湛恩大道上段",
                new String[]{"后", "前", "右"},
                new String[]{"湛恩大道中段", "毛泽东像", "篮球场"}),

        new Room("毛泽东像",
                new String[]{"后", "左"},
                new String[]{"湛恩大道上段", "食堂"}),

        new Room("食堂",
                new String[]{"出口"},
                new String[]{"毛泽东像"}),

        new Room("篮球场",
                new String[]{"出口", "对面"},
                new String[]{"湛恩大道上段", "宿舍"}),

        new Room("宿舍",
                new String[]{"对面"},
                new String[]{"篮球场"})
    };

    public static Room findRoom(String name) {
        for (Room room : ROOMS) {
            if (room.getName().equals(name)) {
                return room;
            }
        }

        throw new IllegalStateException("地图中找不到地点：" + name);
    }
    //循环逐项处理Rooms[],ROOMS[]是一个Room对象数组，findRoom方法用于根据房间名称查找对应的Room对象。如果找到了匹配的房间名称，就返回该Room对象；如果没有找到，则抛出一个IllegalStateException异常，提示地图中找不到指定的地点。

    public static void printMap(String currentLocation) {
        for (Room room : ROOMS) {
            String marker = "";
            if (room.getName().equals(currentLocation)) {
                marker = "【当前位置】";
            }

            System.out.println(marker + room.getName() + room.describeExits());
        }
    }
}