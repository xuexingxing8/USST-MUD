import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private final Player player;
    private final Random random;
    //数组存放题库
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
            // 读取一行输入并处理指令
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
    //ProcessCommand 方法用于处理玩家输入的指令。它首先将输入的指令进行修剪和分割，提取出动作和参数。然后根据动作执行相应的操作，例如查看出口、查看地图、移动、抽题、作答、提示、交谈、寻宝、吃饭、投篮、热身和休息等。如果输入的指令为空或未知，会提示玩家输入有效指令。

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
//Game.move() 方法用于处理玩家移动的逻辑。它首先获取玩家当前所在的房间，然后根据玩家输入的方向查找对应的出口。如果没有找到出口，提示玩家该方向没有出口，并保持在当前房间。如果找到了出口，则将 activeQuestion 设置为 null（表示当前没有活跃的问题），然后更新玩家的位置到新的目的地，并调用 look() 方法显示新的房间信息。
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
