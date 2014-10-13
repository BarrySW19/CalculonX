package nl.zoidberg.calculon.model;

public enum Result {
    RES_NO_RESULT("*"),
   	RES_WHITE_WIN("1-0"),
   	RES_BLACK_WIN("0-1"),
   	RES_DRAW("1/2-1/2");

    private String text;

    Result(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
