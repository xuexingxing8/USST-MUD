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
