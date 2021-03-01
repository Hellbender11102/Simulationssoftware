import controller.Controller;
import view.View;

class Main {
    public static void main(String[] args) {
        View view = new View();
        Controller controller = new Controller(view);
    }
}