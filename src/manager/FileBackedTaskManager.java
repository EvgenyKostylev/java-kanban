package manager;

import exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            if (file.length() != 0) {
                List<String> tasksFromFile = new ArrayList<>();

                while (br.ready()) {
                    String line = br.readLine();

                    tasksFromFile.add(line);
                }
                tasksFromFile.removeFirst();
                tasksFromFile.forEach(fileBackedTaskManager::fromString);
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);

        save();
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);

        save();
        return createdEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);

        save();
        return createdSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);

        save();
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);

        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);

        save();
        return updatedSubtask;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,startTime,duration,epic\n");
            for (Task task : getTasks()) {
                bw.write(toString(task));
            }
            for (Epic epic : getEpics()) {
                bw.write(toString(epic));
            }
            for (Subtask subtask : getSubtasks()) {
                bw.write(toString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private String toString(Task task) {
        StringBuilder lineFromTask = new StringBuilder();
        String additionalInformation = null;

        lineFromTask.append(task.getId()).append(",");
        if (task.getType() == TaskType.EPIC) {
            lineFromTask.append(TaskType.EPIC).append(",");
        } else if (task.getType() == TaskType.SUBTASK) {
            lineFromTask.append(TaskType.SUBTASK).append(",");
            additionalInformation = String.valueOf(((Subtask) task).getEpicId());
        } else {
            lineFromTask.append(TaskType.TASK).append(",");
        }
        lineFromTask.append(task.getName()).append(",").append(task.getStatus()).append(",")
                .append(task.getDescription()).append(",");
        if (task.getStartTime() == null) {
            lineFromTask.append("null,null");
        } else {
            lineFromTask.append(task.getStartTime()).append(",").append(task.getDuration().toMinutes());
        }
        if (additionalInformation != null) {
            lineFromTask.append(",").append(additionalInformation);
        }
        lineFromTask.append("\n");
        return String.valueOf(lineFromTask);
    }

    private void fromString(String line) {
        String[] dataFromLine = line.split(",(?!\\s)");
        TaskType taskTypeFromLine = TaskType.valueOf(dataFromLine[1]);
        Task taskFromLine = taskFromData(dataFromLine);

        if (taskTypeFromLine == TaskType.TASK) {
            super.createTask(taskFromLine);
        } else if (taskTypeFromLine == TaskType.EPIC) {
            super.createEpic(new Epic(taskFromLine));
        } else {
            int epicIdFromLine = Integer.parseInt(dataFromLine[7]);
            Subtask subtaskFromLine = new Subtask(taskFromLine, epicIdFromLine);

            super.createSubtask(subtaskFromLine);
        }
    }

    private Task taskFromData(String[] data) {
        int idFromData = Integer.parseInt(data[0]);
        String nameFromData = data[2];
        Status statusFromData = Status.valueOf(data[3]);
        String descriptionFromData = data[4];
        LocalDateTime startTimeFromData = data[5].equals("null") ? null : LocalDateTime.parse(data[5]);
        Duration durationFromData = data[6].equals("null") ? null : Duration.ofMinutes(Long.parseLong(data[6]));
        Task taskFromData = new Task(nameFromData, descriptionFromData, statusFromData, startTimeFromData,
                durationFromData);

        taskFromData.setId(idFromData);
        return taskFromData;
    }
}