public class Room {
    private final String name;
    private final String[] directions;
    private final String[] destinations;

    public Room(String name, String[] directions, String[] destinations) {
        this.name = name;
        this.directions = directions.clone();
        this.destinations = destinations.clone();
    }

    public String getName() {
        return name;
    }

    public String findExit(String direction) {
        for (int i = 0; i < directions.length; i++) {
            if (directions[i].equals(direction)) {
                return destinations[i];
            }
        }
        return null;
    }

    public String describeExits() {
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < directions.length; i++) {
            text.append("\n ") 
                .append(directions[i])
                .append(" → ")
                .append(destinations[i]);
        }

        return text.toString();
    }
    // Room 类表示游戏中的房间。它包含房间的名称、可用的方向以及对应的目的地。findExit 方法用于根据给定的方向查找对应的出口，如果找不到则返回 null。describeExits 方法用于生成一个描述房间出口的字符串，列出所有可用的方向及其对应的目的地。
    //可以用一个Exit类来表示出口，包含方向和目的地两个属性。Room类可以使用一个Exit数组来存储所有的出口信息，这样可以更清晰地表示房间的出口结构。
}