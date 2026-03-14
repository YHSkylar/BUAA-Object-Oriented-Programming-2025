public class StringProcessor {
    private String string;

    public StringProcessor(String string) {
        this.string = string;
    }

    public void process() {
        string = string.replaceAll("\\s+","");
        for (int i = 0; i < 3; i++) {
            string = string.replaceAll("\\+\\+","+");
            string = string.replaceAll("\\+-","-");
            string = string.replaceAll("-\\+","-");
            string = string.replaceAll("--","+");
            string = string.replaceAll("\\^\\+","^");
            string = string.replaceAll("\\*\\+","*");
        }
    }

    public String getString() {
        return string;
    }
}
