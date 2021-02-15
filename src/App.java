import models.*;

public class App {
    public static void main(String[] args) throws Exception {
        int[] times = {1,2,3,4};
        PositionGraph graph = new PositionGraph(times);
        System.out.println(graph);
    }
}
