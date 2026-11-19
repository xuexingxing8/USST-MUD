# 上海理工校园挑战 · 课堂演示

## 从 CampusMap.java 接着整理

在 VS Code 打开本文件夹，里面是六个配套 Java 文件，可以直接在这个目录运行，也可以把六个源文件一起复制到自己的项目。地图沿用手绘版的 11 个地点。516 校门往前进入湛恩大道，右侧先遇到实验楼小道，再是一教、篮球场。毛泽东像在大道尽头，食堂在左，宿舍和篮球场相连。map 输出地点与路线清单。

1. CampusMap.java 保存地图，Room.java 保存单个地点的名称和出口。
2. Player.java 保存玩家状态，负责加分、学习、资源消耗、归零回宿舍和休息。
3. Question.java 保存题目、答案、提示和是否已答对。
4. Npc.java 保存人物、熟悉度和每日互动记录。
5. Game.java 负责启动、处理指令、组织活动和自动通关。

## 编译运行

在 VS Code 的 CMD 终端逐行输入。最后一行的 0 固定抽题和投篮结果，适合课堂演示。日常玩可以省略 0。

```cmd
cd /d "E:\cwork\Mud\课堂版"
chcp 65001
"E:\Java\JDK\bin\javac.exe" -encoding UTF-8 Game.java Player.java Room.java CampusMap.java Question.java Npc.java
"E:\Java\JDK\bin\java.exe" Game 0
```

## 游戏规则

开局输入姓名，起点 516 校门，学分和答题成绩均为 0，体力 100、心情 70、行动 6。每个学分升一级，学分最高 3。实验楼前两轮各给 1 学分并获得实践章，篮球首次命中给 1 学分和运动章。一教首次答对获得解谜章。一教 4 题每题 10 分，实验楼 3 题每题 20 分，只有首次答对才加成绩，最高 100。三章齐全、3 学分且成绩至少 50 时自动展示成绩单。

实验楼每次提交有效答案扣 25 体力和 1 次行动，投篮扣 15 体力和 1 次行动。体力归零立即回宿舍，额外扣 1 次行动，最低为 0，本轮结果仍结算。宿舍 rest 进入次日，恢复至 100 体力、70 心情和 6 次行动，保留学习进度。走路和对话免费。

毛泽东像旁用 search 找投篮线索。篮球场用 shoot 近投 或 shoot 远投。远投命中率 40%，近投 60%，心情至少 70 时近投为 70%。命中心情 +10，失手 -5。一教答对心情 +5。体力和心情限制在 0～100。

| NPC | 每天首次有效互动 | 熟悉后的帮助 |
| --- | --- | --- |
| 学习委员 | 一教 talk | 抽题后用 hint 看提示 |
| 实验室助教 | 提交一轮练习 | 抽题时提醒还能安全练几轮 |
| 食堂阿姨 | eat | 普通饭体力 +40、心情 +15，加菜为 +50、+25，每天一顿 |
| 篮球队长 | 投篮 | warmup 每天一次，心情 +5 |
| 宿舍室友 | talk | 每天首次聊天心情 +10 |

同一位 NPC 每天最多涨 1 点熟悉度，积满 2 点后开始提供帮助。

## 演示一 · 正常移动、异常输入和随机通关

用种子 0 启动后输入名字。可先按空回车，再输入 abc、go、go 左，依次看到空输入、未知指令、缺少方向和无出口。无出口时留在校门。接着逐行输入以下指令。

```text
  GO   前
go 前
go 右
quiz
answer int
go 出口
go 后
go 右
go 前
quiz
answer false
quiz
answer 欧姆
go 出口
go 出口
go 前
go 前
go 右
shoot 近投
status
quit
```

到一教后第一次随机抽到整数关键字题，答对后学分 0、成绩 10。实验楼两题答对后学分 2、成绩 50、体力 50。球场近投按 70% 命中率抽取结果，本次种子命中，自动弹出成绩单。

## 演示二 · 实验耗尽体力

另开一局。输入姓名后依次输入 go 前、go 右、go 前，到实验楼输入 quiz，连续四次输入 answer abc。错误答案不会使题目消失，方便重复演示。体力依次为 75、50、25、0，第四次自动回宿舍，学分为 2、成绩为 0、当天行动余 1。输入 status 查看，再用 rest 恢复，随后 status 查看次日状态。

## 演示三 · 食堂熟人加菜

另开一局。从校门连续输入四次 go 前，到毛泽东像输入 search，再 go 左 到食堂输入 eat。然后依次输入 go 出口、go 后、go 右、go 对面、rest 到宿舍休息。输入 go 对面、go 出口、go 前、go 左 回食堂，再输入 eat，能看到食堂阿姨加菜和熟悉度 2/2。

## 截图五项任务

| 类 | 职责 | 两个关键属性 | 两个关键方法 |
| --- | --- | --- | --- |
| Game | 读取命令、组织玩法 | player、hallQuestions | run、processCommand |
| Player | 保存和更改玩家状态 | energy、score | consumeEnergy、addScore |
| Room | 描述地点与出口 | name、directions | findExit、describeExits |

1. **阅读代码。** 用表里的三个类介绍职责。在大道入口敲 go 右，run 读取输入，processCommand 匹配 go，move 调用 CampusMap.findRoom 和 Room.findExit，找到「实验楼小道」才调用 player.moveTo，随后 look 打印新地点。代码里可指出一个具体问题，Room 的 directions 和 destinations 靠同一下标配对，更新路线时漏改一个数组可能指向错误地点。以后可用一个出口对象同时存方向与目的地。

2. **封装。** Player 和 Room 的实例字段用 private，构造方法初始化玩家姓名或地点与出口。Room 构造时复制出口数组。外部通过 consumeEnergy 或 addScore 执行规则。现场打开 Player.consumeEnergy，指出它在体力归零后更新宿舍位置和行动次数。接着演示第四次提交触发联动。

3. **数组。** 展示 CampusMap 的 Room[] ROOMS 有 11 个地点，Game 的 Question[] 有两组题，分别为 4 和 3 道。map 调用 printMap，用 for 逐项读取地点，findRoom 也会循环按名字查找。演示校门输入 go 前 能走到大道入口，go 左 在校门则无出口。

4. **输入处理。** main 创建游戏，processCommand 处理指令。trim 去首尾空白，split 按连续空白拆为至多两段，动词转小写后分派。展示只输入 go 的提示，再试空输入、未知指令和带空格的大写 GO。

5. **常用类。** Random.nextInt 一次在未答对题目中抽题，一次用 0～99 的随机数判定投篮。Math.max 给体力和行动兜底，Math.min 给成绩封顶。String 的 equalsIgnoreCase 在 Question.tryAnswer 中忽略答案大小写。Player 重写 Object 的 toString，status 通过它显示姓名、地点、学分、成绩和资源。现场任选一次 quiz 或 shoot 展示随机结果。

   game.java

   ```java
   import java.io.PrintStream;
   import java.nio.charset.StandardCharsets;
   import java.util.Locale;
   import java.util.Random;
   import java.util.Scanner;
   
   public class Game {
       private final Player player;
       private final Random random;
       private final Question[] hallQuestions = {
           new Question("Java 源文件的后缀是什么？请带点。", ".java", "后缀就是语言名字。"),
           new Question("Java 中的真值字面量是什么？", "true", "与 false 相反。"),
           new Question("声明整数变量常用哪个关键字？", "int", "是 integer 的前三个字母。"),
           new Question("我们从几号校门进入游戏？", "516", "回忆起点的地点名。")
       };
       private final Question[] labQuestions = {
           new Question("电阻的单位是什么？请输入中文。", "欧姆", "这是欧姆定律中的人名。"),
           new Question("Java 中的假值字面量是什么？", "false", "与 true 相反。"),
           new Question("Java 中 5 % 2 的结果是多少？", "1", "% 计算余数。")
       };
   
       private final Npc classmate = new Npc("学习委员");
       private final Npc assistant = new Npc("实验室助教");
       private final Npc cook = new Npc("食堂阿姨");
       private final Npc captain = new Npc("篮球队长");
       private final Npc roommate = new Npc("宿舍室友");
       private Question activeQuestion;
       private boolean finished;
   
       public Game(Player player, Random random) {
           this.player = player;
           this.random = random;
       }
   
       public static void main(String[] args) {
           System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
           Random random;
           try {
               random = args.length == 0 ? new Random() : new Random(Long.parseLong(args[0]));
           } catch (NumberFormatException ex) {
               System.out.println("演示种子须用整数，例如 java Game 0。");
               return;
           }
   
           try (Scanner input = new Scanner(System.in, "UTF-8")) {
               System.out.println("欢迎来到《上海理工校园挑战》！");
               System.out.print("请输入你的姓名：");
               while (input.hasNextLine()) {
                   String name = input.nextLine().trim();
                   if (!name.isEmpty()) {
                       new Game(new Player(name), random).run(input);
                       return;
                   }
                   System.out.print("姓名不能为空，请重新输入：");
               }
           }
       }
   
       private void run(Scanner input) {
           System.out.println("集齐三枚印章、达到 3 学分和 50 分答题成绩后自动通关。");
           System.out.println("实验与投篮消耗体力。体力归零会回宿舍并额外扣 1 次行动。");
           System.out.println("输入 help 查看指令。");
           System.out.println(player);
           look();
   
           while (true) {
               System.out.print("\n请输入指令 > ");
               if (!input.hasNextLine() || !processCommand(input.nextLine())) {
                   return;
               }
   
               if (!finished && player.canGraduate()) {
                   finished = true;
                   System.out.println("校园挑战完成！这是你的成绩单，你可以继续探索。");
                   System.out.println(player);
               }
           }
       }
   
       private boolean processCommand(String line) {
           String command = line.trim();
           if (command.isEmpty()) {
               System.out.println("请输入指令，或输入 help 查看帮助。");
               return true;
           }
   
           String[] parts = command.split("\\s+", 2);
           String action = parts[0].toLowerCase(Locale.ROOT);
           String argument = parts.length == 2 ? parts[1] : "";
   
           switch (action) {
               case "quit":
                   System.out.println("游戏结束。");
                   return false;
               case "help":
                   System.out.println("look 查出口 | map 查路线 | status 查状态 | go 方向 | quit 退出");
                   System.out.println("talk 对话 | quiz 抽题 | answer 答案 | hint 提示 | search 寻宝");
                   System.out.println("eat 吃饭 | shoot 近投/远投 | warmup 热身 | rest 在宿舍休息");
                   break;
               case "look": look(); break;
               case "map": CampusMap.printMap(player.getLocation()); break;
               case "status": System.out.println(player); break;
               case "go":
                   if (argument.isEmpty()) {
                       System.out.println("请在 go 后输入方向，例如 go 前。");
                   } else {
                       move(argument);
                   }
                   break;
               case "前": case "后": case "左": case "右": case "出口": case "对面":
                   move(action);
                   break;
               case "quiz": startQuiz(); break;
               case "answer": answerQuestion(argument); break;
               case "hint": hint(); break;
               case "talk": talk(); break;
               case "search": search(); break;
               case "eat": eat(); break;
               case "shoot": shoot(argument); break;
               case "warmup":
                   if (at("篮球场") && captain.helpToday(player.getDay())) {
                       player.changeMood(5);
                       System.out.println("和队长热身，心情 +5（最高 100）。");
                   } else {
                       System.out.println("请在篮球场找熟悉的队长热身，每天一次。");
                   }
                   break;
               case "rest":
                   System.out.println(player.rest() ? "已进入次日，体力和行动已恢复。" : "请回宿舍休息。");
                   break;
               default: System.out.println("未知指令，输入 help 查看帮助。");
           }
           return true;
       }
   
       private boolean at(String location) {
           return location.equals(player.getLocation());
       }
   
       private void look() {
           Room room = CampusMap.findRoom(player.getLocation());
           System.out.println("你现在位于：" + room.getName());
           System.out.println(room.describeExits());
       }
   
       private void move(String direction) {
           Room room = CampusMap.findRoom(player.getLocation());
           String destination = room.findExit(direction);
           if (destination == null) {
               System.out.println("这个方向没有出口，你仍在" + room.getName() + "。");
               return;
           }
   
           activeQuestion = null;
           player.moveTo(destination);
           look();
       }
   
       private void startQuiz() {
           Question[] bank;
           if (at("第一教学楼")) {
               bank = hallQuestions;
           } else if (at("实验楼")) {
               bank = labQuestions;
           } else {
               System.out.println("请到一教或实验楼抽题。");
               return;
           }
   
           if (activeQuestion == null) {
               Question[] available = new Question[bank.length];
               int count = 0;
               for (Question question : bank) {
                   if (!question.isSolved()) {
                       available[count++] = question;
                   }
               }
   
               if (count == 0) {
                   System.out.println("这里的题都已答对。");
                   return;
               }
   
               activeQuestion = available[random.nextInt(count)];
           }
   
           if (at("实验楼") && assistant.isFamiliar()) {
               System.out.println("助教提醒：还能完成 " + player.safeLabRounds()
                       + " 轮练习并留在实验楼。每轮扣 25 体力。");
           }
           System.out.println("题目：" + activeQuestion.getText());
           System.out.println("输入 answer 答案。");
       }
   
       private void answerQuestion(String answer) {
           if (activeQuestion == null || answer.isEmpty()) {
               System.out.println("请先输入 quiz，再用 answer 答案作答。");
               return;
           }
   
           boolean inLab = at("实验楼");
           if (inLab) {
               if (!player.completeLabPractice()) {
                   System.out.println("体力或行动不足，请回宿舍休息。");
                   return;
               }
               assistant.interact(player.getDay());
           }
   
           if (activeQuestion.tryAnswer(answer)) {
               int points = inLab ? 20 : 10;
               player.addScore(points);
               if (!inLab) {
                   player.earnQuizBadge();
                   player.changeMood(5);
               }
               activeQuestion = null;
               System.out.println("答对了！答题成绩 +" + points + "。");
           } else {
               System.out.println("答错了，成绩不变。");
           }
           if (inLab) {
               afterEnergyAction();
           }
       }
   
       private void afterEnergyAction() {
           if (at("宿舍")) {
               activeQuestion = null;
               System.out.println("体力归零，已自动回宿舍，额外扣 1 次行动（最低为 0）。");
               System.out.println("输入 rest 休息。");
           }
           System.out.println(player);
       }
   
       private void hint() {
           if (at("第一教学楼") && activeQuestion != null && classmate.isFamiliar()) {
               System.out.println("提示：" + activeQuestion.getHint());
           } else {
               System.out.println("先与一教的学习委员会面两天，再抽题请求提示。");
           }
       }
   
       private void talk() {
           int day = player.getDay();
           switch (player.getLocation()) {
               case "第一教学楼":
                   classmate.interact(day);
                   System.out.println(classmate.label() + "：输入 quiz 抽一道题吧。");
                   break;
               case "实验楼":
                   System.out.println(assistant.label() + "：输入 quiz，每提交一次答案算一轮练习。");
                   break;
               case "食堂":
                   System.out.println(cook.label() + "：饿了就输入 eat，每天可吃一顿。");
                   break;
               case "篮球场":
                   System.out.println(captain.label() + "：输入 shoot 近投 或 shoot 远投。");
                   break;
               case "宿舍":
                   roommate.interact(day);
                   System.out.println(roommate.label() + "：今天逛校园顺利吗？");
                   if (roommate.helpToday(day)) {
                       player.changeMood(10);
                       System.out.println("聊完心情 +10（最高 100）。");
                   }
                   break;
               default: System.out.println("附近没人可以交谈。");
           }
       }
   
       private void search() {
           if (!at("毛泽东像")) {
               System.out.println("到毛泽东像旁的活动指示牌寻宝。");
           } else if (player.hasClue()) {
               System.out.println("你已经找到投篮线索卡。");
           } else {
               player.findClue();
               System.out.println("在指示牌旁找到线索卡：近投更容易命中，心情达到 70 时还会提高命中率。");
           }
       }
   
       private void eat() {
           if (!at("食堂")) {
               System.out.println("请到食堂吃饭。");
               return;
           }
   
           int day = player.getDay();
           cook.interact(day);
           boolean extra = cook.isFamiliar();
           if (player.eat(extra)) {
               System.out.println(cook.label() + (extra ? "：给你多盛一勺菜。体力 +50，心情 +25。"
                       : "：你的饭菜好了。体力 +40，心情 +15。"));
               System.out.println(player);
           } else {
               System.out.println("今天已经吃过了。");
           }
       }
   
       private void shoot(String style) {
           if (!at("篮球场")) {
               System.out.println("请到篮球场投篮。");
               return;
           }
   
           if (!style.equals("近投") && !style.equals("远投")) {
               System.out.println("请输入 shoot 近投 或 shoot 远投。");
               return;
           }
   
           int chance = player.shotChance(style.equals("近投"));
           if (!player.consumeEnergy(15)) {
               System.out.println("体力或行动不足，请回宿舍休息。");
               return;
           }
   
           captain.interact(player.getDay());
           boolean hit = random.nextInt(100) < chance;
           player.finishShot(hit);
           System.out.println("本次命中率 " + chance + "%，" + (hit ? "命中！首次命中获得运动章和 1 学分。" : "未命中。"));
           afterEnergyAction();
       }
   }
   
   ```

   CampusMap

   ```java
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
   ```

   

Player

```java
public class Player {
    private final String name;
    private String location = "516 校门";
    private int score;
    private int energy = 100;
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

    public void addScore(int points) {
        if (points > 0) {
            score += Math.min(points, 100 - score);
        }
    }

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

```

Npc

```java
public class Npc {
    private final String name;
    private int familiarity;
    private int lastInteractionDay;
    private int lastHelpDay;

    public Npc(String name) {
        this.name = name;
    }

    public void interact(int day) {
        if (lastInteractionDay != day) {
            familiarity = Math.min(2, familiarity + 1);
            lastInteractionDay = day;
        }
    }

    public boolean isFamiliar() {
        return familiarity == 2;
    }

    public boolean helpToday(int day) {
        if (!isFamiliar() || lastHelpDay == day) {
            return false;
        }
        lastHelpDay = day;
        return true;
    }

    public String label() {
        return name + "（熟悉度 " + familiarity + "/2）";
    }
}

```

Question

```java
public class Question {
    private final String text;
    private final String answer;
    private final String hint;
    private boolean solved;

    public Question(String text, String answer, String hint) {
        this.text = text;
        this.answer = answer;
        this.hint = hint;
    }

    public String getText() { return text; }
    public String getHint() { return hint; }
    public boolean isSolved() { return solved; }

    public boolean tryAnswer(String guess) {
        if (solved || !answer.equalsIgnoreCase(guess.trim())) {
            return false;
        }
        solved = true;
        return true;
    }
}

```

Room

```java
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
}
```

