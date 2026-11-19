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
