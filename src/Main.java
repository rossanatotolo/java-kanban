import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.InMemoryTaskManager;
import service.Managers;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Приготовить ужин", "Лазанья", Status.NEW);
        taskManager.createTask(task1);
        System.out.println("Create: " + task1);

        Task task2 = new Task("Купить книгу", "Гарри Поттер", Status.NEW);
        taskManager.createTask(task2);
        System.out.println("Create: " + task2);

        Epic epic1 = new Epic("Собрать наобходимое в Хогвартс", "Согласно перечню из письма");
        taskManager.createEpic(epic1);
        System.out.println("Create: " + epic1);

        SubTask subTask1 = new SubTask("Купить волшебную палочку", "В лавке Олливандера", Status.NEW, 3);
        taskManager.createSubTask(subTask1);
        System.out.println("Create: " + subTask1);
        System.out.println("Create: " + epic1);

        SubTask subTask2 = new SubTask("Купить жмыра", "В магазине, расположенном в Косом переулке", Status.IN_PROGRESS, 3);
        taskManager.createSubTask(subTask2);
        System.out.println("Create: " + subTask2);
        System.out.println("Create: " + epic1);

        Epic epic2 = new Epic("Пойти в кино", "посмотреть мультик");
        taskManager.createEpic(epic2);
        System.out.println("Create: " + epic2);

        SubTask subTask3 = new SubTask("Купить билет", "В кассе", Status.DONE, 6);
        taskManager.createSubTask(subTask3);
        System.out.println("Create: " + subTask3);
        System.out.println("Create: " + epic2);

        System.out.println(); // оставила, для удобства чтения

        //вношу измения в таска, затем обновляю и печатаю список
        task1 = new Task("Приготовить ужин", "Лазанья", Status.IN_PROGRESS, task1.getId());
        taskManager.updateTask(task1);
        task2 = new Task("Купить книгу", "Гарри Поттер", Status.DONE, task2.getId());
        taskManager.updateTask(task2);
        System.out.println(taskManager.getAllTask());
        System.out.println(); // оставила, для удобства чтения

        //вношу изменения в статусы сабтасков, обновляю сабтаски и эпик
        subTask1 = new SubTask("Купить волшебную палочку", "В лавке Олливандера", Status.DONE, 3, subTask1.getId());
        subTask2 = new SubTask("Купить жмыра", "В магазине, расположенном в Косом переулке", Status.NEW, 3, subTask2.getId());
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateEpic(epic1);
        System.out.println("Result: " + taskManager.getAllSubTask()); //печатаю список сабстасков
        System.out.println("Status: " + epic1); //проверяю актуальный статус у эпика1

        taskManager.deleteSubTask(subTask2.getId()); // удаляю сабтаску2\
        System.out.println("Status: " + epic1); //  проверяю поменялся ли статус у эпика1
        System.out.println(); // оставила, для удобства чтения

        //вношу изменения в статус сабтаски3, обновляю сабтаску3 и эпик2
        subTask3 = new SubTask("Купить билет", "В кассе", Status.NEW, 6, subTask3.getId());
        taskManager.updateSubTask(subTask3);
        taskManager.updateEpic(epic2);
        System.out.println("Result: " + taskManager.getAllSubTask()); //печатаю список сабстасков
        System.out.println("Status: " + epic2);

        taskManager.deleteEpic(epic2.getId()); // удаляем эпик2
        System.out.println("Result: " + taskManager.getEpic(epic2.getId()));  //проверяем эпик2
        System.out.println("Result: " + taskManager.getSubTask(subTask3.getId()));  //проверяем сабтаску3
        System.out.println("Result: " + taskManager.getAllSubTask()); //проверяю список всех сабтасок
        System.out.println("Result: " + taskManager.getAllEpic()); //проверяю список всех эпиков


        Task task3 = new Task("Сдать проект", "Уложиться в дедлайн", Status.NEW);
        taskManager.createTask(task3);
        Epic epic3 = new Epic("Найти работу", "Сразу после курсов, удачи мне");
        taskManager.createEpic(epic3);
        SubTask subTask4 = new SubTask("Закончить курсы", "В декабре", Status.NEW, 9 );
        taskManager.createSubTask(subTask4);

        System.out.println();
        //проверка истории просмотров:
        System.out.println("Задачи:");
        System.out.println(taskManager.getAllTask());

        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpic());

        System.out.println("Подзадачи:");
        System.out.println(taskManager.getAllSubTask());

        System.out.println(taskManager.getTask(8));
        System.out.println(taskManager.getSubTask(10));
        System.out.println(taskManager.getEpic(9));

        System.out.println("История:");

        System.out.println(taskManager.getHistory());
        System.out.println(Managers.getDefaultHistory().getHistory()); // проверка, должно быть пустое

    }
}
