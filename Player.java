public class Player {
    // 玩家类表示游戏中的玩家角色。它包含玩家的姓名、当前位置、分数、体力、心情、行动次数、天数以及各种状态标志。玩家可以移动到不同的房间，消耗体力，完成实验实践，获得徽章，改变心情，进行投篮等操作。玩家的状态会随着游戏进程而变化，并且可以通过 toString 方法获取玩家的当前状态信息。
    //构造方法
    private final String name;
    private String location = "516 校门";
    private int score;
    private int energy = 100;//外部代码不应该直接增减玩家体力值
    private int mood = 70;
    private int actions = 6;
    private int day = 1;
    private int labRounds;
    private boolean quizBadge;
    private boolean sportsBadge;
    private boolean clue;
    private boolean ateToday;

    public Player(String name) {
        this.name = name;
    }

    public String getLocation() { return location; }
    public int getDay() { return day; }

    public void moveTo(String destination) {
        location = destination;
    }
// addScore 方法用于增加玩家的分数。它接受一个整数参数 points，表示要增加的分数值。如果 points 大于 0，则将其加到当前分数上，但确保总分不会超过 100。
    public void addScore(int points) {
        if (points > 0) {
            score += Math.min(points, 100 - score);//Math.min(points, 100 - score) 确保不会超过 100
        }
    }
// consumeEnergy 方法用于消耗玩家的体力。它接受一个整数参数 cost，表示要消耗的体力值。如果 cost 小于等于 0，或者玩家的体力为 0，或者玩家的行动次数为 0，则返回 false，表示无法消耗体力。否则，减少玩家的行动次数，并将体力减少 cost，但确保体力不会低于 0。如果体力降至 0，则将玩家的位置重置为 "宿舍"，并减少一次行动次数。最后返回 true，表示成功消耗体力。
    public boolean consumeEnergy(int cost) {
        if (cost <= 0 || energy == 0 || actions == 0) {
            return false;
        }

        actions--;
        energy = Math.max(0, energy - cost);
        if (energy == 0) {
            location = "宿舍";
            actions = Math.max(0, actions - 1);
        }
        return true;
    }

    public boolean completeLabPractice() {
        if (!"实验楼".equals(location) || !consumeEnergy(25)) {
            return false;
        }
        labRounds = Math.min(2, labRounds + 1);
        return true;
    }

    public int safeLabRounds() {
        return Math.min(actions, Math.max(0, energy - 1) / 25);
    }

    public void earnQuizBadge() { quizBadge = true; }
    public void findClue() { clue = true; }
    public boolean hasClue() { return clue; }

    public void changeMood(int amount) {
        mood = (int) Math.max(0, Math.min(100, (long) mood + amount));
    }

    public int shotChance(boolean near) {
        return near ? (mood >= 70 ? 70 : 60) : 40;
    }

    public void finishShot(boolean hit) {
        changeMood(hit ? 10 : -5);
        if (hit) {
            sportsBadge = true;
        }
    }

    public boolean eat(boolean extra) {
        if (ateToday || !"食堂".equals(location)) {
            return false;
        }
        ateToday = true;
        energy = Math.min(100, energy + (extra ? 50 : 40));
        changeMood(extra ? 25 : 15);
        return true;
    }

    public boolean rest() {
        if (!"宿舍".equals(location)) {
            return false;
        }
        day++;
        energy = 100;
        mood = 70;
        actions = 6;
        ateToday = false;
        return true;
    }

    private int credits() {
        return labRounds + (sportsBadge ? 1 : 0);
    }

    public boolean canGraduate() {
        return quizBadge && labRounds == 2 && sportsBadge && credits() >= 3 && score >= 50;
    }
// toString 方法用于生成玩家的当前状态信息的字符串表示。它包括玩家的姓名、当前天数、当前位置、学分、等级、答题成绩、体力、心情、今日行动次数以及各种徽章的获得情况。该方法返回一个格式化的字符串，便于在游戏中显示玩家的状态。
    @Override
    public String toString() {
        return name + " | 第 " + day + " 天 | 当前位置：" + location
                + "\n学分：" + credits() + "/3 | 等级：" + (1 + credits())
                + " | 答题成绩：" + score + "/100"
                + "\n体力：" + energy + "/100 | 心情：" + mood + "/100 | 今日行动：" + actions + "/6"
                + "\n解谜章 " + (quizBadge ? "已获得" : "待获得")
                + " | 实践章 " + (labRounds == 2 ? "已获得" : "待获得")
                + " | 运动章 " + (sportsBadge ? "已获得" : "待获得");
    }
}
