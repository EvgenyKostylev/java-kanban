package manager;

import exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        if (file.length() != 0) {
            List<String> tasksFromFile = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                while (br.ready()) {
                    String line = br.readLine();

                    tasksFromFile.add(line);
                }
                tasksFromFile.removeFirst();
            } catch (IOException e) {
                throw new ManagerSaveException(e);
            }

            for (String line : tasksFromFile) {
                fileBackedTaskManager.fromString(line);
            }
        }
        return fileBackedTaskManager;
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
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
        return subtask;
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
            bw.write("id,type,name,status,description,epic\n");
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
            additionalInformation = ("subtasksId" + ((Epic) task).getSubtasks());
        } else if (task.getType() == TaskType.SUBTASK) {
            lineFromTask.append(TaskType.SUBTASK).append(",");
            additionalInformation = String.valueOf(((Subtask) task).getEpicId());
        } else {
            lineFromTask.append(TaskType.TASK).append(",");
        }
        lineFromTask.append(task.getName()).append(",").append(task.getStatus()).append(",")
                .append(task.getDescription());
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
            Epic epicFromLine = new Epic(taskFromLine);

            if (!dataFromLine[5].equals("subtasksId=null")) {
                epicFromLine.setSubtasksId(subtasksByEpicFromString(dataFromLine[5]));
            }
            super.createEpic(epicFromLine);
        } else {
            int epicIdFromLine = Integer.parseInt(dataFromLine[5]);
            Subtask subtaskFromLine = new Subtask(taskFromLine, epicIdFromLine);

            super.createSubtask(subtaskFromLine);
        }
    }

    private ArrayList<Integer> subtasksByEpicFromString(String line) {
        ArrayList<Integer> subtasksIdByEpic = new ArrayList<>();
        StringBuilder subtasksIdFromLine = new StringBuilder(line);

        subtasksIdFromLine.delete(0, 11);
        subtasksIdFromLine.deleteCharAt(subtasksIdFromLine.length() - 1);
        if (!subtasksIdFromLine.isEmpty()) {
            String[] subtasksId = subtasksIdFromLine.toString().split(", ");

            for (String subtaskId : subtasksId) {
                subtasksIdByEpic.add(Integer.valueOf(subtaskId));
            }
        }
        return subtasksIdByEpic;
    }

    private Task taskFromData(String[] data) {
        int idFromData = Integer.parseInt(data[0]);
        String nameFromData = data[2];
        Status statusFromData = Status.valueOf(data[3]);
        String descriptionFromData = data[4];
        Task taskFromData = new Task(nameFromData, descriptionFromData, statusFromData);

        taskFromData.setId(idFromData);
        return taskFromData;
    }
}
