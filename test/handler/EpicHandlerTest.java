package handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import http.HttpTaskServerTest;
import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest extends HttpTaskServerTest {

    @Test
    public void shouldGetEpicTest() throws IOException, InterruptedException { // get epic
        Epic epic1 = new Epic("Test Epic1", "Test");
        String epicJson = gson.toJson(epic1);

        SubTask subTask1 = new SubTask("Test SubTask1", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 4, 0));
        taskManager.createSubTask(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        epic1.setId(1);

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/1")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Статус код не совпадает");

        Epic epicOfGet = gson.fromJson(response.body(), Epic.class);
        assertEquals(epicOfGet, epic1, "Неверное преобразование задачи или не та задача была получена");

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/3")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Статус код не совпадает");
    }

    @Test
    public void shouldGetAllEpicsTest() throws IOException, InterruptedException { // getAllEpics
        Epic epic1 = new Epic("Test Epic1", "Test");
        String epicJson = gson.toJson(epic1);

        SubTask subTask1 = new SubTask("Test SubTask1", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 4, 0));
        taskManager.createSubTask(subTask1);

        Epic epic2 = new Epic("Test Epic1", "Test");
        String epicJson2 = gson.toJson(epic2);

        SubTask subTask2 = new SubTask("Test SubTask1", "Test", Status.NEW, 3, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 5, 0));
        taskManager.createSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код не совпадает");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertTrue(jsonElement.isJsonArray(), "Получен не список");
        assertEquals(jsonArray.size(), 2, "Количество эпиков неверно");
    }

    @Test
    public void shouldAddEpicTest() throws IOException, InterruptedException { //create epic
        Epic epic1 = new Epic("Test Epic1", "Test");
        String epicJson = gson.toJson(epic1);

        SubTask subTask1 = new SubTask("Test SubTask1", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 4, 0));
        taskManager.createSubTask(subTask1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        epic1.setId(1);
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = taskManager.getAllEpic();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test Epic1", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void shouldUpdateEpicTest() throws IOException, InterruptedException { // update epic
        Epic epic1 = new Epic("Test Epic1", "Test");
        String epicJson = gson.toJson(epic1);

        SubTask subTask1 = new SubTask("Test SubTask1", "Test", Status.IN_PROGRESS, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 4, 0));
        taskManager.createSubTask(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        subTask1 = new SubTask("New name", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 4, 0));
        epic1 = new Epic("New name", "Test");
        taskManager.updateSubTask(subTask1);
        taskManager.updateEpic(epic1);

        assertEquals(epic1.getName(), "New name", "Имя эпика не обновилось");
        assertEquals(epic1.getStatus(), Status.NEW, "Статус эпика не изменился");
        assertEquals(201, response.statusCode(), "Статус код не совпадает");
    }

    @Test
    public void shouldDeleteEpicTest() throws IOException, InterruptedException { // delete epic
        Epic epic1 = new Epic("Test Epic1", "Test");
        String epicJson = gson.toJson(epic1);

        SubTask subTask1 = new SubTask("Test SubTask1", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 4, 0));
        taskManager.createSubTask(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        HttpRequest requestDelete = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/1")).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseDelete.statusCode());

        List<Epic> epicsFromManager = taskManager.getAllEpic();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(0, epicsFromManager.size(), "Количество эпиков не совпадает");
    }
}