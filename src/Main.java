public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        //Создание Tasks
        Task task1 = new Task("Task 1", "Description of Task 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Description of Task 2");
        taskManager.createTask(task2);

        // Создание Epics
        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        taskManager.createEpic(epic1);
        int idEpic1 = epic1.getId();

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1");
        subtask1.setEpicId(idEpic1);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2");
        subtask2.setEpicId(idEpic1);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        taskManager.createEpic(epic2);
        int idEpic2 = epic2.getId();
        Subtask subtask3 = new Subtask("Subtask 3", "Description of Subtask 3");
        subtask3.setEpicId(idEpic2);
        taskManager.createSubtask(subtask3);

        System.out.println("Tasks: ");
        System.out.println(taskManager.getAllTasks());
        System.out.println("Epics: ");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Subtasks: ");
        System.out.println(taskManager.getAllSubtask());

        task1.setStatus(Status.DONE);
        task2.setStatus(Status.IN_PROGRESS);
        System.out.println("Tasks change Status: ");
        System.out.println(taskManager.getAllTasks());

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        taskManager.updateEpic(epic1);
        taskManager.updateEpic(epic1);
        System.out.println(epic1);

        taskManager.removeAllSubtasks();
        System.out.println(taskManager.getAllEpics());
        taskManager.removeTask(task1.getId());
        taskManager.removeEpic(idEpic2);
        System.out.println();

    }
}
