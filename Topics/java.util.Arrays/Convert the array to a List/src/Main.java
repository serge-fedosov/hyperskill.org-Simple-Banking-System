import java.util.Scanner;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Integer[] arr = {scanner.nextInt(), scanner.nextInt(), scanner.nextInt()};
        List<Integer> list = Arrays.asList(arr);

        System.out.println(list.get(0));
    }
}