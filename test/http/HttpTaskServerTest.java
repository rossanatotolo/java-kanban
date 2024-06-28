package http;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServerTest {

    protected HttpTaskServer taskServer;
    protected TaskManager taskManager;
    protected Gson gson;


    public HttpTaskServerTest() {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.clearTask();
        taskManager.clearEpic();
        taskManager.clearSubTask();
        taskServer.start();
    }


    @AfterEach
    public void shotDown() {
        taskServer.stop();
    }
}