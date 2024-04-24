package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    private TaskManager taskManager;

    @BeforeEach
    public void addTask() {
        taskManager = Managers.getDefault();
    }

    @Test // проверьте, что наследники класса Task равны друг другу, если равен их id;
    public void shouldExtendsTasksEqualsIfTheirIdAreEquals() {
        Epic epic = new Epic("Новый эпик", "Описание");
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1);
        taskManager.createSubTask(subTask1);
        final int id = subTask1.getId();

        SubTask subTask2 = new SubTask("Новая подзадача2", "Описание2", Status.IN_PROGRESS, 1, id);
        taskManager.updateSubTask(subTask2);

        assertEquals(subTask1, subTask2, "Задачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getAllSubTask();

        assertNotNull(subTask1, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask2, subTasks.get(0), "Задачи не совпадают.");
    }

    @Test // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    public void shouldEpicObjectCannotBeAddedToASubTask() {
        //у эпика 1 конструктор
        Epic epic1 = new Epic("Тест", "Тест1"); //id 1
        taskManager.createEpic(epic1);

        //у сабтасок есть 2 конструктора
        //первый на создание
        SubTask subTask1 = new SubTask("Тест", "Тест1", Status.IN_PROGRESS, 1);
        taskManager.createSubTask(subTask1);
        //второй на перезапись
        SubTask subTask2 = new SubTask("Тест", "Тест1", Status.IN_PROGRESS, 1, 2);
        taskManager.updateSubTask(subTask2);

        assertEquals(subTask1, subTask2, "Сабтаски не равны, несмотря на идентичность полей");
        assertNotEquals(epic1.getClass(), subTask1.getClass(), "Эпик и сабстаски равны, несмотря на различия классов");
        assertNotEquals(epic1, subTask1, "Эпик и сабтаск равны, несмотря на различия полей");
    }

    @Test // проверьте, что объект Subtask нельзя сделать своим же эпиком;
    public void shouldSubtaskCannotBeMadeYourOwnEpic() {
        Epic epic1 = new Epic("Новый эпик", "Описание");
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1);
        taskManager.createSubTask(subTask1);
        subTask1 = new SubTask("Новая подзадача1", "Описание1", Status.NEW, 1, 1);
        taskManager.updateSubTask(subTask1);
        taskManager.updateEpic(epic1);

        assertNotEquals(epic1, subTask1, "Неверно");
    }
}
//писала в пачку тебе в личку, но возможно приходит слишком много сообщений от студентов и мое где-то затерялось, а время боюсь упустить, тк идет уже жесткий дедлайн(
//поэтому дублирую сообщение и тут:
/* Рома, привет, хотела бы обсудить ревью 5 спринта)) Момент с проверкой эпиков и сабтасок: проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
проверьте, что объект Subtask нельзя сделать своим же эпиком;
Дело в том, что я не могу оформить эти проверки, т.к. моя реализация не позволяет этого... У меня в классе эпик - 1 конструктор с 2 параметрами(String name, String description);
в классе сабтаск у меня - 2 конструктора( первый с такими параметрами - (String name, String description, Status status, int idEpic);
а второй с такими - (String name, String description, Status status, int idEpic, int id)).
Я попыталась что-то придумать, но честно говоря попытка выглядит слабо и я не уверена, что вообще могу еще что-то изобрести, и не знаю что теперь делать(((

По поводу 3 теста: создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер;
этот тест есть у меня в классе ManagersTest, в самом конце. Остальные недочеты по другим вопросам, поправила))
P.S. спрашивала совета у наставника, он сказал, что не надо делать эти тесты.. в общем, решила отправить, что есть, а надо будет удалю их просто тогда))
P.P.S. сорри за много букв :D   */