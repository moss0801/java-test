package spock;

public class NegativeNumberNotAllowException extends RuntimeException {
    private long value;

    public NegativeNumberNotAllowException(long value) {
        super("음수는 계산할 수 없습니다.");
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
