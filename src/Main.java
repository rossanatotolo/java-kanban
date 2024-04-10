import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Приготовить ужин", "Лазанья", Status.NEW);
        taskManager.create(task1);
        System.out.println("Create: " + task1);

        Task task2 = new Task("Купить книгу", "Гарри Поттер", Status.NEW);
        taskManager.create(task2);
        System.out.println("Create: " + task2);

        Epic epic1 = new Epic("Собрать наобходимое в Хогвартс", "Согласно перечню из письма");
        taskManager.createEpics(epic1);
        System.out.println("Create: " + epic1);

        SubTask subTask1 = new SubTask("Купить волшебную палочку", "В лавке Олливандера", Status.NEW, 3);
        taskManager.createSubTasks(subTask1);
        System.out.println("Create: " + subTask1);
        System.out.println("Create: " + epic1);

        SubTask subTask2 = new SubTask("Купить жмыра", "В магазине, расположенном в Косом переулке", Status.IN_PROGRESS, 3);
        taskManager.createSubTasks(subTask2);
        System.out.println("Create: " + subTask2);
        System.out.println("Create: " + epic1);

        Epic epic2 = new Epic("Пойти в кино", "посмотреть мультик");
        taskManager.createEpics(epic2);
        System.out.println("Create: " + epic2);

        SubTask subTask3 = new SubTask("Купить билет", "В кассе", Status.DONE, 6);
        taskManager.createSubTasks(subTask3);
        System.out.println("Create: " + subTask3);
        System.out.println("Create: " + epic2);

        System.out.println("Status: " + taskManager.getEpics((epic1.getId()))); //проверяем актуальный статус у эпика1
        taskManager.deleteSubTasks(subTask2.getId()); // удаляю сабтаску2
        System.out.println("Status: " + taskManager.getEpics((epic1.getId()))); // еще раз проверяю

        taskManager.deleteEpics(epic2.getId()); // удаляем эпик2
        System.out.println("Result: " + taskManager.getEpics(epic2.getId()));  //проверяем эпик2
        System.out.println("Result: " + taskManager.getSubTasks(subTask3.getId()));  //проверяем сабтаску3

    }
}
