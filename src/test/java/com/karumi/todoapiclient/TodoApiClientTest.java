/*
 *   Copyright (C) 2016 Karumi.
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.karumi.todoapiclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.karumi.todoapiclient.dto.TaskDto;
import com.karumi.todoapiclient.exception.ItemNotFoundException;
import com.karumi.todoapiclient.exception.UnknownErrorException;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TodoApiClientTest extends MockWebServerTest {

  private TodoApiClient apiClient;

  @Before public void setUp() throws Exception {
    super.setUp();
    String mockWebServerEndpoint = getBaseEndpoint();
    apiClient = new TodoApiClient(mockWebServerEndpoint);
  }


  //region getAllTask

  @Test public void sendsAcceptAndContentTypeHeaders() throws Exception {
    enqueueMockResponse();

    apiClient.getAllTasks();

    assertRequestContainsHeader("Accept", "application/json");
  }

  @Test public void sendsGetAllTaskRequestToTheCorrectEndpoint() throws Exception {
    enqueueMockResponse();

    apiClient.getAllTasks();

    assertGetRequestSentTo("/todos");
  }

  @Test public void parsesTasksProperlyGettingAllTheTasks() throws Exception {
    enqueueMockResponse(200, "getTasksResponse.json");

    List<TaskDto> tasks = apiClient.getAllTasks();

    assertEquals(tasks.size(), 200);
    assertTaskContainsExpectedValues(tasks.get(0));
  }

  private void assertTaskContainsExpectedValues(TaskDto task) {
    assertEquals(task.getId(), "1");
    assertEquals(task.getUserId(), "1");
    assertEquals(task.getTitle(), "delectus aut autem");
    assertFalse(task.isFinished());
  }

  @Test (expected = UnknownErrorException.class)
  public void shouldThrowExceptionFor500InGetAllTasks() throws Exception {
    enqueueMockResponse(500);

    apiClient.getAllTasks();
  }

    //endregion

    //region get task by id

    @Test
    public void parseTaskById() throws Exception {
        enqueueMockResponse(200, "getTaskByIdResponse.json");

        TaskDto taskDto = apiClient.getTaskById("1");

        assertTaskContainsExpectedValues(taskDto);
    }

    @Test (expected = ItemNotFoundException.class)
    public void shouldThrowExceptionFor404InGetTaskById() throws Exception {
        enqueueMockResponse(404);

        apiClient.getTaskById("1");
    }

    @Test public void sendsAcceptAndContentTypeHeadersForGetTaskById() throws Exception {
        enqueueMockResponse();

        apiClient.getTaskById("1");

        assertRequestContainsHeader("Accept", "application/json");
    }

    @Test (expected = UnknownErrorException.class)
    public void shouldThrowExceptionFor500InGetTaskById() throws Exception {
        enqueueMockResponse(500);

        apiClient.getTaskById("1");
    }

    @Test public void sendsGetTaskByIdRequestToTheCorrectEndpoint() throws Exception {
        enqueueMockResponse();

        apiClient.getTaskById("1");

        assertGetRequestSentTo("/todos/1");
    }

    //endregion

    //region add task

    @Test
    public void shouldReturnOkForAdTask() throws Exception {
      enqueueMockResponse(201, "addTaskResponse.json");

      TaskDto taskDto = apiClient.addTask(createTask());

      assertTaskContainsExpectedValues(taskDto);
    }

    @Test public void sendsGetAddTaskRequestToTheCorrectEndpoint() throws Exception {
        enqueueMockResponse();

        apiClient.addTask(createTask());

        assertPostRequestSentTo("/todos");
    }

    private TaskDto createTask() {
      return new TaskDto("1", "2", "Finish this kata", false);
    }

    @Test public void sendsAcceptAndContentTypeHeadersForCreateTask() throws Exception {
        enqueueMockResponse();

        apiClient.addTask(createTask());

        assertRequestContainsHeader("Accept", "application/json");
    }

    //endregion

    //region delete task

    @Test
    public void sendsAcceptAndContentTypeHeadersForDeleteTask() throws Exception {
        enqueueMockResponse();

        apiClient.deleteTaskById("1");

        assertRequestContainsHeader("Accept", "application/json");
    }

    @Test public void sendsGetDeleteTaskRequestToTheCorrectEndpoint() throws Exception {
        enqueueMockResponse();

        apiClient.deleteTaskById("1");

        assertDeleteRequestSentTo("/todos/1");
    }

    @Test (expected = ItemNotFoundException.class)
    public void shouldThrowExceptionFor404InDeleteTaskd() throws Exception {
        enqueueMockResponse(404);

        apiClient.deleteTaskById("1");
    }

    @Test (expected = UnknownErrorException.class)
    public void shouldThrowExceptionFor500InGetDelete() throws Exception {
        enqueueMockResponse(500);

        apiClient.deleteTaskById("1");
    }

    @Test
    public void shouldParseJsonForDeleteTask() throws Exception {
        enqueueMockResponse(200);
    }

    //endregion

    //region request

    @Test
    public void checkAddTaskRequestIsCorrect() throws Exception {
        enqueueMockResponse(200);

        apiClient.addTask(createTask());

        assertRequestBodyEquals("addTaskRequest.json");
    }

    @Test
    public void checkUpdateTaskRequestIsCorrect() throws Exception {
        enqueueMockResponse(200);

        apiClient.updateTaskById(createTask());

        assertRequestBodyEquals("updateTaskRequest.json");
    }

    //endregion
}
