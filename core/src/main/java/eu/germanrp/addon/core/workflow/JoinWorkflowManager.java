package eu.germanrp.addon.core.workflow;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.JoinSequenceCompletedEvent;
import eu.germanrp.addon.core.common.events.JustJoinedEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import static net.labymod.api.Laby.fireEvent;

public class JoinWorkflowManager {

    private final GermanRPAddon addon;
    private final Set<String> pendingTasks = new HashSet<>();

    @Getter
    @Setter
    private boolean wasAFK;

    @Setter
    @Getter
    private boolean returningToAFK;

    public JoinWorkflowManager(GermanRPAddon addon) {
        this.addon = addon;
    }

    public synchronized void startTask(String taskName) {
        pendingTasks.add(taskName);
        addon.getPlayer().sendDebugMessage("JoinWorkflowManager: Started task %s".formatted(taskName));
    }

    public synchronized void finishTask(String taskName) {
        if (pendingTasks.remove(taskName)) {
            addon.getPlayer().sendDebugMessage("JoinWorkflowManager: Finished task \"%s\". Remaining: %s".formatted(taskName, pendingTasks));
            if (pendingTasks.isEmpty()) {
                completeWorkflow();
            }
        }
    }

    public void completeWorkflow() {
        addon.getPlayer().sendDebugMessage("JoinWorkflowManager: Workflow completed. wasAFK=" + wasAFK);
        if (wasAFK) {
            this.returningToAFK = true;
            this.addon.getChatListener().setAfkEmptyMessages(0);
            addon.getPlayer().sendServerMessage("/afk");
            wasAFK = false;
        }
        pendingTasks.clear();
        fireEvent(new JustJoinedEvent(false));
        fireEvent(new JoinSequenceCompletedEvent());
    }

}
